package com.ssu.ongi.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.ssu.ongi.common.fcm.FcmMessage;
import com.ssu.ongi.common.fcm.FcmService;
import com.ssu.ongi.domain.device.enums.DeviceStatus;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;
import com.ssu.ongi.domain.notification.enums.NotificationType;
import com.ssu.ongi.domain.notification.event.DeviceOfflineEvent;
import com.ssu.ongi.domain.notification.event.MedicationEvent;
import com.ssu.ongi.domain.notification.event.MedicationReminderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final FcmService fcmService;
    private final FcmTokenCleanupService fcmTokenCleanupService;

    /**
     * 복약 완료/미복용 이벤트를 보호자에게 FCM으로 전송합니다.
     */
    public void sendMedicationAlert(MedicationEvent event) {
        if (!StringUtils.hasText(event.fcmToken())) {
            log.info("[FCM] 복약 알림 생략 - memberId={}, result={}, reason=no_fcm_token",
                    event.memberId(), event.result());
            return;
        }

        FcmMessage message = new FcmMessage(
                event.fcmToken(),
                getMedicationTitle(event.result()),
                getMedicationBody(event.medicineName(), event.result()),
                Map.of(
                        "type", getMedicationType(event.result()).name(),
                        "memberId", String.valueOf(event.memberId()),
                        "elderId", String.valueOf(event.elderId()),
                        "medicineId", String.valueOf(event.medicineId()),
                        "recordedAt", event.recordedAt().toString()
                )
        );

        sendFcmWithRetry(message,
                "memberId=" + event.memberId() + " medicineId=" + event.medicineId() + " result=" + event.result());
    }

    /**
     * 복약 시간 알림 이벤트를 보호자에게 FCM으로 전송합니다.
     */
    public void sendMedicationReminder(MedicationReminderEvent event) {
        if (!StringUtils.hasText(event.fcmToken())) {
            log.info("[FCM] 복약 시간 알림 생략 - memberId={}, medicineId={}, reason=no_fcm_token",
                    event.memberId(), event.medicineId());
            return;
        }

        FcmMessage message = new FcmMessage(
                event.fcmToken(),
                "복약 시간 알림",
                event.medicineName() + " 복용 시간이에요.",
                Map.of(
                        "type", NotificationType.MEDICATION_REMINDER.name(),
                        "memberId", String.valueOf(event.memberId()),
                        "elderId", String.valueOf(event.elderId()),
                        "medicineId", String.valueOf(event.medicineId()),
                        "scheduledTime", event.scheduledTime().toString()
                )
        );

        sendFcmWithRetry(message,
                "memberId=" + event.memberId() + " medicineId=" + event.medicineId());
    }

    /**
     * 디바이스 오프라인 상태 변경 이벤트를 보호자에게 FCM으로 전송합니다.
     */
    public void sendDeviceOffline(DeviceOfflineEvent event) {
        if (!StringUtils.hasText(event.fcmToken())) {
            log.info("[FCM] 디바이스 오프라인 알림 생략 - memberId={}, deviceId={}, reason=no_fcm_token",
                    event.memberId(), event.deviceId());
            return;
        }

        FcmMessage message = new FcmMessage(
                event.fcmToken(),
                getDeviceOfflineTitle(event.status()),
                getDeviceOfflineBody(event.status()),
                Map.of(
                        "type", getDeviceOfflineType(event.status()).name(),
                        "memberId", String.valueOf(event.memberId()),
                        "elderId", String.valueOf(event.elderId()),
                        "deviceId", String.valueOf(event.deviceId()),
                        "status", event.status().name(),
                        "lastSeenAt", event.lastSeenAt().toString()
                )
        );

        sendFcmWithRetry(message,
                "memberId=" + event.memberId() + " deviceId=" + event.deviceId() + " status=" + event.status());
    }

    /**
     * FCM 메시지를 전송합니다.
     * - 무효 토큰 오류(UNREGISTERED/INVALID_ARGUMENT): 재시도 없이 DB에서 토큰 삭제
     * - 일시적 FCM 오류(FirebaseMessagingException): 1회 재시도
     * - 그 외 예외(프로그래밍 오류 등): 재시도 없이 에러 로그만 기록
     */
    private void sendFcmWithRetry(FcmMessage message, String logContext) {
        try {
            String messageId = fcmService.send(message);
            log.info("[FCM] 전송 성공 - {} messageId={}", logContext, messageId);
        } catch (FirebaseMessagingException e) {
            if (isInvalidToken(e)) {
                log.warn("[FCM] 무효 토큰 감지, 토큰 삭제 - {}", logContext);
                fcmTokenCleanupService.deleteByToken(message.token());
                return;
            }
            log.warn("[FCM] 1차 전송 실패, 재시도 - {} errorCode={}", logContext, e.getMessagingErrorCode());
            retrySend(message, logContext);
        } catch (Exception e) {
            // NPE, IllegalArgumentException 등 프로그래밍 오류는 재시도해도 동일하게 실패하므로 재시도하지 않음
            log.error("[FCM] 전송 중 예외 발생 (재시도 안 함) - {} message={}", logContext, e.getMessage());
        }
    }

    /** 1차 전송 실패(일시적 FCM 오류) 후 재시도합니다. */
    private void retrySend(FcmMessage message, String logContext) {
        try {
            String messageId = fcmService.send(message);
            log.info("[FCM] 재시도 전송 성공 - {} messageId={}", logContext, messageId);
        } catch (FirebaseMessagingException e) {
            if (isInvalidToken(e)) {
                log.warn("[FCM] 재시도 중 무효 토큰 감지, 토큰 삭제 - {}", logContext);
                fcmTokenCleanupService.deleteByToken(message.token());
            } else {
                log.error("[FCM] 재시도 후 최종 실패 - {} errorCode={}", logContext, e.getMessagingErrorCode());
            }
        } catch (Exception e) {
            log.error("[FCM] 재시도 후 최종 실패 - {} message={}", logContext, e.getMessage());
        }
    }

    /**
     * FCM 에러가 토큰 무효 오류인지 확인합니다.
     * UNREGISTERED: 앱 삭제 또는 토큰 만료, INVALID_ARGUMENT: 잘못된 토큰 형식
     */
    private boolean isInvalidToken(FirebaseMessagingException e) {
        MessagingErrorCode code = e.getMessagingErrorCode();
        return code == MessagingErrorCode.UNREGISTERED
                || code == MessagingErrorCode.INVALID_ARGUMENT;
    }

    /** MedicationResult에 대응하는 알림 타입을 반환합니다. */
    private NotificationType getMedicationType(MedicationResult result) {
        return result == MedicationResult.TAKEN
                ? NotificationType.MEDICATION_TAKEN
                : NotificationType.MEDICATION_MISSED;
    }

    /** MedicationResult에 대응하는 알림 제목을 반환합니다. */
    private String getMedicationTitle(MedicationResult result) {
        return result == MedicationResult.TAKEN ? "복약 완료" : "미복용 알림";
    }

    /** MedicationResult와 약 이름을 조합하여 알림 본문을 반환합니다. */
    private String getMedicationBody(String medicineName, MedicationResult result) {
        return result == MedicationResult.TAKEN
                ? medicineName + " 복용이 확인되었어요."
                : medicineName + " 복용이 확인되지 않았어요.";
    }

    /** DeviceStatus에 대응하는 오프라인 알림 타입을 반환합니다. */
    private NotificationType getDeviceOfflineType(DeviceStatus status) {
        return status == DeviceStatus.LONG_OFFLINE
                ? NotificationType.DEVICE_LONG_OFFLINE
                : NotificationType.DEVICE_TEMP_OFFLINE;
    }

    /** DeviceStatus에 대응하는 오프라인 알림 제목을 반환합니다. */
    private String getDeviceOfflineTitle(DeviceStatus status) {
        return status == DeviceStatus.LONG_OFFLINE ? "디바이스 장시간 오프라인" : "디바이스 연결 불안정";
    }

    /** DeviceStatus에 대응하는 오프라인 알림 본문을 반환합니다. */
    private String getDeviceOfflineBody(DeviceStatus status) {
        return status == DeviceStatus.LONG_OFFLINE
                ? "디바이스가 장시간 연결되지 않고 있어요."
                : "디바이스 연결이 일시적으로 끊겼어요.";
    }
}

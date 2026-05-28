package com.ssu.ongi.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ssu.ongi.common.fcm.FcmMessage;
import com.ssu.ongi.common.fcm.FcmService;
import com.ssu.ongi.domain.device.enums.DeviceStatus;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;
import com.ssu.ongi.domain.notification.enums.NotificationType;
import com.ssu.ongi.domain.notification.event.DeviceOfflineEvent;
import com.ssu.ongi.domain.notification.event.MedicationEvent;
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

    /**
     * 복약 완료/미복용 이벤트를 보호자에게 FCM으로 전송합니다.
     */
    public void sendMedicationAlert(MedicationEvent event) {
        if (!StringUtils.hasText(event.fcmToken())) {
            log.info("[FCM] 복약 알림 생략 - memberId={}, result={}, reason=no_fcm_token",
                    event.memberId(), event.result());
            return;
        }

        try {
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
            String messageId = fcmService.send(message);
            log.info("[FCM] 복약 알림 발송 성공 - memberId={}, medicineId={}, result={}, messageId={}",
                    event.memberId(), event.medicineId(), event.result(), messageId);
        } catch (FirebaseMessagingException e) {
            log.error("[FCM] 복약 알림 발송 실패 - memberId={}, medicineId={}, result={}, errorCode={}, message={}",
                    event.memberId(), event.medicineId(), event.result(), e.getMessagingErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("[FCM] 복약 알림 처리 중 오류 - memberId={}, medicineId={}, result={}, message={}",
                    event.memberId(), event.medicineId(), event.result(), e.getMessage());
        }
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

    /**
     * 디바이스 오프라인 상태 변경 이벤트를 보호자에게 FCM으로 전송합니다.
     */
    public void sendDeviceOffline(DeviceOfflineEvent event) {
        if (!StringUtils.hasText(event.fcmToken())) {
            log.info("[FCM] 디바이스 오프라인 알림 생략 - memberId={}, deviceId={}, reason=no_fcm_token",
                    event.memberId(), event.deviceId());
            return;
        }

        try {
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
            String messageId = fcmService.send(message);
            log.info("[FCM] 디바이스 오프라인 알림 발송 성공 - memberId={}, deviceId={}, status={}, messageId={}",
                    event.memberId(), event.deviceId(), event.status(), messageId);
        } catch (FirebaseMessagingException e) {
            log.error("[FCM] 디바이스 오프라인 알림 발송 실패 - memberId={}, deviceId={}, status={}, errorCode={}, message={}",
                    event.memberId(), event.deviceId(), event.status(), e.getMessagingErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("[FCM] 디바이스 오프라인 알림 처리 중 오류 - memberId={}, deviceId={}, status={}, message={}",
                    event.memberId(), event.deviceId(), event.status(), e.getMessage());
        }
    }

    /**
     * 디바이스 상태에 맞는 오프라인 알림 타입을 반환합니다.
     */
    private NotificationType getDeviceOfflineType(DeviceStatus status) {
        return status == DeviceStatus.LONG_OFFLINE
                ? NotificationType.DEVICE_LONG_OFFLINE
                : NotificationType.DEVICE_TEMP_OFFLINE;
    }

    /**
     * 디바이스 상태에 맞는 오프라인 알림 제목을 반환합니다.
     */
    private String getDeviceOfflineTitle(DeviceStatus status) {
        return status == DeviceStatus.LONG_OFFLINE ? "디바이스 장시간 오프라인" : "디바이스 연결 불안정";
    }

    /**
     * 디바이스 상태에 맞는 오프라인 알림 내용을 반환합니다.
     */
    private String getDeviceOfflineBody(DeviceStatus status) {
        return status == DeviceStatus.LONG_OFFLINE
                ? "디바이스가 장시간 연결되지 않고 있어요."
                : "디바이스 연결이 일시적으로 끊겼어요.";
    }
}

package com.ssu.ongi.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ssu.ongi.common.fcm.FcmMessage;
import com.ssu.ongi.common.fcm.FcmService;
import com.ssu.ongi.domain.notification.enums.NotificationType;
import com.ssu.ongi.domain.notification.event.MedicationTakenEvent;
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

    public void sendMedicationTaken(MedicationTakenEvent event) {
        if (!StringUtils.hasText(event.fcmToken())) {
            log.info("[FCM] 복약 완료 알림 생략 - memberId={}, reason=no_fcm_token", event.memberId());
            return;
        }

        FcmMessage message = new FcmMessage(
                event.fcmToken(),
                "복약 완료",
                event.medicineName() + " 복용이 확인되었어요.",
                Map.of(
                        "type", NotificationType.MEDICATION_TAKEN.name(),
                        "memberId", String.valueOf(event.memberId()),
                        "elderId", String.valueOf(event.elderId()),
                        "medicineId", String.valueOf(event.medicineId()),
                        "recordedAt", event.recordedAt().toString()
                )
        );

        try {
            String messageId = fcmService.send(message);
            log.info("[FCM] 복약 완료 알림 발송 성공 - memberId={}, medicineId={}, messageId={}",
                    event.memberId(), event.medicineId(), messageId);
        } catch (FirebaseMessagingException e) {
            log.error("[FCM] 복약 완료 알림 발송 실패 - memberId={}, medicineId={}, errorCode={}, message={}",
                    event.memberId(), event.medicineId(), e.getMessagingErrorCode(), e.getMessage());
        }
    }
}

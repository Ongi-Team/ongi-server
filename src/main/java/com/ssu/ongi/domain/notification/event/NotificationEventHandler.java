package com.ssu.ongi.domain.notification.event;

import com.ssu.ongi.domain.notification.service.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationCommandService notificationCommandService;

    /**
     * 복약 완료/미복용 이벤트를 수신하여 FCM 알림을 전송합니다.
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMedicationEvent(MedicationEvent event) {
        notificationCommandService.sendMedicationAlert(event);
    }

    /**
     * 디바이스 오프라인 이벤트를 수신하여 FCM 알림을 전송합니다.
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeviceOffline(DeviceOfflineEvent event) {
        notificationCommandService.sendDeviceOffline(event);
    }
}

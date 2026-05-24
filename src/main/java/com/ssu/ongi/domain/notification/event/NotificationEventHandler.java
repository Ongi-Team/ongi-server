package com.ssu.ongi.domain.notification.event;

import com.ssu.ongi.domain.notification.service.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationCommandService notificationCommandService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMedicationTaken(MedicationTakenEvent event) {
        notificationCommandService.sendMedicationTaken(event);
    }
}

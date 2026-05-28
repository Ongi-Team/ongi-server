package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.medicine.repository.MedicationReminderRepository;
import com.ssu.ongi.domain.medicine.repository.MedicineRepository;
import com.ssu.ongi.domain.notification.event.MedicationReminderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationReminderScheduler {

    private final MedicineRepository medicineRepository;
    private final MedicationReminderRepository reminderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    /**
     * 매분 정각에 현재 시각과 일치하는 복약 스케줄을 확인하여 알림을 발행합니다.
     * Redis SET NX로 당일 중복 발송을 방지합니다.
     */
    @Scheduled(cron = "0 * * * * *")
    public void sendMedicationReminders() {
        LocalTime now = LocalTime.now(clock).withSecond(0).withNano(0);
        LocalDate today = LocalDate.now(clock);

        medicineRepository
                .findAllByScheduledTimeWithElder(now)
                .forEach(medicine -> publishIfNotSent(medicine, today));
    }

    /**
     * SET NX로 원자적으로 중복 체크 후 미발송 약에 한해 알림 이벤트를 발행합니다.
     */
    private void publishIfNotSent(Medicine medicine, LocalDate today) {
        if (!reminderRepository.markAsSentIfAbsent(medicine.getId(), today)) {
            log.debug("[복약 알림] 오늘 이미 발송 완료 - medicineId={}", medicine.getId());
            return;
        }

        eventPublisher.publishEvent(new MedicationReminderEvent(
                medicine.getElder().getMember().getId(),
                medicine.getElder().getId(),
                medicine.getId(),
                medicine.getElder().getMember().getFcmToken(),
                medicine.getName(),
                medicine.getScheduledTime()
        ));

        log.info("[복약 알림] 이벤트 발행 - medicineId={} elderId={} time={}",
                medicine.getId(), medicine.getElder().getId(), medicine.getScheduledTime());
    }
}

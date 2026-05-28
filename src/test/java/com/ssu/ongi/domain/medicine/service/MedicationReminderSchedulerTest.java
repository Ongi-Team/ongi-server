package com.ssu.ongi.domain.medicine.service;

import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import com.ssu.ongi.domain.medicine.repository.MedicationReminderRepository;
import com.ssu.ongi.domain.medicine.repository.MedicineRepository;
import com.ssu.ongi.domain.member.entity.Member;
import com.ssu.ongi.domain.notification.event.MedicationReminderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MedicationReminderSchedulerTest {

    // 2026-05-29 08:00:00 KST (= 2026-05-28T23:00:00Z)
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-05-28T23:00:00Z"), KST);
    private static final LocalDate TODAY = LocalDate.of(2026, 5, 29);

    private MedicineRepository medicineRepository;
    private MedicationReminderRepository reminderRepository;
    private ApplicationEventPublisher eventPublisher;
    private MedicationReminderScheduler scheduler;

    @BeforeEach
    void setUp() {
        medicineRepository = mock(MedicineRepository.class);
        reminderRepository = mock(MedicationReminderRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        scheduler = new MedicationReminderScheduler(
                medicineRepository, reminderRepository, eventPublisher, FIXED_CLOCK);
    }

    @Test
    void 복약_시간이_일치하면_알림_이벤트를_발행한다() {
        Medicine medicine = createMedicine(1L, "혈압약", LocalTime.of(8, 0), "fcm-token");
        when(medicineRepository.findAllByScheduledHourAndMinuteWithElder(8, 0))
                .thenReturn(List.of(medicine));
        when(reminderRepository.markAsSentIfAbsent(1L, TODAY)).thenReturn(true);

        scheduler.sendMedicationReminders();

        ArgumentCaptor<MedicationReminderEvent> captor =
                ArgumentCaptor.forClass(MedicationReminderEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        MedicationReminderEvent event = captor.getValue();
        assertThat(event.medicineName()).isEqualTo("혈압약");
        assertThat(event.scheduledTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(event.fcmToken()).isEqualTo("fcm-token");
    }

    @Test
    void 당일_이미_발송된_약은_이벤트를_발행하지_않는다() {
        Medicine medicine = createMedicine(1L, "혈압약", LocalTime.of(8, 0), "fcm-token");
        when(medicineRepository.findAllByScheduledHourAndMinuteWithElder(8, 0))
                .thenReturn(List.of(medicine));
        when(reminderRepository.markAsSentIfAbsent(1L, TODAY)).thenReturn(false);

        scheduler.sendMedicationReminders();

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void 현재_시각에_해당하는_약이_없으면_이벤트를_발행하지_않는다() {
        when(medicineRepository.findAllByScheduledHourAndMinuteWithElder(anyInt(), anyInt()))
                .thenReturn(List.of());

        scheduler.sendMedicationReminders();

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void 여러_약이_같은_시간에_등록되어_있으면_각각_이벤트를_발행한다() {
        Medicine medicine1 = createMedicine(1L, "혈압약", LocalTime.of(8, 0), "fcm-token-1");
        Medicine medicine2 = createMedicine(2L, "당뇨약", LocalTime.of(8, 0), "fcm-token-2");
        when(medicineRepository.findAllByScheduledHourAndMinuteWithElder(8, 0))
                .thenReturn(List.of(medicine1, medicine2));
        when(reminderRepository.markAsSentIfAbsent(1L, TODAY)).thenReturn(true);
        when(reminderRepository.markAsSentIfAbsent(2L, TODAY)).thenReturn(true);

        scheduler.sendMedicationReminders();

        ArgumentCaptor<MedicationReminderEvent> captor =
                ArgumentCaptor.forClass(MedicationReminderEvent.class);
        verify(eventPublisher, org.mockito.Mockito.times(2)).publishEvent(captor.capture());
        assertThat(captor.getAllValues()).hasSize(2);
    }

    /** 테스트용 Medicine 엔티티를 생성합니다. */
    private Medicine createMedicine(Long id, String name, LocalTime scheduledTime, String fcmToken) {
        Member member = Member.create("guardian", "encoded-pw", "보호자", "01000000000");
        ReflectionTestUtils.setField(member, "id", 10L);
        ReflectionTestUtils.setField(member, "fcmToken", fcmToken);

        Elder elder = Elder.create("어르신", 80, "부");
        ReflectionTestUtils.setField(elder, "id", 20L);
        member.addElder(elder);

        Medicine medicine = Medicine.create(elder, name, scheduledTime);
        ReflectionTestUtils.setField(medicine, "id", id);
        return medicine;
    }
}

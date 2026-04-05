package com.ssu.ongi.domain.medicine.entity;

import com.ssu.ongi.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "medicine_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicineSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private Integer dispenserSlot;

    @Column(nullable = false)
    private LocalTime scheduledTime;

    @Builder
    private MedicineSchedule(Medicine medicine, Integer dispenserSlot, LocalTime scheduledTime) {
        this.medicine = medicine;
        this.dispenserSlot = dispenserSlot;
        this.scheduledTime = scheduledTime;
    }

    public static MedicineSchedule create(Medicine medicine, Integer dispenserSlot, LocalTime scheduledTime) {
        return MedicineSchedule.builder()
                .medicine(medicine)
                .dispenserSlot(dispenserSlot)
                .scheduledTime(scheduledTime)
                .build();
    }
}

package com.ssu.ongi.domain.medicine.entity;

import com.ssu.ongi.common.base.BaseEntity;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "medication_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicationRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_schedule_id", nullable = false)
    private MedicineSchedule medicineSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicationResult result;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @Builder
    private MedicationRecord(MedicineSchedule medicineSchedule, Device device,
                             MedicationResult result, LocalDateTime recordedAt) {
        this.medicineSchedule = medicineSchedule;
        this.device = device;
        this.result = result;
        this.recordedAt = recordedAt;
    }

    public static MedicationRecord create(MedicineSchedule medicineSchedule, Device device,
                                          MedicationResult result, LocalDateTime recordedAt) {
        return MedicationRecord.builder()
                .medicineSchedule(medicineSchedule)
                .device(device)
                .result(result)
                .recordedAt(recordedAt)
                .build();
    }
}

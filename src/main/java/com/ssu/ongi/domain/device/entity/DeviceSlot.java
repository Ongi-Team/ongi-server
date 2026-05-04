package com.ssu.ongi.domain.device.entity;

import com.ssu.ongi.common.base.BaseEntity;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.medicine.entity.Medicine;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "device_slot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_slot_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elder_id", nullable = false)
    private Elder elder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private Integer slotNumber;

    @Builder
    private DeviceSlot(Elder elder, Device device, Medicine medicine, Integer slotNumber) {
        this.elder = elder;
        this.device = device;
        this.medicine = medicine;
        this.slotNumber = slotNumber;
    }

    public static DeviceSlot create(Elder elder, Device device, Medicine medicine, Integer slotNumber) {
        return DeviceSlot.builder()
                .elder(elder)
                .device(device)
                .medicine(medicine)
                .slotNumber(slotNumber)
                .build();
    }

    public void updateSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }
}

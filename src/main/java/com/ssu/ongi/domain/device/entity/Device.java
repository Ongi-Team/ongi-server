package com.ssu.ongi.domain.device.entity;

import com.ssu.ongi.common.base.BaseEntity;
import com.ssu.ongi.domain.elder.entity.Elder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "device")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elder_id", nullable = false)
    private Elder elder;

    @Column(nullable = false)
    private String serialNumber;

    @Builder
    private Device(Elder elder, String serialNumber) {
        this.elder = elder;
        this.serialNumber = serialNumber;
    }

    public static Device create(Elder elder, String serialNumber) {
        return Device.builder()
                .elder(elder)
                .serialNumber(serialNumber)
                .build();
    }
}

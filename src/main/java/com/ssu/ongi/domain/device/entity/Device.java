package com.ssu.ongi.domain.device.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ssu.ongi.common.base.BaseEntity;
import com.ssu.ongi.domain.device.enums.DeviceStatus;
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

    @Column(nullable = false, unique = true)
    private String deviceToken;

    // Heartbeat
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    @Column(name = "uptimeSec")
    private Long uptimeSec;

    @Column(name = "rssi")
    private Integer rssi;

    @Column(name = "lastSeenAt")
    private LocalDateTime lastSeenAt;

    @Builder
    private Device(Elder elder, String serialNumber) {
        this.elder = elder;
        this.serialNumber = serialNumber;
        this.deviceToken = UUID.randomUUID().toString();
    }

    public static Device create(Elder elder, String serialNumber) {
        return Device.builder()
                .elder(elder)
                .serialNumber(serialNumber)
                .build();
    }

    public void updateHeartbeat(DeviceStatus status, Long uptimeSec, Integer rssi) {
        this.status = status;
        this.uptimeSec = uptimeSec;
        this.rssi = rssi;
        this.lastSeenAt = LocalDateTime.now();
    }
}

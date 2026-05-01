package com.ssu.ongi.domain.device.entity;

import java.time.LocalDateTime;

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

    // Heartbeat
    private DeviceStatus status; // ONLINE
    private Long uptimeSec; // 부팅 후 경과 시간
    private Integer rssi; // measures the power of a received radio signal
    // private String firmwareVersion;
    private LocalDateTime lastSeenAt;

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

    public void updateHeartbeat(DeviceStatus status, Long uptimeSec, Integer rssi){
        this.status = status;
        this.uptimeSec = uptimeSec;
        this.rssi = rssi;
        this.lastSeenAt = LocalDateTime.now();
    }
}

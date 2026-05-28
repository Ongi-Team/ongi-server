package com.ssu.ongi.domain.device.service;

import com.ssu.ongi.common.properties.DeviceOfflineProperties;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.enums.DeviceStatus;
import com.ssu.ongi.domain.device.repository.DeviceRepository;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.member.entity.Member;
import com.ssu.ongi.domain.notification.event.DeviceOfflineEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceOfflineMonitorServiceTest {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-05-28T03:00:00Z"), KOREA_ZONE);

    private DeviceRepository deviceRepository;
    private ApplicationEventPublisher eventPublisher;
    private DeviceOfflineMonitorService deviceOfflineMonitorService;

    @BeforeEach
    void setUp() {
        deviceRepository = mock(DeviceRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        deviceOfflineMonitorService = new DeviceOfflineMonitorService(
                deviceRepository,
                new DeviceOfflineProperties(5, 30, 60000),
                FIXED_CLOCK,
                eventPublisher
        );
    }

    /**
     * 일시 오프라인 기준 미만이면 상태를 변경하지 않는지 검증합니다.
     */
    @Test
    void 일시_오프라인_기준_미만이면_상태를_변경하지_않는다() {
        Device device = createDevice(1L, DeviceStatus.ONLINE, now().minusMinutes(4));
        when(deviceRepository.findAllWithElderAndMember()).thenReturn(List.of(device));

        deviceOfflineMonitorService.updateOfflineStatuses();

        assertThat(device.getStatus()).isEqualTo(DeviceStatus.ONLINE);
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }

    /**
     * 일시 오프라인 기준 이상이면 TEMP_OFFLINE으로 변경하고 이벤트를 발행하는지 검증합니다.
     */
    @Test
    void 일시_오프라인_기준_이상이면_TEMP_OFFLINE으로_변경한다() {
        Device device = createDevice(1L, DeviceStatus.ONLINE, now().minusMinutes(5));
        when(deviceRepository.findAllWithElderAndMember()).thenReturn(List.of(device));

        deviceOfflineMonitorService.updateOfflineStatuses();

        assertThat(device.getStatus()).isEqualTo(DeviceStatus.TEMP_OFFLINE);
        DeviceOfflineEvent event = captureOfflineEvent();
        assertThat(event.deviceId()).isEqualTo(1L);
        assertThat(event.status()).isEqualTo(DeviceStatus.TEMP_OFFLINE);
    }

    /**
     * 장시간 오프라인 기준 이상이면 LONG_OFFLINE으로 변경하고 이벤트를 발행하는지 검증합니다.
     */
    @Test
    void 장시간_오프라인_기준_이상이면_LONG_OFFLINE으로_변경한다() {
        Device device = createDevice(1L, DeviceStatus.TEMP_OFFLINE, now().minusMinutes(30));
        when(deviceRepository.findAllWithElderAndMember()).thenReturn(List.of(device));

        deviceOfflineMonitorService.updateOfflineStatuses();

        assertThat(device.getStatus()).isEqualTo(DeviceStatus.LONG_OFFLINE);
        DeviceOfflineEvent event = captureOfflineEvent();
        assertThat(event.deviceId()).isEqualTo(1L);
        assertThat(event.status()).isEqualTo(DeviceStatus.LONG_OFFLINE);
    }

    /**
     * 최초 heartbeat 전 디바이스는 오프라인 판별에서 제외되는지 검증합니다.
     */
    @Test
    void 마지막_수신_시각이_없으면_상태를_변경하지_않는다() {
        Device device = createDevice(1L, null, null);
        when(deviceRepository.findAllWithElderAndMember()).thenReturn(List.of(device));

        deviceOfflineMonitorService.updateOfflineStatuses();

        assertThat(device.getStatus()).isNull();
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }

    /**
     * 이미 같은 오프라인 상태이면 이벤트를 다시 발행하지 않는지 검증합니다.
     */
    @Test
    void 이미_같은_오프라인_상태이면_이벤트를_발행하지_않는다() {
        Device device = createDevice(1L, DeviceStatus.LONG_OFFLINE, now().minusMinutes(40));
        when(deviceRepository.findAllWithElderAndMember()).thenReturn(List.of(device));

        deviceOfflineMonitorService.updateOfflineStatuses();

        assertThat(device.getStatus()).isEqualTo(DeviceStatus.LONG_OFFLINE);
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }

    /**
     * 고정 Clock 기준 현재 시각을 반환합니다.
     */
    private LocalDateTime now() {
        return LocalDateTime.now(FIXED_CLOCK);
    }

    /**
     * 테스트용 디바이스에 보호자, 어르신, 상태 필드를 설정합니다.
     */
    private Device createDevice(Long deviceId, DeviceStatus status, LocalDateTime lastSeenAt) {
        Member member = Member.create("guardian", "encoded-password", "보호자", "01012345678");
        ReflectionTestUtils.setField(member, "id", 100L);
        ReflectionTestUtils.setField(member, "fcmToken", "fcm-token");

        Elder elder = Elder.create("어르신", 80, "부");
        ReflectionTestUtils.setField(elder, "id", 10L);
        member.addElder(elder);

        Device device = Device.create(elder, "ONGI-001");
        ReflectionTestUtils.setField(device, "id", deviceId);
        ReflectionTestUtils.setField(device, "status", status);
        ReflectionTestUtils.setField(device, "lastSeenAt", lastSeenAt);
        return device;
    }

    /**
     * 발행된 오프라인 이벤트를 캡처합니다.
     */
    private DeviceOfflineEvent captureOfflineEvent() {
        ArgumentCaptor<DeviceOfflineEvent> captor = ArgumentCaptor.forClass(DeviceOfflineEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        return captor.getValue();
    }
}

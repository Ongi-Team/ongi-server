package com.ssu.ongi.domain.device.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssu.ongi.common.exception.GeneralException;
import com.ssu.ongi.common.status.ErrorStatus;
import com.ssu.ongi.domain.device.dto.request.HeartbeatRequest;
import com.ssu.ongi.domain.device.entity.Device;
import com.ssu.ongi.domain.device.repository.DeviceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceConnectionService {
	private final DeviceRepository deviceRepository;

	@Transactional
	public void updateHeartbeat(String serialNumber, HeartbeatRequest request) {
		Device device = deviceRepository.findBySerialNumber(serialNumber)
			.orElseThrow(() -> new GeneralException(ErrorStatus.DEVICE_NOT_FOUND));

		device.updateHeartbeat(request.status(), request.uptimeSec(), request.rssi());
		log.info("[heartbeat] serialNumber={} status={} rssi={} uptime={}s",
			serialNumber, request.status(), request.rssi(), request.uptimeSec());
	}
}

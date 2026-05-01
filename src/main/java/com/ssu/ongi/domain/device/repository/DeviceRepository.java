package com.ssu.ongi.domain.device.repository;

import java.util.Optional;

import com.ssu.ongi.domain.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
	Optional<Device> findBySerialNumber(String serialNumber);
}

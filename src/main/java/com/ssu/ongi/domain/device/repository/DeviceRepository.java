package com.ssu.ongi.domain.device.repository;

import com.ssu.ongi.domain.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}

package com.ssu.ongi.domain.device.repository;

import java.util.Optional;

import com.ssu.ongi.domain.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceRepository extends JpaRepository<Device, Long> {
	Optional<Device> findBySerialNumber(String serialNumber);

	Optional<Device> findByDeviceToken(String deviceToken);

	Optional<Device> findByElderId(Long elderId);

	@Query("""
			SELECT COUNT(d) > 0
			FROM Device d
			WHERE d.serialNumber = :serialNumber OR d.elder.id = :elderId
			""")
	boolean existsBySerialNumberOrElderId(@Param("serialNumber") String serialNumber, @Param("elderId") Long elderId);
}

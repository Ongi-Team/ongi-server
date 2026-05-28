package com.ssu.ongi.domain.device.repository;

import java.util.List;
import java.util.Optional;

import com.ssu.ongi.domain.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceRepository extends JpaRepository<Device, Long> {
	Optional<Device> findBySerialNumber(String serialNumber);

	Optional<Device> findByDeviceToken(String deviceToken);

	Optional<Device> findByElderId(Long elderId);

	/**
	 * 오프라인 판별 대상인 heartbeat 수신 이력이 있는 디바이스를 보호자 정보와 함께 조회합니다.
	 */
	@EntityGraph(attributePaths = {"elder", "elder.member"})
	@Query("""
			SELECT d FROM Device d
			WHERE d.lastSeenAt IS NOT NULL
			""")
	List<Device> findAllConnectedDevicesWithElderAndMember();

	@Query("""
			SELECT COUNT(d) > 0
			FROM Device d
			WHERE d.serialNumber = :serialNumber OR d.elder.id = :elderId
			""")
	boolean existsBySerialNumberOrElderId(@Param("serialNumber") String serialNumber, @Param("elderId") Long elderId);
}

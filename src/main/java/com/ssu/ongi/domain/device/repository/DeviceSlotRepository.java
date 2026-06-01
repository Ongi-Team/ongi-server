package com.ssu.ongi.domain.device.repository;

import com.ssu.ongi.domain.device.entity.DeviceSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeviceSlotRepository extends JpaRepository<DeviceSlot, Long> {

    int countByElderId(Long elderId);

    @Modifying(clearAutomatically = true)
    void deleteAllByMedicineId(Long medicineId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM DeviceSlot ds WHERE ds.elder.id = :elderId")
    void deleteAllByElderId(@Param("elderId") Long elderId);

    Optional<DeviceSlot> findByElderIdAndSlotNumber(Long elderId, Integer slotNumber);

    Optional<DeviceSlot> findByDeviceIdAndSlotNumber(Long deviceId, Integer slotNumber);

    @Query("SELECT ds FROM DeviceSlot ds " +
            "JOIN FETCH ds.medicine " +
            "WHERE ds.device.id = :deviceId " +
            "ORDER BY ds.slotNumber ASC")
    List<DeviceSlot> findAllWithMedicineByDeviceId(@Param("deviceId") Long deviceId);

    @Query("SELECT ds FROM DeviceSlot ds " +
            "JOIN FETCH ds.elder e " +
            "JOIN FETCH e.member " +
            "JOIN FETCH ds.medicine " +
            "WHERE ds.device.id = :deviceId AND ds.slotNumber = :slotNumber")
    Optional<DeviceSlot> findByDeviceIdAndSlotNumberWithDetails(
            @Param("deviceId") Long deviceId,
            @Param("slotNumber") Integer slotNumber
    );

    List<DeviceSlot> findAllByElderIdOrderByMedicineScheduledTimeAsc(Long elderId);

    @Query("SELECT ds FROM DeviceSlot ds " +
            "JOIN FETCH ds.medicine " +
            "JOIN FETCH ds.device " +
            "WHERE ds.elder.id = :elderId")
    List<DeviceSlot> findAllWithMedicineByElderId(@Param("elderId") Long elderId);
}

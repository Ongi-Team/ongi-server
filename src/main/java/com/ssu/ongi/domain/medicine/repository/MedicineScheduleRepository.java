package com.ssu.ongi.domain.medicine.repository;

import com.ssu.ongi.domain.medicine.entity.MedicineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MedicineScheduleRepository extends JpaRepository<MedicineSchedule, Long> {

    Optional<MedicineSchedule> findByIdAndMedicine_Elder_Id(Long scheduleId, Long elderId);

    @Query("SELECT CASE WHEN COUNT(ms) > 0 THEN true ELSE false END " +
            "FROM MedicineSchedule ms " +
            "WHERE ms.medicine.elder.id = :elderId AND ms.scheduledTime = :scheduledTime")
    boolean existsByElderIdAndScheduledTime(
            @Param("elderId") Long elderId,
            @Param("scheduledTime") LocalTime scheduledTime);

    @Query("SELECT COALESCE(MAX(ms.dispenserSlot), 0) FROM MedicineSchedule ms " +
            "WHERE ms.medicine.elder.id = :elderId")
    int findMaxDispenserSlotByElderId(@Param("elderId") Long elderId);

    @Query("SELECT ms FROM MedicineSchedule ms " +
            "JOIN FETCH ms.medicine " +
            "WHERE ms.medicine.elder.id = :elderId AND ms.dispenserSlot = :dispenserSlot")
    Optional<MedicineSchedule> findByElderIdAndDispenserSlot(
            @Param("elderId") Long elderId,
            @Param("dispenserSlot") Integer dispenserSlot);

    boolean existsByMedicineId(Long medicineId);
}

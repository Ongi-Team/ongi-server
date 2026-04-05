package com.ssu.ongi.domain.medicine.repository;

import com.ssu.ongi.domain.medicine.entity.MedicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationRecordRepository extends JpaRepository<MedicationRecord, Long> {

    boolean existsByMedicineScheduleIdAndRecordedAt(Long scheduleId, LocalDateTime recordedAt);

    @Query("SELECT mr FROM MedicationRecord mr " +
            "JOIN FETCH mr.medicineSchedule ms " +
            "JOIN FETCH ms.medicine " +
            "WHERE ms.medicine.elder.id = :elderId " +
            "AND mr.recordedAt BETWEEN :start AND :end " +
            "ORDER BY mr.recordedAt ASC")
    List<MedicationRecord> findAllByElderIdAndRecordedAtBetween(
            @Param("elderId") Long elderId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT mr FROM MedicationRecord mr " +
            "JOIN FETCH mr.medicineSchedule ms " +
            "WHERE ms.id = :scheduleId " +
            "ORDER BY mr.recordedAt ASC")
    List<MedicationRecord> findAllByMedicineScheduleId(@Param("scheduleId") Long scheduleId);
}

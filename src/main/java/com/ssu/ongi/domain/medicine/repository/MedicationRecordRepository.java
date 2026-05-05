package com.ssu.ongi.domain.medicine.repository;

import com.ssu.ongi.domain.medicine.entity.MedicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationRecordRepository extends JpaRepository<MedicationRecord, Long> {

    boolean existsByMedicineIdAndRecordedAt(Long medicineId, LocalDateTime recordedAt);

    @Modifying(clearAutomatically = true)
    void deleteAllByMedicineId(Long medicineId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM MedicationRecord mr WHERE mr.medicine.elder.id = :elderId")
    void deleteAllByElderId(@Param("elderId") Long elderId);

    @Query("SELECT mr FROM MedicationRecord mr " +
            "JOIN FETCH mr.medicine m " +
            "WHERE m.elder.id = :elderId " +
            "AND mr.recordedAt BETWEEN :start AND :end " +
            "ORDER BY mr.recordedAt ASC")
    List<MedicationRecord> findAllByElderIdAndRecordedAtBetween(
            @Param("elderId") Long elderId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT mr FROM MedicationRecord mr " +
            "JOIN FETCH mr.medicine " +
            "WHERE mr.medicine.id = :medicineId " +
            "ORDER BY mr.recordedAt ASC")
    List<MedicationRecord> findAllByMedicineId(@Param("medicineId") Long medicineId);
}

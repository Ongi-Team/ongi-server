package com.ssu.ongi.domain.medicine.repository;

import com.ssu.ongi.domain.medicine.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findAllByElderId(Long elderId);

    List<Medicine> findAllByElderIdOrderByScheduledTimeAsc(Long elderId);

    Optional<Medicine> findByIdAndElderId(Long id, Long elderId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Medicine m WHERE m.elder.id = :elderId")
    void deleteAllByElderId(@Param("elderId") Long elderId);
}

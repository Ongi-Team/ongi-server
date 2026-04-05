package com.ssu.ongi.domain.medicine.repository;

import com.ssu.ongi.domain.medicine.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findAllByElderId(Long elderId);

    Optional<Medicine> findByIdAndElderId(Long id, Long elderId);
}

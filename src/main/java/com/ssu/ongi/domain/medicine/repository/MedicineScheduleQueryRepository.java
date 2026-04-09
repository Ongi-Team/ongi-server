package com.ssu.ongi.domain.medicine.repository;

import com.ssu.ongi.domain.medicine.entity.MedicineSchedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicineScheduleQueryRepository extends Repository<MedicineSchedule, Long> {

    @Query("SELECT ms FROM MedicineSchedule ms " +
            "JOIN FETCH ms.medicine " +
            "WHERE ms.medicine.elder.id = :elderId " +
            "ORDER BY ms.scheduledTime ASC")
    List<MedicineSchedule> findAllByElderId(@Param("elderId") Long elderId);
}

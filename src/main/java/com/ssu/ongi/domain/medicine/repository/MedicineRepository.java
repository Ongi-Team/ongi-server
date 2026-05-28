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

    /**
     * 복약 시간의 시·분이 일치하는 약을 어르신·보호자 정보와 함께 조회합니다.
     * 매분 실행되는 복약 알림 스케줄러에서 사용합니다.
     */
    @Query("""
            SELECT m FROM Medicine m
            JOIN FETCH m.elder e
            JOIN FETCH e.member
            WHERE HOUR(m.scheduledTime) = :hour
            AND MINUTE(m.scheduledTime) = :minute
            """)
    List<Medicine> findAllByScheduledHourAndMinuteWithElder(
            @Param("hour") int hour,
            @Param("minute") int minute);
}

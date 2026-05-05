package com.ssu.ongi.domain.medicine.entity;

import com.ssu.ongi.common.base.BaseEntity;
import com.ssu.ongi.domain.elder.entity.Elder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "medicine")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Medicine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elder_id", nullable = false)
    private Elder elder;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalTime scheduledTime;

    @Builder
    private Medicine(Elder elder, String name, LocalTime scheduledTime) {
        this.elder = elder;
        this.name = name;
        this.scheduledTime = scheduledTime;
    }

    public static Medicine create(Elder elder, String name, LocalTime scheduledTime) {
        return Medicine.builder()
                .elder(elder)
                .name(name)
                .scheduledTime(scheduledTime)
                .build();
    }
}

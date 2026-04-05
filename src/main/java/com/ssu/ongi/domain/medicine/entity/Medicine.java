package com.ssu.ongi.domain.medicine.entity;

import com.ssu.ongi.common.base.BaseEntity;
import com.ssu.ongi.domain.elder.entity.Elder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    private Medicine(Elder elder, String name) {
        this.elder = elder;
        this.name = name;
    }

    public static Medicine create(Elder elder, String name) {
        return Medicine.builder()
                .elder(elder)
                .name(name)
                .build();
    }
}

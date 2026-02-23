package com.ssu.ongi.domain.elder.entity;

import com.ssu.ongi.common.base.BaseEntity;
import com.ssu.ongi.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "elder")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Elder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "elder_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column
    private String phone;

    @Column(nullable = false)
    private String relationship;

    @Builder
    public Elder(String name, Integer age, String phone, String relationship) {
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.relationship = relationship;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
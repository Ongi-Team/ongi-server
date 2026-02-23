package com.ssu.ongi.domain.member.entity;

import com.ssu.ongi.common.base.BaseEntity;
import com.ssu.ongi.domain.elder.entity.Elder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Elder> elders = new ArrayList<>();

    @Builder
    public Member(String loginId, String password, String name, String phone) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void addElder(Elder elder) {
        this.elders.add(elder);
        elder.setMember(this);
    }
}
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

    @Column(name = "fcm_token", length = 512, unique = true)
    private String fcmToken;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Elder> elders = new ArrayList<>();

    @Builder
    private Member(String loginId, String password, String name, String phone) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public static Member create(String loginId, String encodedPassword, String name, String phone) {
        return Member.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .name(name)
                .phone(phone)
                .build();
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void deleteFcmToken() {
        this.fcmToken = null;
    }

    public void addElder(Elder elder) {
        this.elders.add(elder);
        elder.assignMember(this);
    }
}

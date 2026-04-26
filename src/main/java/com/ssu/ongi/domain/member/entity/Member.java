package com.ssu.ongi.domain.member.entity;

import com.ssu.ongi.common.base.BaseEntity;
import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.member.enums.OsType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE member_id = ?")
@SQLRestriction("deleted_at IS NULL")
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

    @Column(name = "fcm_token", length = 512)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "os_type")
    private OsType osType;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

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

    public void updateFcmToken(String fcmToken, OsType osType) {
        this.fcmToken = fcmToken;
        this.osType = osType;
    }

    public void deleteFcmToken() {
        this.fcmToken = null;
        this.osType = null;
    }

    public void softDelete() {
        this.loginId = "deleted_" + this.id + "_" + this.loginId;
        this.deletedAt = LocalDateTime.now();
    }

    public void addElder(Elder elder) {
        this.elders.add(elder);
        elder.assignMember(this);
    }
}

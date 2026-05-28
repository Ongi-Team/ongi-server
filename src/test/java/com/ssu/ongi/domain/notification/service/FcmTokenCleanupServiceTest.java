package com.ssu.ongi.domain.notification.service;

import com.ssu.ongi.domain.elder.entity.Elder;
import com.ssu.ongi.domain.elder.repository.ElderRepository;
import com.ssu.ongi.domain.member.entity.Member;
import com.ssu.ongi.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FcmTokenCleanupServiceTest {

    private MemberRepository memberRepository;
    private ElderRepository elderRepository;
    private FcmTokenCleanupService fcmTokenCleanupService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        elderRepository = mock(ElderRepository.class);
        fcmTokenCleanupService = new FcmTokenCleanupService(memberRepository, elderRepository);
    }

    @Test
    void 보호자_토큰이_존재하면_토큰을_삭제한다() {
        Member member = createMember(1L, "fcm-member-token");
        when(memberRepository.findByFcmToken("fcm-member-token")).thenReturn(Optional.of(member));

        fcmTokenCleanupService.deleteByToken("fcm-member-token");

        assertThat(member.getFcmToken()).isNull();
        assertThat(member.getOsType()).isNull();
        // 동일 토큰이 어르신 테이블에도 있을 수 있으므로 항상 두 테이블 모두 조회
        verify(elderRepository).findByFcmToken("fcm-member-token");
    }

    @Test
    void 어르신_토큰이_존재하면_토큰을_삭제한다() {
        Elder elder = createElder(10L, "fcm-elder-token");
        when(memberRepository.findByFcmToken("fcm-elder-token")).thenReturn(Optional.empty());
        when(elderRepository.findByFcmToken("fcm-elder-token")).thenReturn(Optional.of(elder));

        fcmTokenCleanupService.deleteByToken("fcm-elder-token");

        assertThat(elder.getFcmToken()).isNull();
        assertThat(elder.getOsType()).isNull();
    }

    @Test
    void 보호자와_어르신_모두_토큰이_없으면_삭제하지_않는다() {
        when(memberRepository.findByFcmToken("unknown-token")).thenReturn(Optional.empty());
        when(elderRepository.findByFcmToken("unknown-token")).thenReturn(Optional.empty());

        // 예외 없이 정상 종료되어야 함
        fcmTokenCleanupService.deleteByToken("unknown-token");

        verify(memberRepository).findByFcmToken("unknown-token");
        verify(elderRepository).findByFcmToken("unknown-token");
    }

    /** 테스트용 보호자 엔티티에 id와 fcmToken을 설정합니다. */
    private Member createMember(Long id, String fcmToken) {
        Member member = Member.create("guardian", "encoded-pw", "보호자", "01000000000");
        ReflectionTestUtils.setField(member, "id", id);
        ReflectionTestUtils.setField(member, "fcmToken", fcmToken);
        return member;
    }

    /** 테스트용 어르신 엔티티에 id와 fcmToken을 설정합니다. */
    private Elder createElder(Long id, String fcmToken) {
        Elder elder = Elder.create("어르신", 80, "부");
        ReflectionTestUtils.setField(elder, "id", id);
        ReflectionTestUtils.setField(elder, "fcmToken", fcmToken);
        return elder;
    }
}

package com.ssu.ongi.domain.notification.service;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.ssu.ongi.common.fcm.FcmService;
import com.ssu.ongi.domain.medicine.enums.MedicationResult;
import com.ssu.ongi.domain.notification.event.MedicationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationCommandServiceTest {

    private FcmService fcmService;
    private FcmTokenCleanupService fcmTokenCleanupService;
    private NotificationCommandService notificationCommandService;

    @BeforeEach
    void setUp() {
        fcmService = mock(FcmService.class);
        fcmTokenCleanupService = mock(FcmTokenCleanupService.class);
        notificationCommandService = new NotificationCommandService(fcmService, fcmTokenCleanupService);
    }

    @Test
    void 전송_성공_시_토큰_정리를_호출하지_않는다() throws Exception {
        when(fcmService.send(any())).thenReturn("msg-id");

        notificationCommandService.sendMedicationAlert(medicationEvent());

        verify(fcmService, times(1)).send(any());
        verify(fcmTokenCleanupService, never()).deleteByToken(any());
    }

    @Test
    void UNREGISTERED_에러_발생_시_재시도_없이_토큰을_정리한다() throws Exception {
        when(fcmService.send(any())).thenThrow(messagingException(MessagingErrorCode.UNREGISTERED));

        notificationCommandService.sendMedicationAlert(medicationEvent());

        // 재시도 없이 1회만 호출
        verify(fcmService, times(1)).send(any());
        verify(fcmTokenCleanupService).deleteByToken("fcm-token");
    }

    @Test
    void INVALID_ARGUMENT_에러_발생_시_재시도_없이_토큰을_정리한다() throws Exception {
        when(fcmService.send(any())).thenThrow(messagingException(MessagingErrorCode.INVALID_ARGUMENT));

        notificationCommandService.sendMedicationAlert(medicationEvent());

        verify(fcmService, times(1)).send(any());
        verify(fcmTokenCleanupService).deleteByToken("fcm-token");
    }

    @Test
    void 일시_오류_발생_시_재시도_후_성공하면_토큰_정리를_호출하지_않는다() throws Exception {
        when(fcmService.send(any()))
                .thenThrow(messagingException(null))  // 1차: 일시 오류 (errorCode=null)
                .thenReturn("msg-id");                 // 재시도: 성공

        notificationCommandService.sendMedicationAlert(medicationEvent());

        verify(fcmService, times(2)).send(any());
        verify(fcmTokenCleanupService, never()).deleteByToken(any());
    }

    @Test
    void 일시_오류_재시도_후에도_실패하면_토큰_정리를_호출하지_않는다() throws Exception {
        when(fcmService.send(any()))
                .thenThrow(messagingException(null))  // 1차: 일시 오류
                .thenThrow(messagingException(null)); // 재시도: 또 일시 오류

        notificationCommandService.sendMedicationAlert(medicationEvent());

        verify(fcmService, times(2)).send(any());
        verify(fcmTokenCleanupService, never()).deleteByToken(any());
    }

    @Test
    void 재시도_중_무효_토큰_에러_발생_시_토큰을_정리한다() throws Exception {
        when(fcmService.send(any()))
                .thenThrow(messagingException(null))                               // 1차: 일시 오류
                .thenThrow(messagingException(MessagingErrorCode.UNREGISTERED));   // 재시도: 무효 토큰

        notificationCommandService.sendMedicationAlert(medicationEvent());

        verify(fcmService, times(2)).send(any());
        verify(fcmTokenCleanupService).deleteByToken("fcm-token");
    }

    @Test
    void FCM_토큰이_없으면_전송을_시도하지_않는다() throws Exception {
        notificationCommandService.sendMedicationAlert(medicationEventWithNoToken());

        verify(fcmService, never()).send(any());
        verify(fcmTokenCleanupService, never()).deleteByToken(any());
    }

    /** 테스트용 복약 이벤트를 생성합니다. */
    private MedicationEvent medicationEvent() {
        return new MedicationEvent(
                MedicationResult.TAKEN,
                1L, 10L, 100L,
                "fcm-token",
                "혈압약",
                LocalDateTime.now()
        );
    }

    /** FCM 토큰이 없는 복약 이벤트를 생성합니다. */
    private MedicationEvent medicationEventWithNoToken() {
        return new MedicationEvent(
                MedicationResult.TAKEN,
                1L, 10L, 100L,
                null,
                "혈압약",
                LocalDateTime.now()
        );
    }

    /**
     * FirebaseMessagingException은 final 클래스이므로 reflection으로 생성합니다.
     * errorCode가 null이면 일시적 오류(INTERNAL 등)를 시뮬레이션합니다.
     */
    private static FirebaseMessagingException messagingException(MessagingErrorCode errorCode) {
        try {
            Constructor<FirebaseMessagingException> constructor =
                    FirebaseMessagingException.class.getDeclaredConstructor(ErrorCode.class, String.class);
            constructor.setAccessible(true);
            FirebaseMessagingException e = constructor.newInstance(ErrorCode.INTERNAL, "test error");

            Field field = FirebaseMessagingException.class.getDeclaredField("errorCode");
            field.setAccessible(true);
            field.set(e, errorCode);

            return e;
        } catch (Exception ex) {
            throw new RuntimeException("FirebaseMessagingException 생성 실패", ex);
        }
    }
}

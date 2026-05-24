package com.ssu.ongi.common.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class FirebaseFcmService implements FcmService {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public String send(FcmMessage fcmMessage) throws FirebaseMessagingException {
        Message.Builder messageBuilder = Message.builder()
                .setToken(fcmMessage.token())
                .setNotification(Notification.builder()
                        .setTitle(fcmMessage.title())
                        .setBody(fcmMessage.body())
                        .build());

        Map<String, String> data = fcmMessage.data();
        if (data != null && !data.isEmpty()) {
            messageBuilder.putAllData(data);
        }

        return firebaseMessaging.send(messageBuilder.build());
    }
}

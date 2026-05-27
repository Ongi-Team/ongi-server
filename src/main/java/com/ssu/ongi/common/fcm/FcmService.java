package com.ssu.ongi.common.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface FcmService {
    String send(FcmMessage fcmMessage) throws FirebaseMessagingException;
}

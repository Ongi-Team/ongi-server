package com.ssu.ongi.common.mqtt;

public class DeviceTopic {

    private DeviceTopic() {}

    private static final String DEVICE_PREFIX = "device/%s";

    public static String openAll(String deviceToken) {
        return String.format(DEVICE_PREFIX + "/command/open-all", deviceToken);
    }

    public static String closeAll(String deviceToken) {
        return String.format(DEVICE_PREFIX + "/command/close-all", deviceToken);
    }

    public static String scheduleUpdated(String deviceToken) {
        return String.format(DEVICE_PREFIX + "/command/schedule-updated", deviceToken);
    }
}

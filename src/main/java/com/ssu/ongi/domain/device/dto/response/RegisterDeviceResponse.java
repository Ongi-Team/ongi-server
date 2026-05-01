package com.ssu.ongi.domain.device.dto.response;

import com.ssu.ongi.domain.device.entity.Device;

public record RegisterDeviceResponse(
        String deviceToken
) {
    public static RegisterDeviceResponse from(Device device) {
        return new RegisterDeviceResponse(device.getDeviceToken());
    }
}

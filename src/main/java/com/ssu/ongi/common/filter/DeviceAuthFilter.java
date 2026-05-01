package com.ssu.ongi.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssu.ongi.common.device.DeviceTokenValidator;
import com.ssu.ongi.common.response.ApiResponse;
import com.ssu.ongi.common.status.ErrorStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DeviceAuthFilter extends OncePerRequestFilter {

    private final DeviceTokenValidator deviceTokenValidator;
    private final ObjectMapper objectMapper;

    private static final String DEVICE_TOKEN_HEADER = "Device-Token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String deviceToken = request.getHeader(DEVICE_TOKEN_HEADER);

        if (!StringUtils.hasText(deviceToken)) {
            sendErrorResponse(response, ErrorStatus.UNAUTHORIZED);
            return;
        }

        deviceTokenValidator.validate(deviceToken)
                .ifPresentOrElse(
                        device -> request.setAttribute("deviceId", device.getId()),
                        () -> sendErrorResponseUnchecked(response, ErrorStatus.DEVICE_NOT_FOUND)
                );

        if (response.isCommitted()) {
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/device/");
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorStatus errorStatus) throws IOException {
        response.setStatus(errorStatus.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> body = new ApiResponse<>(
                false,
                errorStatus.getCode(),
                errorStatus.getMessage(),
                null
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private void sendErrorResponseUnchecked(HttpServletResponse response, ErrorStatus errorStatus) {
        try {
            sendErrorResponse(response, errorStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.ssu.ongi.domain.member.dto.request;

import com.ssu.ongi.domain.elder.dto.request.ElderRequest;
import com.ssu.ongi.domain.member.entity.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(
        @NotBlank(message = "아이디를 입력해주세요.")
        String loginId,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])|(?=.*[a-zA-Z])(?=.*[!@#$%^&*])|(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$",
                message = "비밀번호는 8자 이상, 영문/숫자/특수문자 중 2가지 이상 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "이름을 입력해주세요.")
        String name,

        @NotBlank(message = "전화번호를 입력해주세요.")
        String phone,

        @Valid
        @NotNull(message = "어르신 정보를 입력해주세요.")
        ElderRequest elder
) {
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .name(name)
                .phone(phone)
                .build();
    }
}
package com.ssu.ongi.domain.elder.dto.response;

import com.ssu.ongi.domain.elder.entity.Elder;

public record ElderResponse(
        Long elderId,
        String name,
        Integer age,
        String relationship
) {
    public static ElderResponse from(Elder elder) {
        return new ElderResponse(
                elder.getId(),
                elder.getName(),
                elder.getAge(),
                elder.getRelationship()
        );
    }
}
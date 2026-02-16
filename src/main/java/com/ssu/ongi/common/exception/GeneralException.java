package com.ssu.ongi.common.exception;

import lombok.Getter;
import com.ssu.ongi.common.base.BaseStatus;

@Getter
public class GeneralException extends RuntimeException {
    private final BaseStatus errorStatus;

    public GeneralException(
            BaseStatus errorStatus
    ) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

}

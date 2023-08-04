package com.mobile.mobilebackend.exception;

import com.mobile.mobilebackend.common.BaseResponse;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.common.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.info("RunTimeException:"+e.getMessage(), e);
        return ResultUtil.error(e.getCode(),e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runTimeExceptionHandler(RuntimeException e){
        log.info("RunTimeException:", e);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR,"猜猜是什么错~", "系统内部出现了问题喵~");
    }
}

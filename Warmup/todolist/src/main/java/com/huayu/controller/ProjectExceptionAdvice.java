package com.huayu.controller;

import com.huayu.dto.Result;
import com.huayu.exception.BusinessException;
import com.huayu.exception.SystemException;
import com.huayu.utils.Code;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 异常处理器对象
 */
@RestControllerAdvice
public class ProjectExceptionAdvice {

    /**
     * 处理系统异常的方法
     *
     * @param ex 出现的异常
     */
    @ExceptionHandler(SystemException.class)
    public Result doSystemException(SystemException ex) {

        return new Result(ex.getCode(), null, ex.getMessage());
    }

    /**
     * 处理用户造成的业务异常的方法
     *
     * @param ex 出现的异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result doBusinessException(BusinessException ex) {

        return new Result(Code.BUSINESS_ERR, null, ex.getMessage());
    }

    /**
     * 处理未预料到的异常的方法
     *
     * @param ex 出现的异常
     */
    @ExceptionHandler(Exception.class)
    public Result doException(Exception ex) {
        return new Result(Code.SYSTEM_UNKNOW_ERR, null, "系统繁忙请稍后再试");
    }
}

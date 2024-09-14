package com.discord.user.handler;

import com.discord.common.common.R;
import com.discord.user.utils.Constant;
import com.discord.common.exception.APIException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;


/**
 * @author xck
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public R<String> handleBindException(IOException ex) {
        ex.printStackTrace();
        return R.error(Constant.FILE_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public R<String> handleBindException(Exception ex) {
        ex.printStackTrace();
        return R.error("操作失败");
    }

    @ExceptionHandler(APIException.class)
    public R<String> handleBindException(APIException ex) {
        // TODO 日志
        return R.error(ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public R<String> handleBindException(BindException ex) {
        ObjectError objectError = ex.getBindingResult().getAllErrors().get(0);
        // TODO 日志
        return R.error(objectError.getDefaultMessage());
    }

}

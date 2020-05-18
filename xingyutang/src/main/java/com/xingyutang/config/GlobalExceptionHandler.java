package com.xingyutang.config;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.exception.GlobalResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public ResponseData exceptionHandler(HttpServletRequest request, Exception e) {
        logger.error("exception caught on rest call", e);

        if (e instanceof GlobalResponseException) {
            GlobalResponseException ex = (GlobalResponseException)e;
            return ResponseData.error(ex.getCode(), ex.getMessage());
        } else {
            String errorMessage = e.getMessage();
            if (e instanceof MaxUploadSizeExceededException) {
                errorMessage = "文件太大，无法上传";
            }
            return ResponseData.error(1, errorMessage);
        }
    }
}

package com.patient.demo.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.patient.demo.entity.ApiEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiExceptionAdvice {
    
    @ExceptionHandler({NumberFormatException.class})
    public ResponseEntity<ApiEntity> exceptionHandler(HttpServletRequest request, final NumberFormatException e) {
        
        log.error("NumberFormatException : {} | {}", e);
        
        return ResponseEntity
                .status(ExceptionEnum.RUNTIME_EXCEPTION.getStatus())
                .body(ApiEntity.builder()
                        .Code(ExceptionEnum.RUNTIME_EXCEPTION.getCode())
                        .Message(e.getMessage())
                        .build());
    }
    
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ApiEntity> exceptionHandler(HttpServletRequest request, final RuntimeException e) {
        
        log.error("RuntimeException : {}", e.toString());
        
        return ResponseEntity
                .status(ExceptionEnum.RUNTIME_EXCEPTION.getStatus())
                .body(ApiEntity.builder()
                        .Code(ExceptionEnum.RUNTIME_EXCEPTION.getCode())
                        .Message(e.getMessage())
                        .build());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiEntity> exceptionHandler(HttpServletRequest request, final Exception e) {
        
        log.error("Exception : {}", e.toString());
        
        return ResponseEntity
                .status(ExceptionEnum.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiEntity.builder()
                        .Code(ExceptionEnum.INTERNAL_SERVER_ERROR.getCode())
                        .Message(e.getMessage())
                        .build());
    }
}

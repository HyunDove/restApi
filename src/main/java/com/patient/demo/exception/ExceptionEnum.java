package com.patient.demo.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ExceptionEnum {
	
	// servet Exception 
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR" , "서버 오류가 발생했습니다."),

    // customer Exception [ COMMON ]
    NO_DATA(HttpStatus.BAD_REQUEST, "C0001", "조회 할 데이터가 없습니다."),
    NO_Parameter(HttpStatus.BAD_REQUEST, "C0002", "Parameter를 입력해주세요."),
    
    // customer Exception [ SEARCH ]
    ALREADY_SEARCH(HttpStatus.BAD_REQUEST, "E0003", "이미 등록된 환자입니다."),

    // customer Exception [ FILE ]
	ALREADY_FILE(HttpStatus.BAD_REQUEST, "I0001", "해당 데이터에 이미지가 등록되어있습니다."),
	CHECK_FILE(HttpStatus.BAD_REQUEST, "I0002", "이미지 파일은 jpg, png만 가능합니다."),
	EMPTY_FILE(HttpStatus.BAD_REQUEST, "I0003", "이미지 파일이 비어있습니다."),
	
    // customer Exception [ DELETE ]
	ALREARY_DELETE(HttpStatus.BAD_REQUEST, "D0001", "이미 삭제된 환자입니다.");

    private final HttpStatus status;
    private final String code;
    private String message;

    ExceptionEnum(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    ExceptionEnum(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

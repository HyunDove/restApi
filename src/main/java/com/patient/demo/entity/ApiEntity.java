package com.patient.demo.entity;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiEntity {
    private String Code;
    private String Message;
    private List<SearchSummary> result;
    
    @Builder
    public ApiEntity(HttpStatus status, String Code, String Message, List<SearchSummary> result) {
        this.Message = Message;
        this.Code = Code;
        this.result = result;
    }
     
}

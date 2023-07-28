package com.patient.demo.entity;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.patient.demo.entity.PatientEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
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

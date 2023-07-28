package com.patient.demo.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateTimeConverter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate // 변경한 필드만 대응
@Entity(name = "patient_info")
@EntityListeners(AuditingEntityListener.class)
public class PatientEntity {
    
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(hidden = true)
    private int seq;            
    
    @Id
    @ApiModelProperty(position = 1 ,example = "홍길동", required = true)
    private String name;        
    @ApiModelProperty(position = 2 ,example = "24", required = true)
    private int age;            
    @ApiModelProperty(position = 3 ,example = "남자", required = true)
    private String gender;        
    @ApiModelProperty(position = 4 ,example = "유", required = true)
    private String disease;        
    
    @ApiModelProperty(hidden = true)
    @Column(columnDefinition = "미삭제")
    private String delete_flag;    // 삭제여부
    
    @CreatedDate
    @Column(updatable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    @ApiModelProperty(hidden = true)
    private LocalDateTime create_date;    // 생성일자
    
    @ApiModelProperty(hidden = true)
    private LocalDateTime delete_date;    // 삭제일자
    
}

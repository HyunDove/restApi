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
	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private int seq;			// 고유번호
    
    @Id
    @ApiModelProperty(position = 1 ,example = "가나다라", required = true)
	private String name;		// 이름
    @ApiModelProperty(position = 2 ,example = "24", required = true)
	private int age;			// 나이
    @ApiModelProperty(position = 3 ,example = "남", required = true)
    private String gender;		// 성별
    @ApiModelProperty(position = 4 ,example = "유", required = true)
    private String disease;		// 질병여부
    
    @ApiModelProperty(hidden = true)
    @Column(columnDefinition = "미삭제")
    private String delete_flag;	// 삭제여부
    
    @CreatedDate
    @Column(updatable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    @ApiModelProperty(hidden = true)
    private LocalDateTime create_date;	// 생성일자
    @ApiModelProperty(hidden = true)
    private LocalDateTime delete_date;	// 삭제일자
    
}

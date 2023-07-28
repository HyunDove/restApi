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
    private int seq;			// 고유번호
    
    @Id
	private String name;		// 이름
	private int age;			// 나이
    private String gender;		// 성별
    private String disease;		// 질병여부
    @Column(columnDefinition = "미삭제")
    private String delete_flag;	// 삭제여부
    
    @CreatedDate
    @Column(updatable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime create_date;	// 생성일자
    private LocalDateTime delete_date;	// 삭제일자
    
}

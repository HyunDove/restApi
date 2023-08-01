package com.patient.demo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate // 변경한 필드만 대응
@Entity(name = "patient_info")
public class PatientEntity extends baseEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE) // DB 시퀀스를 사용해서 기본키 할당
    @ApiModelProperty(hidden = true)
    private int seq;

    @NotEmpty
    @Id // 특성 속성을 기본키로 설정
    @ApiModelProperty(position = 1 ,example = "홍길동", required = true)
    private String name;

    @NotNull
    @ApiModelProperty(position = 2 ,example = "24", required = true)
    private int age;

    @NotEmpty
    @ApiModelProperty(position = 3 ,example = "남자", required = true)
    private String gender;

    @NotEmpty
    @ApiModelProperty(position = 4 ,example = "유", required = true)
    private String disease;

    @ApiModelProperty(hidden = true)
    @Column(columnDefinition = "미삭제")
    private String delete_flag;    // 삭제 여부

}

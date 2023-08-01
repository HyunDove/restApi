package com.patient.demo.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "patient_images")
@Getter
@Setter
@Builder
@DynamicUpdate // 변경한 필드만 대응
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageEntity extends baseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String type;
    private Long size;
    private String path;

    @Id
    private int patient_seq;

    @Builder.Default
    private String delete_flag = "미삭제";
    
}

package com.patient.demo.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
// Common Entity
public class BaseEntity {

    @ApiModelProperty(hidden = true)
    @Column(name = "delete_flag" ,columnDefinition = "미삭제")
    private String deleteFlag;    // 삭제 여부

    @ApiModelProperty(hidden = true)
    @Column(name = "create_date",updatable = false)
    private LocalDateTime createDate;

    @ApiModelProperty(hidden = true)
    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @PrePersist
    public void prePersist(){
        this.createDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.deleteDate = LocalDateTime.now();
    }

}

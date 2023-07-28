package com.patient.demo.specification;

import org.springframework.data.jpa.domain.Specification;

import com.patient.demo.entity.PatientEntity;

final public class PatientSpecification {
    
    private PatientSpecification() {}
    
    public static Specification<PatientEntity> equalsSeq(int seq){
        return (root,query,CriteriaBuilder) -> CriteriaBuilder.equal(root.get("seq"), seq);
    }
    
    public static Specification<PatientEntity> equalsDelete_flag(String delete_flag){
        return (root,query,CriteriaBuilder) -> CriteriaBuilder.equal(root.get("delete_flag"), delete_flag);
    }
    
}

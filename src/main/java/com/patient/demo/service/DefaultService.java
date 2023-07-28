package com.patient.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.patient.demo.entity.ImageEntity;
import com.patient.demo.entity.PatientEntity;
import com.patient.demo.entity.SearchSummary;
import com.patient.demo.exception.ApiException;
import com.patient.demo.exception.ExceptionEnum;
import com.patient.demo.repo.ImageRepository;
import com.patient.demo.repo.PatientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@PropertySource("classpath:system.properties")
public class DefaultService {
    
    private final PatientRepository patientRepository;
    private final ImageRepository ImageRepository;
    private final EntityManagerFactory entityManagerFactory;
    
    @Value("${local.path}")
    private String folderPath;
    
    public List<PatientEntity> patientOriginalList(String name){
        return patientRepository.findByName(name);
    }
    
    public List<SearchSummary> patientList(){
        return patientRepository.findAllList();
    }
    
    public List<SearchSummary> patientList(String name){
    /*
     * 2023-07-27 native query를 위하여 주석처리
     * Specification<PatientEntity> spec = (root,query,criteriaBuilder) -> null;
     * spec = spec.and(PatientSpecification.equalsDelete_flag("미삭제")); spec =
     * spec.and(PatientSpecification.equalsSeq(seq.get()));
     */
        
        return patientRepository.findSearchName(name);
    }
    
    public ImageEntity ImagetList(String name){
        return ImageRepository.findByName(name);
    }
    
    public PatientEntity patientInsert(PatientEntity patientEntity){
        return patientRepository.save(patientEntity);
    }
    
    public ImageEntity uploadImage(HashMap<String, Object> params) throws IllegalStateException, IOException {
        
        ImageEntity imageEntity = ImageRepository.save(
                ImageEntity.builder()
                           .name(params.get("name").toString())
                           .type(params.get("type").toString())
                           .size(Long.parseLong(params.get("size").toString()))
                           .patient_seq(Integer.parseInt(params.get("patient_seq").toString()))
                           .path(params.get("path").toString())
                           .build());
        
        if(imageEntity == null) throw new ApiException(ExceptionEnum.RUNTIME_EXCEPTION);
        
        return imageEntity;
    }

    public byte[] downloadImageFromFileSystem(String name) throws IOException {
        
        ImageEntity imageHashMap = ImageRepository.findByName(name);
        
        if(imageHashMap == null) throw new ApiException(ExceptionEnum.NO_DATA);
        
        String fileFullPath = folderPath + File.separator + name + imageHashMap.getType();
         
        return Files.readAllBytes(new File(fileFullPath).toPath());
    }
    
    /* Dirty Checking
     * 설정한 값만 수정하게 구현 
     */
    @Transactional
    public void patientDelete(String name, int seq){
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        tx.begin();
        
        // info 수정
        PatientEntity info_ent = em.find(PatientEntity.class, name);
        info_ent.setDelete_flag("삭제");
        info_ent.setDelete_date(LocalDateTime.now());
        
        // image 수정        
        ImageEntity file_ent = em.find(ImageEntity.class, seq);
        file_ent.setDelete_flag("삭제");
        file_ent.setDelete_date(LocalDateTime.now());
        
        tx.commit();
    }
    
}

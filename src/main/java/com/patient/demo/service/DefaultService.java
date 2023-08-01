package com.patient.demo.service;

import com.patient.demo.entity.ImageEntity;
import com.patient.demo.entity.PatientEntity;
import com.patient.demo.entity.SearchSummary;
import com.patient.demo.exception.ApiException;
import com.patient.demo.exception.ExceptionEnum;
import com.patient.demo.repo.ImageRepository;
import com.patient.demo.repo.PatientRepository;
import com.patient.demo.util.Common;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:system.properties")
@Transactional
public class DefaultService {
    
    private final PatientRepository patientRepository;
    private final ImageRepository ImageRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final Common common;
    
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
    
    public PatientEntity patientInsert(PatientEntity patientEntity ){
        
        patientEntity = PatientEntity.builder().
                                      name(patientEntity.getName()).
                                      age(Integer.valueOf(patientEntity.getAge())).
                                      gender(patientEntity.getGender()).
                                      disease(patientEntity.getDisease()).
                                      build();
        
        return patientRepository.save(patientEntity);
    }
    
    public ImageEntity uploadImage(MultipartFile image, List<PatientEntity> patientEntity, String name) throws IllegalStateException {
        
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                            .path("api/v1/patient/image/")
                                                            .path(name)
                                                            .toUriString();
        
        String contentType = common.getFileType(image.getContentType());
        
        ImageEntity imageEntity = ImageRepository.save(
                                  ImageEntity.builder()
                                             .name(image.getOriginalFilename())
                                             .type(contentType)
                                             .size(image.getSize())
                                             .patient_seq(patientEntity.get(0).getSeq())
                                             .path(fileDownloadUri)
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

    public void patientDelete(String name, int seq){
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        tx.begin();

        /*
        * Repository 영역에서 처리 할 수 있게 처리 예정
        */

        // info 수정
        PatientEntity info_ent = em.find(PatientEntity.class, name);
        info_ent.setDeleteFlag("삭제");

        // image 수정
        ImageEntity file_ent = em.find(ImageEntity.class, seq);
        file_ent.setDeleteFlag("삭제");

        tx.commit();

    }
    
}

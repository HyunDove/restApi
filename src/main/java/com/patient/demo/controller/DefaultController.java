package com.patient.demo.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.patient.demo.entity.ApiEntity;
import com.patient.demo.entity.ImageEntity;
import com.patient.demo.entity.PatientEntity;
import com.patient.demo.entity.SearchSummary;
import com.patient.demo.exception.ApiException;
import com.patient.demo.exception.ExceptionEnum;
import com.patient.demo.service.DefaultService;
import com.patient.demo.util.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:system.properties")
@RequestMapping(value = "/api/v1")
@Api(tags = {"환자 CRD를 위한 Controller"},description = " ")
public class DefaultController {
    
    private final DefaultService defaultService;
    private final Common common;
    
    // 파일이 저장 될  경로
    @Value("${local.path}")
    private String folderPath;
    
    @ApiOperation(value = "환자 목록 조회", notes = ""
            + "name 값을 입력하지않으면 모든 환자 조회 \n "
            + "name 값을 입력하면 해당 환자 조회한다. \n"
            + "※ 단 이미지까지 업로드 한 환자만 조회된다.")
    @ApiImplicitParam(name="name", value="이름", dataType="String")
    @GetMapping(value = {"/patient"})
        public ResponseEntity<ApiEntity> patient_search(@RequestParam(required = false) String name) {
         
        List<SearchSummary> patientList;
        
        /* 
         * 데이터가 없을 시 전체조회, 데이터가 있으면 해당 parameter만 조회
         */
        
        if(StringUtils.hasText(name)) patientList = defaultService.patientList(name); 
        else patientList = defaultService.patientList(); 
        
        log.info("get patient | param => name : {}", name);
        
        if(patientList.isEmpty()) {
            log.error("get patient | param NO_DATA Exception");
            throw new ApiException(ExceptionEnum.NO_DATA);
        }else{
        
            log.info("get patient | search success");
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(ApiEntity.builder()
                                 .Code("success")
                                 .Message("정상적으로 조회되었습니다.")
                                 .result(patientList)
                                 .build());
        }
    }
    
    
    @ApiOperation(value = "환자 등록", notes = ""
            + "환자 정보를 등록한다.\n"
            + "※ 단 이미지까지 업로드 한 환자만 조회된다.")
    @PostMapping("/patient")
    public ResponseEntity<ApiEntity> patient_insert(@Valid @RequestBody(required = true) PatientEntity patientEntity) {
        
        log.info("post patient | params => : {}", patientEntity.toString());
        
        if(!defaultService.patientOriginalList(patientEntity.getName()).isEmpty()) {
            log.error("post patient | insert => ALREADY_SEARCH Exception");
            throw new ApiException(ExceptionEnum.ALREADY_SEARCH); 
        }
        
        if(defaultService.patientInsert(patientEntity) == null) {
            log.error("post patient | RUNTIME EXCEPTION");
            throw new ApiException(ExceptionEnum.RUNTIME_EXCEPTION);
        }
        
        log.info("post patient | success");
        return ResponseEntity.status(HttpStatus.OK)
                             .body(ApiEntity.builder()
                             .Code("success")
                             .Message("[" + patientEntity.getName() + "] 님이 정상적으로 등록되었습니다. 이미지를 업로드해야 조회됩니다.")
                             .build());
    }
    
    @ApiOperation(value = "환자 파일 업로드", notes = "해당 환자 데이터에 이미지 파일을 업로드한다. ( jpg / png 파일만 가능 ) ")
    @ApiImplicitParam(name = "name", value = "이름", dataType = "String")
    @PostMapping(value = "/patient/image")
    public ResponseEntity<ApiEntity> uploadImage(@RequestPart(name = "image", required = true) MultipartFile image, @RequestParam(name = "name", required = true) Optional<String> name) throws IllegalStateException, IOException {
        
        if(!name.isPresent()) {
            log.error("post patient_image | NO_Parameter EXCEPTION");
            throw new ApiException(ExceptionEnum.NO_Parameter); 
        }
        
        log.info("post patient_image | param => name : {}", image.getContentType() ,name);
        
        List<PatientEntity> patientEntity = defaultService.patientOriginalList(name.get());
        
        if(defaultService.ImagetList(name.get()) != null) {
            log.error("post patient_image | ALREADY_FILE EXCEPTION");
            throw new ApiException(ExceptionEnum.ALREADY_FILE);
        }
        
        if(patientEntity.isEmpty()) {
            log.error("post patient_image | NO_DATA EXCEPTION");
            throw new ApiException(ExceptionEnum.NO_DATA);
        }
        
        File folder = new File(folderPath);
        String contentType = common.getFileType(image.getContentType());
        
        if(!folder.isDirectory()) {
            folder.mkdirs();
        }
        
        if (image.isEmpty()) {
            log.error("post patient_image | EMPTY_FILE EXCEPTION");
            throw new ApiException(ExceptionEnum.EMPTY_FILE);
        } else {
            
            if (ObjectUtils.isEmpty(contentType)) {
                log.error("post patient_image | CHECK_FILE EXCEPTION");
                throw new ApiException(ExceptionEnum.CHECK_FILE); 
            }
            
            String fullFilePath = folderPath + File.separator + name.get() + contentType;
            File image_file = new File(fullFilePath);
            
            image.transferTo(image_file);
            
        }
        
        HashMap<String, Object> params = new HashMap<String, Object>();
        
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                            .path("api/v1/patient/image/")
                                                            .path(name.get()+"")
                                                            .toUriString();
        
        params.put("name", image.getOriginalFilename());
        params.put("type", contentType);
        params.put("size", image.getSize());
        params.put("patient_seq", patientEntity.get(0).getSeq());
        params.put("path", fileDownloadUri);
        
        if(defaultService.uploadImage(params) == null) {
            log.error("post patient_image | RUNTIME EXCEPTION");
            throw new ApiException(ExceptionEnum.RUNTIME_EXCEPTION);
        }
        
        log.info("post patient_image | success");
        return ResponseEntity.status(HttpStatus.OK)
                             .body(ApiEntity.builder()
                             .Code("success")
                             .Message("정상적으로 업로드 되었습니다.")
                             .build());
    }

    @GetMapping(value = {"/patient/{name}/image"})
    @ApiOperation(value = "환자 파일 가져오기", notes = "해당 환자 데이터에 등록된 이미지파일을 가져온다.")
    @ApiImplicitParam(name="name", value="이름", dataType="String")
        public ResponseEntity<?> downloadImageToFileSystem(@RequestParam(required = true) Optional<String> name) throws IOException {
        
        if(!name.isPresent()) {
            log.info("get patient_image | NO_Parameter");
            throw new ApiException(ExceptionEnum.NO_Parameter);
        }
        
        log.info("get patient_image | param => name : {}", name);
        
        byte[] downloadImage = defaultService.downloadImageFromFileSystem(name.get());
        
        log.info("get patient_image | success");
        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.valueOf("image/png"))
                             .body(downloadImage);
    }
    
    @DeleteMapping(value = {"/patient"})
    @ApiOperation(value = "환자 데이터 삭제", notes = "해당 환자 데이터에 등록된 데이터를 삭제한다.")
    @ApiImplicitParam(name="name", value="이름", dataType="String")
        public ResponseEntity<ApiEntity> patient_delete(@RequestParam(required = true) Optional<String> name) {
        
        if(!name.isPresent()) {
            log.info("delete patient | NO_Parameter");
            throw new ApiException(ExceptionEnum.NO_Parameter);
        }
        
        log.info("delete patient | param => name : {}", name);
        
        ImageEntity patientEntity = defaultService.ImagetList(name.get());
        
        if(patientEntity == null) {
            log.info("delete patient | NO_DATA");
            throw new ApiException(ExceptionEnum.NO_DATA);
        }else {
            File deleteFile = new File(folderPath + File.separator + name.get() + patientEntity.getType());
            
            if(deleteFile.exists()) {
                deleteFile.delete(); 
                log.info("delete patient | file delete success");
            } else {
                log.info("delete patient | file not exists");
            }
            
            defaultService.patientDelete(name.get(),patientEntity.getPatient_seq());
        }
        
        log.info("delete patient | success");
        return ResponseEntity.status(HttpStatus.OK)
                             .body(ApiEntity.builder()
                             .Code("success")
                             .Message("[ " + name.get() + " ] 님의 데이터가 정상적으로 삭제되었습니다.")
                             .build());
    }
}

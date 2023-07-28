package com.patient.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:system.properties")
@RequestMapping(value = "/api/v1")
public class DefaultController {
	
	private final DefaultService patientService;
	private final Common common;
	
	@Value("${local.path}")
	private String folderPath;
	
	@GetMapping(value = {"/patient","/patient/{name}"})
		public ResponseEntity<ApiEntity> patient_search(@PathVariable(required = false) Optional<String> name) {
		
		List<SearchSummary> patientList;
		
		if(name.isPresent()) patientList = patientService.patientList(name.get());
		else patientList = patientService.patientList();
		
		log.info("patientList : {}", patientList.toString());
		
		if(patientList.isEmpty()) {
			throw new ApiException(ExceptionEnum.NO_DATA);
		}else{
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(ApiEntity.builder()
							.Code("success")
							.Message("정상적으로 조회되었습니다.")
							.result(patientList)
							.build());
		}
	}
	
	@PostMapping("/patient")
		public ResponseEntity<ApiEntity> patient_insert(@RequestBody HashMap<String, String> params) {
		
		if( params.isEmpty() || 
			params.get("name") == null 	  || "".equals(params.get("name")) ||
			params.get("age") == null 	  || "".equals(params.get("age")) || 
			params.get("gender") == null  || "".equals(params.get("gender")) || 
			params.get("disease") == null || "".equals(params.get("disease")) 
		) throw new ApiException(ExceptionEnum.NO_Parameter);
		
		if(!patientService.patientList(params.get("name")).isEmpty()) throw new ApiException(ExceptionEnum.ALREADY_SEARCH); 
		
		PatientEntity patientEntity = PatientEntity.builder().
								name(params.get("name")).
								age(Integer.parseInt(params.get("age"))).
								gender(params.get("gender")).
								disease(params.get("disease")).
								delete_flag("미삭제").
								build();
		
		if(patientService.patientInsert(patientEntity) == null) throw new ApiException(ExceptionEnum.RUNTIME_EXCEPTION);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiEntity.builder()
						.Code("success")
						.Message("[" + params.get("name") + "] 님이 정상적으로 등록되었습니다.")
						.build());
	}
	
	// 업로드
    @PostMapping(value = "/patient/image")
    public ResponseEntity<ApiEntity> uploadImage(@RequestParam("image") MultipartFile multipartFile, @RequestParam("name") Optional<String> name) throws IllegalStateException, IOException {
    	
    	List<PatientEntity> patientEntity = patientService.patientOriginalList(name.get());
    	
    	if(!name.isPresent()) throw new ApiException(ExceptionEnum.NO_Parameter); 
    	if(patientService.ImagetList(name.get()) != null) throw new ApiException(ExceptionEnum.ALREADY_FILE);
    	if(patientEntity.isEmpty()) throw new ApiException(ExceptionEnum.NO_DATA);
    	
    	
    	/* resources 생성 시 path를 미리 설정해두면, folder이 없을 때 오류 발생.
    	 * 상대경로로 지정 시 FileNotFound 오류 발생
    	 * => local일때만 직접경로로 지정.
    	 */
    	
    	File folder = new File(folderPath);
    	String contentType = common.getFileType(multipartFile.getContentType());
    	
		if(!folder.isDirectory()) {
            folder.mkdirs();
        }
        
        if (!multipartFile.isEmpty()) {
			
			if (ObjectUtils.isEmpty(contentType)) throw new ApiException(ExceptionEnum.CHECK_FILE); 
			
			String fullFilePath = folderPath + File.separator + name.get() + contentType;
			
			File conv = new File(fullFilePath);
			
			multipartFile.transferTo(conv);
		} else {
			throw new ApiException(ExceptionEnum.EMPTY_FILE);
		}
        
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api/v1/patient/image/")
                .path(name.get()+"")
                .toUriString();
		
		params.put("name", multipartFile.getOriginalFilename());
		params.put("type", contentType);
		params.put("size", multipartFile.getSize());
		params.put("patient_seq", patientEntity.get(0).getSeq());
		params.put("path", fileDownloadUri);
		
        if(patientService.uploadImage(params) == null) throw new ApiException(ExceptionEnum.RUNTIME_EXCEPTION);
        
        return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiEntity.builder()
						.Code("success")
						.Message("정상적으로 업로드 되었습니다.")
						.build());
    }

    @GetMapping(value = {"/patient/image","/patient/image/{name}"})
    public ResponseEntity<?> downloadImageToFileSystem(@PathVariable(required = false) Optional<String> name) throws IOException {
    	
    	if(!name.isPresent()) throw new ApiException(ExceptionEnum.NO_Parameter);
    	
        byte[] downloadImage = patientService.downloadImageFromFileSystem(name.get());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(downloadImage);
    }
    
	@DeleteMapping(value = {"/patient","/patient/{name}"})
		public ResponseEntity<ApiEntity> patient_delete(@PathVariable(required = false) Optional<String> name) {
		
		if(!name.isPresent()) throw new ApiException(ExceptionEnum.NO_Parameter);
		
		ImageEntity patientEntity = patientService.ImagetList(name.get());
		
		if(patientEntity == null)
			throw new ApiException(ExceptionEnum.NO_DATA);
			// TODO 이미지 파일 및 DB도 삭제해야함.
		else {
			File deleteFile = new File(folderPath + File.separator + name.get() + patientEntity.getType());
			
			log.info("path : {}", folderPath + File.separator + name.get() + patientEntity.getType());
			
			if(deleteFile.exists()) {
	            deleteFile.delete(); 
	            log.info("파일을 삭제하였습니다.");
	        } else {
	        	log.info("파일이 존재하지 않습니다.");
	        }
			
			patientService.patientDelete(name.get(),patientEntity.getPatient_seq());
		}
		
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiEntity.builder()
						.Code("success")
						.Message("[ " + name.get() + " ] 님의 데이터가 정상적으로 삭제되었습니다.")
						.build());
		
	}
	
}

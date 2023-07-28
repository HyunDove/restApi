package com.patient.demo.controller;

import java.io.File;
import java.io.IOException;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
	
	@Value("${local.path}")
	private String folderPath;
	
	@ApiOperation(value = "환자 목록 조회", notes = ""
			+ "@RequestParam \n "
			+ "name 값을 입력하지않으면 모든 환자 조회 \n "
			+ "name 값을 입력하면 해당 환자 조회한다. \n"
			+ "※ 단 이미지까지 업로드 한 환자만 조회된다.")
	@ApiImplicitParam(name="name", value="이름", dataType="String")
	@GetMapping(value = {"/patient/{name}"})
//		public ResponseEntity<ApiEntity> patient_search(@PathVariable(required = false) Optional<String> name) {
		public ResponseEntity<ApiEntity> patient_search(@RequestParam(required = false) String name) {
		
		log.info("patient_search name : {}", name);
		
		List<SearchSummary> patientList;
		
		if("".equals(name) || name == null) patientList = defaultService.patientList(); 
		else patientList = defaultService.patientList(name);
		
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
	
	@ApiOperation(value = "환자 등록", notes = ""
			+ "환자 정보를 등록한다.\n"
			+ "※ 단 이미지까지 업로드 한 환자만 조회된다.")
	@PostMapping("/patient")
//		public ResponseEntity<ApiEntity> patient_insert(@RequestBody HashMap<String, String> params) {
	public ResponseEntity<ApiEntity> patient_insert(@RequestBody PatientEntity params) {
		
		if( params == null || 
				params.getName().isEmpty() ||
				Integer.valueOf(params.getAge()) == null ||
				params.getGender().isEmpty() ||
				params.getDisease().isEmpty()
			) throw new ApiException(ExceptionEnum.NO_Parameter);
			
			if(!defaultService.patientList(params.getName()).isEmpty()) throw new ApiException(ExceptionEnum.ALREADY_SEARCH); 
			
			PatientEntity patientEntity = PatientEntity.builder().
									name(params.getName()).
									age(Integer.valueOf(params.getAge())).
									gender(params.getName()).
									disease(params.getName()).
									delete_flag("미삭제").
									build();
		
		if(defaultService.patientInsert(patientEntity) == null) throw new ApiException(ExceptionEnum.RUNTIME_EXCEPTION);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiEntity.builder()
						.Code("success")
						.Message("[" + params.getName() + "] 님이 정상적으로 등록되었습니다. \n 이미지를 업로드해야 조회됩니다.")
						.build());
	}
	
	// 업로드
	@ApiOperation(value = "환자 파일 업로드", notes = "해당 환자 데이터에 이미지 파일을 업로드한다. ( jpg / png 파일만 가능 ) ")
	@ApiImplicitParams({
		@ApiImplicitParam(name="image", value="파일 ( jpg / png )", dataType="String"),
		@ApiImplicitParam(name="name", value="이름", dataType="String")
	})
    @PostMapping(value = "/patient/image")
    public ResponseEntity<ApiEntity> uploadImage(@RequestParam("image") MultipartFile multipartFile, @RequestParam("name") Optional<String> name) throws IllegalStateException, IOException {
    	
    	List<PatientEntity> patientEntity = defaultService.patientOriginalList(name.get());
    	
    	if(!name.isPresent()) throw new ApiException(ExceptionEnum.NO_Parameter); 
    	if(defaultService.ImagetList(name.get()) != null) throw new ApiException(ExceptionEnum.ALREADY_FILE);
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
		
        if(defaultService.uploadImage(params) == null) throw new ApiException(ExceptionEnum.RUNTIME_EXCEPTION);
        
        return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiEntity.builder()
						.Code("success")
						.Message("정상적으로 업로드 되었습니다.")
						.build());
    }

    @GetMapping(value = {"/patient/image/{name}"})
    @ApiOperation(value = "환자 파일 가져오기", notes = "해당 환자 데이터에 등록된 이미지파일을 가져온다.")
    @ApiImplicitParam(name="name", value="이름", dataType="String")
    	public ResponseEntity<?> downloadImageToFileSystem(@RequestParam(required = false) Optional<String> name) throws IOException {
    	
    	if(!name.isPresent()) throw new ApiException(ExceptionEnum.NO_Parameter);
    	
        byte[] downloadImage = defaultService.downloadImageFromFileSystem(name.get());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(downloadImage);
    }
    
	@DeleteMapping(value = {"/patient/{name}"})
	@ApiOperation(value = "환자 데이터 삭제", notes = "해당 환자 데이터에 등록된 데이터를 삭제한다.")
	@ApiImplicitParam(name="name", value="이름", dataType="String")
		public ResponseEntity<ApiEntity> patient_delete(@RequestParam(required = false) Optional<String> name) {
		
		if(!name.isPresent()) throw new ApiException(ExceptionEnum.NO_Parameter);
		
		ImageEntity patientEntity = defaultService.ImagetList(name.get());
		
		if(patientEntity == null)
			throw new ApiException(ExceptionEnum.NO_DATA);
		else {
			File deleteFile = new File(folderPath + File.separator + name.get() + patientEntity.getType());
			
			log.info("path : {}", folderPath + File.separator + name.get() + patientEntity.getType());
			
			if(deleteFile.exists()) {
	            deleteFile.delete(); 
	            log.info("파일을 삭제하였습니다.");
	        } else {
	        	log.info("파일이 존재하지 않습니다.");
	        }
			
			defaultService.patientDelete(name.get(),patientEntity.getPatient_seq());
		}
		
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiEntity.builder()
						.Code("success")
						.Message("[ " + name.get() + " ] 님의 데이터가 정상적으로 삭제되었습니다.")
						.build());
		
	}
	
}

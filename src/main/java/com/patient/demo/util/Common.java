package com.patient.demo.util;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.patient.demo.exception.ApiException;
import com.patient.demo.exception.ExceptionEnum;

import lombok.extern.slf4j.Slf4j;

@PropertySource("classpath:system.properties")
@Slf4j
@Component
public class Common {
    
    // 파일이 저장 될  경로
    @Value("${local.path}")
    private String folderPath;
    
    // 이미지 파일의 확장자명을 얻음
    public String getFileType(String type) {
        
        String originalFileExtension = "";
        
        if (type.contains("image/jpeg")) originalFileExtension = ".jpg";
        else if (type.contains("image/png")) originalFileExtension = ".png";
        else throw new ApiException(ExceptionEnum.CHECK_FILE);
        
        return originalFileExtension;
    }
    
    // 파일 업로드
    public boolean fileUpload(File folder, MultipartFile image, String fileName) throws IllegalStateException, IOException {
        
        String contentType = getFileType(image.getContentType());
        
        // 해당 경로에 폴더가 없으면 생성해준다.
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
            
            String fullFilePath = folderPath + File.separator + fileName + contentType;
            File image_file = new File(fullFilePath);
            
            image.transferTo(image_file);
            
        }
        
        return true;
    }
    
    // 파일 삭제
    public String fileDelete(File file) {
        
        if(file.exists()) {
            file.delete(); 
            return "delete patient | file delete success";
        } else {
            return "delete patient | file not exists";
        }
        
    }
    
}

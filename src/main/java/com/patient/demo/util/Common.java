package com.patient.demo.util;

import org.springframework.stereotype.Component;

import com.patient.demo.exception.ApiException;
import com.patient.demo.exception.ExceptionEnum;

@Component
public class Common {
    
    // 이미지 파일의 확장자명을 얻는다.
    public String getFileType(String type) {
        
        String originalFileExtension = "";
        
        if (type.contains("image/jpeg")) originalFileExtension = ".jpg";
        else if (type.contains("image/png")) originalFileExtension = ".png";
        else throw new ApiException(ExceptionEnum.CHECK_FILE);
        
        return originalFileExtension;
    }
    
}

package com.patient.demo.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patient.demo.entity.PatientEntity;

@Transactional
@AutoConfigureMockMvc
@PropertySource("classpath:system.properties")
@SpringBootTest
public class RestApiControllerTest {
    
    // 파일이 저장 될  경로
    @Value("${local.path}")
    private String folderPath;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void 환자_등록() throws Exception {
        
        // Given
        PatientEntity patientEntity = PatientEntity.builder()
                                                   .name("말티즈")     // 이름
                                                   .age(28)         // 나이
                                                   .gender("남자")    // 성별 [ 남자 / 여자 ]
                                                   .disease("유")    // 질병유무 [ 유 / 무 ]
                                                   .build();
        
        String json = objectMapper.writeValueAsString(patientEntity);
        
        // When
        MvcResult result = mockMvc.perform(post("/api/v1/patient")
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .content(json))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        assertEquals("0", response.get("result"));
        assertEquals("success", response.get("message"));
    }

    @Test
    public void 환자_등록_이미지업로드() throws Exception {
        
        // Given
        String fileName = "홍길동"; //파일명
        String contentType = "png"; //파일타입
        String filePath = folderPath + File.separator + fileName + "." + contentType; //파일경로
        
        
        // When
        MockMultipartFile image = new MockMultipartFile("image",        // name
                                                        "test.png",        // originalFileName
                                                        "image/png",    // Type
                                                        new FileInputStream(filePath)); // 실제 파일 경로
        // Then
        mockMvc.perform(
                multipart("/api/v1/patient/image")
                .file(image)
                .param("name", fileName)
                ).andExpect(status().isOk());
    }
    
    @Test
    public void 환자_조회() throws Exception {
        
        // Given
        String name = "말티즈";
        
        // When
        MvcResult result = mockMvc.perform(get("/api/v1/patient")
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .param("name",name))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        assertEquals("0", response.get("result"));
        assertEquals("success", response.get("message"));
    }
    
    @Test
    public void 환자_삭제() throws Exception {
        
        // Given
        String name = "말티즈";
        
        // When
        MvcResult result = mockMvc.perform(delete("/api/v1/patient")
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .param("name",name))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        assertEquals("0", response.get("result"));
        assertEquals("success", response.get("message"));
    }
    
}

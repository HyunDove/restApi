## ❓ 개발 환경 및 언어 
- Spring Tool Suite 4 (4.4.0 RELEASE)
- SPRING BOOT
- JAVA 1.8
- JPA
- MySql

 
## 🛠 문제 해결 방법
- **정보 삭제 시** 삭제여부와 삭제일자만 값 세팅하고 update하는 과정에서 <br/>세팅하지않은 값도 update되는
현상 발생<br/>
👌 **dirty checking (Transactional)** 방식으로 변경


- 저장 할 파일 경로 지정 후 file.transferTo 호출 시 상대 경로로 입력하게되면, FileNotFound Exception 발생<br/>
👌 저장 할 파일의 경로 방식을 **직접 경로** 방식으로  수정


## 🚗 Swagger  
- http://localhost:8080/swagger-ui.html

## 🛠 DataBase ERD

![RestApi](https://github.com/HyunDove/restApi/assets/139856413/0c3edff2-4628-4ebd-aa47-0706c4b1ff62)

   



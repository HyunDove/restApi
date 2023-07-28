## ❓ 개발 환경 및 언어 
- Spring Tool Suite 4 (4.4.0 RELEASE)
- SPRING BOOT
- Java 1.8
- JPA
- MySql


## 🛠 문제 해결 방법
- **정보 삭제 시** 삭제여부와 삭제일자만 값 세팅하고 update하는 과정에서 <br/>세팅하지않은 값도 update되는
현상 발생<br/>
👌 **dirty checking (Transactional)** 방식으로 변경


- 저장 할 파일 경로 지정 후 file.transferTo 호출 시 상대 경로로 입력하게되면, FileNotFound Exception 발생<br/>
👌 저장 할 파일의 경로 방식을 **직접 경로** 방식으로  수정


## 🌐 Swagger  
- http://localhost:8080/swagger-ui.html

## 🪧 DataBase ERD

![RestApi](https://github.com/HyunDove/restApi/assets/139856413/0c3edff2-4628-4ebd-aa47-0706c4b1ff62) 
   
##  🖥️ DataBase 생성문
- **고객 정보**
```
CREATE TABLE `patient_info` (
  `seq` int(11) NOT NULL AUTO_INCREMENT COMMENT '고유 번호',
  `name` varchar(20) DEFAULT NULL COMMENT '이름',
  `age` int(2) DEFAULT NULL COMMENT '나이',
  `gender` varchar(10) DEFAULT NULL COMMENT '성별',
  `disease` varchar(10) DEFAULT NULL COMMENT '질병유무',
  `delete_flag` varchar(10) DEFAULT '미삭제' COMMENT '삭제여부',
  `create_date` datetime DEFAULT NULL COMMENT '생성일자',
  `delete_date` datetime DEFAULT NULL COMMENT '삭제일자',
  PRIMARY KEY (`seq`)
) ENGINE=MyISAM AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb3;
```
- **이미지 파일**
```
CREATE TABLE `patient_images` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '고유 번호',
  `name` varchar(200) NOT NULL COMMENT '이름',
  `type` varchar(100) NOT NULL COMMENT '파일 저장 타입',
  `size` int(11) NOT NULL COMMENT '파일 크기',
  `path` varchar(100) NOT NULL COMMENT '이미지 경로',
  `patient_seq` int(11) NOT NULL COMMENT '회원 정보 고유번호',
  `delete_flag` varchar(3) DEFAULT '미삭제' COMMENT '삭제여부',
  `create_date` datetime NOT NULL COMMENT '생성일자',
  `delete_date` datetime DEFAULT NULL COMMENT '삭제일자',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb3;
```

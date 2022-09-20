# Getting Started

### 기술 스택
<hr>

#### Java
 - openjdk 11

#### Spring boot 2.6.11
 - spring-boot-starter-data-jpa
 - spring-boot-starter-data-redis
 - spring-boot-starter-circuitbreaker-resilience4j

#### 3rd Patty
 - lombok
 - retrofit 2.4.0
 - embedded-redis 0.7.2
 - h2
#### Build
 - Gradle-7.0.2

<hr>

### 모듈 구성
프로젝트 확장에 따른 비용을 최소화 하고자 멀티 모듈로 구성 한다.

* search-service : Root 프로젝트
    - search-domain : 도메인 모델 모듈
    - search-app-api : 검색 api 모듈

<hr>

### API 명세서  
* spring-rest-doc
  - search-app-api\src\main\resources\static\docs\blog-search.html

<hr>

### 장애 대응
Circuit Breaker(서킷브레이커) Resilience4J 

#### 서킷브레이커 상태
http://localhost:8080/actuator/health

#### 서킷브레이커 목록
http://localhost:8080/actuator/circuitbreakers

### 에러 핸들링
- GlobalExceptionHandler 클래스에 에러 정의
- @RestControllerAdvice

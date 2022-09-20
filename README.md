# 블로그 검색 서비스

### 주요 기능
 - 블로그 검색
 - 인기 검색어 목록
 - 블로그 검색 로그

### 기술 스택

<hr>

#### Java

- openjdk 11

#### Spring boot 2.6.11

- spring-boot-starter-data-jpa
- spring-boot-starter-data-redis : 키워드 별로 검색된 횟수의 정확도(동시성 문제 해결)
- spring-boot-starter-circuitbreaker-resilience4j : 카카오 검색 API 장애 시 네이버 검색 API로 전환(서킷블레이커 적용)
- org.springframework.restdocs : Api 명세서 자동화

#### 3rd Patty

- lombok
- retrofit 2.4.0 : http client
- embedded-redis 0.7.2 : 내장 redis로 검증
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

### 실행
```
java -jar search-app-api-1.0.jar
```

<hr>

### API 명세서

* spring-rest-doc
    - search-app-api\src\main\resources\static\docs\blog-search.html

#### Http Method 정의

* 본 REST API 에서 사용하는 HTTP 동사(verbs)는 가능한 표준 HTTP 와 REST 규약을 따릅니다.

|Http Method |Description|
|:---|:---|
|GET|리소스를 가져올때 사용|
|POST|새 리소스를 만들 때 사용|
|PUT|기존 리소스를 수정할 때 사용|
|PATCH|기존 리소스의 일부를 수정할 때 사용|
|DELETE|기존 리소스를 삭제할 떄 사용|

### 에러 응답 예

```
{
  timestamp: "2022-09-18 20:17:35",
  status: 400,
  error: "Bad Request",
  message: "Required request parameter 'query' for method parameter type String is not present",
  path: "/blogs"
}
```

### 블로그 검색 API

* 블로그 검색 결과 입니다.

#### Http request

```http request
GET /api/search/v1/blogs?query=%EC%8A%A4%ED%94%84%EB%A7%81&page=1&size=10&sort=accuracy HTTP/1.1
Accept: application/json
Host: localhost:8080
```

|Parameter |Description|
|:---|:---|
|query|검색어|
|page|페이지 번호|
|size|사이즈|
|sort|정렬(accuracy : 정확도, recency: 최신순)|

#### Http response

```
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 851

{
 "content":
  [
    {"title":"스프링1","contents":null,"url":null,"blogname":"블로그1","thumbnail":null,"datetime":null},
    {"title":"스프링2","contents":null,"url":null,"blogname":"블로그2","thumbnail":null,"datetime":null},
    {"title":"스프링3","contents":null,"url":null,"blogname":"블로그3","thumbnail":null,"datetime":null},
    {"title":"스프링4","contents":null,"url":null,"blogname":"블로그4","thumbnail":null,"datetime":null},
    {"title":"스프링5","contents":null,"url":null,"blogname":"블로그5","thumbnail":null,"datetime":null}
  ],
 "pageable":{
  "sort":{
    "empty":true,
    "sorted":false,
    "unsorted":true
  },
  "offset":0,
  "pageNumber":0,
  "pageSize":10,
  "paged":true,
  "unpaged":false
  },
  "totalPages":1,
  "last":true,
  "totalElements":10,
  "size":10,
  "number":0,
  "sort":{
    "empty":true,
    "sorted":false,
    "unsorted":true
   },
   "first":true,
   "numberOfElements":5,
   "empty":false
 }
```

#### Http status

|:---|:---|
|Http Status|Description|
|200 OK|요청을 성공적으로 처리함|
|400 Bad Request|잘못된 요청을 보낸 경우. 응답 본문에 오류에 대한 정보가 담겨있다.|

### 블로그 검색 랭킹 API

* 블로그 검색 랭킹 결과 입니다.

#### Http request

```http request
GET /api/search/v1/rankings HTTP/1.1
Accept: application/json
Host: localhost:8080
```
#### Http response
```http response
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 89

[
  {"keyword":"Spring","count":10},{"keyword":"JPA","count":5},{"keyword":"MSA","count":1}
]
```

#### Http status

|Http Status|Description|
|:---|:---|
|200 OK|요청을 성공적으로 처리함|
|400 Bad Request|잘못된 요청을 보낸 경우. 응답 본문에 오류에 대한 정보가 담겨있다.|

### 블로그 검색 로그 API

* 블로그 검색 로그 결과 입니다.

#### Http request

```http request
GET /api/search/v1/logs?page=1&size=10 HTTP/1.1
Accept: application/json
Host: localhost:8080
```

|Parameter |Description|
|:---|:---|
|page|페이지 번호|
|size|사이즈|

#### Http response

```http response
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 851

{
 "content":
  [
    {"id":1,"query":"스프링","searchTime":"2022-09-20T18:03:52.5451142","source":"KAKAO"}
  ],
 "pageable":{
  "sort":{
    "empty":true,
    "sorted":false,
    "unsorted":true
  },
  "offset":0,
  "pageNumber":0,
  "pageSize":10,
  "paged":true,
  "unpaged":false
  },
  "totalPages":1,
  "last":true,
  "totalElements":10,
  "size":10,
  "number":0,
  "sort":{
    "empty":true,
    "sorted":false,
    "unsorted":true
   },
   "first":true,
   "numberOfElements":5,
   "empty":false
 }
```

#### Http status

|Http Status|Description|
|:---|:---|
|200 OK|요청을 성공적으로 처리함|
|400 Bad Request|잘못된 요청을 보낸 경우. 응답 본문에 오류에 대한 정보가 담겨있다.|

<hr>

### 장애 대응

- Circuit Breaker(서킷브레이커) - Resilience4J

#### 서킷브레이커 상태

- http://localhost:8080/actuator/health

#### 서킷브레이커 목록

- http://localhost:8080/actuator/circuitbreakers

### 에러 핸들링

- GlobalExceptionHandler 클래스에 에러 정의


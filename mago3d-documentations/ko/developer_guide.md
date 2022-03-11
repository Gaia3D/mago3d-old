# 목차 

0. [mago3D 아키텍처 및 구성](#0-mago3d-아키텍처-및-구성)
1. [개발환경 구축](#1-개발환경-구축)
2. [프로젝트 구성](#2-프로젝트-구성)
3. [DataGroup, DataInfo와  F4D와의 관계](#3-datagroup-datainfo와-f4d의-관계)
4. [RabbitMQ 동작 이해하기](#4-rabbitmq-동작-이해하기)
5. [3차원 데이터 자동 변환](#5-3차원-데이터-자동-변환)
6. [레이어 관리](#6-레이어-관리)
7. [권한 관리](#7-권한-관리)
8. [정적 컨텐츠 갱신](#8-정적-컨텐츠-갱신)
9. [암호화](#9-암호화)
10. [메모리 캐시](#10-메모리-캐시)
11. [자바](#11-자바)
12. [DB 규약](#12-db-규약)
13. [코딩 규약](#13-코딩-규약)
14. [빌드](#14-빌드)
15. [로깅](#15-로깅)
16. [Web/ WAS Server 차이점](#16-webwas-server-차이점)
17. [Web/WAS Server 연동 및 테스트](#17-web--was-server-연동-및-테스트)
18. [Tomcat 설정](#18-tomcat-설정)
19. [서비스 등록](#19-서비스-등록)
20. [Windows 서버 구축하기](#20-windows-서버--구축하기미션)



## 0. [mago3D 아키텍처 및 구성](./README.md) 

## 1. 개발환경 구축

- java 설치
- docker-compose 로 개발환경을 구축하는 경우 geoserver, database, rabbitmq 설정은 skip
- intellij 에서 개발환경을 구축하는 경우 static resource 들을 build 없이 갱신하기 위하여 resource 경로를 file path 로 잡아 주기 때문에, 실행을 bootRun 으로 실행하거나 configuration 에 Working directory 를 **$MODULE_WORKING_DIR$** 로 설정해 주어야 한다.
- [설치 가이드](./installation_guide.md)



## 2. 프로젝트 구성

| **프로젝트명**      | **설명**                                                     |
| :------------------ | :----------------------------------------------------------- |
| common              | 암호화(보안), 통계모듈 등 공통 기능 관리                     |
| doc                 | Database(ddl, dml 등), Geoserver 관련 문서, Docker(dockerfile 등), 프로젝트 문서를 관리 |
| html                | html 디자인 파일 관리                                        |
| mago3d-admin        | 플랫폼(mago3D) 관리자                                        |
| mago3d-user         | 2차원/3차원 공간데이터 조회, 시뮬레이션 연동 등등            |
| mago3d-converter    | 3차원 공간정보 자동화 관리                                   |
| mago3d-tiler        | 스마트 타일 자동화 관리                                      |
| mago3d-sensorthings | 센서 데이터 자동화 관리                                      |



## 3. DataGroup, DataInfo와 F4D의 관계

###  경로 설정파일

/src/resources/mago3d.properties

### 기본 경로

| **Path**  | **관리자**  | **사용자**    |
| :-------- | :---------- | :------------ |
| F4D       | /infra/data | /service/data |
| SmartTile | /infra/tile | /service/tile |

- 관리자에서 등록하는 데이터는 **infra/data** 를 기본 path 로 사용하고, 
- 사용자에서 등록하는 데이터는 **service/data** 를 기본 path 로 사용한다.
- 관리자/사용자 모두 기본적으로 **basic** 이라는 기본 그룹을 가지고,
- **기본 path/user_id/data_group_key/** 경로에 변환된 F4D 데이터가 저장된다.

### F4D 데이터 저장 경로 (전체)

![img](./images/f4d_data1.png)

### F4D 데이터 저장 경로 (상세)

![img](./images/f4d_data2.png)

 

## 4. RabbitMQ 동작 이해하기

###  ![:rabbit:](https://pf-emoji-service--cdn.us-east-1.prod.public.atl-paas.net/standard/a51a7674-8d5d-4495-a2d2-a67c090f5c3b/64x64/1f430.png) **RabbitMQ**

[RabbitMQ](https://www.rabbitmq.com/)는 Erlang으로 [AMQP](https://ko.wikipedia.org/wiki/AMQP)를 구현한 메시지 브로커 시스템이다.

### AMQP(Advanced Message Queuing Protocol)

- 클라이언트가 메시지 미들웨어 브로커와 통신할 수 있게 해주는 **메세징 프로토콜**
- RabbitMQ에서 중요한 개념으로 **Producer, Consumer, Queue, Exchange, Binding**이 있다.

| **항목**     | **설명**                                                     |
| :----------- | :----------------------------------------------------------- |
| **Producer** | * 메세지를 생성하고 발송하는 주체<br />* 주의해야 할 점은 Producer는 Queue에 직접 접근하지 않고, 항상 Exchange를 통해 접근하게 된다. |
| **Exchange** | * Producer들이 발송한 메세지들을 어떤 Queue들에게 발송할지 결정하는 객체<br />* Exchange는 4가지 타입이 있으며, 일종의 라우터 개념이다. |
| **Binding**  | * Exchange에게 메세지를 라우팅 할 규칙을 지정하는 행위<br />* 특정 조건에 맞는 메세지를 특정 큐에 전송하도록 설정할 수 있는데, 이는 해당 Exchange 타입에 맞게 설정되어야 한다. <br />* Exchange와 Queue는 M:N binding이 가능합니다. |
| **Queue**    | * Producer들이 발송한 메세지들이 Consumer가 소비하기 전까지 보관되는 장소<br />* Queue는 이름으로 구분되는데, 같은 이름과 같은 설정으로 Queue를 생성하면 에러 없이 기존 Queue에 연결되지만, 같은 이름과 다른 설정으로 Queue를 생성하려고 시도하면 에러가 발생합니다. |
| **Consumer** | * 메세지를 수신하는 주체<br />* Consumer는 Queue에 직접 접근하여 메세지를 가져온다. |

![img](./images/rabbitmq1.png)

- 메시지를 발행하는 **produce**r 에서 **broker** 의 **exchange** 로 메시지를(**routing key**) 전달하면, **binding** 규칙에 의해 연결된 **queue** 로 메시지가 복사된다.
- 메시지를 받아가는 **consumer** 에서는 브로커의 **queue** 를 통해 메시지를 받아가서 처리한다.

![img](./images/rabbitmq2.png)

- Exchange에는 4가지 타입이 있는데, 가 타입별 특징은 다음과 같다.

| **타입** | **설명** | **특징** |
| :------- | :------- | :------- |
|          |          |          |

| **타입**    | **설명**                                                     | **특징**  |
| :---------- | :----------------------------------------------------------- | :-------- |
| **Direct**  | Routing key가 **정확히 일치**하는 Queue에 메세지 전송        | Unicast   |
| **Topic**   | Routing key **패턴이 일치**하는 Queue에 메세지 전송          | Multicast |
| **Headers** | **[ key - value ]** 로 이루어진 header 값을 기준으로 일치하는 Queue에 메세지 전송 | Multicast |
| **Fanout**  | 해당 Exchange에 등록된 **모든** Queue에 메세지 전송          | Broadcast |

### 기타 설정값

- durability
  - 브로커가 재시작 될 때 남아 있는지 여부
  - durable : 재시작해도 유지 가능
  - transient : 재시작하면 사라짐
-  auto-delete
  - 마지막 queue 연결이 해제되면 삭제

## 5. 3차원 데이터 자동 변환

### 흐름도

![img](./images/3dflow1.png)

 

![:one:](https://pf-emoji-service--cdn.us-east-1.prod.public.atl-paas.net/standard/a51a7674-8d5d-4495-a2d2-a67c090f5c3b/64x64/31-20e3.png) 데이터 업로드

![:two:](https://pf-emoji-service--cdn.us-east-1.prod.public.atl-paas.net/standard/a51a7674-8d5d-4495-a2d2-a67c090f5c3b/64x64/32-20e3.png) 메세지큐 작업등록 및 상태응답

![:three:](https://pf-emoji-service--cdn.us-east-1.prod.public.atl-paas.net/standard/a51a7674-8d5d-4495-a2d2-a67c090f5c3b/64x64/33-20e3.png) 자동변환 API(mago3d-converter)가 RabbitMQ의 Queue를 보고 있다가 Queue에 메세지가 들어올 경우 F4DConverter에 변환을 요청

![:four:](https://pf-emoji-service--cdn.us-east-1.prod.public.atl-paas.net/standard/a51a7674-8d5d-4495-a2d2-a67c090f5c3b/64x64/34-20e3.png) F4DConverter에서 변환이 완료되면 자동변환 API(mago3d-converter)로 응답을 보내고,

응답을 받은 API에서 변환결과(로그, 속성, 위치)를 수집한다.

 

속성정보, 위치정보가 있을 경우에만 속성, 위치 파일을 생성

위치파일 : 변환된 F4D 디렉토리 하위에 `lonsLats.json`

속성파일 : 변환된 F4D 디렉토리 하위에 `attributes.json`

 

로그 파일은 항상 생성

로그파일 : `ConverterServiceImpl`, `executeConverter`

 

![:five:](https://pf-emoji-service--cdn.us-east-1.prod.public.atl-paas.net/standard/a51a7674-8d5d-4495-a2d2-a67c090f5c3b/64x64/35-20e3.png) 데이터 변환결과 갱신 

 

## 6. 레이어 관리 

- 레이어 관리는 mago3D 관리자에서만 등록 및 수정 삭제가 가능하고, 사용자에서는 관리자가 등록한 레이어들을 가시화만 해준다.
- vector 
  - 현재 업로드 가능 대상은 Shape 파일이다. 
  - shape 파일 등록 시 ogr2ogr 을 사용하기 때문에 gdal 에 의존적이다. 
- ![img](./images/lm1.png)
- raster 
  - raster 데이터는 대용량인 경우가 많아서 관리자에서 별도로 업로드 하지 않고, 직접 storage 에 있는 데이터를 geoserver 로 등록 후 관리자에서 관리한다. 
  - geoserver 에 등록된 레이어중 선택한 레이어를 서비스 대상 레이어로 설정 할 수 있고, wms 외에캐시 사용 유무를 설정하여 geowebcache 를 사용할 수 있다.
- ![img](./images/lm2.png)



## 7. 권한 관리

모든 사용자는 그룹을 가지고 있고 해당 그룹에 따라 접근 가능한 메뉴와 권한이 결정된다. 

![img](./images/role1.png)

![img](./images/role2.png)



## 8. 정적 컨텐츠 갱신

- ![img](./images/static_contents.png)

- 정적인 컨테츠인 css, js 등은 브라우저가 캐시를 해두기 때문에 새로운 내용이 브라우저 캐시를 지우기 전까지는 반영되지 않는다. 

- 컨테츠 경로에 쿼리스트링을 ?version=20201202123453 와 같이 timestamp 를 붙여도 되지만 이렇게 할 경우 컨텐츠 내용에 변경사항이 없을 때도 캐시된 컨텐츠가 아닌 새로운 컨텐츠를 요청후 가져온다.

- 컨텐츠에 변경이 있을 경우에만 캐시를 갱신하기 위해 다음과 같이 경로에  **cacheVersion** 을 붙이고 필요한 경우에만 갱신한다.

  ```
  <script type="text/javascript" th:src="@{/js/{lang}/layer.js(lang=${lang},cacheVersion=${contentCacheVersion})}"></script>
  ```

  

## 9. 암호화 

1) jdbc connection 정보 및 각종 계정정보 암호화

   –  seed 암호화 알고리즘을 사용.(128비트 암호화키를 사용하는 블록 암호화 알고리즘)
   –  16 바이트의 key 를 사용하며, 해당 **key 는 프로젝트 별로 다르게 해야한다.** 프로젝트마다 key 를 같게 한다면 하나의 key 를 가지고 다른 프로젝트까지 복호화 할 수 있기 때문이다.

   – application.properties 의 db connection 정보를 암호화 복호화 할때는  common 프로젝트의
   gaia3d/security 패키지의 CryptTest 의 테스트를 수행해서 암복호화 한다. 

![img](./images/encryption.png)

2) 로그인 사용자 패스워드 암호화 

​    –  사용자 정보를 암호화 할때는 mago3d-admin 프로젝트의 gaia3d.utils 패키지의 PasswordTest 의
   bCryptPasswordEncoderTest 테스트를 수행해서 암호화 한다. 

​    – spring security 의 `BCryptPasswordEncoder` 를 사용하여 암호화 하여 저장한다. 
​    –  BCryptPasswordEncoder 의 **strength 를 10 이상으로 할 경우 속도가 느려지므로 주의.**



## 10. 메모리 캐시

- policy, geopolicy, userGroup, menu ,role 에 대해 메모리 캐시를 사용한다. 
- 관리자에서 위 항목에 대해 변동 사항이 있을 경우 해당 내용을 사용자 애플리케이션으로 전파해 주기 때문에  **위 항목들에 대해 갱신할 경우에는 사용자 애플리케이션도 기동중이어야 한다.**
- **애플리케이션이 실행될 때 db 에서 해당 정보들을 가져와서 메모리에 담기 때문에 애플리케이션이 기동중인 상태에서 해당 내용들을 수동으로 갱신 했을 경우에는 애플리케이션을 재기동해야 한다.**



## 11. 자바

- **패키지**

–  api : 외부에서 사용할 api. 로그를 남겨야 할 경우 APIController 인터페이스를 상속하여 사용

– controller

– rest : RestController

– view : Controller

– interceptor : locale, login 등에 관련된 각종 인터셉터 . 
  ConfigInterceptor : view 에서 사용할 정보를 바인딩(메뉴, contentVersion 등)

- **resources**

– develop : 개발 서버용 설정 파일 

– messages : 다국어 처리를 위한 message properties

– static

– externlib : 외부 라이브러리

- logback.xml : 상세 로그를 보고 싶은 경우 

```
logger name="org.springframework" 값을 "org.springframework.web" 으로 변경
```

- **문법**
  – 트랜잭션 관리
     읽기와 쓰기, 수정, 삭제를 분리. @Transactional , @Transactional(read-only)

  – 단일 트랜잭션의 경우 Controller 에서 Service 계층을 호출할때 여러번 호출 되어서는 안됨.
     복잡한 로직의 경우 Service 계층에서 private 메소드 등으로 분리
  – 서비스 계층 예외 처리의 경우 spring 은 runtimeException 만 rollback을 하므로
     기타 예외의 경우 rollback-for 를 명시적으로 사용하거나, try, catch 구문에서 RuntimeException 으로변환해서 throw

     예) 게시판 목록, 게시판 상세, 첨부 파일
  –  코드값의 경우  enum을 사용하고, 각 도메인에서 사용하는 enum은 도메인 안에 포함하고, 공통의 경우 별도 파일로 분리. 현재 작업 진행중.
  – 날짜 관련 부분은 Calendar, Date 보다 LocalDateTime을 사용.
  – API 및 JSON Response 결과값의 경우 LocalDateTime 값이 제대로 표시되지 않음.
     viewInsertDate 를 사용하여 꼼수 처리중. 추후 개선 예정
  – foreach 의 경우 stream 사용, 성능이 느려지는 문법 사용시 주의.
  – HA Tomcat Clustering 으로 서버간 세션 공유 및 네트웍 통신 데이터의 경우 Serializable 을 구현
  – 다국어 처리를 위해 서버 메시지의 경우 message.properties, client 의 경우 message.js 파일에 작성.
     서버 처리가 힘든 경우 json id 를 이용하여, message.js 파일과 연동 

- 보안

  - Spring Security 적용 하지 않음. Role 부분 확장에 제약이 있어 추후 적용 예정
  - SQL Injection 의 경우 XSSFilter를 사용하는 OWASP Antisamic 이나 NHN Lucy 적용 예정

- Spring Boot

  - Runnable jar 가 개발시에는 편한데…. 고객사에서 설정(경로, ip) 등을 바꿔야 할 경우와

    이중화 이슈가 있어서…. 전부 war 로 변경할려고 함



## 12. DB 규약

- pk 가 되는 칼럼명은 테이블명_id 로 사용 

- Fk 는 개발 편이성을 위해 운영전 반영

- data 건수가 많을 경우 파티션 사용(30년 기준 100만 단위가 넘는 데이터)
    **pg12 기준 enable_partition_pruning = on 으로 설정해야 적용됨**

- 30년 기준, 약 10억건이 넘어가는 데이터에 대해서는(long type) bigint 로 사용

- 통계성 데이터의 경우, 최적화를 위해 중복 컬럼 허용
  년, 월, 일, 주 등은 insert_date 와 중복이 되나 미리 계산하여 저장

- 통계성 데이터를  실시간 표시해야 하는 이슈가 있는 경우 람다 아키텍처 사용

- 경도, 위도, 높이의 경우, Geometry(POINT, 4326), Numeric 타입(Altitude) 로 저장

-  시간 타입의 경우 timestamp with time zone 사용

  ![img](./images/db_convention.png)



## 13. 코딩 규약

- **java**
  – e.printStackTrace를 출력 하고 싶은 경우는 `LogMessageSupport.printMessage(exception);`를 사용할것

  - 각 기능별 권한 사용의 경우 Controller 의 roleValidator를 참조하여 작성
  - form tag 가 전송되는 페이지에는 

  `SQLInjectSupport.replaceSqlInection` 를 맨 처음 실행.(보안 감사)

  - Rest API(Http API) 나 Ajax 통신의 Return 값으로 ResponseEntity를 던졌으나 Client 단에서

    예외 처리 등의 어려움이 있어서…. Map 안에 고정 키 값을 가지는 형태로 변경함

- **FrontEnd**

  – thymeleaf 코딩 규약 : [Thymeleaf 형식](https://gaia3d.atlassian.net/wiki/spaces/project/pages/32309256) 

  – handlebar.js **:** thymeleaf 사용시 server 와  view 가 강하게 결합되기 때문에 사용하지 않고 동적인 컨텐츠를 랜더링 하기 위해 사용([handlebar.js 규칙](https://gaia3d.atlassian.net/wiki/spaces/project/pages/28049409) )

  - 웹 접근성 준수
  - 다국어 처리를 위한 폴더 분리.
  - admin 페이지와 user 페이지에서 같은 이름의 js, css 파일을 사용하지 말것. 캐싱이 남음

- **mybatis** 

– 예약어는 대문자로, SQL 첫 문장에는 /* 호출하는 sql Id */ 를 작성

– SELECT , INSERT, UPDATE, DELETE 순으로 작성

– parameterType, resultType 에 map은 사용하지 말것. int, long, string 등은 기본 alias 사용

– 다음의 들여쓰기 규칙을 공통으로 사용 

```
UPDATE table 
SET
  column = ${value};
  
INSERT INTO table (
  column1, column2.....
) VALUES (
  value1, value2...
)

SELECT 
  칼럼
FROM
WHERE
ORDER BY
```



## 14. 빌드 

- mago3d project 에서는 gradle 6.5.0 버전을 현재 사용중이며, 빌드시에는 별도로 설치한 gradle 을 사용하거나, 프로젝트에 포함된 gradle wrapper 를 사용해도 된다.

- 프로젝트 전체를 빌드할 경우 root 경로에서, 특정 프로젝트를 빌드 할 때는 특정 프로젝트 경로에서 아래 명령어를 각각 수행한다.

  ```
  gradlew clean
  gradlew build
  ```

- default properties 설정에는 resource 경로를 file path 로 사용중이기 때문에 Servlet container 에 애플리케이션을 올릴 경우에는 classpath 를 사용해야 한다.

- properties 설정을 배포할 때 마다 바꾸는 일이 번거롭기 때문에 별도의 properties 들을 만들어 두고, 빌드시 profile 옵션을 사용하여 해당 설정파일들로 빌드 되도록 할 수 있다.

- develop 으로 빌드할 경우 

  ```
  gradlew build -Pprofile=develop
  ```

- 빌드 성공시 프로젝트 경로/build/libs 에 build.gradle 설정에 따라 war 또는 jar 파일이 생성된다. 

- war vs jar

  - jar 로 빌드할 경우 springboot 에 내장된 톰캣으로 손쉽게 애플리케이션을 기동시킬 수 있다. 하지만 jar 로 빌드할 경우 properties 설정을 변경 할 수 없기 때문에 고객사에 납품해야 하는 경우 설정이 다를 수 있기 때문에 오히려 더 불편할 수도 있다.
  - war 로 빌드할 경우 애플리케이션을 위한 별도의 Servlet Container(tomcat,jetty, jboss 등등) 가 필요하다. 



## 15. 로깅 

- resources/logback-spring.xml 에서 관리 

- 로컬 개발환경에서는 ConsoleAppender 를 사용하고 로그레벨을 debug 로 사용한다. 

- 개발중에 에러가 발생했는데 저 정확한 정보가 필요할 경우 org.springframework logger 를 다음과 같이 변경한다.

  ```
  <logger name="org.springframework.web" level="info" additivity="false" />
  ```

- 로컬 환경에서는 로깅을 debug 레벨로 사용해도 상관없지만, 운영 환경에서는 debug 레벨로 운영시 너무 많은 로그가 남게 되고, 로그를 기록하는 것 또한 성능에 영향을 미치기 때문에 운영레벨에서는 로그레벨을 info 정도로 하는것이 적당하다.  

- 로컬에서 ConsoleAppender 를 사용했지만, 운영환경에서는 로그들을 파일로 관리할 필요가 있기 때문에 FileAppender 를 사용한다.



## 16. Web/WAS Server 차이점

- web server 

  - 웹브라우저 클라이언트로부터 HTTP 요청을 받아 html, js 등과 같은 **정적 컨테츠**를 제공하는 서버 
  - Apache, IIS, Nginx …

- was server 

  -  DB 조회나 다양한 로직 처리를 요구하는 **동적인 컨텐츠**를 제공하기 위해 만들어진 Application Server

  - HTTP 를 통해 컴퓨터나 장치에 애플리케이션을 수행해주는 서버

  - Tomcat, WebSphere, JBoss …

    ![img](./images/web1.png)

- Web Server 와 Was Server 를 분리하는 이유 

  - Was Server 는 DB 조회나 다양한 로직을 처리하느라 부하가 많이 걸리기 때문에 단순한 정적 컨텐츠는 Web Server 에서 빠르게 클라이언트에게 제공하는 것이 좋다. 만약 정적 컨텐츠 요청까지 Was Server 가 처리한다면 정적 데이터 처리로 인해 부하가 커지게 되고, 동적 컨텐츠의 처리가 지연됨에 따라 수행 속도가 느려진다.
  - 물리적으로 분리하여 보안 강화
  - Load Balancing, fail over 처리 등에 유리하다.



## 17. Web & WAS Server 연동 및 테스트

- Apache & Tomcat 
  - mod_jk(추천)
    - JKMount 옵션을 이용하면 URL 이나 컨텐츠별로 유연한 설정이 가능하다. 
    - 설정이 어려움
    - 톰캣 전용
  - mod_proxy, mod_proxy_ajp
    - 별도 모듈 설치가 필요 없고 설정이 간편하다.
    - 특정 WAS 에 의존적이지 않다. 
    - URL 별 유연한 설정이 어렵다.
- Nginx & Tomcat
  - nginx conf 파일의 location 에 프록시 설정을 추가하여 연동 가능하다.
- 테스트 
  - was 에 배포한 application 의 특정 url 에 접근하여 web/was server 의 access_log 파일들을 확인.
  - ![img](./images/web2.png)



## 18. Tomcat 설정

- server.xml 에 설정에 따라 tomcat 경로의 webapps 밑에 application 을 배치하고 서비스 하거나 특정 경로에 두고 서비스가 가능하다.
  - appBase : application 이 실행 되는 가상 호스트 디렉토리(webapps)
  - docBase : application 이 실행 되는 별도의 디렉토리 지정
- docBase 사용시에는 application 경로는 webapps 하위 경로를 사용하지 않는다. mago3D application 기동시 logback 설정등과 관련하여 충돌이 있는 경우가 있으므로…
- 리눅스 환경에서 사용시에는 catalina.out 파일이 제한없이 계속 커지기 때문에 catalina.sh 파일에서 catalina.out 파일을 사용하지 않도록 설정하거나 리눅스의 log rotate 설정을 한다. 
- setenv.sh(리눅스), setenv.bat(윈도우) 파일에 jvm 관련 옵션들을 사용할 수 있다. 
  - 윈도우 환경에서 서비스를 등록시 setenv.bat 파일에 설정한 옵션들이 적용되지 않으므로 이 경우에는 service.bat 파일에 적용해야 한다.



## 19. 서비스 등록

- tomcat
  - tomcat 폴더의 service.bat 파일을 이용하여 서비스를 등록 할 수 있다.
- nginx.exe & mago3d-converter.jar
  - winsw 라는 프로그램을 통해 서비스를 등록 할 수 있다.



## 20. Windows 서버  구축하기(미션)

- java 설치 및 환경변수 등록 java install and set environment variable
- postgresql 설치 install postgresql
  - port 5432 로 설치 
  - **mago3d admin/user 의 jdbc url 은 15432 port 로 되어 있으므로 서비스 배포시 postgresql port 에 맞게 변경 필요** change jdbc url port from 15432 to 5432
- gdal 설치 및 환경변수 등록 install gdal and set environment variable
- rabbitmq 설치  및 환경변수 등록 install rabbitmq and set environment variable
- f4d converter 설치 install f4d converter
- geoserver tomcat 으로 배포 후 서비스 등록 
  deploy geoserver in tomcat and register tomcat as windows service
  - geoserver_data_dir 은 별도의 경로로 지정 set path geoserver_data_dir
  - jvm size min max 4G 설정 set jvm memory 4gigabyte
- mago3d-admin / mago3d-user 를 하나의 tomcat 에 각각의 서비스로 배포 후 서비스 등록
  deploy mago3d-admin/mago3d-user in tomcat and register tomcat as windows service
  - jvm size min max 4G 설정 set jvm memory 4gigabyte
  - admin/user 를 파일로 로깅  set file logging (admin/user)
- nginx 서비스 등록 및 mago3d tomcat 서버와 연동하고 정적 컨텐츠는 nginx 가 처리하도록 설정
  register nginx as windows service and set up tomcat with nginx
  static contents -> nginx
- mago3d-converter.jar 서비스 등록 register mago3d-converter.jar as windows service
  - 파일로 로깅 set file logging
- 테스트 test
  - mago3d-admin 에서 행정구역 레이어 등록 
    register 2d layer on mago3d-admin
  - mago3d-user 에서 3D 레이어 자동 변환 및 가시화 
    3d file converting and visualization on mago3d-user


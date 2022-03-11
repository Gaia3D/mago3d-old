## 설치 가이드 

- 설치 소프트웨어 

  - Java: OpenJDK 11.0.2(build 11.0.2+9)
  - Geoserver : 2.17.x

  - PostgresSQL : 12.x

  - PostGIS : 3.0.x

  - RabbitMQ: 3.8.x

  - Erlang : 23.x

  - Gdal 3.x or QGIS 3.10 이상

  - mago3D Converter : github 최신 버전([F4DConverter/install at master · Gaia3D/F4DConverter](https://github.com/Gaia3D/F4DConverter/tree/master/install))

  - tomcat 9.x

  - nginx 1.18.0

  - 시뮬레이션 기능을 사용할 경우 다음의 geoserver extension 들을 설치해야한다.

    - *WPS Extentions : 2.17.x (옵션)*

    - *OpenGXT + GeoTools Extensions : 2.16.x (옵션)*



- windows directory
- linux directory
  - /data
    - /postgres
    - /mago3d
    - /geoserver
    - /terrain
  - /home
    - /gaia3d
      - /mago3d
        - /tools
          - /mago3d-tomcat
          - /mago3d-converter
          - /f4dconverter
          - /java(yum 설치가 아닌 파일로 사용시 버전별 관리) 
          - /geoserver-tomcat 
        - /setup
        - /backup

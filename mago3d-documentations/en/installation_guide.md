## Installation Guide

- Software Installation

  - Java : OpenJDK 11.0.2 (build 11.0.2+9)

  - GeoServer : 2.17.x

  - PostgresSQL : 12.x

  - PostGIS : 3.0.x

  - RabbitMQ: 3.8.x

  - Erlang : 23.x

  - GDAL 3.x or QGIS 3.10 or newer

  - mago3D Converter : GitHub latest version([F4DConverter/install at master · Gaia3D/F4DConverter](https://github.com/Gaia3D/F4DConverter/tree/master/install))

  - Tomcat 9.x

  - NginX 1.18.0

  - You should install the following GeoServer extensions if you want to use the simulation functions.

    - *WPS Extentions : 2.17.x (option)*

    - *OpenGXT + GeoTools Extensions : 2.16.x (option)*



- Windows directory
- Linux directory
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
          - /java (when use as file not yum, per-version management) 
          - /geoserver-tomcat 
        - /setup
        - /backup

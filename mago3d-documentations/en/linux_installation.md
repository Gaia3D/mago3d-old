# 목차 
0. [Installation Version and Directories](https://github.com/endofcap/mago3D-Documentations/blob/main/installation_guide.md)
1. [Java](#1-java)
2. [PostgreSQL / PostGIS](#2-postgresql-postgis)
3. [GDAL](#3-gdal)
4. [RabbitMQ](#4-rabbitmq)
5. [GeoServer](#5-geoserver)
6. [mago3D](#6-mago3d)
7. [NginiX](#7-nginix) 
8. [Firewall and Service Management](#8-firewall-and-service-management)
9. [F4D Converter](#9-f4d-converter)

## 1. Java

- If your server is a standalone you can "global" install Java using Yum (Yellow dog Updater, Modified). If not, download and use Java files because the installation using Yum can affect other systems. Here we install Java using Yum.

- First, check if there is a Java pre-installed. You can do this with a rpm or yum command.

  ```
  rpm -qa | grep java
  ```
   
  Or
  
  ```
  yum list installed | grep java
  ```

  ![img](../images/L-Java-1.png)

- Before installing, check if the version of Java to install is in the repository.

  - ![img](../images/l-java-2.png)

- Installing Java

  - Java v.11

    ```
    sudo yum install -y java-11-openjdk
    ```

  - java v.16 (July 14th 2021)

    ```
    sudo yum install -y java-latest-openjdk
    ```

  - When downloading archive files (unzip them, then manage them by the version of Java)

    ```
    tar -xvf openjdk-11.0.2_linux-x64_bin
    ```

  - Setting Java environment variables (according to the installation path)

    ```
    vi ~/.bash_profile
    ```

    ```
    # JAVA_PATH
    export JAVA_HOME=/home/gaia3d/mago3d/tools/java/jdk-11.0.2
    export PATH=$PATH:$JAVA_HOME/bin
    export CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/*
    ```

    ```
    source ~/.bash_profile
    ```

- Managing the versions

  - Because there is a pre-installed Java, the "global" version of Java does not change even if you install v.11.

  - ![img](../images/l-java-3.png)

  - Using **alternatives** (a tool that can manage symbolic links), select and set a symbolic link Java v.11 out of installed versions.

    ```
    sudo alternatives --config java
    ```

  - ![img](../images/l-java-4.png)

    If you download a Java file without using Yum, you should create a separate symbolic link.

  - [How to quickly change the version of Java(JDK) on Ubuntu/centOS (update-alternatives)](https://wgtech.github.io/posts/2019/07/14/Change-the-JDK-using-update-alternatives/) 

    - Check all the installation locations of JDK.

      - Usually, the installation path of JDK is `/usr/lib/jvm`. (Need to check!)

    - Create a separate symbolic link. (You need to set it for not affecting development using sudo and chown options.)

      ```
      mkdir -R /opt/jdk
      ln -s /usr/lib/jvm/java-7-openjdk /opt/jdk/current
      ```

    - Register it using update-alternatives

      ```
      update-alternatives --install /usr/bin/java java /opt/jdk/current/bin/java 1
      update-alternatives --config java
      ```

> **How to use update-alternatives install options**
> ```
> alternatives --install <link> <name> <path> <priority>
> ```

## 2. PostgreSQL / PostGIS

- Adding repositories

  - epel repository : This community-based repository by Fedora Project provides the latest versions of various packages. When installing PostGIS and building F4DConverter it should be added before installing PostGIS. Because there are some packages which are dependent to epel repository such as libdap, hdf4, etc.

  - pgdg repository : This repository should be added for installing the latest version of PostgreSQL.

    ```
    sudo yum -y install epel-release
    sudo yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm
    ```

  - ![img](../images/p-1.png)

- Installing PostgreSQL

  - Check an installable version.

    ```
    yum list | grep postgresql12
    ```

    - ![img](../images/p-2.png)

  - Which version should we install?

    - There are many lists which you can install by versions; you can check the differences by versions at https://yum.postgresql.org/ .
    - Descriptions about installation items which are not listed at the above site can be found at Linux Package Search website https://pkgs.org/ .
      - postgresql12 : PostgreSQL client 
      - postgresql12-contrib : Includes various extensions.
      - postgresql12-server : Basic PostgreSQL v.12 Server Installation version.
      - postgresql12-tcl : TCL client library for PostgreSQL.

  - Install

    ```
    sudo yum install -y postgresql12-server
    ```

- Installing PostGIS

  - The name of the package to install is postgis{version_number}_{postgresql_version}.

    ```
    sudo yum install -y postgis30_12
    ```

- initdb

  - We created `/data/postgres` folder as root, so change owner & group of this folder as postgres.

    ```
    sudo chown -R postgres:postgres /data/postgres
    ```

    - ![img](../images/p-3.png)

  - After changing postgres account, run initdb to `/data/pg-data` path.

    ```
    sudo su postgres
    /usr/pgsql-12/bin/initdb -D /data/postgres/ -E UTF-8
    exit (logout; change back to gaia3d account)
    ```

    - ![img](../images/p-4.png)

    - Change PGDATA path of service file.

      ```
      sudo vim /usr/lib/systemd/system/postgresql-12.service
      ```

      - ![img](../images/p-5.png)

      - systemctl reload

        ```
        sudo systemctl daemon-reload 
        ```

      - Setup pruning : Enable table partition function to use it.

        ```
        sudo su postgres
        vim /data/postgres/postgresql.conf
        ```

        - ![img](../images/p-6.png)

      - Start postgresql & check status

        ```
        exit (change to gaia3d account)
        sudo systemctl start postgresql-12
        sudo systemctl status postgresql-12
        ```

        - ![img](../images/p-7.png)

- Test

  - Check data path of extensions and if the installation of PostGIS extensions is succeeded. 

    ```
    sudo su postgres
    psql -U posgres
    ```

    ```
    create extension postgis;
    select postgis_version();
    show data_directory;
    ```

    - ![img](../images/p-8.png)

## 3. GDAL

- Install gdal31, because when building f4d converter we use gdal31-devel. We use GDAL to insert * append shape data into DB using ogr2ogr.

  ```
  sudo yum install gdal31
  ```

- Adding ogr2ogr path

  - Add ogr2ogr path to PATH to let mago3d application use ogr2ogr.

    ```
    vim ~/.bash_profile 
    ```

  - ![img](../images/Gdal-1.png)

  - ![img](../images/gdal-2.png)

## 4. RabbitMQ

- RabbitMQ is created by a programming language "Erlang", you should install Erlang before installing RabbitMQ. You can check the version of Erlang required by each RabbitMQ version at the official RabbitMQ homepage. (https://www.rabbitmq.com/which-erlang.html)

- Installing erlnag repository

  ```
  sudo yum install http://packages.erlang-solutions.com/erlang-solutions-1.0-1.noarch.rpm
  ```

  - ![img](../images/rabbitmq-1.png)

- Installing erlang & rabbitmq

  ```
  sudo yum install erlang
  sudo yum install https://dl.bintray.com/rabbitmq/all/rabbitmq-server/3.8.5/rabbitmq-server-3.8.5-1.el7.noarch.rpm
  ```

- Installing management plugin & starting rabbitmq

  ```
  sudo /usr/sbin/rabbitmq-plugins enable rabbitmq_management
  sudo systemctl start rabbitmq-server
  ```

- Go to RabbitMQ Administration page ([http://localhost:15672](http://localhost:15672/)).
- Login ID and password are both "guest".

![img](../images/1cf84fa2-a2e4-47ad-b841-7dbfbca3ebd8.png)

- Click the [Exchange] tap at the topside.

- Click [Add a new exchanges] at the bottomside and enter as the following screen. Then click [Add exchange] button.

  - *Name : f4d.converter*
  - *Type : topic*
  - *Durability : Durable*

  ![img](../images/88198c22-8c7c-4156-990e-cdf72b6cbe30.png)

- Click the [Queues] tap at the topside.

- Click [Add a new queue] at the bottom and enter as the following screen. Then click [Add queue] button.

  ![img](../images/ff5e92b7-3c79-45dc-93b6-2d756de039b0.png)

- When installing RabbitMQ for the first time, "guest" account has the permissions as Administrator. But if you access the application with this account, a connection error occurs.

- Click [Add a user] at the bottomside of Admin menu to create another admin account and enter as the following screen. Then click [Add a user] button. (ID and password are same.)

  - Username : mago3d

  - Password : mago3d 

    ![img](../images/b2448bde-1ea4-44b6-abe2-7082ae64cb97.png)

- Click the newly created mago3D account under the guest account.

- Create "Current permissions" and "Current topic permissions" as the following screen. Enter the password (mago3d) at [Update this user] and click [Update user] button.

  ![](../images/d7b0e288-9c6a-4f45-af18-461a693c6a9a.png)


## 5. GeoServer 

- Tomcat

  - Download & Unzip

    ```
    wget -P /home/gaia3d/mago3d/setup/ https://downloads.apache.org/tomcat/tomcat-9/v9.0.41/bin/apache-tomcat-9.0.41.tar.gz
    cd /home/gaia3d/mago3d/tools/
    tar -xvzf /home/gaia3d/mago3d/setup/apache-tomcat-9.0.41.tar.gz -C .
    mv apache-tomcat-9.0.41 geoserver-tomcat
    rm -rf geoserver-tomcat/webapps/*
    ```

  - Setting cors

    ```
    vim /home/gaia3d/mago3d/tools/geoserver-tomcat/conf/web.xml
    ```

    - Insert the settings right above the </web-app> at the bottom.

      ```
      <filter>
        <filter-name>CorsFilter</filter-name>
        <filter-class>org.apache.catalina.filters.CorsFilter</filter-class>
        <init-param>
          <param-name>cors.allowed.origins</param-name>
          <param-value>*</param-value>
        </init-param>
        <init-param>
          <param-name>cors.allowed.methods</param-name>
          <param-value>GET,POST,HEAD,OPTIONS,PUT</param-value>
        </init-param>
        <init-param>
          <param-name>cors.allowed.headers</param-name>
          <param-value>Content-Type,X-RequestedWith,accept,Origin,Access-Control-Request-Method,Access-ControlRequest-Headers</param-value>
        </init-param>
        <init-param>
          <param-name>cors.exposed.headers</param-name>
          <param-value>Access-Control-Allow-Origin,Access-Control-AllowCredentials</param-value>
        </init-param>
        <init-param>
          <param-name>cors.preflight.maxage</param-name>
          <param-value>10</param-value>
        </init-param>
      </filter>
      <filter-mapping>
       <filter-name>CorsFilter</filter-name>
       <url-pattern>/*</url-pattern>
      </filter-mapping>
      ```

- GeoServer

  ```
  cd /home/gaia3d/tools/geoserver-tomcat/webapps/
  wget http://sourceforge.net/projects/geoserver/files/GeoServer/2.17.3/geoserver-2.17.3-war.zip
  unzip geoserver-2.17.3-war.zip 
  find . ! -name "geoserver.war" -delete
  ```

- Registering service

  ```
  sudo vim /usr/lib/systemd/system/mago3d-geoserver.service
  ```

  ```
  [Unit]
  Description=mago3d-geoserver
  After=syslog.target
  
  [Service]
  Type=forking
  User=gaia3d
  Group=gaia3d
  ExecStart=/home/gaia3d/mago3d/tools/geoserver-tomcat/bin/startup.sh
  ExecStop=/home/gaia3d/mago3d/tools/geoserver-tomcat/bin/shutdown.sh
  SuccessExitStatus=143
  
  [Install]
  WantedBy=multi-user.target
  ```

  ```
  sudo systemctl daemon-reload
  ```

- Changing data_dir & Setting jvm size

  ```
  vim /home/gaia3d/mago3d/tools/geoserver-tomcat/bin/setenv.sh
  ```

  ```
  export CATALINA_OPTS="$CATALINA_OPTS -Xms4096m"
  export CATALINA_OPTS="$CATALINA_OPTS -Xmx4096m"
  export GEOSERVER_DATA_DIR=/data/geoserver
  ```

- Changing data_dir permissions

  ```
  sudo chown -R gaia3d:gaia3d /data/geoserver
  ```

- Starting service

  ```
  sudo systemctl start mago3d-geoserver
  ```

  - Setting logs 

    - When starting GeoServer, logging.xml file is created in data folder. Add the following settings in that file.

      ```
      <logging>
        <level>DEFAULT_LOGGING.properties</level>
      <location>/home/gaia3d/mago3d/tools/geoserver-tomcat/logs/geoserver.log</location>
        <stdOutLogging>true</stdOutLogging>
      </logging>
      ```

    - When restarting GeoServer, a logs folder is created. Edit DEFAULT_LOGGING.properties file in  logs folder as follows.

      ```
      log4j.appender.geoserverlogfile.MaxBackupIndex=30 
      log4j.appender.geoserverlogfile.MaxFileSize=200MB 
      ```

    - ![img](../images/geoserver-1.png)

    - All the logs will be accumulated in one catalina.out file. As the server operation is going on, this file will become too large. Modify to not create catalina.out file. 

      ```
      vim /home/gaia3d/mago3d/tools/geoserver-tomcat/bin/catalina.sh
      ```

      ```
      CATALINA_OUT=/dev/null
      ```

    - ![img](../images/geoserver-2.png)

## 6. mago3D 

- Creating folders required for the service

  ```
  sudo chown -R gaia3d:gaia3d /data/mago3d
  ```

  ```
  mkdir /data/mago3d/f4d/
  mkdir -p /data/mago3d/f4d/data-logs/
  mkdir -p /data/mago3d/f4d/infra/data/
  mkdir -p /data/mago3d/f4d/service/data/
  mkdir /data/mago3d/upload-data/
  mkdir /data/mago3d/bulk-upload-data/
  mkdir /data/mago3d/smart-tiling/
  mkdir /data/mago3d/attribute/
  mkdir -p /data/mago3d/mago3d-admin/upload/layer/
  mkdir -p /data/mago3d/mago3d-admin/export/temp/
  mkdir -p /data/mago3d/guide/f4d/
  ```

- Creating DB and tables

  - Copy `doc/database` folder of mago3D project to the server. Here we use pscp.exe as the copy tool.

    ```
    pscp.exe -r D:\source\mago3d-CMS\doc\database gaia3d@ServerIP:/tmp
    ```

  - Change the ownership of the duplicated folder to postgres. 

    ```
    sudo chown -R postgres:postgres /tmp/database
    ```

  - Run SQL files using PostgreSQL.

    ```
    sudo su postgres
    cd /tmp/database
    ```

    - Creating database : Run database.sql and check whether the database is created or not by connecting DB with psql.

      ```
      psql -U postgres -f database.sql
      psql -U postgres 
      \l
      \q
      ```

      - ![img](../images/mago-1.png)

      - Installing PostGIS extension

        ```
        psql -U postgres -d mago3d
        create extension postgis;
        \q
        ```

    - ddl 

      ```
      psql -U postgres -d mago3d -f ddl/sequence/sequence.sql
      ```

      - There are so many files to run; run them at once by combining them to one file with Linux commands. **Do not run constraint/reference.sql**.

        ```
        ls ddl/*.sql | xargs cat > ddl.sql
        psql -U postgres -d mago3d -f ddl.sql
        ```

    - dml

      ```
      psql -U postgres -d mago3d -f dml/insert.sql
      psql -U postgres -d mago3d -f dml/update.sql
      ```

    - index 

      ```
      psql -U postgres -d mago3d -f index/access_log.sql
      psql -U postgres -d mago3d -f index/data_info_log.sql 
      psql -U postgres -d mago3d -f index/index.sql
      ```

- Building Project

  - Cleaning & building at root path of the project

    ```
    gradlew.bat clean
    gradlew.bat build -Pprofile=develop
    ```

  - Copying files

    ```
    pscp.exe D:\source\mago3d-CMS\mago3d-admin\build\libs\mago3d-admin-0.0.1-SNAPSHOT.war gaia3d@ServerIP:/home/gaia3d/mago3d/setup/
    pscp.exe D:\source\mago3d-CMS\mago3d-user\build\libs\mago3d-user-0.0.1-SNAPSHOT.war gaia3d@ServerIP:/home/gaia3d/mago3d/setup/
    pscp.exe D:\source\mago3d-CMS\mago3d-converter\build\libs\mago3d-converter-0.0.1-SNAPSHOT.jar gaia3d@ServerIP:/home/gaia3d/mago3d/setup/
    ```

- mago3d-tomcat

  - Install GeoServer using downloaded Tomcat archive file.
  
    ```
    cd /home/gaia3d/mago3d/tools/
    tar -xvzf /home/gaia3d/mago3d/setup/apache-tomcat-9.0.41.tar.gz -C .
    mv apache-tomcat-9.0.41 mago3d-tomcat
    rm -rf mago3d-tomcat/webapps/*
    ```

  - Create folders for Tomcat and unzip WAR files.

    ```
    mkdir -p /home/gaia3d/mago3d/tools/mago3d-tomcat/source/mago3d-admin
    mkdir /home/gaia3d/mago3d/tools/mago3d-tomcat/source/mago3d-user
    
    unzip /home/gaia3d/mago3d/setup/mago3d-admin-0.0.1-SNAPSHOT.war -d /home/gaia3d/mago3d/tools/mago3d-tomcat/source/mago3d-admin/
    unzip /home/gaia3d/mago3d/setup/mago3d-user-0.0.1-SNAPSHOT.war -d /home/gaia3d/mago3d/tools/mago3d-tomcat/source/mago3d-user/
    ```

  - Edit server.xml file. Modify it to be able to service both admin / user with one Tomcat instance.

    - GeoServer 8005 port is already being used, so shutdown port should use another port.

    - ![img](../images/mago-2.png)

      ```
      <Service name="Catalina">
      
          <Connector port="10080" protocol="HTTP/1.1"
                     connectionTimeout="20000"
                     redirectPort="8444" />
         
          <Engine name="Catalina" defaultHost="localhost">
          
            <Realm className="org.apache.catalina.realm.LockOutRealm">
              
              <Realm className="org.apache.catalina.realm.UserDatabaseRealm"
                     resourceName="UserDatabase"/>
            </Realm>
      
            <Host name="localhost"  appBase="webapps"
                  unpackWARs="true" autoDeploy="false">
           
              <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                     prefix="localhost_access_log" suffix=".txt"
                     pattern="%h %l %u %t &quot;%r&quot; %s %b" />
      		<Context docBase="/home/gaia3d/mago3d/tools/mago3d-tomcat/source/mago3d-user" path="" reloadable="false" />
            </Host>
          </Engine>
        </Service>
        
        <Service name="Catalina2">
      
          <Connector port="19090" protocol="HTTP/1.1"
                     connectionTimeout="20000"
                     redirectPort="8445" />
      
             <Engine name="Catalina2" defaultHost="localhost">
          
            <Realm className="org.apache.catalina.realm.LockOutRealm">
              
              <Realm className="org.apache.catalina.realm.UserDatabaseRealm"
                     resourceName="UserDatabase"/>
            </Realm>
      
            <Host name="localhost"  appBase="webapps"
                  unpackWARs="true" autoDeploy="false">
           
              <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                     prefix="localhost_access_log2" suffix=".txt"
                     pattern="%h %l %u %t &quot;%r&quot; %s %b" />
      		<Context docBase="/home/gaia3d/mago3d/tools/mago3d-tomcat/source/mago3d-admin" path="" reloadable="false" />
            </Host>
          </Engine>
        </Service>
      ```

  - Cache resource

    ```
    vim /home/gaia3d/mago3d/tools/mago3d-tomcat/conf/context.xml
    ```

    - Add the follwing line in the "context" tag of context.xml file.

    ```
    <Resources cachingAllowed="true" cacheMaxSize="100000"/>
    ```

  - Registering service

    ```
    sudo vim /usr/lib/systemd/system/mago3d-tomcat.service
    ```

    ```
    [Unit]
    Description=mago3d-tomcat
    After=syslog.target
    
    [Service]
    Type=forking
    User=gaia3d
    Group=gaia3d
    ExecStart=/home/gaia3d/mago3d/tools/mago3d-tomcat/bin/startup.sh
    ExecStop=/home/gaia3d/mago3d/tools/mago3d-tomcat/bin/shutdown.sh
    SuccessExitStatus=143
    
    [Install]
    WantedBy=multi-user.target
    ```

    ```
    sudo systemctl daemon-reload
    ```

  - Setting jvm size

    ```
    vim /home/gaia3d/mago3d/tools/mago3d-tomcat/bin/setenv.sh
    ```

    ```
    export CATALINA_OPTS="$CATALINA_OPTS -Xms4096m"
    export CATALINA_OPTS="$CATALINA_OPTS -Xmx4096m"
    ```

  - Disabling catalina.out file

    ```
    vim /home/gaia3d/mago3d/tools/mago3d-tomcat/bin/catalina.sh
    ```

    ```
    CATALINA_OUT=/dev/null
    ```

    - ![img](../images/mago-3.png)

  - Starting service

    ```
    sudo systemctl start mago3d-tomcat
    ```

- mago3d-converterk

  - Since it is not a WAR file but JAR file that runs standalone, you just register and use service without using Tomcat.

    ```
    cp /home/gaia3d/mago3d/setup/mago3d-converter-0.0.1-SNAPSHOT.jar /home/gaia3d/mago3d/tools/mago3d-converter/
    ```

    ```
    sudo vim /usr/lib/systemd/system/mago3d-converter.service
    ```

    ```
    [Unit]
    Description=mago3d-converter
    After=syslog.target
    
    [Service]
    Type=simple
    User=gaia3d
    Group=gaia3d
    WorkingDirectory=/home/gaia3d/mago3d/tools/mago3d-converter
    PIDFile=/home/gaia3d/mago3d/tools/mago3d-converter/bin/app.pid
    Environment=DISPLAY=:99
    Environment=LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/gaia3d/mago3d/tools/f4dconverter/lib:/usr/local/lib
    ExecStart=/bin/bash -c "exec java -Xmx4096m -jar /home/gaia3d/mago3d/tools/mago3d-converter/mago3d-converter-0.0.1-SNAPSHOT.jar"
    
    SuccessExitStatus=143
    
    [Install]
    WantedBy=multi-user.target
    ```

  - Xvfb(X Virtual Frame Buffer) 

    - Xvfb is a **Display server** in case that you want to access local or remote server using just a console and run window console program, without using X. `Xvfb` does graphic operations on memory level while other Display servers outputs displays. Thus the server (computer) that runs `Xvfb` does not require any output or input device to visualize service.([http://plus4070.github.io/nhn entertainment devdays/2016/03/27/Xvfb/](http://plus4070.github.io/nhn%20entertainment%20devdays/2016/03/27/Xvfb/))

    - Installing Xvfb
      - Run `sudo yum list | grep -i xvfb` command. Install the items those are the search results of xvfb from the respository.
      
      - ![img](../images/mago-4.png)
      
      - ```
        sudo yum install xorg-x11-server-Xvfb
        ```
      
    - Registering Xvfb service
      - **DISPLAY value of mago3d-converter service script should be same with the argument value in /usr/bin/Xvfb of xvfb service script.**
      
        ```
        sudo vim /usr/lib/systemd/system/xvfb.service
        ```
      
        ```
        [Unit]
        Description=X Virtual Frame Buffer Service
        After=network.target
        
        [Service]
        ExecStart=/usr/bin/Xvfb :99 -ac -screen 0 1920x1080x24 -nolisten tcp
        
        [Install]
        WantedBy=multi-user.target
        ```
      
    - Starting service

      ```
      sudo systemctl daemon-reload
      
      sudo systemctl start mago3d-converter
      sudo systemctl start xvfb
      ```

## 7. NginX

- Adding repository

  ```
  sudo vim /etc/yum.repos.d/nginx.repo
  ```

  ```
  [nginx] 
  name=nginx repo 
  baseurl=http://nginx.org/packages/centos/7/$basearch/ 
  gpgcheck=0 
  enabled=1
  ```

  - Check if the repository is well added using `yum repolist` command.
    - ![img](../images/nginix-1.png)

- Installing NginX

  ```
  sudo yum install nginx
  ```

  - Reference 
    - The location of NginX executable file : `/usr/sbin/nginx`
    - The location of NginX log file : `/var/log/nginx/`

- Setting `/etc/nginx/nginx.conf` file (virtual hosting setup)

  - **You should be careful when editing nginx.conf file, because nginx.conf of Windows and nginx.conf of Linux are different from each other.** In Windows, nginx.conf file has all the settings while in Linux the settings are separated between nginx.conf and conf.d/default.conf files. Here we let nginx.conf file have all the settings.

  - Calls **listen port** receives are passed to URL set as **upstream**.

  - **mime.types** in nginx folder must be included. If not, when NginX processes static files, it passes them as texts; CSS or images will be broken.

  - **client_max_body_size** must be setup because the default file upload size is small. If not setting this option, when uploading file at URL which NginX accesses, an error will occur.

  - Be aware **when setting paths, use 2 backslashes in Windows and 1 slash in Linux** to write paths.

    ```
    user  nginx;
    worker_processes  1;
    
    error_log  /var/log/nginx/error.log warn;
    pid        /var/run/nginx.pid;
    
    
    events {
        worker_connections  1024;
    }
    
    http {
            include         mime.types;
            #default_type  application/octet-stream;
    
            #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
        #                  '$status $body_bytes_sent "$http_referer" '
        #                  '"$http_user_agent" "$http_x_forwarded_for"';
    
            #access_log  logs/access.log  main;
    
            sendfile        on;
            client_max_body_size 4G;
            client_body_buffer_size 256k;
    
            #tcp_nopush     on;
    
            #keepalive_timeout  0;
            client_body_timeout 10;
            client_header_timeout 10;
            keepalive_timeout  15;
            send_timeout 10;
    
            #gzip  on;
    
            upstream mago3d_user {
                    ip_hash;
                    server 127.0.0.1:10080;
            }
    
            upstream mago3d_admin {
                    ip_hash;
                    server 127.0.0.1:19090;
            }
    
            server {
                    listen       80;
                    server_name  localhost;
    
                    #charset koi8-r;
    
                    #access_log  logs/host.access.log  main;
    
                    location / {
                            #add_header 'Access-Control-Allow-Origin' '*';
                            proxy_set_header    Host $http_host;
                            proxy_set_header    X-Real-IP $remote_addr;
                            proxy_set_header    X-Forwarded-For $proxy_add_x_forwarded_for;
                            proxy_set_header    X-Forwarded-Proto $scheme;
                            proxy_set_header    X-NginX-Proxy true;
    
                            proxy_pass http://mago3d_user;
                            proxy_redirect      off;
                            charset utf-8;
                    }
    
                    location ~ ^/(css|externlib|images|js)/ {
                            root "/etc/nginx/html/mago3d-user";
                    }
    
                    #error_page  404              /404.html;
            # redirect server error pages to the static page /50x.html
    
                    error_page   500 502 503 504  /50x.html;
                    location = /50x.html {
                            root   html;
                    }
            }
    
            server {
                    listen       9090;
                    server_name  localhost;
    
                    #charset koi8-r;
                    #access_log  logs/host.access.log  main;
    
                    location / {
                            #add_header 'Access-Control-Allow-Origin' '*';
                            proxy_set_header    Host $http_host;
                            proxy_set_header    X-Real-IP $remote_addr;
                            proxy_set_header    X-Forwarded-For $proxy_add_x_forwarded_for;
                            proxy_set_header    X-Forwarded-Proto $scheme;
                            proxy_set_header    X-NginX-Proxy true;
    
                            proxy_pass http://mago3d_admin;
                            proxy_redirect      off;
                            charset utf-8;
                    }
    
                    location ~ ^/(css|externlib|images|js)/ {
                            root "/etc/nginx/html/mago3d-admin";
                    }
    
                    #error_page  404              /404.html;
            # redirect server error pages to the static page /50x.html
                    error_page   500 502 503 504  /50x.html;
                    location = /50x.html {
                            root   html;
                    }
            }
    }
    ```

- Creating and copying static resource folder

  ```
  sudo mkdir -p /etc/nginx/html/mago3d-admin
  sudo mkdir  /etc/nginx/html/mago3d-user
  
  sudo cp -R /home/gaia3d/mago3d/tools/mago3d-tomcat/source/mago3d-admin/WEB-INF/classes/static/* /etc/nginx/html/mago3d-admin/
  sudo cp -R /home/gaia3d/mago3d/tools/mago3d-tomcat/source/mago3d-user/WEB-INF/classes/static/* /etc/nginx/html/mago3d-user/
  ```

- Adding semanage port

  - In case the following error occurs because semanage port does not have 9090:

    - ![img](../images/nginix-2.png)

  - Adding semanage port

    - There may not be semanage tools if you do not install devtools when installing CentOS. In this case, you have to install semanage tools separately.

    - Add the following rules in order to NginX be able to access network resources.

      ```
      sudo semanage port -m -t http_port_t -p tcp 9090
      sudo setsebool -P httpd_can_network_connect 1
      ```

- Starting service

  ```
  sudo systemctl start nginx
  ```

## 8. Firewall and Service Management 

- Firewall 

  - Open the port that supports external access. You can open the port using firewall command or editing zone XML file in `/etc/firewalld/zones/`. Here we use firewall command. 

  - Open only the essential port that supports external access.

    ```
    sudo firewall-cmd --permanent --zone=public --add-port=80/tcp
    sudo firewall-cmd --permanent --zone=public --add-port=8080/tcp
    sudo firewall-cmd --permanent --zone=public --add-port=9090/tcp
    
    sudo firewall-cmd --reload
    sudo firewall-cmd --list-all
    ```

- Service 

  - Configure that service can be restarted even if the server is rebooted.

    ```
    sudo systemctl enable postgresql-12
    sudo systemctl enable rabbitmq-server
    sudo systemctl enable xvfb
    sudo systemctl enable nginx
    sudo systemctl enable mago3d-tomcat
    sudo systemctl enable mago3d-converter
    sudo systemctl enable mago3d-geoserver
    
    sudo systemctl list-unit-files --type service | grep enabled (checking auto-restart items list)
    ```

    

## 9. F4D Converter 

- Environment
  - OS : CentOS 7.9 (sudo cat /etc/redhat-release)
  - Build Tool : CMake 3.17.3
  - Compiler : GCC 7.3.1 20180303 (Red Hat 7.3.1-5)
  - Libraries 

|  **Library**   | **Installation version** |   **Installation Type**    |
| -------------- | ------------------------ | -------------------------- |
| boost          | 1.71.0                   | Package installation       |
| assimp         | 4.1.0                    | Source installation (auto) |
| glad           | 0.1.11                   | Source installation (auto) |
| glfw           | 3.2.1                    | Source installation (auto) |
| glm            | 0.9.9.3                  | Source installation (auto) |
| stb            | -                        | Source installation (auto) |
| ifcplusplus    | 1.0                      | Source installation (auto) |
| jsoncpp        | 0.10.7                   | Source installation (auto) |
| proj.4         | 4.9.3                    | Package installation       |
| xerces-c       | 3.2.2-11                 | Package installation       |
| libcitygml     | 2.0.9                    | Source installation (auto) |
| gdal           | 3.1                      | Package installation       |
| libgeotiff     | 1.4.2-10                 | Package installation       |
| libLAS         | 1.8.1-3                  | Source installation (auto) |

- Building environment configuration

  - Installing GCC compiler and related tools

    - Installing Software Collection package

      ```
      sudo yum -y install centos-release-scl
      ```

    - Installing GCC 7 using devtoolset-7 Software Collection

      ```
      sudo yum -y install devtoolset-7
      ```

    - **Adding repository**

      - EFEL and PGDG repositories are required. If you installed PostgreSQL then you can skip this step. If you did not install PostgreSQL and installed f4dconverter as a standalone, refer the adding repository portion from installation processes of PostgreSQL and add EFEL and PGDG repositories.

  - Configuring the shell environment in which you can use the tools included in devtoolset-7.

    - This is a configuration of the shell environment for building. If you have to build just once and not again then you may do this temporarily. If not, you should apply this step to .bash_profile and make it permanent.

    - Applying just once

      ```
      source scl_source enable devtoolset-7
      ```

    - Applying permanently

      ```
      vim ~/.bash_profile
      ```

      - ![img](../images/f4d-1.png)

      - After modifying .bash_profile, you can apply it by logout and login (a new session) or running the following command.

        ```
        . ~/.bash_profile
        ```

      - Check if gcc and g++ versions are displayed as the following screen after you applied the modification.

      - ![img](../images/f4d-2.png)

- Installing development tools

  - CMake

    - CMake applied at the default repository of CentOS 7 is version 2.8. You should install version 3.9 or later in EFEL repository. You may install these two different versions, since the names of the each package are also different. **But remember that the command of v.2 is cmake, v.3 cmake3.**

    - Checking the installable version of CMake

      ```
      yum list | grep cmake
      ```

      - ![img](../images/f4d-3.png)

    - Installing CMake

      ```
      sudo yum -y install cmake3
      ```

      - Checking installation

        ```
        cmake3 --version
        ```

        - ![img](../images/f4d-4.png)

  - git

    - Download and build newer version, because the default git version of CentOS is very old.

      ```
      <Dependency related Installation>
      sudo yum groupinstall 'Development Tools' (Skip this line if you install "global" development tools)
      sudo yum install curl-devel expat-devel gettext-devel openssl-devel zlib-devel perl-CPAN perl-devel
      
      <compile & install>
      cd /home/gaia3d/mago3d/setup/
      wget https://mirrors.edge.kernel.org/pub/software/scm/git/git-2.18.0.tar.gz
      tar xf git-2.18.0.tar.gz
      cd git-2.18.0/
      
      make configure
      ./configure --prefix=/usr/local
      
      make all
      
      sudo make install
      ```

      - You can check the newer version of git if you logout and login to a new session.
        - ![img](../images/f4d-5.png)

- Dependency Installation

  - Boost 

    ```
    sudo yum -y install boost-devel
    ```

  - GLFW

    - Installing XWindow library packages

      ```
      sudo yum -y install libXrandr-devel libXinerama-devel libXcursor-devel libXi-devel
      ```

    - Installing Mesa 3D graphic library packages

      ```
      sudo yum -y install mesa-libGL-devel mesa-libGLU-devel
      ```

  - Proj4

    ```
    sudo yum -y install proj71-devel
    ```

  - Xerces

    ```
    sudo yum -y install xerces-c-devel
    ```

  - GDAL

    - Skip this if you installed gdal31 from PostgreSQL installation.

      ```
      sudo yum -y install gdal31-devel geos38
      ```

  - Geotiff

    ```
    sudo yum -y install libgeotiff-devel
    ```

  - libLAS

    - Check the version of the dependency libraries.

      - Boost 1.38.0 or later : Required dependency library, used when building

        - Checking the version

          ```
          yum list installed | grep boost
          ```

          ![img](../images/f4d-6.png)

        - If CMake can not find Boost library, the values of Boost_INCLUDE_DIR and Boost_LIBRARY_DIRS are required to be set.

      - (optional) GDAL 1.7 or later : Optional but used when building

        - Checking the version

          ```
          yum list installed | grep gdal
          ```

          ![img](../images/f4d-7.png)

      - (optional) libgeotiff 1.3.0 or later : Optional but used when building

        - Checking the version

          ```
          yum list installed | grep libgeotiff
          ```

          ![img](../images/f4d-8.png)

    - Installing sources

      - Installing Zlib package (skip this if you installed the newer version of git)

        ```
        sudo yum -y install zlib-devel
        ```

      - Registering GDAL path environment variable

        - It is required only when building, use it just once and do not register it to .bash_profile.

          ```
          export GDAL_ROOT=/usr/gdal31
          ```

      - Downloading and unzipping source codes

        ```
        cd /home/gaia3d/mago3d/setup
        wget http://download.osgeo.org/liblas/libLAS-1.8.1.tar.bz2
        tar -xvf libLAS-1.8.1.tar.bz2
        ```

      - Changing location and creating build path

        ```
        cd libLAS-1.8.1
        mkdir build
        cd build
        ```

      - Creating build environment

        ```
        cmake3 .. -DGEOTIFF_INCLUDE_DIR="/usr/include/libgeotiff"
        ```

      - Running build, test and install

        ```
        make
        LD_LIBRARY_PATH=$PWD/bin/Release make test
        sudo make install
        ```

- Building and checking source codes

  - Downloading source codes

    - Use --recursive option to download source codes with Git submodule.

      ```
      cd /home/gaia3d/mago3d/setup
      git clone --recursive https://github.com/Gaia3D/NewF4DConverter.git
      ```

  - Changing location and creating build path

    ```
    cd NewF4DConverter
    mkdir build
    cd build
    ```

  - Creating build environment

    - Use installation location option (CMAKE_INSTALL_PREFIX) to install at `/home/gaia3d/mago3d/tools/f4dconverter`.

    - **Settings of cmake are changing due to DPROJ4_INCLUDE_DIR and DPROJ4_INCLUDE_DIRS paths. (Need to find why.) For the meantime, run "make" with DPROJ4_INCLUDE_DIR and if it fails, run "make" again with DPROJ4_INCLUDE_DIRS.**

      ```
      cmake3 .. -DCMAKE_INSTALL_PREFIX=/home/gaia3d/mago3d/tools/f4dconverter -DPROJ4_INCLUDE_DIRS=/usr/proj71/include -DPROJ4_LIBRARY=/usr/proj71/lib/libproj.so -DGDAL_CONFIG=/usr/gdal31/bin/gdal-config
      ```

  - Running build and install 

    - The speed of building can be improved by splitting jobs into many, like -j4.

      ```
      make
      make install
      ```

  - Copying Proj library resource files

    - mago3D will read resources from proj folder in the location of F4DConverter executable file instead of the location of default resources Proj library. Thus you need to copy the resources folder of Proj library to the location of F4DConverter executable file.

      ```
      cp -r /usr/proj71/share/proj /home/gaia3d/mago3d/tools/f4dconverter/bin
      ```

  - Adding path

    ```
    vim ~/.bash_profile
    
    export LD_LIBRARY_PATH=/home/gaia3d/mago3d/tools/f4dconverter/lib:/usr/local/lib:$LD_LIBRARY_PATH
    export PATH=/home/gaia3d/mago3d/tools/f4dconverter/bin/:$PATH
    
    . ~/.bash_profile
    ```

    ![img](../images/f4d-9.png))

  - Check if it runs properly.

    ```
    F4DConverter
    ```

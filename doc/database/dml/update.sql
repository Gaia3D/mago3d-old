update geopolicy set
	geoserver_data_url = 'http://localhost:18080/geoserver',
	geoserver_data_workspace = 'mago3d',
	geoserver_data_store ='mago3d',
	geoserver_user ='admin',
	geoserver_password = 'geoserver';

-- 적절한 위치로 수정해야 함
update geopolicy
set init_longitude = '126.71899000749444',
    init_latitude = '37.36771484526041';
commit;

update geopolicy set
	geoserver_data_url = 'http://localhost:18080/geoserver',
	geoserver_data_workspace = 'mago3d',
	geoserver_data_store ='mago3d',
	geoserver_user ='admin',
	geoserver_password = 'geoserver';

-- 적절한 위치로 수정해야 함
update geopolicy
set init_longitude = '127.00598139968887',
    init_latitude = '37.44829387479118';
commit;

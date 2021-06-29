-- 데이터 그룹 (알파돔, 시립대)
INSERT INTO data_group(data_group_id, data_group_key, data_group_name, data_group_path, data_group_target, sharing, user_id, ancestor, parent, depth,
                       view_order, children, basic, available, tiling, data_count, location, altitude, metainfo)
VALUES
(50000, 'alphadom', 'alphadom', 'infra/data/alphadom/', 'admin', 'common', 'admin', 50000, 0, 1, 2, 0, false, true, false, 1, ST_GeomFromText('POINT(127.11209614609372 37.394169200581445)', 4326), 10.45, TO_JSON('{"isPhysical": false}'::json)),
(50001, 'uos21c', 'uos21c', 'infra/data/uos21c/', 'admin', 'common', 'admin', 50001, 0, 1, 2, 0, false, true, false, 1, ST_GeomFromText('POINT(127.11209614609372 37.394169200581445)', 4326), 10.45, TO_JSON('{"isPhysical": false}'::json));

-- 데이터 (알파돔, 시립대)
INSERT INTO data_info(data_id, data_group_id, converter_job_id, data_key, data_name, data_type, sharing, user_id, mapping_type, location, altitude, heading,
                      pitch, roll, metainfo, status, attribute_exist, object_attribute_exist)
VALUES
(5000000, 50000, NULL, 'Alphadom_IndoorGML_data', '알파돔', 'indoorgml', 'common', 'admin', 'origin', ST_GeomFromText('POINT(127.11209614609372 37.394169200581445)', 4326), 10.45, 0, 0, 0, '{"isPhysical": true, "heightReference": "clampToGround", "floors" : [7,8,9,10,11,12,13,14,15,16,17,18,19,20,21]}', 'use', false, false),
(5000001, 50001, NULL, 'admin_20201013064147_346094873669678', '시립대', 'indoorgml', 'common', 'admin', 'origin', ST_GeomFromText('POINT (127.05851812380287 37.58321465738111)', 4326), 0, 0, 0, 0, '{"isPhysical": true, "heightReference": "clampToGround", "floors" : [1]}', 'use', false, false);

-- MICRO SERVICE 등록
INSERT INTO micro_service (micro_service_id, micro_service_key, micro_service_name, micro_service_type, server_ip, url_scheme, url_host, url_port, url_path,
                           status, available, description)
VALUES
(NEXTVAL('micro_service_seq'), 'SENSOR_THINGS', 'SensorThingsAPI', 'sensor-things', 'iot.openindoormap.io', 'http', 'iot.openindoormap.io', 80, 'v1.0/', 'up', true, 'SensorThingsAPI 서비스');
-- FK, Index 는 별도 파일로 분리. 맨 마지막에 작업 예정
drop table if exists micro_service cascade;
drop table if exists micro_service_log cascade;
drop table if exists micro_service_log_2021 cascade;
commit;

-- 서비스 관리
create table micro_service (
	micro_service_id			        int,
	micro_service_key				    varchar(30),
	micro_service_name				    varchar(60)			not null,
	micro_service_type				    varchar(30)			not null,
	server_ip					        varchar(45)			not null,
	api_key						        varchar(256),
	url_scheme					        varchar(10)			not null,
	url_host					        varchar(256)		not null,
	url_port					        int,
	url_path					        varchar(256),
	status						        varchar(20)			default 'use',
	available					        boolean				default true,
	description					        varchar(256),
	last_health_date                    timestamp with time zone,
	update_date				            timestamp with time zone,
	insert_date				            timestamp with time zone			default now(),
	constraint micro_service_pk primary key (micro_service_id)
);

comment on table micro_service is '서비스 관리';
comment on column micro_service.micro_service_id is '고유키';
comment on column micro_service.micro_service_key is '서비스 키';
comment on column micro_service.micro_service_name is '서비스명';
comment on column micro_service.micro_service_type is '서비스 유형. cache(캐시 Reload), simulation, sensor-things';
comment on column micro_service.server_ip is '서버 IP';
comment on column micro_service.api_key is 'API KEY';
comment on column micro_service.url_scheme is '사용할 프로토콜';
comment on column micro_service.url_host is '호스트';
comment on column micro_service.url_port is '포트';
comment on column micro_service.url_path is '경로, 리소스 위치';
comment on column micro_service.status is '상태. up : 실행, down : 정지, unknown : 알수 없음';
comment on column micro_service.available is 'true : 사용, false : 사용안함';
comment on column micro_service.description is '설명';
comment on column micro_service.last_health_date is '마지막 Health Check 시간';
comment on column micro_service.update_date is '수정일';
comment on column micro_service.insert_date is '등록일';


-- Micro Service 이력
create table micro_service_log(
    micro_service_log_id					bigint,
    micro_service_id			            int,
    micro_service_name				        varchar(60)			not null,
    micro_service_type				        varchar(30)			not null,
    url_path					            varchar(256),
    status						            varchar(20)			default 'use',
    error_message                           varchar(256),
    year						char(4)				default to_char(now(), 'YYYY'),
    month						varchar(2)			default to_char(now(), 'MM'),
    day							varchar(2)			default to_char(now(), 'DD'),
    year_week					varchar(2)			default to_char(now(), 'WW'),
    week						varchar(2)			default to_char(now(), 'W'),
    hour						varchar(2)			default to_char(now(), 'HH24'),
    minute						varchar(2)			default to_char(now(), 'MI'),
    insert_date				                timestamp 			with time zone			default now()
) partition by range(insert_date);

comment on table micro_service_log is 'Micro Service 이력';
comment on column micro_service_log.micro_service_log_id is '고유키';
comment on column micro_service_log.micro_service_id is '서비스 키';
comment on column micro_service_log.micro_service_name is '서비스명';
comment on column micro_service_log.micro_service_type is '서비스 유형. cache(캐시 Reload), simulation, sensor-things';
comment on column micro_service_log.url_path is '호출 url';
comment on column micro_service_log.status is 'http status';
comment on column micro_service_log.error_message is 'status 가 에러일때 에러 메시지(256자까지)';
comment on column micro_service_log.year is '년';
comment on column micro_service_log.month is '월';
comment on column micro_service_log.day is '일';
comment on column micro_service_log.year_week is '일년중 몇주';
comment on column micro_service_log.week is '이번달 몇주';
comment on column micro_service_log.hour is '시간';
comment on column micro_service_log.minute is '분';
comment on column micro_service_log.insert_date is '등록일';


create table micro_service_log_2021 partition of micro_service_log for values from ('2021-01-01 00:00:00.000000') to ('2022-01-01 00:00:00.000000');
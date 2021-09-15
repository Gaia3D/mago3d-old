-- FK, Index 는 별도 파일로 분리. 맨 마지막에 작업 예정
drop table if exists schedule_info cascade;

-- 스케줄 정보
create table schedule_info(
	schedule_id					integer,
	job_name				    varchar(32)							not null,
	job_group                   varchar(32)                         not null,
	trigger_name				varchar(32)							not null,
	trigger_group				varchar(32)						    not null,
	cron_schedule				varchar(32)						    not null,
	data				        varchar(32)						    not null,
	update_date					timestamp with time zone,
	insert_date					timestamp with time zone			default now(),
	constraint schedule_info_pk primary key(schedule_id)
);

comment on table schedule_info is '스케줄 정보';
comment on column schedule_info.schedule_id is '고유번호';
comment on column schedule_info.job_name is '작업명';
comment on column schedule_info.job_group is '작업 그룹';
comment on column schedule_info.trigger_name is '트리거 명';
comment on column schedule_info.trigger_group is '트리거 그룹';
comment on column schedule_info.cron_schedule is '주기';
comment on column schedule_info.data is '데이터';
comment on column schedule_info.update_date is '수정일';
comment on column schedule_info.insert_date is '등록일';
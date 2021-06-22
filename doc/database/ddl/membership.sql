-- FK, Index 는 별도 파일로 분리. 맨 마지막에 작업 예정
drop table if exists membership cascade;
drop table if exists membership_usage cascade;
drop table if exists membership_history cascade;


-- 멤버십 기본정보
create table membership(
    membership_id                   integer,
    membership_name 			    varchar(32),
    upload_file_size				numeric(20,15)			        not null,
    convert_file_count				integer,
    constraint membership_pk primary key(membership_id)
);

comment on table membership is '멤버십 정책';
comment on column membership.membership_id is '고유번호';
comment on column membership.membership_name is '멤버십 이름 (일반, 실버, 골드, 플레티넘)';
comment on column membership.upload_file_size is '허용 용량(GB)';
comment on column membership.convert_file_count is '허용 횟수';

-- 멤버십 사용량(월별)
create table membership_usage(
    membership_usage_id             integer,
    membership_id                   integer,
    user_id 					    varchar(32),
    use_upload_file_size			numeric(20,15)					default 0,
    use_convert_file_count			integer                         default 0,
    update_date					    timestamp with time zone,
    insert_date					    timestamp with time zone		default now(),
    constraint membership_usage_pk primary key(membership_usage_id)
);

comment on table membership_usage is '멤버십 기본정보';
comment on column membership_usage.membership_usage_id is '고유번호';
comment on column membership_usage.membership_id is '멤버십 고유번호';
comment on column membership_usage.user_id is '사용자 아이디';
comment on column membership_usage.use_upload_file_size is '사용량(GB)';
comment on column membership_usage.use_convert_file_count is '사용횟수';
comment on column membership_usage.update_date is '멤버십 변경 날짜';
comment on column membership_usage.insert_date is '등록일';

-- 멤버십 변경 기록
create table membership_log(
    membership_history_id           integer,
    membership_id                   integer,
    user_id 					    varchar(32),
    status                          varchar(10)                     default 'request',
    insert_date					    timestamp with time zone	    default now(),
    constraint membership_history_pk primary key(membership_history_id)
);

comment on table membership_log is '멤버십 변경 요청';
comment on column membership_log.membership_history_id is '고유번호';
comment on column membership_log.membership_id is '멤버십 고유번호';
comment on column membership_log.user_id is '사용자 아이디';
comment on column membership_log.status is '멤버십 상태 (request, approval)';
comment on column membership_log.insert_date is '등록일';
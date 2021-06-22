-- FK, Index 는 별도 파일로 분리. 맨 마지막에 작업 예정
drop table if exists membership cascade;
drop table if exists membership_usage cascade;
drop table if exists membership_history cascade;


-- 멤버십 기본정보
create table membership(
    membership_id                   integer,
    membership_name 			    varchar(32),
    allow_capacity				    numeric(20,15)			        not null,
    allow_count					    integer,
    constraint membership_pk primary key(membership_id)
);

comment on table membership is '멤버십 정책';
comment on column membership.membership_id is '고유번호';
comment on column membership.membership_name is '멤버십 이름 (일반, 실버, 골드, 플레티넘)';
comment on column membership.allow_capacity is '허용 용량(GB)';
comment on column membership.allow_count is '허용 횟수';

-- 멤버십 사용량(일별)
create table membership_usage(
    membership_usage_id             integer,
    membership_id                   integer,
    membership_name 			    varchar(32),
    user_id 					    varchar(32),
    use_capacity				    numeric(20,15)					default 0,
    use_count					    integer                         default 0,
    update_date					    timestamp with time zone,
    insert_date					    timestamp with time zone		default now(),
    constraint membership_usage_pk primary key(membership_usage_id)
);

comment on table membership_usage is '멤버십 기본정보';
comment on column membership_usage.membership_usage_id is '고유번호';
comment on column membership_usage.membership_id is '멤버십 고유번호';
comment on column membership_usage.membership_name is '멤버십 이름(일반, 실버, 골드, 플레티넘)';
comment on column membership_usage.user_id is '사용자 아이디';
comment on column membership_usage.use_capacity is '사용량(GB)';
comment on column membership_usage.use_count is '사용횟수';
comment on column membership_usage.update_date is '멤버십 변경 날짜';
comment on column membership_usage.insert_date is '등록일';

-- 멤버십 사용량(일별)
create table membership_history(
    membership_history_id           integer,
    membership_id                   integer,
    membership_name 			    varchar(32),
    user_id 					    varchar(32),
    status                          varchar(10)                     default '대기',
    insert_date					    timestamp with time zone	    default now(),
    constraint membership_history_pk primary key(membership_history_id)
);

comment on table membership_history is '멤버십 변경 요청';
comment on column membership_history.membership_history_id is '고유번호';
comment on column membership_history.membership_id is '멤버십 고유번호';
comment on column membership_history.membership_name is '멤버십 이름(일반, 실버, 골드, 플레티넘)';
comment on column membership_history.user_id is '사용자 아이디';
comment on column membership_history.status is '멤버십 상태 (대기, 승인)';
comment on column membership_history.insert_date is '등록일';
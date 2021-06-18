drop table if exists rule cascade;
drop table if exists rule_group cascade;
drop table if exists rule_data_group cascade;
drop table if exists rule_layer_group cascade;


-- rule_group ---

-- rule 그룹
create table rule_group (
    rule_group_id				        integer,
    rule_group_name      		        varchar(256)					not null,
    rule_group_key      		        varchar(256)					not null,
    rule_type                           varchar(50),
    rule_inherit_type                   boolean                         default true,
    user_id						        varchar(32),
    ancestor					        integer							default 0,
    parent						        integer							default 1,
    depth						        integer							default 1,
    view_order					        integer							default 1,
    basic						        boolean							default false,
    available					        boolean							default true,
    description					        varchar(256),
    update_date             	        timestamp with time zone,
    insert_date					        timestamp with time zone		default now(),
    constraint rule_group_pk 		    primary key (rule_group_id)
);

comment on table rule_group is '룰 그룹';
comment on column rule_group.rule_group_id is '룰 그룹 고유번호';
comment on column rule_group.rule_group_name is '룰 그룹명';
comment on column rule_group.rule_group_key is '룰 그룹 Key. 확장용';
comment on column rule_group.rule_type is '룰 구분. data, data_group, data_attributes, data_library, layer, design_layer 등';
comment on column rule_group.rule_inherit_type is '최상위 룰 상속 여부. true : 상속, false : 상속 안함';
comment on column rule_group.user_id is '사용자 아이디';
comment on column rule_group.view_order is '나열 순서';
comment on column rule_group.available is '사용 여부';
comment on column rule_group.description is '설명';
comment on column rule_group.update_date is '수정일';
comment on column rule_group.insert_date is '등록일';

-- rule 관리
create table rule (
    rule_id					            integer,
    rule_group_id				        integer,
    rule_key					        varchar(100)					not null,
    rule_name					        varchar(256)					not null,
    user_id						        varchar(32),
    attributes					        json,
    view_order					        integer							default 1,
    available					        boolean							default true,
    description					        varchar(256),
    update_date					        timestamp with time zone		default now(),
    insert_date					        timestamp with time zone 		default now(),
    constraint rule_pk 		            primary key (rule_id)
);

comment on table rule is '룰';
comment on column rule.rule_id is '룰 고유번호';
comment on column rule.rule_group_id is '룰 그룹 고유번호';
comment on column rule.rule_key is '룰 고유키(API용)';
comment on column rule.rule_name is '룰명';
comment on column rule.user_id is '사용자 아이디';
comment on column rule.attributes is '속성';
comment on column rule.view_order is '나열 순서';
comment on column rule.available is '사용유무.';
comment on column rule.description is '설명';
comment on column rule.update_date is '수정일';
comment on column rule.insert_date is '등록일';

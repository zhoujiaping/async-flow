drop table t_async_taks;
create table t_async_taks(
    id bigint(20) primary key auto_increment,
    uid varchar(36) '任务的唯一标识',
    version int(11) default 0,
    create_time datetime default now(),
    update_time datetime default now(),
    valid int default 1 comment '逻辑删除标识。1：有效，0：无效',
    status int default -1 comment '-1:init,1:processing,2:finished,3:error'
    expect_run_time datetime comment '延时任务预期执行时间',
    exec_times int default 0 comment '延时任务执行次数',
    delay int comment '延时时间',
    time_unit varchar(36) comment '时间单位',
    bean_name varchar(128) comment '流程对应的bean',
    method varchar(128) comment '任务对应的bean方法',
    args varchar(5000) comment '任务的业务参数，json格式',
    extra varchar(2000) comment '任务的控制参数，json格式'
);
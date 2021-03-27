drop table t_async_taks;
create table t_async_taks(
    id bigint(20) primary key auto_increment,
    uid varchar(36),
    version int(11) default 0,
    create_time datetime default now(),
    update_time datetime default now(),
    valid int default 1,
    status int default -1 comment '-1:init,1:processing,2:finished,3:error'
    expect_run_time datetime,
    exec_times int default 0,
    delay int,
    time_unit varchar(36),
    bean_name varchar(128),
    method varchar(128),
    args varchar(5000),
    extra varchar(2000)
);
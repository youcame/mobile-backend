create table mobile.team
(
    id          bigint auto_increment
        primary key,
    name        varchar(255)             null comment '队伍名称',
    description varchar(1024)            null comment '队伍描述',
    maxNum      int                      null comment '最大人数',
    expireTime  datetime                 null comment '过期时间',
    creatorId   int                      null comment '创建人Id',
    status      int          default 0   not null comment '0-公开， 1-私有， 2-加密',
    password    varchar(255)             null comment '密码',
    createTime  datetime                 not null on update CURRENT_TIMESTAMP comment '创建时间',
    updateTime  datetime                 not null comment '更新时间',
    isDelete    varchar(255) default '0' not null comment '是否删除0-未删，1-删除'
);


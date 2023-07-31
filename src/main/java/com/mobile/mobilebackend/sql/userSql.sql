create table mobile.user
(
    id          bigint auto_increment comment '用户Id'
        primary key,
    username    varchar(255) not null comment '用户昵称',
    password    varchar(255) not null comment '密码',
    userAccount varchar(255) not null comment '登录账号',
    avatarUrl   varchar(255) null comment '用户头像',
    gender      int          null comment '性别  0-男 1-女',
    phone       varchar(20)  null comment '电话',
    email       varchar(255) null comment '邮箱',
    userStatus  int          null comment '用户状态',
    createTime  datetime     null on update CURRENT_TIMESTAMP comment '创建时间',
    updateTime  datetime     null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    int          null comment '逻辑删除 0-未删除 1-已删除',
    userRole    int          null comment '权限 0-普通用户',
    constraint userAccount
        unique (userAccount)
)
    comment '用户信息表' collate = utf8mb4_0900_ai_ci;

create index idx_username
    on mobile.user (username);

alter table user add column tags varchar(1024) null comment '标签列表'


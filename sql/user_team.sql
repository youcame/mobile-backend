create table mobile.user_team
(
    id         bigint   not null comment '关系表Id'
        primary key,
    userId     bigint   null comment '用户Id',
    teamId     bigint   null comment '队伍Id',
    joinTime   datetime null comment '加入时间',
    createTime datetime not null on update CURRENT_TIMESTAMP comment '创建时间',
    updateTime datetime null comment '更新时间',
    isDelete   int      null comment '是否删除 0-未删除， 1-删除'
);


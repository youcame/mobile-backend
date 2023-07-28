DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签id',
                        `tagName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签名称',
                        `userId` bigint NULL DEFAULT NULL COMMENT '用户id\r\n',
                        `parentId` bigint NULL DEFAULT NULL COMMENT '父标签id',
                        `isParent` tinyint NULL DEFAULT NULL COMMENT '0-非父标签，1-父标签',
                        `createTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updateTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '0-未删除，1-删除',
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE INDEX `uniIdx_tagName`(`tagName` ASC) USING BTREE,
                        INDEX `idx_userId`(`userId` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;

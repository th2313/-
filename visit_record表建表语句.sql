-- =============================================
-- 多次就医记录表 (visit_record)
-- 用于存储患者每次就诊的核心信息
-- =============================================
-- 使用前请确保已选择 keshe 数据库：USE keshe;

DROP TABLE IF EXISTS `visit_record`;

CREATE TABLE `visit_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键，唯一标识一条就医记录',
    `hspID` VARCHAR(20) NOT NULL COMMENT '关联患者住院编号（对应patientinfo表hspID字段）',
    `doctorID` VARCHAR(20) NOT NULL COMMENT '关联医生编号（对应doctorinfo表doctorID字段）',
    `recordID` BIGINT DEFAULT NULL COMMENT '关联电子病历ID（对应medrecordinfo表主键，可为空）',
    `depart_name` VARCHAR(50) DEFAULT NULL COMMENT '就诊科室名称（冗余存储，避免频繁关联查询）',
    `visit_type` VARCHAR(20) NOT NULL COMMENT '就诊类型：门诊/住院/复诊',
    `visit_time` DATETIME NOT NULL COMMENT '就诊时间',
    `main_complaint` VARCHAR(500) NOT NULL COMMENT '患者主诉（主要症状描述）',
    `visit_status` VARCHAR(20) NOT NULL DEFAULT '治疗中' COMMENT '就诊状态：已完成/治疗中/已出院',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注（可选）',
    PRIMARY KEY (`id`),
    INDEX `idx_hspID` (`hspID`),
    INDEX `idx_doctorID` (`doctorID`),
    INDEX `idx_visit_time` (`visit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='多次就医记录表';

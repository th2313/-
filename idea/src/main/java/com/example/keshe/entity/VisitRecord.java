package com.example.keshe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 多次就医记录表
 * 存储患者每次就诊的核心信息
 */
@Data
@TableName("visit_record") // 数据库表名，需手动创建
public class VisitRecord {
    // 自增主键，唯一标识一次就医记录
    @TableId(type = IdType.AUTO)
    private Long id;
    // 关联患者（必填）：对应Patientinfo的hspID
    private String hspID;
    // 关联医生（必填）：对应Doctorinfo的doctorID
    private String doctorID;
    // 关联电子病历（可选）：对应Medrecordinfo的主键（若有）
    private Long recordID;
    // 就诊科室（冗余存储，避免频繁查医生表）
    private String departName;
    // 就诊类型（如：门诊/住院/复诊，必填）
    private String visitType;
    // 就诊时间（必填）
    private Date visitTime;
    // 主诉（患者主要症状，必填）
    private String mainComplaint;
    // 就诊状态（如：已完成/治疗中/已出院，必填）
    private String visitStatus;
    // 备注（可选）
    private String remark;

}
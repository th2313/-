package com.example.keshe.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  电子病历实体类
 * </p>
 *
 * @author jaun
 * @since 2025-09-21
 */
@Data
@TableName("medrecordinfo")
public class Medrecordinfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("HspID")
    @TableField("HspID")
    private String hspID;

    @TableField("主诉")
    private String 主诉;

    @TableField("现病史")
    private String 现病史;

    @TableField("既往史")
    private String 既往史;

    @TableField("婚育史")
    private String 婚育史;

    @TableField("月经史")
    private String 月经史;

    @TableField("家族史")
    private String 家族史;

    @TableField("体格检查")
    private String 体格检查;

    @TableField("专科检查")
    private String 专科检查;

    @TableField("辅助检查")
    private String 辅助检查;

    @TableField("鉴别诊断")
    private String 鉴别诊断;

    @TableField("初步诊断")
    private String 初步诊断;

    @TableField("诊疗计划")
    private String 诊疗计划;
}

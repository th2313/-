package com.example.keshe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author jaun
 * @since 2025-09-29
 */
@Data
@TableName(value = "drugs", excludeProperty = {"delMark"})  // 排除逻辑删除字段
public class Drugs implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("DrugsCode")
    private String drugsCode;

    @TableField("DrugsName")
    private String drugsName;

    @TableField("DrugsFormat")
    private String drugsFormat;

    @TableField("DrugsUnit")
    private String drugsUnit;

    @TableField("Manufacturer")
    private String manufacturer;

    @TableField("DrugsDosageID")
    private Integer drugsDosageID;

    @TableField("DrugsTypeID")
    private Integer drugsTypeID;

    @TableField("DrugsPrice")
    private BigDecimal drugsPrice;

    @TableField("MnemonicCode")
    private String mnemonicCode;

    @TableField("CreationDate")
    private LocalDateTime creationDate;

    @TableField("LastUpdateDate")
    private LocalDateTime lastUpdateDate;

    @TableField("DelMark")
    private Integer delMark;
}
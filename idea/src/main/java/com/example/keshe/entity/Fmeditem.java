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
 * @since 2025-10-04
 */
@Data
@TableName("fmeditem")
public class Fmeditem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目编码
     */
    @TableField("ItemCode")
    private String itemCode;

    /**
     * 项目名称
     */
    @TableField("ItemName")
    private String itemName;

    /**
     * 规格
     */
    @TableField("Format")
    private String format;

    /**
     * 单价
     */
    @TableField("Price")
    private BigDecimal price;

    /**
     * 所属费用科目ID
     */
    @TableField("ExpClassID")
    private Integer expClassID;

    /**
     * 执行科室ID
     */
    @TableField("DeptID")
    private Integer deptID;

    /**
     * 拼音助记码
     */
    @TableField("MnemonicCode")
    private String mnemonicCode;

    /**
     * 创建时间
     */
    @TableField("CreationDate")
    private LocalDateTime creationDate;

    /**
     * 最后修改时间
     */
    @TableField("LastUpdateDate")
    private LocalDateTime lastUpdateDate;

    /**
     * 项目类型
     */
    @TableField("RecordType")
    private Integer recordType;

    /**
     * 删除标记
     */
    @TableField("DelMark")
    private Integer delMark;
}

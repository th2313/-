package com.example.keshe.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  科室信息实体类
 * </p>
 *
 * @author jaun
 * @since 2025-09-21
 */
@Data
@TableName("departinfo")
public class Departinfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("DepartID")
    private String departID;

    @TableField("DepartName")
    private String departName;

    @TableField("Remarks")
    private String remarks;
}

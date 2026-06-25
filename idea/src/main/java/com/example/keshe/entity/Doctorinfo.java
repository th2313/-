package com.example.keshe.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  医生信息实体类
 * </p>
 *
 * @author jaun
 * @since 2025-09-21
 */
@Data
@TableName("doctorinfo")
public class Doctorinfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("DoctorID")
    private String doctorID;

    @TableField("Name")
    private String name;

    @TableField("DepartID")
    private String departID;

}

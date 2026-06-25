package com.example.keshe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("doctor_schedule")
public class DoctorSchedule implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;             // 排班ID（自增）

    @TableField("doctorId")
    private String doctorId;     // 医生ID（关联Doctorinfo的doctorID）

    @TableField("departId")
    private String departId;     // 科室ID（关联Departinfo的departID）

    @TableField("scheduleDate")
    private Date scheduleDate;   // 排班日期

    @TableField("workTime")
    private String workTime;     // 工作时间（上午/下午/全天）

}
package com.example.keshe.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 病人信息实体类
 * @author jaun
 * @since 2025-09-21
 */
@Data
@TableName("patientinfo")
public class Patientinfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "HspID")
    private String hspID;

    @TableField("Name")
    private String name; // 病人姓名（必填）

    @TableField("Gender")
    private String gender; // 性别（必填）

    @TableField("DepartName")
    private String departName; // 科室（必填）

    @TableField("DoctorName")
    private String doctorName; // 主治医生（必填）

    @TableField("BedID")
    private String bedID; // 病床号（非必填）

    @TableField("InHspDiagnose")
    private String inHspDiagnose; // 入院诊断（非必填）

    @TableField("IDCardNo")
    private String iDCardNo; // 身份证号（非必填）

    @TableField("ContactsPhone")
    private String contactsPhone; // 联系电话（必填）

    @TableField("Birthday")
    private String birthday; // 出生日期（必填）

    // 其他非必填字段
    @TableField("InHspTimes")
    private Integer inHspTimes;

    @TableField("DepartZone")
    private String departZone;

    @TableField("MRID")
    private String mrid;

    @TableField("MedInsuranceType")
    private String medInsuranceType;

    @TableField("MedInsuranceID")
    private String medInsuranceID;

    @TableField("InHspType")
    private String inHspType;

    @TableField("Illness")
    private String illness;

    @TableField("IllSeason")
    private String illSeason;

    @TableField("Marriage")
    private String marriage;

    @TableField("Nation")
    private String nation;

    @TableField("Profession")
    private String profession;

    @TableField("Address")
    private String address;

    @TableField("Corporation")
    private String corporation;

    @TableField("ContactsName")
    private String contactsName;

    @TableField("Relation")
    private String relation;

    @TableField("PostCode")
    private String postCode;

    @TableField("HspTime")
    private String hspTime;

    // 生成短编号的方法（新增时调用）
    public void generateShortHspID(Integer dailyCount) {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String serial = String.format("%03d", dailyCount + 1);
        this.hspID = date + serial;
    }
}
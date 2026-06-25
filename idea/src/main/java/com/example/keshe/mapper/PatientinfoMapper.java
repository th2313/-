package com.example.keshe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.keshe.entity.Patientinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 病人信息Mapper接口
 */
@Mapper
public interface PatientinfoMapper extends BaseMapper<Patientinfo> {

    /**
     * 统计今日已生成的住院编号数量
     * 注意：列名 HspTime 必须与数据库列名大小写一致
     */
    @Select("SELECT COUNT(*) FROM patientinfo WHERE HspID LIKE CONCAT(DATE_FORMAT(CURDATE(), '%Y%m%d'), '%')")
    Integer countByToday();
}
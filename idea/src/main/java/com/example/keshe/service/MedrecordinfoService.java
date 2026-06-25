package com.example.keshe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.keshe.entity.Medrecordinfo;
import com.example.keshe.entity.Templateinfo;

import java.util.List;


/**
 * 电子病历服务接口
 */
public interface MedrecordinfoService extends IService<Medrecordinfo> {
    // 根据患者ID获取病历
    Medrecordinfo getByHspId(String hspID);
    List<Medrecordinfo> listByHspId(String hspID);
    // 保存或更新病历
    boolean saveOrUpdateRecord(Medrecordinfo medrecordinfo);

    // 将病历保存为模板
    boolean saveAsTemplate(Medrecordinfo medrecordinfo, String templateID);

    // 根据模板创建病历
    Medrecordinfo createFromTemplate(String hspID, String templateID);

    List<Medrecordinfo> listByHspIds(List<String> idList);

    boolean removeByHspId(String hspID);

    boolean removeByHspIds(List<String> hspIDs);
}

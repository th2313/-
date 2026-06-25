package com.example.keshe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.keshe.entity.VisitRecord;

import java.util.List;

/**
 * 多次就医记录服务接口
 */
public interface IVisitRecordService extends IService<VisitRecord> {

    /**
     * 根据患者住院编号查询所有就医记录
     */
    List<VisitRecord> findByHspID(String hspID);
}

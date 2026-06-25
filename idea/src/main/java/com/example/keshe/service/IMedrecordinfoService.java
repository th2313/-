package com.example.keshe.service;

import com.example.keshe.entity.Medrecordinfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jaun
 * @since 2025-09-29
 */
public interface IMedrecordinfoService extends IService<Medrecordinfo> {
    // 根据患者ID删除病历
    boolean removeByHspId(String hspID);

    // 批量删除病历
    boolean removeByHspIds(List<String> hspIDs);
}

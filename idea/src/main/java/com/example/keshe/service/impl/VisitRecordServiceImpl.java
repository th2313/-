package com.example.keshe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.keshe.entity.VisitRecord;
import com.example.keshe.mapper.VisitRecordMapper;
import com.example.keshe.service.IVisitRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 多次就医记录服务实现类
 */
@Service
public class VisitRecordServiceImpl extends ServiceImpl<VisitRecordMapper, VisitRecord> implements IVisitRecordService {

    /**
     * 根据患者住院编号查询所有就医记录，按就诊时间降序排列
     */
    @Override
    public List<VisitRecord> findByHspID(String hspID) {
        QueryWrapper<VisitRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("hspID", hspID)
                .orderByDesc("visit_time");
        return baseMapper.selectList(wrapper);
    }
}

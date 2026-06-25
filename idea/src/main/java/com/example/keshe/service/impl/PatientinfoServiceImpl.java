package com.example.keshe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.keshe.entity.Patientinfo;
import com.example.keshe.mapper.PatientinfoMapper;
import com.example.keshe.service.IPatientinfoService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 病人信息服务实现类
 */
@Service
public class PatientinfoServiceImpl extends ServiceImpl<PatientinfoMapper, Patientinfo> implements IPatientinfoService {

    @Override
    public boolean savePatient(Patientinfo patientinfo) {
        if (patientinfo.getHspID() == null || patientinfo.getHspID().isEmpty()) {
            Integer dailyCount = baseMapper.countByToday();
            patientinfo.generateShortHspID(dailyCount != null ? dailyCount : 0);
            patientinfo.setHspTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
        return saveOrUpdate(patientinfo);
    }

    @Override
    public Integer countByToday() {
        return baseMapper.countByToday();
    }

    /**
     * 按姓名模糊查询患者
     */
    @Override
    public List<Patientinfo> findByNameLike(String name) {
        QueryWrapper<Patientinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name); // SQL: WHERE name LIKE '%name%'
        return baseMapper.selectList(queryWrapper);
    }
}
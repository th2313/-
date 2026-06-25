package com.example.keshe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.keshe.entity.Medrecordinfo;
import com.example.keshe.entity.Templateinfo;
import com.example.keshe.mapper.MedrecordinfoMapper;
import com.example.keshe.mapper.TemplateinfoMapper;
import com.example.keshe.service.MedrecordinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 电子病历服务实现类
 */
@Service
public class MedrecordinfoServiceImpl extends ServiceImpl<MedrecordinfoMapper, Medrecordinfo> implements MedrecordinfoService {

    @Autowired
    private TemplateinfoMapper templateinfoMapper;

    @Override
    public Medrecordinfo getByHspId(String hspID) {
        return baseMapper.selectById(hspID);
    }

    @Override
    public boolean saveOrUpdateRecord(Medrecordinfo medrecordinfo) {
        return saveOrUpdate(medrecordinfo);
    }

    @Transactional
    @Override
    public boolean saveAsTemplate(Medrecordinfo medrecordinfo, String templateID) {
        // 创建模板对象并复制病历内容
        Templateinfo template = new Templateinfo();
        template.setTemplateID(templateID);
        template.set主诉(medrecordinfo.get主诉());
        template.set现病史(medrecordinfo.get现病史());
        template.set既往史(medrecordinfo.get既往史());
        template.set婚育史(medrecordinfo.get婚育史());
        template.set月经史(medrecordinfo.get月经史());
        template.set家族史(medrecordinfo.get家族史());
        template.set体格检查(medrecordinfo.get体格检查());
        template.set专科检查(medrecordinfo.get专科检查());
        template.set辅助检查(medrecordinfo.get辅助检查());
        template.set鉴别诊断(medrecordinfo.get鉴别诊断());
        template.set初步诊断(medrecordinfo.get初步诊断());
        template.set诊疗计划(medrecordinfo.get诊疗计划());

        // 保存模板
        return templateinfoMapper.insert(template) > 0;
    }

    @Override
    public Medrecordinfo createFromTemplate(String hspID, String templateID) {
        // 获取模板信息
        Templateinfo template = templateinfoMapper.selectById(templateID);
        if (template == null) {
            return null;
        }

        // 创建新病历并复制模板内容
        Medrecordinfo medrecordinfo = new Medrecordinfo();
        medrecordinfo.setHspID(hspID);
        medrecordinfo.set主诉(template.get主诉());
        medrecordinfo.set现病史(template.get现病史());
        medrecordinfo.set既往史(template.get既往史());
        medrecordinfo.set婚育史(template.get婚育史());
        medrecordinfo.set月经史(template.get月经史());
        medrecordinfo.set家族史(template.get家族史());
        medrecordinfo.set体格检查(template.get体格检查());
        medrecordinfo.set专科检查(template.get专科检查());
        medrecordinfo.set辅助检查(template.get辅助检查());
        medrecordinfo.set鉴别诊断(template.get鉴别诊断());
        medrecordinfo.set初步诊断(template.get初步诊断());
        medrecordinfo.set诊疗计划(template.get诊疗计划());

        return medrecordinfo;
    }
    @Override
    public List<Medrecordinfo> listByHspId(String hspID) {
        // 使用 MyBatis-Plus 模糊查询
        return baseMapper.selectList(
                new LambdaQueryWrapper<Medrecordinfo>()
                        .like(Medrecordinfo::getHspID, hspID)
        );
    }
    @Override
    public List<Medrecordinfo> listByHspIds(List<String> hspIds) {
        QueryWrapper<Medrecordinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("hspID", hspIds); // 条件：hspID在传入的列表中
        return baseMapper.selectList(queryWrapper);
    }
    @Override
    public boolean removeByHspId(String hspID) {
        QueryWrapper<Medrecordinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("HspID", hspID);
        return baseMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean removeByHspIds(List<String> hspIDs) {
        QueryWrapper<Medrecordinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("HspID", hspIDs);
        return baseMapper.delete(queryWrapper) > 0;
    }
}

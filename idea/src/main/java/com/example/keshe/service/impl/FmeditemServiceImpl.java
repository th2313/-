package com.example.keshe.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.keshe.entity.Fmeditem;
import com.example.keshe.mapper.FmeditemMapper;
import com.example.keshe.service.IFmeditemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检查项目服务实现类
 */
@Service
public class FmeditemServiceImpl extends ServiceImpl<FmeditemMapper, Fmeditem> implements IFmeditemService {

    /**
     * 根据科室ID查询检查项目
     * @param deptId 科室ID
     * @return 检查项目列表
     */
    @Override
    public List<Fmeditem> getByDeptId(Integer deptId) {
        // 查询未删除的项目
        return baseMapper.selectByDeptId(deptId);
    }

    /**
     * 根据项目名称模糊查询
     * @param name 项目名称关键词
     * @return 检查项目列表
     */
    @Override
    public List<Fmeditem> getByNameLike(String name) {
        return baseMapper.selectByNameLike("%" + name + "%");
    }

    /**
     * 批量添加检查项目
     * @param items 检查项目列表
     * @return 是否添加成功
     */
    @Override
    @Transactional
    public boolean batchAdd(List<Fmeditem> items) {
        // 设置公共字段
        LocalDateTime now = LocalDateTime.now();
        for (Fmeditem item : items) {
            item.setCreationDate(now);
            item.setLastUpdateDate(now);
            if (item.getDelMark() == null) {
                item.setDelMark(0);
            }
        }
        return saveBatch(items);
    }
    @Override
    public boolean existsById(Integer id) {
        return baseMapper.existsById(id);
    }
}

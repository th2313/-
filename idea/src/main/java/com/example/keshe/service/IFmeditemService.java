package com.example.keshe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.keshe.entity.Fmeditem;

import java.util.List;

/**
 * 检查项目服务接口
 */
public interface IFmeditemService extends IService<Fmeditem> {

    /**
     * 根据科室ID查询检查项目
     * @param deptId 科室ID
     * @return 检查项目列表
     */
    List<Fmeditem> getByDeptId(Integer deptId);

    /**
     * 根据项目名称模糊查询
     * @param name 项目名称关键词
     * @return 检查项目列表
     */
    List<Fmeditem> getByNameLike(String name);

    /**
     * 批量添加检查项目
     * @param items 检查项目列表
     * @return 是否添加成功
     */
    boolean batchAdd(List<Fmeditem> items);
    boolean existsById(Integer id);
}

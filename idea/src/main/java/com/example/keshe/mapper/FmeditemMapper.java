package com.example.keshe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.keshe.entity.Fmeditem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 检查项目Mapper接口
 */
@Mapper // 添加此注解
public interface FmeditemMapper extends BaseMapper<Fmeditem> {

    /**
     * 根据科室ID查询检查项目
     * @param deptId 科室ID
     * @return 检查项目列表
     */
    List<Fmeditem> selectByDeptId(@Param("deptId") Integer deptId);

    /**
     * 根据项目名称模糊查询
     * @param name 项目名称关键词
     * @return 检查项目列表
     */
    List<Fmeditem> selectByNameLike(@Param("name") String name);

    boolean existsById(Integer id);
}

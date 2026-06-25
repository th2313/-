package com.example.keshe.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.keshe.entity.Departinfo;
import com.example.keshe.service.IDepartinfoService;
import com.example.keshe.dto.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 科室信息控制器
 * 适配前端科室下拉列表、数据校验和跨域需求
 */
@RestController
@RequestMapping("/departinfo")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 与病人接口保持一致的跨域配置
public class DepartinfoController {

    @Resource
    private IDepartinfoService departinfoService;

    /**
     * 获取所有科室列表（供前端下拉选择）
     * 接口地址：GET /departinfo/list
     * 前端调用：用于新增/编辑病人时选择科室
     */
    @GetMapping("/list")
    public Result<List<Departinfo>> getAllDepartments() {
        try {
            // 按科室ID升序查询，确保下拉列表顺序固定
            List<Departinfo> departments = departinfoService.list(
                    Wrappers.<Departinfo>query().orderByAsc("departID")
            );
            // 适配前端对"code=200"的判断（Result内部应包含code字段）
            return Result.success(departments);
        } catch (Exception e) {
            // 异常时返回错误信息，前端会捕获并显示
            return Result.fail("加载科室失败：" + e.getMessage());
        }
    }

    /**
     * 根据科室ID获取单个科室详情
     * 接口地址：GET /departinfo/{id}
     * 可用于前端回显科室名称（编辑病人时）
     */
    @GetMapping("/{id}")
    public Result<Departinfo> getDepartById(@PathVariable String id) {
        // 参数校验：科室ID不能为空
        if (id == null || id.trim().isEmpty()) {
            return Result.fail("科室ID不能为空");
        }
        try {
            Departinfo department = departinfoService.getById(id);
            if (department == null) {
                return Result.fail("未找到ID为【" + id + "】的科室");
            }
            return Result.success(department);
        } catch (Exception e) {
            return Result.fail("查询科室失败：" + e.getMessage());
        }
    }

    /**
     * 新增科室（后端管理功能，可选）
     * 接口地址：POST /departinfo
     */
    @PostMapping
    public Result<?> add(@RequestBody Departinfo departinfo) {
        // 校验必填字段
        if (departinfo.getDepartID() == null || departinfo.getDepartID().trim().isEmpty()) {
            return Result.fail("科室ID不能为空");
        }
        if (departinfo.getDepartName() == null || departinfo.getDepartName().trim().isEmpty()) {
            return Result.fail("科室名称不能为空");
        }
        try {
            // 检查科室ID是否已存在
            if (departinfoService.getById(departinfo.getDepartID()) != null) {
                return Result.fail("科室ID【" + departinfo.getDepartID() + "】已存在");
            }
            boolean saved = departinfoService.save(departinfo);
            return saved ? Result.success("新增科室【" + departinfo.getDepartName() + "】成功")
                    : Result.fail("新增科室失败");
        } catch (Exception e) {
            return Result.fail("新增科室异常：" + e.getMessage());
        }
    }

    /**
     * 编辑科室（后端管理功能，可选）
     * 接口地址：PUT /departinfo
     */
    @PutMapping
    public Result<?> update(@RequestBody Departinfo departinfo) {
        if (departinfo.getDepartID() == null || departinfo.getDepartID().trim().isEmpty()) {
            return Result.fail("科室ID不能为空");
        }
        if (departinfo.getDepartName() == null || departinfo.getDepartName().trim().isEmpty()) {
            return Result.fail("科室名称不能为空");
        }
        try {
            // 检查科室是否存在
            if (departinfoService.getById(departinfo.getDepartID()) == null) {
                return Result.fail("科室ID【" + departinfo.getDepartID() + "】不存在");
            }
            boolean updated = departinfoService.updateById(departinfo);
            return updated ? Result.success("编辑科室成功")
                    : Result.fail("编辑科室失败");
        } catch (Exception e) {
            return Result.fail("编辑科室异常：" + e.getMessage());
        }
    }

    /**
     * 删除科室（后端管理功能，可选）
     * 接口地址：DELETE /departinfo/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable String id) {
        if (id == null || id.trim().isEmpty()) {
            return Result.fail("科室ID不能为空");
        }
        try {
            // 检查科室是否存在
            if (departinfoService.getById(id) == null) {
                return Result.fail("科室ID【" + id + "】不存在");
            }
            boolean deleted = departinfoService.removeById(id);
            return deleted ? Result.success("删除科室成功")
                    : Result.fail("删除科室失败");
        } catch (Exception e) {
            return Result.fail("删除科室异常：" + e.getMessage());
        }
    }
}
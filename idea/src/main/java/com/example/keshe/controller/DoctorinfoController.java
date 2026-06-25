package com.example.keshe.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.keshe.entity.Doctorinfo;
import com.example.keshe.service.IDoctorinfoService;
import com.example.keshe.dto.Result; // 假设你有统一返回类
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 医生信息控制器
 * 处理医生信息的查询、新增、编辑、删除等请求
 */
@RestController
@RequestMapping("/doctorinfo")
public class DoctorinfoController {

    @Resource
    private IDoctorinfoService doctorinfoService;

    /**
     * 根据科室ID查询医生列表（已在排班控制器中使用，这里可复用或扩展）
     * 前端调用：/doctorinfo/byDepart/{departId}
     */
    @GetMapping("/byDepart/{departId}")
    public Result<List<Doctorinfo>> getByDepartId(@PathVariable String departId) {
        if (departId == null || departId.isEmpty()) {
            return Result.fail("科室ID不能为空");
        }
        QueryWrapper<Doctorinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("departID", departId); // 注意字段名与数据库一致（departID）
        List<Doctorinfo> doctors = doctorinfoService.list(queryWrapper);
        return Result.success(doctors);
    }

    /**
     * 根据医生ID查询医生详情
     * 前端调用：/doctorinfo/{doctorId}
     */
    @GetMapping("/{doctorId}")
    public Result<Doctorinfo> getById(@PathVariable String doctorId) {
        if (doctorId == null || doctorId.isEmpty()) {
            return Result.fail("医生ID不能为空");
        }
        Doctorinfo doctor = doctorinfoService.getById(doctorId);
        if (doctor == null) {
            return Result.fail("未找到该医生信息");
        }
        return Result.success(doctor);
    }

    /**
     * 查询所有医生（分页可自行添加，避免数据量过大）
     * 前端调用：/doctorinfo/all
     */
    @GetMapping("/all")
    public Result<List<Doctorinfo>> getAll() {
        List<Doctorinfo> doctors = doctorinfoService.list();
        return Result.success(doctors);
    }

    /**
     * 新增医生信息
     * 前端调用：/doctorinfo（POST）
     */
    @PostMapping
    public Result<?> add(@RequestBody Doctorinfo doctorinfo) {
        // 简单参数校验（实际可更详细）
        if (doctorinfo.getDoctorID() == null || doctorinfo.getDoctorID().isEmpty()) {
            return Result.fail("医生ID不能为空");
        }
        boolean saved = doctorinfoService.save(doctorinfo);
        return saved ? Result.success("新增成功") : Result.fail("新增失败");
    }

    /**
     * 编辑医生信息
     * 前端调用：/doctorinfo（PUT）
     */
    @PutMapping
    public Result<?> update(@RequestBody Doctorinfo doctorinfo) {
        if (doctorinfo.getDoctorID() == null || doctorinfo.getDoctorID().isEmpty()) {
            return Result.fail("医生ID不能为空");
        }
        boolean updated = doctorinfoService.updateById(doctorinfo);
        return updated ? Result.success("编辑成功") : Result.fail("编辑失败或医生不存在");
    }

    /**
     * 删除医生信息
     * 前端调用：/doctorinfo/{doctorId}（DELETE）
     */
    @DeleteMapping("/{doctorId}")
    public Result<?> delete(@PathVariable String doctorId) {
        if (doctorId == null || doctorId.isEmpty()) {
            return Result.fail("医生ID不能为空");
        }
        boolean deleted = doctorinfoService.removeById(doctorId);
        return deleted ? Result.success("删除成功") : Result.fail("删除失败或医生不存在");
    }
}
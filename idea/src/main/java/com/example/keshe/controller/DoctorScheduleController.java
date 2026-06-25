package com.example.keshe.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.keshe.entity.DoctorSchedule;
import com.example.keshe.service.IDoctorScheduleService;
import com.example.keshe.dto.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 医生排班控制器（统一返回格式和异常处理）
 */
@RestController
@RequestMapping("/doctorSchedule")
public class DoctorScheduleController {

    @Resource
    private IDoctorScheduleService scheduleService;

    /**
     * 获取排班列表（支持按科室、医生和日期筛选）
     * 接口地址：GET /doctorSchedule/list?departId=xxx&doctorId=xxx&date=xxx
     */
    @GetMapping("/list")
    public Result<List<DoctorSchedule>> getScheduleList(
            @RequestParam(required = false) String departId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String date) {

        QueryWrapper<DoctorSchedule> query = new QueryWrapper<>();

        if (StringUtils.hasText(departId)) {
            query.eq("departId", departId);
        }
        if (StringUtils.hasText(doctorId)) {
            query.eq("doctorId", doctorId);
        }
        if (StringUtils.hasText(date)) {
            query.eq("scheduleDate", date);
        }

        query.orderByAsc("scheduleDate").orderByAsc("workTime");

        List<DoctorSchedule> schedules = scheduleService.list(query);
        return Result.success(schedules);
    }

    /**
     * 根据ID获取单个排班详情
     * 接口地址：GET /doctorSchedule/{id}
     */
    @GetMapping("/{id}")
    public Result<DoctorSchedule> getScheduleById(@PathVariable Long id) {
        if (id == null) {
            return Result.fail("排班ID不能为空");
        }

        DoctorSchedule schedule = scheduleService.getById(id);
        if (schedule == null) {
            return Result.fail("未找到该排班信息");
        }

        return Result.success(schedule);
    }

    /**
     * 新增排班
     * 接口地址：POST /doctorSchedule
     */
    @PostMapping
    public Result<?> addSchedule(@RequestBody DoctorSchedule schedule) {
        // 参数校验
        if (!StringUtils.hasText(schedule.getDoctorId())) {
            return Result.fail("医生ID不能为空");
        }
        if (!StringUtils.hasText(schedule.getDepartId())) {
            return Result.fail("科室ID不能为空");
        }
        if (schedule.getScheduleDate() == null) {
            return Result.fail("排班日期不能为空");
        }
        if (!StringUtils.hasText(schedule.getWorkTime())) {
            return Result.fail("工作时间不能为空");
        }

        boolean success = scheduleService.save(schedule);
        return success ? Result.success("新增排班成功") : Result.fail("新增排班失败");
    }

    /**
     * 更新排班信息
     * 接口地址：PUT /doctorSchedule
     */
    @PutMapping
    public Result<?> updateSchedule(@RequestBody DoctorSchedule schedule) {
        // 参数校验
        if (schedule.getId() == null) {
            return Result.fail("排班ID不能为空");
        }
        if (!StringUtils.hasText(schedule.getDoctorId())) {
            return Result.fail("医生ID不能为空");
        }
        if (!StringUtils.hasText(schedule.getDepartId())) {
            return Result.fail("科室ID不能为空");
        }
        if (schedule.getScheduleDate() == null) {
            return Result.fail("排班日期不能为空");
        }
        if (!StringUtils.hasText(schedule.getWorkTime())) {
            return Result.fail("工作时间不能为空");
        }

        // 检查排班是否存在
        DoctorSchedule existingSchedule = scheduleService.getById(schedule.getId());
        if (existingSchedule == null) {
            return Result.fail("排班不存在");
        }

        boolean success = scheduleService.updateById(schedule);
        return success ? Result.success("更新排班成功") : Result.fail("更新排班失败");
    }

    /**
     * 删除排班信息
     * 接口地址：DELETE /doctorSchedule/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteSchedule(@PathVariable Long id) {
        if (id == null) {
            return Result.fail("排班ID不能为空");
        }

        // 检查排班是否存在
        DoctorSchedule existingSchedule = scheduleService.getById(id);
        if (existingSchedule == null) {
            return Result.fail("排班不存在");
        }

        boolean success = scheduleService.removeById(id);
        return success ? Result.success("删除排班成功") : Result.fail("删除排班失败");
    }

    /**
     * 根据医生ID获取排班列表
     * 接口地址：GET /doctorSchedule/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<DoctorSchedule>> getScheduleByDoctorId(@PathVariable String doctorId) {
        if (!StringUtils.hasText(doctorId)) {
            return Result.fail("医生ID不能为空");
        }

        QueryWrapper<DoctorSchedule> query = new QueryWrapper<>();
        query.eq("doctorId", doctorId)
                .orderByAsc("scheduleDate")
                .orderByAsc("workTime");

        List<DoctorSchedule> schedules = scheduleService.list(query);
        return Result.success(schedules);
    }

    /**
     * 根据日期获取排班列表
     * 接口地址：GET /doctorSchedule/date/{date}
     */
    @GetMapping("/date/{date}")
    public Result<List<DoctorSchedule>> getScheduleByDate(@PathVariable String date) {
        if (!StringUtils.hasText(date)) {
            return Result.fail("日期不能为空");
        }

        QueryWrapper<DoctorSchedule> query = new QueryWrapper<>();
        query.eq("scheduleDate", date)
                .orderByAsc("doctorId")
                .orderByAsc("workTime");

        List<DoctorSchedule> schedules = scheduleService.list(query);
        return Result.success(schedules);
    }

    /**
     * 根据科室ID获取排班列表
     * 接口地址：GET /doctorSchedule/depart/{departId}
     */
    @GetMapping("/depart/{departId}")
    public Result<List<DoctorSchedule>> getScheduleByDepartId(@PathVariable String departId) {
        if (!StringUtils.hasText(departId)) {
            return Result.fail("科室ID不能为空");
        }

        QueryWrapper<DoctorSchedule> query = new QueryWrapper<>();
        query.eq("departId", departId)
                .orderByAsc("scheduleDate")
                .orderByAsc("workTime");

        List<DoctorSchedule> schedules = scheduleService.list(query);
        return Result.success(schedules);
    }
}
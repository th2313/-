package com.example.keshe.controller;

import com.example.keshe.dto.Result;
import com.example.keshe.entity.Patientinfo;
import com.example.keshe.service.IPatientinfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 病人信息控制器
 */
@RestController
@RequestMapping("/patientinfo")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PatientinfoController {

    private final IPatientinfoService patientinfoService;

    public PatientinfoController(IPatientinfoService patientinfoService) {
        this.patientinfoService = patientinfoService;
    }

    /**
     * 新增病人
     */
    @PostMapping
    public Result<String> addPatient(@RequestBody Patientinfo patient) {
        // 验证必填字段
        if (patient.getName() == null || patient.getName().trim().isEmpty()) {
            return Result.fail("病人姓名不能为空");
        }
        if (patient.getGender() == null || patient.getGender().trim().isEmpty()) {
            return Result.fail("性别不能为空");
        }
        if (patient.getDepartName() == null || patient.getDepartName().trim().isEmpty()) {
            return Result.fail("科室名称不能为空");
        }
        if (patient.getDoctorName() == null || patient.getDoctorName().trim().isEmpty()) {
            return Result.fail("主治医生姓名不能为空");
        }
        if (patient.getContactsPhone() == null || patient.getContactsPhone().trim().isEmpty()) {
            return Result.fail("联系电话不能为空");
        }
        if (patient.getBirthday() == null || patient.getBirthday().trim().isEmpty()) {
            return Result.fail("出生日期不能为空");
        }

        try {
            Integer dailyCount = patientinfoService.countByToday();
            patient.generateShortHspID(dailyCount);
            boolean success = patientinfoService.savePatient(patient);
            if (success) {
                return Result.success(patient.getHspID());
            } else {
                return Result.fail("病人信息添加失败");
            }
        } catch (Exception e) {
            return Result.fail("添加失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有病人列表
     */
    @GetMapping
    public Result<List<Patientinfo>> getPatientList() {
        try {
            List<Patientinfo> patientList = patientinfoService.list();
            return Result.success(patientList);
        } catch (Exception e) {
            return Result.fail("查询病人列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据住院编号查询病人
     */
    @GetMapping("/{hspID}")
    public Result<Patientinfo> getPatientByHspID(@PathVariable("hspID") String hspID) {
        try {
            Patientinfo patient = patientinfoService.getById(hspID);
            if (patient != null) {
                return Result.success(patient);
            } else {
                return Result.error(404, "未找到该病人");
            }
        } catch (Exception e) {
            return Result.fail("查询病人失败：" + e.getMessage());
        }
    }

    /**
     * 按姓名模糊查询患者（支持部分匹配）
     */
    @GetMapping("/name")
    public Result<List<Patientinfo>> getPatientsByNameLike(@RequestParam String name) {
        try {
            String trimmedName = name.trim();
            if (trimmedName.isEmpty()) {
                return Result.fail("姓名不能为空");
            }
            List<Patientinfo> patients = patientinfoService.findByNameLike(trimmedName);
            return Result.success(patients);
        } catch (Exception e) {
            return Result.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 更新病人信息
     */
    @PutMapping("/{hspID}")
    public Result<String> updatePatient(
            @PathVariable("hspID") String hspID,
            @RequestBody Patientinfo patient) {
        patient.setHspID(hspID);
        try {
            boolean success = patientinfoService.updateById(patient);
            if (success) {
                return Result.success("病人信息更新成功");
            } else {
                return Result.error(404, "未找到该病人，更新失败");
            }
        } catch (Exception e) {
            return Result.fail("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除病人信息
     */
    @DeleteMapping("/{hspID}")
    public Result<String> deletePatient(@PathVariable("hspID") String hspID) {
        try {
            boolean success = patientinfoService.removeById(hspID);
            if (success) {
                return Result.success("病人信息删除成功");
            } else {
                return Result.error(404, "未找到该病人，删除失败");
            }
        } catch (Exception e) {
            return Result.fail("删除失败：" + e.getMessage());
        }
    }
}
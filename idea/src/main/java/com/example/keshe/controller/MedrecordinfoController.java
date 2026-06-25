package com.example.keshe.controller;

import com.example.keshe.dto.Result;
import com.example.keshe.entity.Medrecordinfo;
import com.example.keshe.entity.Patientinfo;
import com.example.keshe.entity.Templateinfo;
import com.example.keshe.service.MedrecordinfoService;
import com.example.keshe.service.IPatientinfoService;
import com.example.keshe.service.TemplateinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 电子病历控制器
 */
@RestController
@RequestMapping("/medrecordinfo")
public class MedrecordinfoController {

    @Autowired
    private MedrecordinfoService medrecordinfoService;

    @Autowired
    private IPatientinfoService  patientInfoService;

    @Autowired
    private TemplateinfoService templateinfoService;

    /**
     * 根据患者ID获取病历信息
     */
    @GetMapping("/{hspID}")
    public Result getRecord(@PathVariable String hspID) {
        // 验证患者是否存在
        Patientinfo patient = patientInfoService.getById(hspID);
        if (patient == null) {
            return Result.fail("患者不存在");
        }

        Medrecordinfo record = medrecordinfoService.getByHspId(hspID);

        // 如果病历不存在，返回空对象
        if (record == null) {
            record = new Medrecordinfo();
            record.setHspID(hspID);
        }

        return Result.success(record);
    }

    /**
     * 保存或更新病历信息
     */
    @PostMapping("/save")
    public Result saveRecord(@RequestBody Medrecordinfo medrecordinfo) {
        // 验证患者是否存在
        Patientinfo patient = patientInfoService.getById(medrecordinfo.getHspID());
        if (patient == null) {
            return Result.fail("患者不存在，无法创建病历");
        }

        boolean success = medrecordinfoService.saveOrUpdateRecord(medrecordinfo);
        return success ? Result.success("保存成功") : Result.fail("保存失败");
    }

    /**
     * 将病历保存为模板
     */
    @PostMapping("/saveAsTemplate")
    public Result saveAsTemplate(@RequestBody Medrecordinfo medrecordinfo,
                                 @RequestParam String templateID) {
        // 检查模板ID是否已存在
        Templateinfo existingTemplate = templateinfoService.getByTemplateId(templateID);
        if (existingTemplate != null) {
            return Result.fail("模板ID已存在");
        }

        boolean success = medrecordinfoService.saveAsTemplate(medrecordinfo, templateID);
        return success ? Result.success("模板保存成功") : Result.fail("模板保存失败");
    }

    /**
     * 获取所有模板
     */
    @GetMapping("/templates")
    public Result getAllTemplates() {
        List<Templateinfo> templates = templateinfoService.getAllTemplates();
        return Result.success(templates);
    }

    /**
     * 根据模板创建新病历
     */
    @PostMapping("/createFromTemplate")
    public Result createFromTemplate(@RequestParam String hspID, @RequestParam String templateID) {
        // 验证患者是否存在
        Patientinfo patient = patientInfoService.getById(hspID);
        if (patient == null) {
            return Result.fail("患者不存在");
        }

        // 验证模板是否存在
        Templateinfo template = templateinfoService.getByTemplateId(templateID);
        if (template == null) {
            return Result.fail("模板不存在");
        }

        // 根据模板创建病历
        Medrecordinfo medrecordinfo = medrecordinfoService.createFromTemplate(hspID, templateID);
        if (medrecordinfo == null) {
            return Result.fail("创建病历失败");
        }

        // 保存新病历
        medrecordinfoService.saveOrUpdateRecord(medrecordinfo);
        return Result.success(medrecordinfo);
    }
    /**
     * 批量获取病历（支持按患者ID模糊查询）
     */
    @GetMapping("/list")
    public Result getRecordList(@RequestParam(required = false) String hspID) {
        List<Medrecordinfo> records;
        if (hspID != null && !hspID.trim().isEmpty()) {
            // 模糊查询
            records = medrecordinfoService.listByHspId(hspID);
        } else {
            // 查询全部
            records = medrecordinfoService.list();
        }
        return Result.success(records);
    }
    @GetMapping("/listByHspIDs")
    public Result listByHspIDs(@RequestParam("hspIDs") String hspIDs) {
        try {
            // 将逗号分隔的ID字符串转为列表（去空处理）
            List<String> idList = Arrays.stream(hspIDs.split(","))
                    .map(String::trim)
                    .filter(id -> !id.isEmpty())
                    .collect(Collectors.toList());

            // 批量查询病历
            List<Medrecordinfo> records = medrecordinfoService.listByHspIds(idList);
            return Result.success(records);
        } catch (Exception e) {
            return Result.fail("批量查询病历失败：" + e.getMessage());
        }
    }
    /**
     * 根据患者ID删除病历
     */
    @DeleteMapping("/{hspID}")
    public Result deleteRecord(@PathVariable String hspID) {
        // 验证患者是否存在
        Patientinfo patient = patientInfoService.getById(hspID);
        if (patient == null) {
            return Result.fail("患者不存在");
        }

        // 验证病历是否存在
        Medrecordinfo record = medrecordinfoService.getByHspId(hspID);
        if (record == null) {
            return Result.fail("该患者无病历记录");
        }

        boolean success = medrecordinfoService.removeByHspId(hspID);
        return success ? Result.success("病历删除成功") : Result.fail("病历删除失败");
    }

    /**
     * 批量删除病历
     */
    @DeleteMapping("/batch")
    public Result batchDeleteRecords(@RequestBody List<String> hspIDs) {
        if (hspIDs == null || hspIDs.isEmpty()) {
            return Result.fail("请选择需要删除的病历");
        }

        // 验证所有患者是否存在
        List<String> invalidIds = hspIDs.stream()
                .filter(id -> patientInfoService.getById(id) == null)
                .collect(Collectors.toList());

        if (!invalidIds.isEmpty()) {
            return Result.fail("以下患者不存在：" + String.join(",", invalidIds));
        }

        // 执行批量删除
        boolean success = medrecordinfoService.removeByHspIds(hspIDs);
        return success ? Result.success("批量删除成功") : Result.fail("批量删除失败");
    }
}

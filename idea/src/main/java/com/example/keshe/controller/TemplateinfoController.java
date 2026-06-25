package com.example.keshe.controller;

import com.example.keshe.dto.Result;
import com.example.keshe.entity.Templateinfo;
import com.example.keshe.service.TemplateinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 病历模板控制器
 * 专门处理模板的增删改查等操作
 */
@RestController
@RequestMapping("/templateinfo")
public class TemplateinfoController {

    @Autowired
    private TemplateinfoService templateinfoService;

    /**
     * 获取所有模板列表
     */
    @GetMapping("/list")
    public Result<List<Templateinfo>> getAllTemplates() {
        List<Templateinfo> templates = templateinfoService.getAllTemplates();
        return Result.success(templates);
    }

    /**
     * 根据模板ID获取模板详情
     */
    @GetMapping("/{templateID}")
    public Result<Templateinfo> getTemplateById(@PathVariable String templateID) {
        Templateinfo template = templateinfoService.getByTemplateId(templateID);
        if (template == null) {
            return Result.fail("模板不存在");
        }
        return Result.success(template);
    }

    /**
     * 新增模板
     */
    @PostMapping("/add")
    public Result addTemplate(@RequestBody Templateinfo templateinfo) {
        // 检查模板ID是否已存在
        Templateinfo existingTemplate = templateinfoService.getByTemplateId(templateinfo.getTemplateID());
        if (existingTemplate != null) {
            return Result.fail("模板ID已存在");
        }
        boolean success = templateinfoService.save(templateinfo);
        return success ? Result.success("模板新增成功") : Result.fail("模板新增失败");
    }

    /**
     * 更新模板内容
     */
    @PutMapping("/update")
    public Result updateTemplate(@RequestBody Templateinfo templateinfo) {
        // 检查模板是否存在
        Templateinfo existingTemplate = templateinfoService.getByTemplateId(templateinfo.getTemplateID());
        if (existingTemplate == null) {
            return Result.fail("模板不存在，无法更新");
        }
        boolean success = templateinfoService.updateById(templateinfo);
        return success ? Result.success("模板更新成功") : Result.fail("模板更新失败");
    }

    /**
     * 删除模板
     */
    @DeleteMapping("/{templateID}")
    public Result deleteTemplate(@PathVariable String templateID) {
        // 检查模板是否存在
        Templateinfo template = templateinfoService.getByTemplateId(templateID);
        if (template == null) {
            return Result.fail("模板不存在，无法删除");
        }
        boolean success = templateinfoService.removeById(templateID);
        return success ? Result.success("模板删除成功") : Result.fail("模板删除失败");
    }

    /**
     * 导入模板
     */
    @PostMapping("/import")
    public Result importTemplates(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.fail("请选择要导入的文件");
            }
            int count = templateinfoService.importTemplates(file);
            return Result.success("导入成功，共导入" + count + "条模板");
        } catch (Exception e) {
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    /**
     * 导出所有模板（带关键词筛选）
     */
    @GetMapping("/export")
    public void exportTemplates(@RequestParam(required = false) String keyword, HttpServletResponse response) {
        try {
            templateinfoService.exportTemplates(keyword, response);
        } catch (IOException e) {
            handleExportError(response, e);
        }
    }

    /**
     * 按选中的模板ID列表导出模板
     */
    @GetMapping("/exportByIds")
    public void exportByIds(@RequestParam List<String> templateIds, HttpServletResponse response) {
        try {
            // 校验参数
            if (templateIds == null || templateIds.isEmpty()) {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":400,\"msg\":\"请选择要导出的模板\"}");
                return;
            }
            // 调用服务层方法导出
            templateinfoService.exportByIds(templateIds, response);
        } catch (IOException e) {
            handleExportError(response, e);
        }
    }

    /**
     * 统一处理导出异常
     */
    private void handleExportError(HttpServletResponse response, Exception e) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":500,\"msg\":\"导出失败：" + e.getMessage() + "\"}");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        e.printStackTrace();
    }
}

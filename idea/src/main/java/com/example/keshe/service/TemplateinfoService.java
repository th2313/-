package com.example.keshe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.keshe.entity.Templateinfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 病历模板服务接口
 */
public interface TemplateinfoService extends IService<Templateinfo> {
    // 获取所有模板
    List<Templateinfo> getAllTemplates();

    // 根据模板ID获取模板
    Templateinfo getByTemplateId(String templateID);

    // 按关键词导出模板
    void exportTemplates(String keyword, HttpServletResponse response) throws IOException;

    // 导入模板
    int importTemplates(MultipartFile file) throws Exception;

    // 新增：按ID列表导出选中的模板
    void exportByIds(List<String> templateIds, HttpServletResponse response) throws IOException;
}

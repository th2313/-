package com.example.keshe.service;

import com.example.keshe.entity.Templateinfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 模板服务接口
 */
public interface ITemplateinfoService extends IService<Templateinfo> {
    List<Templateinfo> getAllTemplates();

    Templateinfo getByTemplateId(String templateID);

    // 导入模板
    int importTemplates(MultipartFile file) throws Exception;

    // 导出模板
    void exportTemplates(String keyword, HttpServletResponse response) throws IOException;
}

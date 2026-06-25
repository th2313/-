package com.example.keshe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.keshe.entity.Templateinfo;
import com.example.keshe.mapper.TemplateinfoMapper;
import com.example.keshe.service.TemplateinfoService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 病历模板服务实现类（Excel导出优化版）
 * 核心优化：样式美化、内容防溢出、中文防乱码、代码复用
 */
@Service
public class TemplateinfoServiceImpl extends ServiceImpl<TemplateinfoMapper, Templateinfo> implements TemplateinfoService {

    // ---------------------- 原有基础方法（保持不变） ----------------------
    @Override
    public List<Templateinfo> getAllTemplates() {
        return baseMapper.selectList(null);
    }

    @Override
    public Templateinfo getByTemplateId(String templateID) {
        return baseMapper.selectById(templateID);
    }

    // ---------------------- 导入方法（保持原有逻辑） ----------------------
    @Override
    public int importTemplates(MultipartFile file) throws Exception {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // 读取第一个工作表

        int successCount = 0;
        // 从第二行开始读（跳过表头）
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) continue;

            Templateinfo template = new Templateinfo();
            Cell idCell = row.getCell(0);
            if (idCell != null) {
                template.setTemplateID(getCellValue(idCell));
                // 避免ID重复
                if (baseMapper.selectById(template.getTemplateID()) == null) {
                    // 按Excel列顺序赋值
                    template.set主诉(getCellValue(row.getCell(1)));
                    template.set现病史(getCellValue(row.getCell(2)));
                    template.set既往史(getCellValue(row.getCell(3)));
                    template.set婚育史(getCellValue(row.getCell(4)));
                    template.set月经史(getCellValue(row.getCell(5)));
                    template.set家族史(getCellValue(row.getCell(6)));
                    template.set体格检查(getCellValue(row.getCell(7)));
                    template.set专科检查(getCellValue(row.getCell(8)));
                    template.set辅助检查(getCellValue(row.getCell(9)));
                    template.set鉴别诊断(getCellValue(row.getCell(10)));
                    template.set初步诊断(getCellValue(row.getCell(11)));
                    template.set诊疗计划(getCellValue(row.getCell(12)));

                    baseMapper.insert(template);
                    successCount++;
                }
            }
        }

        workbook.close();
        return successCount;
    }

    // ---------------------- 按关键词导出（优化版） ----------------------
    @Override
    public void exportTemplates(String keyword, HttpServletResponse response) throws IOException {
        List<Templateinfo> templates = getTemplatesByKeyword(keyword); // 复用查询逻辑
        String fileName = "病历模板列表_" + System.currentTimeMillis(); // 文件名加时间戳防重复
        exportToExcel(templates, fileName, response); // 调用通用导出方法
    }

    // ---------------------- 按选中ID导出（新增核心方法） ----------------------
    @Override
    public void exportByIds(List<String> templateIds, HttpServletResponse response) throws IOException {
        // 按ID列表查询数据（MyBatis-Plus自带方法）
        List<Templateinfo> templates = baseMapper.selectBatchIds(templateIds);
        // 空数据校验，避免生成空Excel
        if (templates.isEmpty()) {
            throw new IOException("未找到对应模板数据，请检查模板ID是否正确");
        }
        // 文件名区分“选中导出”，加时间戳防重复
        String fileName = "选中病历模板_" + System.currentTimeMillis();
        exportToExcel(templates, fileName, response); // 复用通用导出方法
    }

    // ---------------------- 通用Excel导出（整合所有优化点） ----------------------
    private void exportToExcel(List<Templateinfo> templates, String fileName, HttpServletResponse response) throws IOException {
        // 1. 创建Excel工作簿（XLSX格式，兼容Excel 2007+）
        Workbook workbook = WorkbookFactory.create(true);
        Sheet sheet = workbook.createSheet("病历模板数据");
        sheet.setDefaultColumnWidth(15); // 默认列宽，避免过窄

        // 2. 定义样式（美观+易读）
        CellStyle titleStyle = createTitleStyle(workbook);    // 标题行样式（蓝色背景+加粗）
        CellStyle headerStyle = createHeaderStyle(workbook);  // 表头样式（灰色背景+边框）
        CellStyle dataStyle1 = createDataStyle(workbook, IndexedColors.WHITE.getIndex());       // 数据行样式1（白色）
        CellStyle dataStyle2 = createDataStyle(workbook, IndexedColors.PALE_BLUE.getIndex());   // 数据行样式2（浅蓝，交替显示）

        // 3. 构建Excel内容：标题行 → 表头行 → 数据行
        // 3.1 标题行（合并列，醒目）
        int titleRowNum = 0;
        Row titleRow = sheet.createRow(titleRowNum);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("病历模板导出数据"); // 标题内容
        titleCell.setCellStyle(titleStyle);
        // 合并标题行（从第0列到第12列，与表头列数一致）
        sheet.addMergedRegion(new CellRangeAddress(titleRowNum, titleRowNum, 0, 12));
        titleRow.setHeightInPoints(30); // 标题行高度，避免文字挤在一起

        // 3.2 表头行（灰色背景，加粗边框）
        int headerRowNum = 1; // 表头行在标题行下方
        Row headerRow = sheet.createRow(headerRowNum);
        // 表头内容（与数据库字段对应，顺序一致）
        String[] headers = {
                "模板ID", "主诉", "现病史", "既往史", "婚育史",
                "月经史", "家族史", "体格检查", "专科检查",
                "辅助检查", "鉴别诊断", "初步诊断", "诊疗计划"
        };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            // 关键：长文本列手动调宽（避免“主诉”“现病史”等内容溢出）
            if (i == 1 || i == 2 || i == 7 || i == 8) { // 1=主诉，2=现病史，7=体格检查，8=专科检查
                sheet.setColumnWidth(i, 30 * 256); // POI中1字符=256单位，30字符足够显示长内容
            }
        }
        headerRow.setHeightInPoints(25); // 表头行高度

        // 3.3 数据行（交替背景+自动换行+空值处理）
        for (int i = 0; i < templates.size(); i++) {
            Templateinfo template = templates.get(i);
            int dataRowNum = headerRowNum + 1 + i; // 数据行从表头行下方开始
            Row dataRow = sheet.createRow(dataRowNum);
            // 交替应用样式（白→浅蓝→白...，防眼花）
            CellStyle currentStyle = (i % 2 == 0) ? dataStyle1 : dataStyle2;
            dataRow.setHeightInPoints(20); // 数据行高度，适配自动换行

            // 按表头顺序赋值，空值显示“-”（避免空白误解）
            setCellValue(dataRow, 0, template.getTemplateID(), currentStyle);
            setCellValue(dataRow, 1, template.get主诉(), currentStyle);
            setCellValue(dataRow, 2, template.get现病史(), currentStyle);
            setCellValue(dataRow, 3, template.get既往史(), currentStyle);
            setCellValue(dataRow, 4, template.get婚育史(), currentStyle);
            setCellValue(dataRow, 5, template.get月经史(), currentStyle);
            setCellValue(dataRow, 6, template.get家族史(), currentStyle);
            setCellValue(dataRow, 7, template.get体格检查(), currentStyle);
            setCellValue(dataRow, 8, template.get专科检查(), currentStyle);
            setCellValue(dataRow, 9, template.get辅助检查(), currentStyle);
            setCellValue(dataRow, 10, template.get鉴别诊断(), currentStyle);
            setCellValue(dataRow, 11, template.get初步诊断(), currentStyle);
            setCellValue(dataRow, 12, template.get诊疗计划(), currentStyle);
        }

        // 4. 响应配置（核心：防中文乱码+禁缓存）
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // 正确的Excel mime类型
        response.setCharacterEncoding("UTF-8");
        // 中文文件名编码（兼容Chrome、Edge、Firefox等所有浏览器）
        String encodedFileName = URLEncoder.encode(fileName + ".xlsx", "UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        // 禁用缓存（确保每次下载的都是最新数据，避免缓存旧文件）
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setDateHeader("Expires", 0);

        // 5. 写入响应流（try-with-resources自动关闭流，避免资源泄漏）
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
            os.flush();
        } finally {
            workbook.close(); // 手动关闭工作簿，释放资源
        }
    }

    // ---------------------- 样式工具方法（复用，避免重复代码） ----------------------
    /**
     * 标题行样式：蓝色背景+16号加粗字体+居中
     */
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 字体配置
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16); // 16号字，醒目
        font.setColor(IndexedColors.DARK_BLUE.getIndex()); // 深蓝色字体
        style.setFont(font);
        // 对齐方式
        style.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        // 背景色
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); // 浅蓝色背景
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 纯色填充
        return style;
    }

    /**
     * 表头样式：灰色背景+12号加粗字体+黑色边框
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 字体配置
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12); // 12号字，清晰
        font.setColor(IndexedColors.BLACK.getIndex()); // 黑色字体
        style.setFont(font);
        // 对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 背景色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // 浅灰色背景
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 边框（调用通用边框方法）
        setCellBorder(style, BorderStyle.THIN, IndexedColors.BLACK.getIndex());
        return style;
    }

    /**
     * 数据行样式：自动换行+边框+自定义背景色
     */
    private CellStyle createDataStyle(Workbook workbook, short bgColor) {
        CellStyle style = workbook.createCellStyle();
        // 字体配置（11号字，常规）
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        // 对齐方式（水平左对齐，长文本易读）
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 背景色（传入的颜色值，支持交替显示）
        style.setFillForegroundColor(bgColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 边框（深灰色，比表头淡，层次分明）
        setCellBorder(style, BorderStyle.THIN, IndexedColors.GREY_50_PERCENT.getIndex());
        // 关键：自动换行（长文本会折行显示，无需手动拉列宽）
        style.setWrapText(true);
        return style;
    }

    /**
     * 通用边框设置方法：给单元格加上下左右边框（复用）
     */
    private void setCellBorder(CellStyle style, BorderStyle borderStyle, short color) {
        style.setBorderTop(borderStyle);
        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setBorderRight(borderStyle);
        // 边框颜色
        style.setTopBorderColor(color);
        style.setBottomBorderColor(color);
        style.setLeftBorderColor(color);
        style.setRightBorderColor(color);
    }

    // ---------------------- 辅助工具方法 ----------------------
    /**
     * 按关键词查询模板（复用逻辑，避免exportTemplates方法冗余）
     */
    private List<Templateinfo> getTemplatesByKeyword(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            // 多字段模糊查询（模板ID、主诉、初步诊断）
            QueryWrapper<Templateinfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("TemplateID", keyword)
                    .or().like("主诉", keyword)
                    .or().like("初步诊断", keyword);
            return baseMapper.selectList(queryWrapper);
        }
        // 无关键词时，查询所有模板
        return baseMapper.selectList(null);
    }

    /**
     * 设置单元格值（空值处理：显示“-”，避免空白单元格误解）
     */
    private void setCellValue(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        // 空值判断：null或空字符串都显示“-”
        String cellValue = (value == null || value.trim().isEmpty()) ? "-" : value;
        cell.setCellValue(cellValue);
        cell.setCellStyle(style); // 应用样式
    }

    /**
     * 读取Excel单元格值（处理不同类型：字符串、数字、日期等，避免科学计数法）
     */
    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue(); // 字符串类型直接取
            case NUMERIC:
                // 日期类型转字符串，普通数字转long避免科学计数法（如ID为数字时）
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()); // 布尔类型转字符串
            case FORMULA:
                return cell.getCellFormula(); // 公式类型取公式（可根据需求调整）
            default:
                return ""; // 其他类型返回空字符串
        }
    }
}
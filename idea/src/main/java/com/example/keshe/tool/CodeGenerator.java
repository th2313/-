package com.example.keshe.tool;


import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Types;
import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/keshe?useSSL=false&serverTimezone=UTC", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("jaun") // 设置作者
                            .disableOpenDir()
                            .outputDir((System.getProperty("user.dir")+"/src/main/java")); // 指定输出目录
                })
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.SMALLINT) {
                                // 自定义类型转换
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder ->
                        builder.parent("com.example.keshe") // 设置父包名
                                .moduleName("") // 设置模块名（默认值），就是父包名的子模块名称，适用于单体多模块项目
                                .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir")+"/src/main/resources/mapper")) // 设置mapperXml生成路径
                )
                .strategyConfig(builder ->
                        builder.enableSkipView()
                                .entityBuilder().enableLombok(new ClassAnnotationAttributes("@Data","lombok.Data"))
                                .mapperBuilder().mapperAnnotation(Mapper.class)
                )
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}

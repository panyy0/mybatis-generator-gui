package com.taurus.mybatis.generator.plugins;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MapperExtPlugin extends PluginAdapter {

    private static final String INTERFACE_EXT = "Ext";

    private ShellCallback shellCallback = null;

    public MapperExtPlugin() {
        shellCallback = new DefaultShellCallback(false);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        // 获取mapper接口所在路径，接口名字，生成一个Ext的接口
        JavaFormatter javaFormatter = context.getJavaFormatter();
        String mapperTargetDir = context.getJavaClientGeneratorConfiguration().getTargetProject();
        String mapperTargetPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
        List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<>();
        String javaFileEncoding = context.getProperty("javaFileEncoding");
        List<GeneratedJavaFile> generatedJavaFiles = introspectedTable.getGeneratedJavaFiles();
        String mapperName = null;
        for (GeneratedJavaFile generatedJavaFile : generatedJavaFiles) {
            if (generatedJavaFile.getCompilationUnit().getType().getShortName().endsWith("Mapper")) {
                mapperName = generatedJavaFile.getCompilationUnit().getType().getShortName();
                break;
            }
        }
        Interface mapperInterface = new Interface(mapperTargetPackage + "." + mapperName + INTERFACE_EXT);
        mapperInterface.setVisibility(JavaVisibility.PUBLIC);

        mapperInterface.addJavaDocLine("/**");
        mapperInterface.addJavaDocLine(" * " + "生成的Ext类，自定义的数据库操作在这里编写");
        mapperInterface.addJavaDocLine(" */");

        mapperInterface.addSuperInterface(new FullyQualifiedJavaType(mapperTargetPackage + "." + mapperName));
        GeneratedJavaFile mapperJavaFile = new GeneratedJavaFile(mapperInterface, mapperTargetDir, javaFileEncoding, javaFormatter);
        try {
            File mapperDir = shellCallback.getDirectory(mapperTargetDir, mapperTargetPackage);
            File mapperFile = new File(mapperDir, mapperJavaFile.getFileName());
            // 文件不存在
            if (!mapperFile.exists()) {
                mapperJavaFiles.add(mapperJavaFile);
            }
        } catch (ShellException e) {
            log.error("生成文件错误", e);
        }
        return mapperJavaFiles;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        List<GeneratedXmlFile> generatedXmlFiles = introspectedTable.getGeneratedXmlFiles();
        String mapperXmlTargetDir = context.getSqlMapGeneratorConfiguration().getTargetPackage();
        log.info("mapperXmlTargetDir: {}", mapperXmlTargetDir);
        GeneratedXmlFile mapperXmlFile = introspectedTable.getGeneratedXmlFiles().get(0);
        String mapperName = null;
        String shortMapperName = null;
        List<GeneratedJavaFile> generatedJavaFiles = introspectedTable.getGeneratedJavaFiles();
        for (GeneratedJavaFile generatedJavaFile : generatedJavaFiles) {
            if (generatedJavaFile.getCompilationUnit().getType().getShortName().endsWith("Mapper")) {
                mapperName = generatedJavaFile.getCompilationUnit().getType().getFullyQualifiedName();
                shortMapperName = generatedJavaFile.getCompilationUnit().getType().getShortName();
                break;
            }
        }
        String extMapperXmlName = shortMapperName + INTERFACE_EXT + ".xml";
        log.info("mapperName={}", mapperName + INTERFACE_EXT);
        // 生成ext xml文件
        Document document = new Document("-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
        XmlElement element = new XmlElement("mapper");
        element.addAttribute(new Attribute("namespace", mapperName + INTERFACE_EXT));
        element.addElement(new TextElement("<!-- show your code -- >"));
        XmlElement rootElement = new XmlElement(element);
        document.setRootElement(rootElement);
        GeneratedXmlFile generatedXmlFile = new GeneratedXmlFile(
                document,
                extMapperXmlName,
                mapperXmlTargetDir,
                mapperXmlFile.getTargetProject(),
                true,
                context.getXmlFormatter());
        File mapperFile = new File(mapperXmlTargetDir, extMapperXmlName);
        if (!mapperFile.exists()) {
            generatedXmlFiles.add(generatedXmlFile);
        }

        return generatedXmlFiles;
    }

    @Override
    public boolean validate(List<String> list) {

        return true;
    }
}

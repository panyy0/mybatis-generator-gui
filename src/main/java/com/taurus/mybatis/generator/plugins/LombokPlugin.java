package com.taurus.mybatis.generator.plugins;


import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.*;

/**
 * 生成代码时，自动添加Lombok注解
 */
public class LombokPlugin extends PluginAdapter {

    private final Collection<Annotations> annotations;

    /**
     * LombokPlugin constructor
     */
    public LombokPlugin() {
        annotations = new LinkedHashSet<Annotations>(Annotations.values().length);
    }

    /**
     * @param warnings
     *          list of warnings
     * @return always true
     */
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * Intercepts base record class generation
     *
     * @param topLevelClass
     *            the generated base record class
     * @param introspectedTable
     *            The class containing information about the table as
     *            introspected from the database
     * @return always true
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        addDataAnnotation(topLevelClass);
        return true;
    }

    /**
     * Intercepts primary key class generation
     *
     * @param topLevelClass
     *            the generated primary key class
     * @param introspectedTable
     *            The class containing information about the table as
     *            introspected from the database
     * @return always true
     */
    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        addDataAnnotation(topLevelClass);
        return true;
    }

    /**
     * Intercepts "record with blob" class generation
     *
     * @param topLevelClass
     *            the generated record with BLOBs class
     * @param introspectedTable
     *            The class containing information about the table as
     *            introspected from the database
     * @return always true
     */
    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addDataAnnotation(topLevelClass);
        return true;
    }

    /**
     * Prevents all getters from being generated.
     * See SimpleModelGenerator
     *
     * @param method
     *            the getter, or accessor, method generated for the specified
     *            column
     * @param topLevelClass
     *            the partially implemented model class
     * @param introspectedColumn
     *            The class containing information about the column related
     *            to this field as introspected from the database
     * @param introspectedTable
     *            The class containing information about the table as
     *            introspected from the database
     * @param modelClassType
     *            the type of class that the field is generated for
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }

    /**
     * Prevents all setters from being generated
     * See SimpleModelGenerator
     *
     * @param method
     *            the setter, or mutator, method generated for the specified
     *            column
     * @param topLevelClass
     *            the partially implemented model class
     * @param introspectedColumn
     *            The class containing information about the column related
     *            to this field as introspected from the database
     * @param introspectedTable
     *            The class containing information about the table as
     *            introspected from the database
     * @param modelClassType
     *            the type of class that the field is generated for
     * @return always false
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }

    /**
     * 将Lombok库中的注解导入语句添加到类中，并在类或其属性、方法上添加相应的Lombok注解，以便利用Lombok提供的简化代码功能，如自动生成getter和setter方法、构造函数等。
     *
     * @param topLevelClass
     *            the partially implemented model class
     */
    private void addDataAnnotation(TopLevelClass topLevelClass) {
        for (Annotations annotation : annotations) {
            topLevelClass.addImportedType(annotation.javaType);
            topLevelClass.addAnnotation(annotation.name);
        }
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        //@Data is default annotation
        annotations.add(Annotations.DATA);
        annotations.add(Annotations.TO_STRING);
        annotations.add(Annotations.EQUALS_AND_HASHCODE);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            boolean isEnable = Boolean.parseBoolean(entry.getValue().toString());

            if (isEnable) {
                String paramName = entry.getKey().toString().trim();
                Annotations annotation = Annotations.getValueOf(paramName);
                if (annotation != null) {
                    annotations.add(annotation);
                    annotations.addAll(Annotations.getDependencies(annotation));
                }
            }
        }
    }

    private enum Annotations {
        DATA("data", "@Data", "lombok.Data"),
        BUILDER("builder", "@Builder", "lombok.Builder"),
        ALL_ARGS_CONSTRUCTOR("allArgsConstructor", "@AllArgsConstructor", "lombok.AllArgsConstructor"),
        NO_ARGS_CONSTRUCTOR("noArgsConstructor", "@NoArgsConstructor", "lombok.NoArgsConstructor"),
        TO_STRING("toString", "@ToString", "lombok.ToString"),
        EQUALS_AND_HASHCODE("equalsAndHashCode", "@EqualsAndHashCode", "lombok.EqualsAndHashCode"),

        ;


        private final String paramName;
        private final String name;
        private final FullyQualifiedJavaType javaType;


        Annotations(String paramName, String name, String className) {
            this.paramName = paramName;
            this.name = name;
            this.javaType = new FullyQualifiedJavaType(className);
        }

        private static Annotations getValueOf(String paramName) {
            for (Annotations annotation : Annotations.values())
                if (String.CASE_INSENSITIVE_ORDER.compare(paramName, annotation.paramName) == 0)
                    return annotation;

            return null;
        }

        private static Collection<Annotations> getDependencies(Annotations annotation) {
            if (annotation == ALL_ARGS_CONSTRUCTOR)
                return Collections.singleton(NO_ARGS_CONSTRUCTOR);
            else
                return Collections.emptyList();
        }
    }
}

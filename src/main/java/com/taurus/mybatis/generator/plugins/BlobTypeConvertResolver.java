package com.taurus.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;
import java.util.Objects;

public class BlobTypeConvertResolver extends JavaTypeResolverDefaultImpl {

    @Override
    public FullyQualifiedJavaType calculateJavaType(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType qualifiedJavaType = null;
        JdbcTypeInformation jdbcTypeInformation;
        // 如果是 text或者longtext，转换为varchar
        if (Types.LONGVARCHAR == introspectedColumn.getJdbcType()) {
            introspectedColumn.setJdbcType(Types.VARCHAR);
            jdbcTypeInformation = typeMap.get(Types.VARCHAR);
        }
        // 其他类型不修改
        else {
            jdbcTypeInformation = typeMap.get(introspectedColumn.getJdbcType());
        }
        if (Objects.nonNull(jdbcTypeInformation)) {
            qualifiedJavaType = jdbcTypeInformation.getFullyQualifiedJavaType();
            qualifiedJavaType = overrideDefaultType(introspectedColumn, qualifiedJavaType);
        }
        return qualifiedJavaType;
    }
}

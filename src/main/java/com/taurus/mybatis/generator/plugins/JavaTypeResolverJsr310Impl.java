package com.taurus.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * @author hanakei
 * @since 2018/4/28
 */
public class JavaTypeResolverJsr310Impl extends JavaTypeResolverDefaultImpl {

    @Override
    protected FullyQualifiedJavaType overrideDefaultType(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer = defaultType;

        switch (column.getJdbcType()) {
            case Types.BIT:
                answer = calculateBitReplacement(column, defaultType);
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                answer = calculateBigDecimalReplacement(column, defaultType);
                break;
            case Types.DATE:
                answer = new FullyQualifiedJavaType(LocalDate.class.getName());
                break;
            case Types.TIME:
                answer = new FullyQualifiedJavaType(LocalTime.class.getName());
                break;
            case Types.TIMESTAMP:
                answer = new FullyQualifiedJavaType(LocalDateTime.class.getName());
                break;
            default:
                break;
        }

        return answer;
    }

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

package com.flagwind.mybatis.helpers;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.meta.EntityField;
import com.flagwind.mybatis.utils.StringUtil;
import com.flagwind.mybatis.utils.TypeUtils;
import com.flagwind.persistent.AggregateEntry;
import com.flagwind.persistent.ColumnTypeEntry;
import com.flagwind.persistent.annotation.Aggregate;
import com.flagwind.persistent.annotation.ColumnType;


import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

/**
 * 数据列帮助类
 */
public class ColumnHelper {

    public static JdbcType formJavaType(Class<?> javaType){
        if(javaType==null){
            return  JdbcType.UNDEFINED;
        }
        if(javaType.isAssignableFrom(String.class)){
            return JdbcType.VARCHAR;
        }
        if(javaType.isAssignableFrom(Integer.class)
                ||javaType.isAssignableFrom(int.class)){
            return JdbcType.INTEGER;
        }
        if(javaType.isAssignableFrom(Number.class)){
            return JdbcType.NUMERIC;
        }
        if(javaType.isAssignableFrom(Long.class)
                ||javaType.isAssignableFrom(long.class)){
            return JdbcType.NUMERIC;
        }
        if(javaType.isAssignableFrom(Double.class)
                ||javaType.isAssignableFrom(double.class)){
            return JdbcType.NUMERIC;
        }
        if(javaType.isAssignableFrom(Boolean.class)
                ||javaType.isAssignableFrom(boolean.class)){
            return JdbcType.TINYINT;
        }
        if(javaType.isAssignableFrom(Float.class)
                ||javaType.isAssignableFrom(float.class)){
            return JdbcType.FLOAT;
        }

        if(javaType.isAssignableFrom(Timestamp.class) ){
            return JdbcType.TIMESTAMP;
        }

        if(javaType.isAssignableFrom(java.sql.Time.class) ){
            return JdbcType.TIME;
        }

        if(javaType.isAssignableFrom(java.sql.Date.class)
                ||javaType.isAssignableFrom(java.util.Date.class)){
            return JdbcType.DATE;
        }

        if(javaType.isAssignableFrom(byte[].class) ){
            return JdbcType.BINARY;
        }
        if(javaType.isEnum()){
            return JdbcType.VARCHAR;
        }
        return JdbcType.UNDEFINED;
    }


    public static AggregateEntry getAggregateEntry(EntityField field) {
        if (field.isAnnotationPresent(Aggregate.class)) {
            return null;
        }
        Aggregate aggregate = field.getAnnotation(Aggregate.class);
        String column = aggregate.column();
        column = StringUtil.isEmpty(column) ? field.getName() : column;
        return new AggregateEntry(aggregate.type(), field.getName(), column);
    }

    public static Class<? extends  TypeHandler<?>> getEnumTypeHandler(EntityField field) {

        if (field.getJavaType().isEnum()) {
            if (field.isAnnotationPresent(Enumerated.class)) {
                // 获取注解对象
                Enumerated enumerated = field.getAnnotation(Enumerated.class);
                // 设置了value属性
                if (enumerated.value() == EnumType.ORDINAL) {
                    EnumOrdinalTypeHandler<? extends Enum<?>> typeHandler = new EnumOrdinalTypeHandler(field.getJavaType());
                    return TypeUtils.castTo( typeHandler.getClass());
                }
                EnumTypeHandler<? extends Enum<?>> typeHandler = new EnumTypeHandler(field.getJavaType());
                return TypeUtils.castTo( typeHandler.getClass());
            }

        }
        return null;
    }

    public static ColumnTypeEntry getColumnTypeEntry(EntityField field){
        ColumnTypeEntry entry = new ColumnTypeEntry();
        if (field.isAnnotationPresent(ColumnType.class)) {
            ColumnType columnType = field.getAnnotation(ColumnType.class);

            if (columnType.jdbcType() != JdbcType.UNDEFINED) {
                entry.setJdbcType(columnType.jdbcType());
            }
            if (columnType.typeHandler() != UnknownTypeHandler.class) {
                entry.setTypeHandler(columnType.typeHandler());
            }

        }
        if (entry.getJdbcType() == JdbcType.UNDEFINED ||entry.getJdbcType() == null) {
            entry.setJdbcType(formJavaType(field.getJavaType()));
        }
        return entry;
    }

    public static MutablePair<String,Column> getColumnName(EntityField field, Style style){
        MutablePair<String,Column> duplex = new MutablePair<>();
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            duplex= MutablePair.of(column.name(),column);
        }
        if (StringUtil.isEmpty(duplex.left)) {
            duplex.setLeft(StringUtil.convertByStyle(field.getName(), style));
        }
        return duplex;
    }

    public static String getPropertyName(EntityField field){
        return field.getName();
    }

}

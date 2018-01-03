package com.flagwind.mybatis.helpers;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.meta.EntityField;
import com.flagwind.mybatis.utils.StringUtil;
import com.flagwind.persistent.annotation.ColumnType;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import javax.persistence.Column;
import java.sql.Timestamp;

public class ColumnHelper {

    public static JdbcType formJavaType(Class<?> javaType){
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
        if(javaType.isAssignableFrom(java.sql.Date.class)
                ||javaType.isAssignableFrom(java.util.Date.class)){
            return JdbcType.DATE;
        }
        if(javaType.isAssignableFrom(Timestamp.class) ){
            return JdbcType.TIMESTAMP;
        }
        if(javaType.isAssignableFrom(byte[].class) ){
            return JdbcType.BINARY;
        }
        if(javaType.isEnum()){
            return JdbcType.VARCHAR;
        }
        return JdbcType.UNDEFINED;
    }

    public static MutableTriple<ColumnType,JdbcType ,Class<? extends TypeHandler<?>>> getColumnType(EntityField field){
        MutableTriple<ColumnType,JdbcType ,Class<? extends TypeHandler<?>>> triple = new MutableTriple<>();
        if (field.isAnnotationPresent(ColumnType.class)) {
            ColumnType columnType = field.getAnnotation(ColumnType.class);
            triple.setLeft(columnType);
            if (columnType.jdbcType() != JdbcType.UNDEFINED) {
                triple.setMiddle(columnType.jdbcType());
            }
            if (columnType.typeHandler() != UnknownTypeHandler.class) {
                triple.setRight(columnType.typeHandler());
            }

        }
        if (triple.middle == JdbcType.UNDEFINED ||triple.middle == null) {
            triple.setMiddle(formJavaType(field.getJavaType()));
        }
        return triple;
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

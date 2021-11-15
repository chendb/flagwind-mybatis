package com.flagwind.mybatis.handlers;

import com.flagwind.commons.StringUtils;
import com.flagwind.lang.CodeType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class CodeTypeHandler<E extends CodeType> extends BaseTypeHandler<E> {

    private Class<E> type;

    public CodeTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {

        if (jdbcType == null) {
            ps.setString(i, parameter.getValue());
        } else {
            ps.setObject(i, parameter.getValue(), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String s = rs.getString(columnName);

        return getNullableResult(s);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String s = rs.getString(columnIndex);

        return getNullableResult(s);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String s = cs.getString(columnIndex);

        return getNullableResult(s);
    }

    public E getNullableResult(String s) {
        E e = null;
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        try {
            if (Enum.class.isAssignableFrom(type)) {
                return valueOf(type, s);
            }
            e = type.getConstructor(String.class).newInstance(s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return e;
    }

    private static <E> E valueOf(Class<E> type, String s) {
        E[] enumValues = type.getEnumConstants();

        if (CodeType.class.isAssignableFrom(type)) {
            E e = Arrays.stream(enumValues).filter(g -> ((CodeType) g).getValue().equalsIgnoreCase(s) || ((CodeType) g).getText().equalsIgnoreCase(s)).findFirst().orElse(null);
            if (e != null) {
                return e;
            }
        }

        for (E e : enumValues) {
            Enum en = (Enum) e;
            if (en.name().equalsIgnoreCase(s)) {
                return e;
            }
            if (Integer.toString(en.ordinal()).equals(s)) {
                return e;
            }
        }

        return null;
    }
}
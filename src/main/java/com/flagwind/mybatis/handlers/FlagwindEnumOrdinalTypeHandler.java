package com.flagwind.mybatis.handlers;

import com.flagwind.lang.CodeType;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class FlagwindEnumOrdinalTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private Class<E> type;

    private final E[] enums;

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

    private E toOrdinalEnum(int ordinal) {
        try {
            return enums[ordinal];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + ordinal + " to " + type.getSimpleName() + " by ordinal value.", ex);
        }
    }

    public FlagwindEnumOrdinalTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        this.enums = type.getEnumConstants();
        if (this.enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        if (CodeType.class.isAssignableFrom(type)) {
            if (jdbcType == null) {
                ps.setString(i, ((CodeType) parameter).getValue());
            } else {
                ps.setObject(i, ((CodeType) parameter).getValue(), jdbcType.TYPE_CODE);
            }
            return;
        }
        ps.setInt(i, parameter.ordinal());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {

        if (CodeType.class.isAssignableFrom(type)) {
            String s = rs.getString(columnName);
            if (StringUtils.isEmpty(s)) {
                return null;
            }
            return valueOf(type, s);
        }

        int ordinal = rs.getInt(columnName);
        if (ordinal == 0 && rs.wasNull()) {
            return null;
        }

        return toOrdinalEnum(ordinal);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if (CodeType.class.isAssignableFrom(type)) {
            String s = rs.getString(columnIndex);
            if (StringUtils.isEmpty(s)) {
                return null;
            }
            return valueOf(type, s);
        }
        int ordinal = rs.getInt(columnIndex);
        if (ordinal == 0 && rs.wasNull()) {
            return null;
        }
        return toOrdinalEnum(ordinal);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (CodeType.class.isAssignableFrom(type)) {
            String s = cs.getString(columnIndex);
            if (StringUtils.isEmpty(s)) {
                return null;
            }
            return valueOf(type, s);
        }
        int ordinal = cs.getInt(columnIndex);
        if (ordinal == 0 && cs.wasNull()) {
            return null;
        }
        return toOrdinalEnum(ordinal);
    }


}
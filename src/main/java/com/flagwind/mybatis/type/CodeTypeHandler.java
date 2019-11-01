package com.flagwind.mybatis.type;

import com.flagwind.commons.StringUtils;
import com.flagwind.lang.CodeType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        E e = null;
        if (StringUtils.isEmpty(s)) {
            return e;
        }
        try {
            e = type.getConstructor(String.class).newInstance(s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return e;
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String s = rs.getString(columnIndex);
        E e = null;
        if (StringUtils.isEmpty(s)) {
            return e;
        }
        try {
            e = type.getConstructor(String.class).newInstance(s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return e;
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String s = cs.getString(columnIndex);
        E e = null;
        if (StringUtils.isEmpty(s)) {
            return e;
        }
        try {
            e = type.getConstructor(String.class).newInstance(s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return e;
    }
}
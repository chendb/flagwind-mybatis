package com.flagwind.mybatis.metadata.processors;

import com.flagwind.lang.CodeType;
import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.handlers.CodeTypeHandler;
import com.flagwind.mybatis.utils.TypeUtils;
import com.flagwind.reflect.entities.EntityField;
import org.apache.ibatis.type.JdbcType;

public class CodeColumnProcessor implements ColumnProcessor {
    @Override
    public void process(EntityColumn column, EntityField field, Style style) {
        if (CodeType.class.isAssignableFrom(field.getJavaType())) {
            CodeTypeHandler typeHandler = new CodeTypeHandler(field.getJavaType());
            column.setTypeHandler(TypeUtils.castTo(typeHandler.getClass()));
            column.setJdbcType(JdbcType.VARCHAR);
        }
    }

}

package com.flagwind.mybatis.tenant;


import com.flagwind.mybatis.definition.interceptor.tenant.TenantHandler;
import com.flagwind.mybatis.metadata.EntityTable;
import com.flagwind.mybatis.metadata.EntityTableFactory;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author chendb
 * @description: 基于注解的租户处理实现
 * @date 2020-05-05 15:17:22
 */
public class AnnotationTenantHandler implements TenantHandler {

    @Override
    public Expression getTenantId(boolean where, String tableName) {

        // 如果不是查询，直接返回当前租户信息
        if (!where) {
            return new StringValue("t1");
        }
        Tenant tenant = getTenant(tableName);
//
//        if (tenant.mode() == Tenant.VisitMode.Pessimistic && UserContext.details() == null) {
//            return new NullValue();
//        }

        List<String> keys = TenantContext.instance().getValues(tenant.strategy(), "t1");
        // 如果只有一个值，则返回该值
        if (keys.size() == 1) {
            if (StringUtils.isEmpty(keys.get(0))) {
                return new NullValue();
            } else {
                return new StringValue(keys.get(0));
            }
        }

        // 多值，则返回多值表达式
        ValueListExpression expression = new ValueListExpression();
        ExpressionList list = new ExpressionList(keys.stream().map(key -> StringUtils.isEmpty(key) ? new NullValue() : new StringValue(key)).collect(Collectors.toList()));
        expression.setExpressionList(list);
        return expression;
    }


    private Tenant getTenant(String tableName) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(tableName);
        if (entityTable != null) {
            return AnnotationUtils.getAnnotation(entityTable.getEntityClass(), Tenant.class);
        }
        return null;
    }

    @Override
    public String getTenantIdColumn() {
        return "tenantId";
    }

    @Override
    public boolean doTableFilter(String tableName) {
        Tenant tenant = getTenant(tableName);
        if (tenant == null) {
            return true;
        }

        if (tenant.mode() == Tenant.VisitMode.Optimistic && StringUtils.isEmpty("t1")) {
            return true;
        }

        return !tenant.value().equalsIgnoreCase(this.getTenantIdColumn());

    }
}

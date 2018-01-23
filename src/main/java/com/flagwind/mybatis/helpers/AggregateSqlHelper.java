package com.flagwind.mybatis.helpers;

import com.flagwind.mybatis.meta.EntityColumn;
import com.flagwind.persistent.AggregateEntry;
import com.flagwind.persistent.AggregateType;

import java.util.Set;

/**
 * 聚合查询Sql帮助类
 */
public class AggregateSqlHelper {
    public static String selectAllColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(getAllColumns(entityClass)).append(" ");
        return sql.toString();
    }

    public static String getAllColumns(Class<?> entityClass) {
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        for (EntityColumn entityColumn : columnList) {
            if (entityColumn.getAggregate() != null) {
                AggregateEntry entry = entityColumn.getAggregate();
                AggregateType type = entry.getType();
                sql.append(type.name()).append("(")
                        .append(entry.getColumn())
                        .append(")").append(entry.getAlias()).append(",");
            }else {
                sql.append(entityColumn.getColumn()).append(",");
            }
        }
        return sql.substring(0, sql.length() - 1);
    }

    public static String groupBy(Class<?> entityClass) {
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
       boolean hasGroupByField = columnList.stream().anyMatch(g->g.getAggregate()!=null);
       if(!hasGroupByField){
           return "";
       }
        StringBuilder sql = new StringBuilder();
       sql.append(" group by ");
        for (EntityColumn entityColumn : columnList) {
            if (entityColumn.getAggregate() == null) {
                sql.append(entityColumn.getColumn()).append(",");
            }
        }
        return sql.substring(0, sql.length() - 1);
    }
}

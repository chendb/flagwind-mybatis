//package com.flagwind.mybatis.definition.helper;
//
//import com.flagwind.mybatis.metadata.EntityColumn;
//import com.flagwind.mybatis.metadata.EntityTableFactory;
//import com.flagwind.persistent.AggregateEntry;
//import com.flagwind.persistent.AggregateType;
//
//import java.util.Set;
//
///**
// * 聚合查询Sql帮助类
// */
//public class AggregateSqlHelper {
//
//    public static String selectAllColumns(Class<?> entityClass) {
//        return "SELECT " + columns(entityClass) + " ";
//    }
//
//    public static String columns(Class<?> entityClass) {
//        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
//        StringBuilder sql = new StringBuilder();
//        for (EntityColumn entityColumn : columnList) {
//            if (entityColumn.getAggregate() != null) {
//                AggregateEntry entry = entityColumn.getAggregate();
//                AggregateType type = entry.getType();
//                sql.append(type.name()).append("(")
//                        .append(entry.getColumn())
//                        .append(")").append(entry.getAlias()).append(",");
//            }else {
//                sql.append(entityColumn.getColumn()).append(",");
//            }
//        }
//        return sql.substring(0, sql.length() - 1);
//    }
//
//    public static String groupBy(Class<?> entityClass) {
//        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
//       boolean hasGroupByField = columnList.stream().anyMatch(g->g.getAggregate()!=null);
//       if(!hasGroupByField){
//           return "";
//       }
//        StringBuilder sql = new StringBuilder();
//       sql.append(" group by ");
//        for (EntityColumn entityColumn : columnList) {
//            if (entityColumn.getAggregate() == null) {
//                sql.append(entityColumn.getColumn()).append(",");
//            }
//        }
//        return sql.substring(0, sql.length() - 1);
//    }
//}

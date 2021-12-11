package com.flagwind.mybatis.definition.builder;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.definition.Config;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.metadata.EntityTable;
import com.flagwind.mybatis.metadata.EntityTableFactory;
import com.flagwind.mybatis.utils.NameUtils;

import java.text.MessageFormat;
import java.util.Set;

public class BaseSqlBuilder {

    public static String getIdentity(Config config,EntityColumn column) {
        return MessageFormat.format(config.getIdentity(), column.getSequenceName(), column.getColumn(), column.getProperty(), column.getTable().getName());
    }


    public static String wherePKColumn(Config config,String columnPrefix, Class<?> entityClass, String keyName) {
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getPKColumns(entityClass);
        if (columnList.size() == 1) {
            EntityColumn column = columnList.iterator().next();
            sql.append(" where ");
            if (StringUtils.isNotEmpty(columnPrefix) && !column.getColumn().contains(".")) {
                sql.append(columnPrefix).append(".");
            }
            sql.append(getColumnName(config, column));
            sql.append(" = #{").append(keyName).append("}");
        } else {
            throw new MapperException("实体类[" + entityClass.getCanonicalName() + "]中必须只有一个带有 @Id 注解的字段");
        }
        return sql.toString();
    }

    public static String columns(Config config,Set<EntityColumn> columnList, String columnPrefix, String aliasPrefix) {
        StringBuilder selectBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(aliasPrefix)) {
            aliasPrefix += "_";
        }
        for (EntityColumn entityColumn : columnList) {
            if (StringUtils.isNotEmpty(columnPrefix) && !entityColumn.getColumn().contains(".")) {
                selectBuilder.append(columnPrefix).append(".");
            }

            if (entityColumn.getColumn().equalsIgnoreCase(entityColumn.getProperty())
                    && StringUtils.isEmpty(aliasPrefix)) {
                selectBuilder.append(getColumnName(config,entityColumn));
            } else {
                selectBuilder.append(entityColumn.getColumn()).append(" as ");
                if (StringUtils.isNotEmpty(aliasPrefix)) {
                    selectBuilder.append(aliasPrefix);
                }
                selectBuilder.append(entityColumn.getProperty());
            }
            selectBuilder.append(",");
        }
        String sql = selectBuilder.substring(0, selectBuilder.length() - 1);
        return sql;
    }

    public static String getTableAlias(Class<?> entityClass) {
        String name = entityClass.getSimpleName();
        return "_" + name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public static String getColumnAlias(Config config, EntityColumn column) {
        if (!column.getProperty().equalsIgnoreCase(column.getColumn())) {
            return NameUtils.formatName(config, column.getColumn()) + " as " + NameUtils.formatName(config, column.getProperty());
        }
        return NameUtils.formatName(config, column.getColumn());
    }

    public static String getColumnName(Config config, EntityColumn column) {
        return NameUtils.formatName(config,column.getColumn());
    }

    public static String getColumnName(Config config, String column) {
        return NameUtils.formatName(config,column);
    }
    public static String getTableName(Config config, Class<?> entityClass) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        String prefix = entityTable.getPrefix();
        if (StringUtils.isEmpty(prefix)) {
            // 使用全局配置
            prefix = config.getPrefix();
        }
        if (StringUtils.isNotEmpty(prefix)) {
            return prefix + "." + NameUtils.formatName(config, entityTable.getName());
        }
        return NameUtils.formatName(config,entityTable.getName());
    }

    /**
     * 返回格式如:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @return
     */
    public static String getColumnEqualsHolder(Config config, EntityColumn column) {
        return getColumnEqualsHolder(config,null,column);
    }

    /**
     * 返回格式如:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName
     * @return
     */
    public static String getColumnEqualsHolder(Config config,String entityName, EntityColumn column) {
        return getColumnName(config, column) + " = " + column.getColumnHolder(entityName);
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column 列
     */
    public static String getBindCache(EntityColumn column) {
        String sql = "<bind name=\"" +
                column.getProperty() + "_cache\" " +
                "value=\"" + column.getProperty() + "\"/>";
        return sql;
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column 列
     */
    public static String getBindValue(EntityColumn column, String value) {
        String sql = "<bind name=\"" + column.getProperty() + "_bind\" " + "value='" + value + "'/>";
        return sql;
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column 列
     */
    public static String getIfCacheNotNull(EntityColumn column, String contents) {
        String sql = "<if test=\"" + column.getProperty() + "_cache != null\">" +
                contents +
                "</if>";
        return sql;
    }

    /**
     * 如果_cache == null
     *
     * @param column 列
     */
    public static String getIfCacheIsNull(EntityColumn column, String contents) {
        String sql = "<if test=\"" + column.getProperty() + "_cache == null\">" +
                contents +
                "</if>";
        return sql;
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param column   列
     * @param contents
     * @param empty
     */
    public static String getIfNotNull(EntityColumn column, String contents, boolean empty) {
        return getIfNotNull(null, column, contents, empty);
    }

    /**
     * 判断自动==null的条件结构
     *
     * @param column   列
     * @param contents
     * @param empty
     */
    public static String getIfIsNull(EntityColumn column, String contents, boolean empty) {
        return getIfIsNull(null, column, contents, empty);
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param entityName
     * @param column     列
     * @param contents
     * @param empty
     */
    public static String getIfNotNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
        if (StringUtils.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" != null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" and ");
            if (StringUtils.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" != '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 判断自动==null的条件结构
     *
     * @param entityName
     * @param column     列
     * @param contents
     * @param empty
     */
    public static String getIfIsNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
        if (StringUtils.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" == null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" or ");
            if (StringUtils.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" == '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    private static String getSeqNextVal(String sequenceFormat, EntityColumn column) {
        return MessageFormat.format(sequenceFormat, column.getSequenceName(), column.getColumn(), column.getProperty(), column.getTable().getName());
    }
}
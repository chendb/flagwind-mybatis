package com.flagwind.mybatis.definition.helper;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.definition.Config;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.metadata.EntityTable;
import com.flagwind.mybatis.metadata.EntityTableFactory;

import java.text.MessageFormat;
import java.util.Set;

public class TemplateSqlHelper {

    public static String getTableName(Config config, Class<?> entityClass) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        String prefix = entityTable.getPrefix();
        if (StringUtils.isEmpty(prefix)) {
            // 使用全局配置
            prefix = config.getPrefix();
        }
        if (StringUtils.isNotEmpty(prefix)) {
            return prefix + "." + entityTable.getName();
        }
        return entityTable.getName();
    }

    public static String tableName(Config config, Class<?> entityClass, boolean addDefaultAlias) {
        StringBuilder sb = new StringBuilder();
        String table = getTableName(config, entityClass);
        sb.append(table);
        if (addDefaultAlias) {
            sb.append(" ").append(tableAlias(entityClass));
        }
        return sb.toString();
    }


    public static String selectColumnsFromTable(Config config, Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        String baseColumns = TemplateSqlHelper.getBaseColumns(entityClass);
        boolean hasTableAlias = baseColumns.contains(".");
        sql.append("select ");
        sql.append(baseColumns);
        sql.append(" FROM ");
        sql.append(tableName(config, entityClass, hasTableAlias));
        return sql.toString();
    }

    public static String selectCountFromTable(Config config, Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(0) FROM ");
        sql.append(tableName(config, entityClass, false));
        return sql.toString();
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
        String sql = "<bind name=\"" +
                column.getProperty() + "_bind\" " +
                "value='" + value + "'/>";
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

    /**
     * 生成选择的列SQL，格式如下：_user.name as user_name,_user.id as user_id
     *
     * @param entityClass  业务实体
     * @param columnPrefix 列前缀
     * @param aliasPrefix  别名前缀
     * @return
     */
    public static String columns(Class<?> entityClass, String columnPrefix, String aliasPrefix) {
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
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
                selectBuilder.append(entityColumn.getColumn());
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

    public static String getBaseColumns(Class<?> entityClass) {
        return getBaseColumns(entityClass, null);
    }

    public static String getBaseColumns(Class<?> entityClass,String alias) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        if (entityTable.getBaseSelect() != null) {
            return entityTable.getBaseSelect();
        }

        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();
        for (EntityColumn entityColumn : columnList) {
            selectBuilder.append(StringUtils.isNotEmpty(alias) ? alias + "." : "");
            selectBuilder.append(entityColumn.getColumn()).append(",");
        }
        String sql = selectBuilder.substring(0, selectBuilder.length() - 1);
        entityTable.setBaseSelect(sql);
        return sql;
    }



    /**
     * 表的别名  如：where com_user _user  ,其中_user为别名
     *
     * @param entityClass 实体类型
     * @return
     */
    public static String tableAlias(Class<?> entityClass) {
        String name = entityClass.getSimpleName();
        return "_" + name.substring(0, 1).toLowerCase() + name.substring(1);
    }



    /**
     * update tableName - 动态表名
     *
     * @param entityClass
     * @param config 默认表名
     */
    public static String updateTable(Config config,Class<?> entityClass) {
        String sql = "UPDATE " +
                tableName(config, entityClass, false) +
                " ";
        return sql;
    }


    /**
     * delete tableName - 动态表名
     *
     * @param entityClass
     * @param config
     */
    public static String deleteFromTable(Config config, Class<?> entityClass) {
        String sql = "DELETE FROM " +
                tableName(config, entityClass, false) +
                " ";
        return sql;
    }

    /**
     * insert into tableName - 动态表名
     *
     * @param entityClass
     * @param config
     */
    public static String insertIntoTable(Config config, Class<?> entityClass) {
        String sql = "INSERT INTO " +
                tableName(config, entityClass, false) +
                " ";
        return sql;
    }

    /**
     * insert table()列
     *
     * @param entityClass
     * @param skipId      是否从列中忽略id类型
     */
    public static String insertColumns(Class<?> entityClass, boolean skipId ) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            sql.append(column.getColumn()).append(",");
//            if (notNull) {
//                sql.append(TemplateSqlHelper.getIfNotNull(column, column.getColumn() + ",", config.isNotEmpty()));
//            } else {
//                sql.append(column.getColumn()).append(",");
//            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    private static String getSeqNextVal(String sequenceFormat,EntityColumn column) {
        return MessageFormat.format(sequenceFormat, column.getSequenceName(), column.getColumn(), column.getProperty(), column.getTable().getName());
    }

    public static String insertValues(Class<?> entityClass,Config config) {
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        sql.append("<trim prefix=\"VALUES(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            {
                // 优先使用传入的属性值,当原属性 property!=null 时，用原属性
                // 自增的情况下,如果默认有值,就会备份到 property_cache 中,所以这里需要先判断备份的值是否存在
                if (column.isIdentity()) {
                    sql.append(TemplateSqlHelper.getIfCacheNotNull(column, column.getColumnHolder(null, "_cache", ",")));
                } else {
                    //其他情况值仍然存在原property中
                    sql.append(TemplateSqlHelper.getIfNotNull(column, column.getColumnHolder(null, null, ","), config.isNotEmpty()));
                }
            }
            {
                // 当属性为null时，如果存在主键策略，会自动获取值，如果不存在，则使用null
                // 序列的情况
                if (StringUtils.isNotEmpty(column.getSequenceName())) {
                    sql.append(TemplateSqlHelper.getIfIsNull(column, getSeqNextVal(config.getSequenceFormat(), column) + " ,", false));
                } else if (column.isIdentity()) {
                    sql.append(TemplateSqlHelper.getIfCacheIsNull(column, column.getColumnHolder() + ","));
                } else if (column.isUuid()) {
                    sql.append(TemplateSqlHelper.getIfIsNull(column, column.getColumnHolder(null, "_bind", ","), config.isNotEmpty()));
                } else {
                    // 当 null 的时候，如果不指定jdbcType，oracle可能会报异常，指定VARCHAR不影响其他
                    sql.append(TemplateSqlHelper.getIfIsNull(column, column.getColumnHolder(null, null, ","), config.isNotEmpty()));
                }
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }



    /**
     * insert-values()列
     *
     * @param entityClass
     * @param skipId      是否从列中忽略id类型
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     */
    public static String insertValuesColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            if (notNull) {
                sql.append(TemplateSqlHelper.getIfNotNull(column, column.getColumnHolder() + ",", notEmpty));
            } else {
                sql.append(column.getColumnHolder()).append(",");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * update set列
     *
     * @param entityClass
     * @param entityName  实体映射名
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     */
    public static String updateSetColumns(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isUpdatable()) {
                if (notNull) {
                    sql.append(TemplateSqlHelper.getIfNotNull(entityName, column, column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                } else {
                    sql.append(column.getColumnEqualsHolder(entityName)).append(",");
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * where主键条件(参数为单个值如userReository.getById("123456"))
     *
     * @param entityClass
     * @param keyName
     */
    public static String wherePKColumn(Class<?> entityClass, String keyName) {
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getPKColumns(entityClass);
        if (columnList.size() == 1) {
            EntityColumn column = columnList.iterator().next();
            sql.append(" where ");
            sql.append(column.getColumn());
            sql.append(" = #{").append(keyName).append("}");
        } else {
            throw new MapperException("实体类[" + entityClass.getCanonicalName() + "]中必须只有一个带有 @Id 注解的字段");
        }
        return sql.toString();
    }

    /**
     * where主键条件(参数为对象如userRepository.selectByKey(user))
     *
     * @param entityClass
     */
    public static String wherePKColumns(Class<?> entityClass, String entityName) {
        StringBuilder sql = new StringBuilder();
        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getPKColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            sql.append(" AND ").append(column.getColumnEqualsHolder(entityName));
        }
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * where所有列的条件，会判断是否!=null
     *
     * @param entityClass
     */
    public static String whereAllIfColumns(Class<?> entityClass, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            sql.append(getIfNotNull(column, " AND " + column.getColumnEqualsHolder(), empty));
        }
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 获取默认的orderBy，通过注解设置的
     *
     * @param entityClass
     */
    public static String orderByDefault(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        String orderByClause = EntityTableFactory.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append(" ORDER BY ");
            sql.append(orderByClause);
        }
        return sql.toString();
    }


}
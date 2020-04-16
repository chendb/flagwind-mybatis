package com.flagwind.mybatis.definition.helper;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.definition.Config;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.metadata.EntityTable;
import com.flagwind.mybatis.metadata.EntityTableFactory;

import java.util.Set;

public class TemplateSqlHelper {

    private static String getTableName(Config config,Class<?> entityClass) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        String prefix = entityTable.getPrefix();
        if (StringUtils.isEmpty(prefix)) {
            //使用全局配置
            prefix = config.getPrefix();
        }
        if (StringUtils.isNotEmpty(prefix)) {
            return prefix + "." + entityTable.getName();
        }
        return entityTable.getName();
    }

    public static String tableName(Config config,Class<?> entityClass, boolean addDefaultAlias) {
        StringBuilder sb = new StringBuilder();
        String table = getTableName(config,entityClass);
        sb.append(table);
        if (addDefaultAlias) {
            sb.append(" ").append(TemplateSqlHelper.tableAlias(entityClass));
        }
        return sb.toString();
    }


    public static String tableName(Class<?> entityClass, String defaultTableName) {
        return defaultTableName;
    }

    public static String selectColumnsFromTable(Config config,Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();

        sql.append("select ");
        sql.append(TemplateSqlHelper.columns(entityClass));
        sql.append(" FROM ");
        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(tableName(config, entityClass, hasTableAlias));

        return sql.toString();

    }

    public static String selectCountFromTable(Config config,Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        Set<EntityColumn> pkColumns = EntityTableFactory.getPKColumns(entityClass);
        if (pkColumns.size() == 1) {
            sql.append("COUNT(").append(pkColumns.iterator().next().getColumn()).append(") ");
        } else {
            sql.append("COUNT(*) ");
        }

        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(tableName(config,entityClass, hasTableAlias));

        return sql.toString();
    }

    /**
     * select case when count(x) > 0 then 1 else 0 end
     *
     * @param entityClass
     */
    public static String selectCountExistsFromTable(Config config,Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CASE WHEN ");
        Set<EntityColumn> pkColumns = EntityTableFactory.getPKColumns(entityClass);
        if (pkColumns.size() == 1) {
            sql.append("COUNT(").append(pkColumns.iterator().next().getColumn()).append(") ");
        } else {
            sql.append("COUNT(*) ");
        }
        sql.append(" > 0 THEN 1 ELSE 0 END AS result ");

        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(tableName(config,entityClass, hasTableAlias));
        return sql.toString();
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column 列
     */
    public static String getBindCache(EntityColumn column) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"");
        sql.append(column.getProperty()).append("_cache\" ");
        sql.append("value=\"").append(column.getProperty()).append("\"/>");
        return sql.toString();
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column 列
     */
    public static String getBindValue(EntityColumn column, String value) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"");
        sql.append(column.getProperty()).append("_bind\" ");
        sql.append("value='").append(value).append("'/>");
        return sql.toString();
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column 列
     */
    public static String getIfCacheNotNull(EntityColumn column, String contents) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"").append(column.getProperty()).append("_cache != null\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 如果_cache == null
     *
     * @param column 列
     */
    public static String getIfCacheIsNull(EntityColumn column, String contents) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"").append(column.getProperty()).append("_cache == null\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
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
     * 获取所有查询列，如id,name,code...
     *
     * @param entityClass
     */
    public static String columns(Class<?> entityClass) {
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        for (EntityColumn entityColumn : columnList) {
            sql.append(entityColumn.getColumn()).append(",");
        }
        return sql.substring(0, sql.length() - 1);
    }


    /**
     * 表的别名  如：where com_user _user  ,其中_user为别名
     *
     * @param entityClass 实体类型
     * @return
     */
    public static String tableAlias(Class<?> entityClass) {
        return "_" + entityClass.getSimpleName();
    }




//    /**
//     * select xxx,xxx...
//     *
//     * @param entityClass
//     */
//    public static String selectAllColumns(Class<?> entityClass) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT ");
//        sql.append(columns(entityClass));
//        sql.append(" ");
//        return sql.toString();
//    }

    /**
     * select count(x)
     *
     * @param entityClass
     */
//    public static String selectCount(Class<?> entityClass) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT ");
//        Set<EntityColumn> pkColumns = EntityTableFactory.getPKColumns(entityClass);
//        if (pkColumns.size() == 1) {
//            sql.append("COUNT(").append(pkColumns.iterator().next().getColumn()).append(") ");
//        } else {
//            sql.append("COUNT(*) ");
//        }
//        return sql.toString();
//    }




//    /**
//     * from tableName - 动态表名
//     *
//     * @param entityClass
//     * @param defaultTableName
//     */
//    public static String fromTable(Class<?> entityClass, String defaultTableName) {
//        StringBuilder sql = new StringBuilder();
//        sql.append(" FROM ");
//        sql.append(tableName(entityClass, defaultTableName));
//        sql.append(" ");
//        return sql.toString();
//    }


    /**
     * update tableName - 动态表名
     *
     * @param entityClass
     * @param config 默认表名
     */
    public static String updateTable(Config config,Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(tableName(config,entityClass, false));
        sql.append(" ");
        return sql.toString();
    }


    /**
     * delete tableName - 动态表名
     *
     * @param entityClass
     * @param config
     */
    public static String deleteFromTable(Config config, Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(tableName(config,entityClass,false));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * insert into tableName - 动态表名
     *
     * @param entityClass
     * @param config
     */
    public static String insertIntoTable(Config config, Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append( tableName(config,entityClass,false));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * insert table()列
     *
     * @param entityClass
     * @param skipId      是否从列中忽略id类型
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     */
    public static String insertColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
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
            if (notNull) {
                sql.append(TemplateSqlHelper.getIfNotNull(column, column.getColumn() + ",", notEmpty));
            } else {
                sql.append(column.getColumn() + ",");
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
                sql.append(column.getColumnHolder() + ",");
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
                    sql.append(column.getColumnEqualsHolder(entityName) + ",");
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
            sql.append(" AND " + column.getColumnEqualsHolder(entityName));
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
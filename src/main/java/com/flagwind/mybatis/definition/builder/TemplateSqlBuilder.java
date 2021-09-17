package com.flagwind.mybatis.definition.builder;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.definition.Config;
import com.flagwind.mybatis.definition.helper.MappedStatementHelper;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.metadata.EntityTable;
import com.flagwind.mybatis.metadata.EntityTableFactory;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.ibatis.mapping.MappedStatement;

import java.text.MessageFormat;
import java.util.Set;

public class TemplateSqlBuilder extends BaseSqlBuilder {



    public TemplateSqlBuilder(Config config) {
        this.config = config;
    }

    private Config config;


    public String tableName(Class<?> entityClass, boolean addDefaultAlias) {
        StringBuilder sb = new StringBuilder();
        String table = getTableName(config, entityClass);
        sb.append(table);
        if (addDefaultAlias) {
            sb.append(" ").append(tableAlias(entityClass));
        }
        return sb.toString();
    }

    public String selectColumnsFromTable(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        String baseColumns = getBaseColumns(entityClass);
        boolean hasTableAlias = baseColumns.contains(".");
        sql.append("select ");
        sql.append(baseColumns);
        sql.append(" FROM ");
        sql.append(tableName(entityClass, hasTableAlias));
        return sql.toString();
    }

    public String selectCountFromTable(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(0) FROM ");
        sql.append(tableName(entityClass, false));
        return sql.toString();
    }

    public String getBaseColumns(Class<?> entityClass) {
        return getBaseColumns(entityClass, null);
    }

    public String getBaseColumns(Class<?> entityClass, String alias) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        if (entityTable.getBaseSelect() != null) {
            return entityTable.getBaseSelect();
        }

        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();
        for (EntityColumn entityColumn : columnList) {
            selectBuilder.append(StringUtils.isNotEmpty(alias) ? alias + "." : "");
            selectBuilder.append(getColumnName(config, entityColumn)).append(",");
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
     */
    public String updateTable(Class<?> entityClass) {
        String sql = "UPDATE " +
                tableName(entityClass, false) +
                " ";
        return sql;
    }


    /**
     * delete tableName - 动态表名
     *
     * @param entityClass
     */
    public String deleteFromTable(Class<?> entityClass) {
        String sql = "DELETE FROM " +
                tableName(entityClass, false) +
                " ";
        return sql;
    }

    public MutablePair<String, Boolean> getSequenceKeyMapping( Class<?> entityClass,
                                                               MappedStatement ms) {

        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        boolean hasIdentityKey = false;
        StringBuilder sql = new StringBuilder();
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (StringUtils.isNotEmpty(column.getSequenceName())) {
            } else if (column.isIdentity()) {
                //这种情况下,如果原先的字段有值,需要先缓存起来,否则就一定会使用自动增长
                //这是一个bind节点
                sql.append(getBindCache(column));
                //如果是Identity列，就需要插入selectKey
                //如果已经存在Identity列，抛出异常
                if (hasIdentityKey) {
                    //jdbc类型只需要添加一次
                    if (column.getGenerator() != null && column.getGenerator().equals("JDBC")) {
                        continue;
                    }
                    throw new MapperException(
                            ms.getId() + "对应的实体类" + entityClass.getCanonicalName() + "中包含多个MySql的自动增长列,最多只能有一个!");
                }
                //插入selectKey
                MappedStatementHelper.newSelectKeyMappedStatement(ms, column, entityClass, config.isBefore(), BaseSqlBuilder.getIdentity(config, column));
                hasIdentityKey = true;
            } else if (column.isUuid()) {
                //uuid的情况，直接插入bind节点
                sql.append(BaseSqlBuilder.getBindValue(column, config.getUuid()));
            }
        }
        return MutablePair.of(sql.toString(), hasIdentityKey);
    }

    /**
     * insert into tableName - 动态表名
     *
     * @param entityClass
     */
    public String insertIntoTable( Class<?> entityClass) {
        String sql = "INSERT INTO " +
                tableName(entityClass, false) +
                " ";
        return sql;
    }

    /**
     * insert table()列
     *
     * @param entityClass
     * @param skipId      是否从列中忽略id类型
     */
    public String insertColumns(Class<?> entityClass, boolean skipId) {
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
            sql.append(getColumnName(config,column)).append(",");
        }
        sql.append("</trim>");
        return sql.toString();
    }

    private String getSeqNextVal(String sequenceFormat, EntityColumn column) {
        return MessageFormat.format(sequenceFormat, column.getSequenceName(), getColumnName(config,column), column.getProperty(), column.getTable().getName());
    }

    public String insertValues(Class<?> entityClass) {
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
                    sql.append(getIfCacheNotNull(column, column.getColumnHolder(null, "_cache", ",")));
                } else {
                    //其他情况值仍然存在原property中
                    sql.append(getIfNotNull(column, column.getColumnHolder(null, null, ","), config.isNotEmpty()));
                }
            }
            {
                // 当属性为null时，如果存在主键策略，会自动获取值，如果不存在，则使用null
                // 序列的情况
                if (StringUtils.isNotEmpty(column.getSequenceName())) {
                    sql.append(getIfIsNull(column, getSeqNextVal(config.getSequenceFormat(), column) + " ,", false));
                } else if (column.isIdentity()) {
                    sql.append(getIfCacheIsNull(column, column.getColumnHolder() + ","));
                } else if (column.isUuid()) {
                    sql.append(getIfIsNull(column, column.getColumnHolder(null, "_bind", ","), config.isNotEmpty()));
                } else {
                    // 当 null 的时候，如果不指定jdbcType，oracle可能会报异常，指定VARCHAR不影响其他
                    sql.append(getIfIsNull(column, column.getColumnHolder(null, null, ","), config.isNotEmpty()));
                }
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }


    public String getInsertValueSql(Class<?> entityClass, String entityName) {
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        int i = 0;
        for (EntityColumn column : columnList) {

            if (column.isInsertable()) {
                sql.append(i != 0 ? "," : "").append(column.getColumnHolder(entityName));
                i++;
            }
        }
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
    public String insertValuesColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
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
                sql.append(getIfNotNull(column, column.getColumnHolder() + ",", notEmpty));
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
    public String updateSetColumns(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isUpdatable()) {
                String columnEqualsHolder = getColumnEqualsHolder(config, entityName, column);
                if (notNull) {
                    sql.append(getIfNotNull(entityName, column, columnEqualsHolder + ",", notEmpty));
                } else {
                    sql.append(columnEqualsHolder).append(",");
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
    public String wherePKColumn(Class<?> entityClass, String keyName) {
       return wherePKColumn(null,entityClass,keyName);
    }

    /**
     * where主键条件(参数为对象如userRepository.selectByKey(user))
     *
     * @param entityClass
     */
    public String wherePKColumns(Class<?> entityClass, String entityName) {
        StringBuilder sql = new StringBuilder();
        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getPKColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            String columnEqualsHolder = getColumnEqualsHolder(config, entityName, column);
            sql.append(" AND ").append(columnEqualsHolder);
        }
        sql.append("</where>");
        return sql.toString();
    }

    public String wherePKColumn(String columnPrefix, Class<?> entityClass, String keyName) {
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
    public String whereKeyInList(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();

        Set<EntityColumn> columnList = EntityTableFactory.getPKColumns(entityClass);
        if (columnList.size() == 1) {
            EntityColumn column = columnList.iterator().next();
            sql.append(" where ");
            sql.append(getColumnName(config, column));
            sql.append(" in (${_parameter})");
        } else {
            throw new MapperException("继承 selectByIds 方法的实体类[" + entityClass.getCanonicalName() + "]中必须只有一个带有 @Id 注解的字段");
        }
        return sql.toString();
    }


}
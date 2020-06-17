package com.flagwind.mybatis.metadata;

import com.flagwind.mybatis.definition.helper.TemplateSqlHelper;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.reflect.entities.EntityField;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityTable {
    private static final Pattern DELIMITER = Pattern.compile("^[`\\[\"]?(.*?)[`\\]\"]?$");

    private String name;
    private String catalog;
    private String schema;
    private String orderByClause;
    private String baseSelect;
    private Set<EntityColumn> entityClassColumns;
    private Set<EntityColumn> entityClassPKColumns;
    private List<String> keyProperties;
    private List<String> keyColumns;
    private List<EntityField> associationFields;


    /**
     * resultMap对象
     */
    private ResultMap resultMap;

    /**
     * 属性和列对应
     */
    private Map<String, EntityColumn> propertyMap;

    /**
     * 类
     */
    private final Class<?> entityClass;

    public boolean isAssociationRegisted() {
        return associationRegisted;
    }

    public void setAssociationRegisted(boolean associationRegisted) {
        this.associationRegisted = associationRegisted;
    }

    private boolean associationRegisted = false;

    public List<EntityField> getAssociationFields() {
        if (associationFields == null) {
            associationFields = new ArrayList<>();
        }
        return associationFields;
    }

    public void setAssociationFields(List<EntityField> associationFields) {
        this.associationFields = associationFields;
    }


    public EntityTable(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.entityClassColumns = new LinkedHashSet<>();
        this.entityClassPKColumns = new LinkedHashSet<>();
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setTable(Table table) {
        this.name = table.name();
        this.catalog = table.catalog();
        this.schema = table.schema();
    }

    public void setKeyColumns(List<String> keyColumns) {
        this.keyColumns = keyColumns;
    }

    public void setKeyProperties(List<String> keyProperties) {
        this.keyProperties = keyProperties;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getBaseSelect() {
        return baseSelect;
    }

    public void setBaseSelect(String baseSelect) {
        this.baseSelect = baseSelect;
    }

    public String getPrefix() {
        if (StringUtils.isNotEmpty(catalog)) {
            return catalog;
        }
        if (StringUtils.isNotEmpty(schema)) {
            return schema;
        }
        return "";
    }

    public String tableAlias() {
       return TemplateSqlHelper.tableAlias(this.entityClass);
    }

    public String getAliasColumn(String name, String tableAlias) {
        // 若为_a.id说明是带有别名的列，直接返回即可
        if (StringUtils.startsWith(name, "_") || StringUtils.contains(name, ".")) {
            return name;
        }
        if (StringUtils.isEmpty(tableAlias)) {
            tableAlias = this.tableAlias();
        }
        // 若表的别名与当前主要相同，则说明是主表字段
        if (StringUtils.equalsIgnoreCase(tableAlias, this.tableAlias())) {
            EntityColumn entityColumn = this.getColumn(name);
            if (entityColumn != null) {
                return tableAlias + "." + entityColumn.getColumn();
            } else {
                return tableAlias + "." + name;
            }
        }
        if (name.contains(".")) {
            String[] arr = name.split(".");
            EntityField entityField = this.getAssociationFields().stream().filter(g -> StringUtils.equalsIgnoreCase(arr[0], g.getName())).findFirst().get();
            EntityTable entityTable = EntityTableFactory.getEntityTable(entityField.getJavaType());
            return entityTable.getAliasColumn(arr[1], "_" + entityField.getName());
        }

        for (EntityField entityField : this.getAssociationFields()) {
            EntityTable entityTable = EntityTableFactory.getEntityTable(entityField.getJavaType());
            String c = entityTable.getAliasColumn(name, "_" + entityField.getName());
            if (StringUtils.isNotEmpty(c)) {
                return c;
            }
        }
        return "";
    }


    public Set<EntityColumn> getEntityClassColumns() {
        return entityClassColumns;
    }

    public void setEntityClassColumns(Set<EntityColumn> entityClassColumns) {
        this.entityClassColumns = entityClassColumns;
    }

    public Set<EntityColumn> getEntityClassPKColumns() {
        return entityClassPKColumns;
    }

    public void setEntityClassPKColumns(Set<EntityColumn> entityClassPKColumns) {
        this.entityClassPKColumns = entityClassPKColumns;
    }

    public String[] getKeyProperties() {
        if (keyProperties != null && keyProperties.size() > 0) {
            return keyProperties.toArray(new String[]{});
        }
        return new String[]{};
    }

    public void setKeyProperties(String keyProperty) {
        if (this.keyProperties == null) {
            this.keyProperties = new ArrayList<>();
            this.keyProperties.add(keyProperty);
        } else {
            this.keyProperties.add(keyProperty);
        }
    }

    public String[] getKeyColumns() {
        if (keyColumns != null && keyColumns.size() > 0) {
            return keyColumns.toArray(new String[]{});
        }
        return new String[]{};
    }

    public void setKeyColumns(String keyColumn) {
        if (this.keyColumns == null) {
            this.keyColumns = new ArrayList<>();
            this.keyColumns.add(keyColumn);
        } else {
            this.keyColumns.add(keyColumn);
        }
    }

    /**
     * 生成当前实体的resultMap对象
     *
     * @param configuration
     * @return
     */
    public ResultMap getResultMap(Configuration configuration) {
        if (this.resultMap != null) {
            return this.resultMap;
        }
        if (entityClassColumns == null || entityClassColumns.size() == 0) {
            return null;
        }
        List<ResultMapping> resultMappings = new ArrayList<>();
        for (EntityColumn entityColumn : entityClassColumns) {
            String column = entityColumn.getColumn();
            //去掉可能存在的分隔符
            Matcher matcher = DELIMITER.matcher(column);
            if (matcher.find()) {
                column = matcher.group(1);
            }
            ResultMapping.Builder builder = new ResultMapping.Builder(configuration, entityColumn.getProperty(), column, entityColumn.getJavaType());
            if (entityColumn.getJdbcType() != null) {
                builder.jdbcType(entityColumn.getJdbcType());
            }
            if (entityColumn.getTypeHandler() != null) {
                try {
                    builder.typeHandler(getInstance(entityColumn.getJavaType(), entityColumn.getTypeHandler()));
                } catch (Exception e) {
                    throw new MapperException(e);
                }
            } else {
                TypeHandler<?> typeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(entityColumn.getJavaType(), entityColumn.getJdbcType());
                if (typeHandler != null) {
                    builder.typeHandler(typeHandler);
                }
            }
            List<ResultFlag> flags = new ArrayList<>();
            if (entityColumn.isId()) {
                flags.add(ResultFlag.ID);
            }
            builder.flags(flags);
            resultMappings.add(builder.build());
        }
        ResultMap.Builder builder = new ResultMap.Builder(configuration, "BaseMapperResultMap", this.entityClass, resultMappings, true);
        this.resultMap = builder.build();
        return this.resultMap;
    }

    /**
     * 初始化 - Example 会使用
     */
    public void build() {
        propertyMap = new HashMap<>(getEntityClassColumns().size());
        for (EntityColumn column : getEntityClassColumns()) {
            propertyMap.put(column.getProperty(), column);
        }
    }

//	private Map<String, EntityColumn> getPropertyMap() {
//		return propertyMap;
//	}


    public EntityColumn getColumn(String name) {
        for (Map.Entry<String, EntityColumn> kv : propertyMap.entrySet()) {
            if (StringUtils.equalsIgnoreCase(name, kv.getKey())) {
                return kv.getValue();
            }
        }
        return null;
    }

    /**
     * 实例化TypeHandler
     *
     * @param javaTypeClass
     * @param typeHandlerClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        if (javaTypeClass != null) {
            try {
                Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
                return (TypeHandler<T>) c.newInstance(javaTypeClass);
            } catch (NoSuchMethodException ignored) {
                // ignored
            } catch (Exception e) {
                throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
            }
        }
        try {
            Constructor<?> c = typeHandlerClass.getConstructor();
            return (TypeHandler<T>) c.newInstance();
        } catch (Exception e) {
            throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
    }

}

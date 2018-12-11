package com.flagwind.mybatis.metadata;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.common.Config;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.reflection.entities.EntityField;
import com.flagwind.mybatis.reflection.EntityTypeHolder;
import com.flagwind.mybatis.metadata.processors.*;
import com.flagwind.mybatis.utils.AssociationUtils;
import com.flagwind.mybatis.utils.SimpleTypeUtils;
import com.flagwind.persistent.annotation.NameStyle;
import org.apache.ibatis.type.JdbcType;

import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.*;

/**
 * 实体类工具类 - 处理实体和数据库表以及字段关键的一个类
 */
public class EntityTableFactory
{
	private static final Map<Class<?>, EntityTable> CACHE = Collections.synchronizedMap(new WeakHashMap<Class<?>, EntityTable>());


	private static final Map<Class<?>, TableProcessor> CACHE_TABLE_PROCESSOR = Collections.synchronizedMap(new WeakHashMap<Class<?>, TableProcessor>());

	private static final Map<Class<?>, ColumnProcessor> CACHE_COLUMN_PROCESSOR = Collections.synchronizedMap(new WeakHashMap<Class<?>, ColumnProcessor>());

	static
	{
		CACHE_TABLE_PROCESSOR.put(DefaultTableProcessor.class, new DefaultTableProcessor());
		CACHE_COLUMN_PROCESSOR.put(IdAnnotationProcessor.class, new IdAnnotationProcessor());
		CACHE_COLUMN_PROCESSOR.put(ColumnAnnotationProcessor.class, new ColumnAnnotationProcessor());
		CACHE_COLUMN_PROCESSOR.put(ColumnTypeAnnotationProcessor.class, new ColumnTypeAnnotationProcessor());
		CACHE_COLUMN_PROCESSOR.put(OrderByAnnotationProcessor.class, new OrderByAnnotationProcessor());
		CACHE_COLUMN_PROCESSOR.put(EnumeratedAnnotationProcessor.class, new EnumeratedAnnotationProcessor());
		CACHE_COLUMN_PROCESSOR.put(AggregateAnnotationProcessor.class, new AggregateAnnotationProcessor());
		CACHE_COLUMN_PROCESSOR.put(SequenceGeneratorAnnotationProcessor.class, new SequenceGeneratorAnnotationProcessor());
		CACHE_COLUMN_PROCESSOR.put(GeneratedValueAnnotationProcessor.class, new GeneratedValueAnnotationProcessor());
	}


	// region 注册处理器
	public synchronized static void registerTableProcessor(Class<TableProcessor> clzss)
	{
		if(!CACHE_TABLE_PROCESSOR.containsKey(clzss)){
			try
			{
				CACHE_TABLE_PROCESSOR.put(clzss,clzss.newInstance());
			}
			catch(Exception e)
			{
				throw new MapperException("注册实体表处理器异常，类型:" + clzss.getName(), e);
			}
		}
	}

	public synchronized static <P extends TableProcessor> void registerTableProcessor(P processor)
	{
		Class clzss = processor.getClass();
		CACHE_TABLE_PROCESSOR.putIfAbsent(clzss, processor);
	}

	public synchronized static void registerColumnProcessor(Class<ColumnProcessor> clzss){
		if(!CACHE_COLUMN_PROCESSOR.containsKey(clzss)){
			try
			{
				CACHE_COLUMN_PROCESSOR.put(clzss,clzss.newInstance());
			}
			catch(Exception e)
			{
				throw new MapperException("注册实体列处理器异常，类型:" + clzss.getName(), e);
			}
		}
	}

	public synchronized static <P extends ColumnProcessor> void registerColumnProcessor(P processor)
	{
		Class clzss = processor.getClass();
		CACHE_COLUMN_PROCESSOR.putIfAbsent(clzss, processor);
	}

	// endregion

	/**
	 * 判断实体类型是否注册
	 * @param entityClass
	 */
	public static boolean hasEntityTable(Class<?> entityClass)
	{
		EntityTable entityTable = CACHE.get(entityClass);
		return entityTable != null;
	}


	/**
	 * 获取表对象
	 * @param entityClass
	 */
	public static EntityTable getEntityTable(Class<?> entityClass)
	{
		EntityTable entityTable = CACHE.get(entityClass);
		if(entityTable == null)
		{
			throw new MapperException("无法获取实体类" + entityClass.getCanonicalName() + "对应的表名!");
		}
		return entityTable;
	}

	/**
	 * 获取默认的orderby语句
	 * @param entityClass
	 */
	public static String getOrderByClause(Class<?> entityClass)
	{
		EntityTable table = getEntityTable(entityClass);
		if(table.getOrderByClause() != null)
		{
			return table.getOrderByClause();
		}
		StringBuilder orderBy = new StringBuilder();
		for(EntityColumn column : table.getEntityClassColumns())
		{
			if(column.getOrderBy() != null)
			{
				if(orderBy.length() != 0)
				{
					orderBy.append(",");
				}
				orderBy.append(column.getColumn()).append(" ").append(column.getOrderBy());
			}
		}
		table.setOrderByClause(orderBy.toString());
		return table.getOrderByClause();
	}

	/**
	 * 获取全部列
	 * @param entityClass
	 */
	public static Set<EntityColumn> getColumns(Class<?> entityClass)
	{
		return getEntityTable(entityClass).getEntityClassColumns();
	}

	public static EntityColumn getColumn(Class<?> entityClass, String name)
	{
		Set<EntityColumn> columns = getColumns(entityClass);
		if(columns == null || columns.size() == 0)
		{
			return null;
		}
		for(EntityColumn col : columns)
		{
			if(col.getProperty().equalsIgnoreCase(name) || col.getColumn().equalsIgnoreCase(name))
			{
				return col;
			}
		}
		return null;
	}

	/**
	 * 获取主键信息
	 * @param entityClass
	 */
	public static Set<EntityColumn> getPKColumns(Class<?> entityClass)
	{
		return getEntityTable(entityClass).getEntityClassPKColumns();
	}

	/**
	 * 获取查询的Select
	 * @param entityClass
	 */
	public static String getSelectColumns(Class<?> entityClass)
	{
		EntityTable entityTable = getEntityTable(entityClass);
		if(entityTable.getBaseSelect() != null)
		{
			return entityTable.getBaseSelect();
		}
		Set<EntityColumn> columnList = getColumns(entityClass);
		StringBuilder selectBuilder = new StringBuilder();
		boolean skipAlias = Map.class.isAssignableFrom(entityClass);
		for(EntityColumn entityColumn : columnList)
		{
			selectBuilder.append(entityColumn.getColumn());
			if(!skipAlias && !entityColumn.getColumn().equalsIgnoreCase(entityColumn.getProperty()))
			{
				//不等的时候分几种情况，例如`DESC`
				if(entityColumn.getColumn().substring(1, entityColumn.getColumn().length() - 1).equalsIgnoreCase(entityColumn.getProperty()))
				{
					selectBuilder.append(",");
				}
				else
				{
					selectBuilder.append(" AS ").append(entityColumn.getProperty()).append(",");
				}
			}
			else
			{
				selectBuilder.append(",");
			}
		}
		entityTable.setBaseSelect(selectBuilder.substring(0, selectBuilder.length() - 1));
		return entityTable.getBaseSelect();
	}


	/**
	 * 初始化实体属性
	 * @param entityClass
	 * @param config
	 */
	public static synchronized void register(Class<?> entityClass, Config config)
	{
		if(CACHE.get(entityClass) != null)
		{
			return;
		}
		Style style = config.getStyle();
		//style，该注解优先于全局配置
		if(entityClass.isAnnotationPresent(NameStyle.class))
		{
			NameStyle nameStyle = entityClass.getAnnotation(NameStyle.class);
			style = nameStyle.value();
		}

		//创建并缓存EntityTable
		EntityTable entityTable = new EntityTable(entityClass);
		for(TableProcessor processor : CACHE_TABLE_PROCESSOR.values()){
			processor.process(entityTable,config);
		}

		//处理所有列
		List<EntityField> fields =  EntityTypeHolder.getFields(entityClass);
		//        }
		for(EntityField field : fields)
		{
			//如果启用了简单类型，就做简单类型校验，如果不是简单类型，直接跳过
			if(config.isUseSimpleType() && !SimpleTypeUtils.isSimpleType(field.getJavaType()))
			{
				continue;
			}
			if(AssociationUtils.isAssociationField(field))
			{
				entityTable.getAssociationFields().add(field);
				continue;
			}
			processField(entityTable, style, field);
		}
		//当pk.size=0的时候使用所有列作为主键
		if(entityTable.getEntityClassPKColumns().size() == 0)
		{
			entityTable.setEntityClassPKColumns(entityTable.getEntityClassColumns());
		}
		entityTable.build();
		CACHE.put(entityClass, entityTable);
	}


	/**
	 * 处理一列
	 * @param entityTable
	 * @param style
	 * @param field
	 */
	private static void processField(EntityTable entityTable, Style style, EntityField field)
	{
		//排除字段
		if(field.isAnnotationPresent(Transient.class))
		{
			return;
		}

		EntityColumn entityColumn = new EntityColumn(entityTable);
		entityColumn.setProperty(field.getName());
		entityColumn.setJavaType(field.getJavaType());

		for(ColumnProcessor processor : CACHE_COLUMN_PROCESSOR.values())
		{
			processor.process(entityColumn, field, style);
		}

		if(entityColumn.getColumn() == null){
			entityColumn.setColumn(field.getName());
		}

		if(entityColumn.getJdbcType() == null)
		{
			entityColumn.setJdbcType(formJavaType(entityColumn.getJavaType()));
		}
		entityTable.getEntityClassColumns().add(entityColumn);
		if(entityColumn.isId())
		{
			entityTable.getEntityClassPKColumns().add(entityColumn);
		}
	}

	public static JdbcType formJavaType(Class<?> javaType)
	{
		if(javaType == null)
		{
			return JdbcType.UNDEFINED;
		}
		if(javaType.isAssignableFrom(String.class))
		{
			return JdbcType.VARCHAR;
		}
		if(javaType.isAssignableFrom(Integer.class) || javaType.isAssignableFrom(int.class))
		{
			return JdbcType.INTEGER;
		}
		if(javaType.isAssignableFrom(Number.class))
		{
			return JdbcType.NUMERIC;
		}
		if(javaType.isAssignableFrom(Long.class) || javaType.isAssignableFrom(long.class))
		{
			return JdbcType.NUMERIC;
		}
		if(javaType.isAssignableFrom(Double.class) || javaType.isAssignableFrom(double.class))
		{
			return JdbcType.NUMERIC;
		}
		if(javaType.isAssignableFrom(Boolean.class) || javaType.isAssignableFrom(boolean.class))
		{
			return JdbcType.TINYINT;
		}
		if(javaType.isAssignableFrom(Float.class) || javaType.isAssignableFrom(float.class))
		{
			return JdbcType.FLOAT;
		}

		if(javaType.isAssignableFrom(Timestamp.class))
		{
			return JdbcType.TIMESTAMP;
		}

		if(javaType.isAssignableFrom(java.sql.Time.class))
		{
			return JdbcType.TIME;
		}

		if(javaType.isAssignableFrom(java.sql.Date.class) || javaType.isAssignableFrom(java.util.Date.class))
		{
			return JdbcType.DATE;
		}

		if(javaType.isAssignableFrom(byte[].class))
		{
			return JdbcType.BINARY;
		}
		if(javaType.isEnum())
		{
			return JdbcType.VARCHAR;
		}
		return JdbcType.UNDEFINED;
	}

}
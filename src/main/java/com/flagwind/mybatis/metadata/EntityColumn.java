package com.flagwind.mybatis.metadata;

import com.flagwind.persistent.AggregateEntry;
import com.flagwind.persistent.Functions;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * 实体字段对应数据库列的信息
 * @author chendb
 */
public class EntityColumn {
	private EntityTable table;
	private String property;
	private String column;
	private Class<?> javaType;
	private JdbcType jdbcType;
	private Class<? extends TypeHandler<?>> typeHandler;
	private String sequenceName;
	private boolean id = false;
	private boolean uuid = false;
	private boolean identity = false;
	private String generator;

	/**
	 * 聚合字段定义
	 */
	private AggregateEntry aggregate;





	/**
	 * 排序
	 */
	private String orderBy;

	/**
	 * 可插入
	 */
	private boolean insertable = true;

	/**
	 * 可更新
	 */
	private boolean updatable = true;



	public EntityColumn() {
	}

	public EntityColumn(EntityTable table) {
		this.table = table;
	}
	public EntityTable getTable() {
		return table;
	}

	public void setTable(EntityTable table) {
		this.table = table;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getColumn() {
			return column;
	}

	public void setColumn(String column) {
		this.column = Functions.invoke(column);
	}

	public Class<?> getJavaType() {
		return javaType;
	}

	public AggregateEntry getAggregate() {
		return aggregate;
	}

	public void setAggregate(AggregateEntry aggregate) {
		this.aggregate = aggregate;
	}


	public void setJavaType(Class<?> javaType) {
		this.javaType = javaType;
	}


	public JdbcType getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(JdbcType jdbcType) {
		this.jdbcType = jdbcType;
	}

	public Class<? extends TypeHandler<?>> getTypeHandler() {
		return typeHandler;
	}

	public void setTypeHandler(Class<? extends TypeHandler<?>> typeHandler) {
		this.typeHandler = typeHandler;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public boolean isId() {
		return id;
	}

	public void setId(boolean id) {
		this.id = id;
	}

	public boolean isUuid() {
		return uuid;
	}

	public void setUuid(boolean uuid) {
		this.uuid = uuid;
	}

	public boolean isIdentity() {
		return identity;
	}

	public void setIdentity(boolean identity) {
		this.identity = identity;
	}

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isInsertable() {
		return insertable;
	}

	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}

	public boolean isUpdatable() {
		return updatable;
	}

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}

	/**
	 * 返回格式如:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
	 *
	 * @return
	 */
	public String getColumnEqualsHolder() {
		return getColumnEqualsHolder(null);
	}

	/**
	 * 返回格式如:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
	 *
	 * @param entityName
	 * @return
	 */
	public String getColumnEqualsHolder(String entityName) {
		return this.column + " = " + getColumnHolder(entityName);
	}

	/**
	 * 返回格式如:#{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
	 *
	 * @return
	 */
	public String getColumnHolder() {
		return getColumnHolder(null);
	}

	/**
	 * 返回格式如:#{entityName.age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
	 *
	 * @param entityName
	 * @return
	 */
	public String getColumnHolder(String entityName) {
		return getColumnHolder(entityName, null);
	}

	/**
	 * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
	 *
	 * @param entityName
	 * @param suffix
	 * @return
	 */
	public String getColumnHolder(String entityName, String suffix) {
		return getColumnHolder(entityName, null, null);
	}

	/**
	 * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler},
	 *
	 * @param entityName
	 * @param suffix
	 * @return
	 */
	public String getColumnHolderWithComma(String entityName, String suffix) {
		return getColumnHolder(entityName, suffix, ",");
	}

	/**
	 * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}+separator
	 *
	 * @param entityName
	 * @param suffix
	 * @param separator
	 * @return
	 */
	public String getColumnHolder(String entityName, String suffix, String separator) {
		StringBuffer sb = new StringBuffer("#{");
		if (StringUtils.isNotEmpty(entityName)) {
			sb.append(entityName);
			sb.append(".");
		}
		sb.append(this.property);
		if (StringUtils.isNotEmpty(suffix)) {
			sb.append(suffix);
		}
		if (this.jdbcType != null) {
			sb.append(",jdbcType=");
			sb.append(this.jdbcType.toString());
		} else if (this.typeHandler != null) {
			sb.append(",typeHandler=");
			sb.append(this.typeHandler.getCanonicalName());
			//当类型为数组时，不设置javaType#103
		} else if (!this.javaType.isArray()) {
			sb.append(",javaType=");
			sb.append(javaType.getCanonicalName());
		}
		sb.append("}");
		if (StringUtils.isNotEmpty(separator)) {
			sb.append(separator);
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		EntityColumn that = (EntityColumn) o;

		if (id != that.id) {
			return false;
		}
		if (uuid != that.uuid) {
			return false;
		}
		if (identity != that.identity) {
			return false;
		}
		if (table != null ? !table.equals(that.table) : that.table != null) {
			return false;
		}
		if (property != null ? !property.equals(that.property) : that.property != null) {
			return false;
		}
		if (column != null ? !column.equals(that.column) : that.column != null) {
			return false;
		}
		if (javaType != null ? !javaType.equals(that.javaType) : that.javaType != null) {
			return false;
		}
		if (jdbcType != that.jdbcType) {
			return false;
		}
		if (typeHandler != null ? !typeHandler.equals(that.typeHandler) : that.typeHandler != null) {
			return false;
		}
		if (sequenceName != null ? !sequenceName.equals(that.sequenceName) : that.sequenceName != null) {
			return false;
		}
		if (generator != null ? !generator.equals(that.generator) : that.generator != null) {
			return false;
		}
		return !(orderBy != null ? !orderBy.equals(that.orderBy) : that.orderBy != null);

	}

	@Override
	public int hashCode() {
		int result = table != null ? table.hashCode() : 0;
		result = 31 * result + (property != null ? property.hashCode() : 0);
		result = 31 * result + (column != null ? column.hashCode() : 0);
		result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
		result = 31 * result + (jdbcType != null ? jdbcType.hashCode() : 0);
		result = 31 * result + (typeHandler != null ? typeHandler.hashCode() : 0);
		result = 31 * result + (sequenceName != null ? sequenceName.hashCode() : 0);
		result = 31 * result + (id ? 1 : 0);
		result = 31 * result + (uuid ? 1 : 0);
		result = 31 * result + (identity ? 1 : 0);
		result = 31 * result + (generator != null ? generator.hashCode() : 0);
		result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
		return result;
	}
}

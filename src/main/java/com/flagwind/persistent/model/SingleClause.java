package com.flagwind.persistent.model;

public class SingleClause implements Clause {
	private String name;
	private Object[] values;
	private ClauseOperator operator;
	
	public SingleClause(String name,  Object...values) {
		this(name,ClauseOperator.Equal,values);
	}

	public SingleClause(String name, ClauseOperator operator, Object... values) {

		this.name = name;
		this.operator = operator;
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public ClauseOperator getOperator() {
		return operator;
	}

	public void setOperator(ClauseOperator operator) {
		this.operator = operator;
	}

	
	public Object getValue() {
		if (this.getValues().length > 0) {
			return this.getValues()[0];
		} else {
			return null;
		}
	}
	
	public Object getStartValue() {
		if (this.getValues().length > 0) {
			return this.getValues()[0];
		} else {
			return null;
		}
	}

	public Object getEndValue() {
		if (this.getValues().length > 1) {
			return this.getValues()[1];
		} else {
			return null;
		}
	}
	

	// region 链式方法

	public CombineClause toCombine() {
		return toCombine(ClauseCombine.And);
	}

	public CombineClause toCombine(ClauseCombine combine) {
		CombineClause clause = new CombineClause(combine);
		clause.add(this);
		return clause;
	}

	// endregion

	// region 静态构造方法

	/**
	 * 构建等值条件
	 */
	public static SingleClause equal(String name, Object value) {
		return new SingleClause(name, ClauseOperator.Equal, value);
	}

	/**
	 * 构建不等值条件
	 */
	public static SingleClause notEqual(String name, Object value) {
		return new SingleClause(name, ClauseOperator.NotEqual, value);
	}

	/**
	 * 构建like条件
	 */
	public static SingleClause like(String name, String value) {
		return new SingleClause(name, ClauseOperator.Like, value);
	}

	/**
	 * 构建not like条件
	 */
	public static SingleClause notLike(String name, String value) {
		return new SingleClause(name, ClauseOperator.NotLike, value);
	}

	/**
	 * 构建in条件
	 */
	public static SingleClause in(String name, Object... values) {
		return new SingleClause(name, ClauseOperator.In, values);
	}

	/**
	 * 构建not in条件
	 */
	public static SingleClause notIn(String name, Object... values) {
		return new SingleClause(name, ClauseOperator.NotIn, values);
	}
	/**
	 * 构建>条件
	 */
	public static SingleClause greaterThan(String name, Object value) {
		return new SingleClause(name, ClauseOperator.GreaterThan, value);
	}
	/**
	 * 构建>=条件
	 */
	public static SingleClause greaterThanEqual(String name, Object value) {
		return new SingleClause(name, ClauseOperator.GreaterThanEqual, value);
	}
	/**
	 * 构建<条件
	 */
	public static SingleClause lessThan(String name, Object value) {
		return new SingleClause(name, ClauseOperator.LessThan, value);
	}
	/**
	 * 构建<=条件
	 */
	public static SingleClause lessThanEqual(String name, Object value) {
		return new SingleClause(name, ClauseOperator.LessThanEqual, value);
	}
	/**
	 * 构建between条件
	 */
	public static SingleClause between(String name, Object start,Object end) {
		return new SingleClause(name, ClauseOperator.Between, start,end);
	}
	/**
	 * 构建is null条件
	 */
	public static SingleClause isNull(String name) {
		return new SingleClause(name, ClauseOperator.Null, true);
	}
	/**
	 * 构建not is null条件
	 */
	public static SingleClause notNull(String name) {
		return new SingleClause(name, ClauseOperator.NotNull, true);
	}

	// endregion
}


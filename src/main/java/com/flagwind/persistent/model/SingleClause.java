package com.flagwind.persistent.model;

/**
 * 
* @Description: 单条查询项
* @author chendb
* @date 2015年10月5日 下午4:07:05 
*
 */
public class SingleClause implements Clause {
	private String name;
	private Object[] values;
	private ClauseOperator operator;
	
	public SingleClause(String name,  Object...values) {
		this(name,ClauseOperator.Equal,values);
	}

	public SingleClause(String name, ClauseOperator operator, Object... values) {
		// TODO Auto-generated constructor stub
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
		if (this.getValues().length > 0)
			return this.getValues()[0];
		else
			return null;
	}
	
	public Object getStartValue() {
		if (this.getValues().length > 0)
			return this.getValues()[0];
		else
			return null;
	}

	public Object getEndValue() {
		if (this.getValues().length > 1)
			return this.getValues()[1];
		else
			return null;
	}
	
	public CombineClause toCombine(){
		return toCombine(ClauseCombine.And);
	}
	
	public CombineClause toCombine(ClauseCombine combine){
		CombineClause clause = new CombineClause(combine);
		clause.add(this);
		return clause;
	}
}

package com.flagwind.persistent.model;


public class ChildClause extends CombineClause {

    private static final long serialVersionUID = 5776135358546775949L;
    private String name;
    private String childField;
    private String childTable;
    private boolean included = true;
    private ClauseOperator operator;

    public ChildClause(String name, boolean included, String childField, String childTable, ClauseCombine combine) {
        super(combine);
        this.name = name;
        this.included = included;
        this.childField = childField;
        this.childTable = childTable;
        this.operator = ClauseOperator.Child;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getChildField() {
        return childField;
    }

    public void setChildField(String childField) {
        this.childField = childField;
    }

    public String getChildTable() {
        return childTable;
    }

    public void setChildTable(String childTable) {
        this.childTable = childTable;
    }

    public ClauseOperator getOperator() {
        return operator;
    }

    public void setOperator(ClauseOperator operator) {
        this.operator = operator;
    }

    public boolean getIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }


    // region 链式方法

    /**
     * 设置组合方法
     */
    public ChildClause combine(ClauseCombine combine) {
        this.setCombine(combine);
        return this;
    }

    // endregion


    // region 静态方法

    /**
     * 构建 id in (select id from table where x=xx and y=yy) 子查询条件
     */
    public static ChildClause include(String name, String childField, String childTable) {
        return new ChildClause(name, true, childField, childTable, ClauseCombine.And);
    }

    /**
     * 构建 id not in (select id from table where x=xx and y=yy) 子查询条件
     */
    public static ChildClause exclude(String name, String childField, String childTable) {
        return new ChildClause(name, false, childField, childTable, ClauseCombine.And);
    }

    // endregion

}

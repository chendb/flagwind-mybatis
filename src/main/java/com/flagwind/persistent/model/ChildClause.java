package com.flagwind.persistent.model;


public class ChildClause extends CombineClause {

    private static final long serialVersionUID = 5776135358546775949L;
    private String name;
    private String childField;
    private String childTable;
    private boolean included = true;
    private ClauseOperator operator;

    public ChildClause(String name, boolean included, String childFeild, String childTable, ClauseCombine combine) {
        super(combine);
        this.name = name;
        this.included = included;
        this.childField = childFeild;
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

}

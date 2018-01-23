package com.flagwind.persistent;


public class AggregateEntry {
    private AggregateType type;
    private String column;
    private String alias;

    public AggregateEntry(AggregateType type, String alias) {
        this.type = type;
        this.column = alias;
        this.alias = alias;
    }

    public AggregateEntry(AggregateType type, String column, String alias) {
        this.type = type;
        this.column = column;
        this.alias = alias;
    }

    public AggregateType getType() {
        return type;
    }

    public void setType(AggregateType type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}

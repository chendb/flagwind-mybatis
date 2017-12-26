package com.flagwind.mybatis.entity;

public class RelationshipTable {

	private String fromTable; 
	private String toTable;
	private String join;
	private String fromFeild;
	private String toFeild;
	public String getFromTable() {
		return fromTable;
	}
	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}
	public String getToTable() {
		return toTable;
	}
	public void setToTable(String toTable) {
		this.toTable = toTable;
	}
	public String getJoin() {
		return join;
	}
	public void setJoin(String join) {
		this.join = join;
	}
	public String getFromFeild() {
		return fromFeild;
	}
	public void setFromFeild(String fromFeild) {
		this.fromFeild = fromFeild;
	}
	public String getToFeild() {
		return toFeild;
	}
	public void setToFeild(String toFeild) {
		this.toFeild = toFeild;
	}
	

  
}

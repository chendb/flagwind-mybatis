package com.flagwind.persistent.model;

public enum ClauseOperator {
	// 等于
	Equal("=", "等于"),
	// 不等于
	NotEqual("!=", "不等于"),
	// 大于
	GreaterThan(">", "大于"),
	// 大于或等于
	GreaterThanEqual(">=", "大于等于"),
	// 小于
	LessThan("<", "小于"),
	// 小于或等于
	LessThanEqual("<=", "小于等于"),
	// 介于
	Between("Between", "介于"),
	// 范围
	In("In", "范围"),
	// 排除范围
	NotIn("Not In", "排除范围"),
	// 空值
	Null("Is", "空值"),
	// 非空值
	NotNull("Is Not", "非空值"),
	// 模糊查询
	Like("Like", "匹配"),
	// NotLike
	NotLike("Not Like", "不匹配"),
	// Child
	Child("Child", "子查询");

	private final String description;
	private final String alias;

	// 构造器默认也只能是private, 从而保证构造函数只能在内部使用
	ClauseOperator(String alias, String description) {
		this.description = description;
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public String getDescription() {
		return description;
	}
}

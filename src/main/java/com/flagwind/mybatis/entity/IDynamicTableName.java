package com.flagwind.mybatis.entity;

public interface IDynamicTableName {

    /**
     * 获取动态表名 - 只要有返回值，不是null和''，就会用返回值作为表名
     *
     * @return
     */
    String getDynamicTableName();
}

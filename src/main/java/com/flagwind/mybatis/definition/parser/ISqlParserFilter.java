package com.flagwind.mybatis.definition.parser;

import org.apache.ibatis.reflection.MetaObject;

/**
 * @author chendb
 * @description: SQL 解析过滤器
 * @date 2020-04-15 22:19:00
 */
public interface ISqlParserFilter {

    boolean doFilter(MetaObject metaObject);

}
package com.flagwind.mybatis.metadata;

import com.flagwind.mybatis.code.DatabaseType;

/**
 * 自定义函数处理接口
 */
public interface FunctionProcessor {
    String process(String arguments, String alias, DatabaseType databaseType);
}

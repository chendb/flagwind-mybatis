package com.flagwind.mybatis.definition.parser;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author chendb
 * @description: sql info
 * @date 2020-04-15 21:11:04
 */
@Data
@Accessors(chain = true)
public class SqlInfo {

    /**
     * SQL 内容
     */
    private String sql;
    /**
     * 是否排序
     */
    private boolean orderBy = true;

    public static SqlInfo newInstance() {
        return new SqlInfo();
    }
}
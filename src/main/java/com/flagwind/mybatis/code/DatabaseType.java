/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.flagwind.mybatis.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据库类型
 *
 * @author chendb
 */
@Getter
@AllArgsConstructor
public enum DatabaseType {

    /**
     * MySQL
     */
    MySQL("mysql", "MySql数据库"),
    /**
     * MariaDB
     */
    MariaDB("mariadb", "MariaDB数据库"),
    /**
     * Oracle
     */
    Oracle("oracle", "Oracle11g及以下数据库(高版本推荐使用ORACLE_NEW)"),
    /**
     * oracle12c new pagination
     */
    Oracle_12C("oracle12c", "Oracle12c+数据库"),

    /**
     * DB2
     */
    DB2("db2", "DB2数据库"),
    /**
     * H2
     */
    H2("h2", "H2数据库"),
    /**
     * HSQL
     */
    HSQL("hsql", "HSQL数据库"),
    /**
     * Sqlite
     */
    Sqlite("sqlite", "SQLite数据库"),
    /**
     * POSTGRE
     */
    PostgreSQL("postgresql", "Postgre数据库"),
    /**
     * SQLSERVER2005
     */
    SqlServer2005("sqlserver2005", "SQLServer2005数据库"),
    /**
     * SQLSERVER
     */
    SqlServer("sqlserver", "SQLServer数据库"),
    /**
     * DM
     */
    DM("dm", "达梦数据库"),
    /**
     * xugu
     */
    XU_GU("xugu", "虚谷数据库"),
    /**
     * Kingbase
     */
    KingbaseES("kingbasees", "人大金仓数据库"),

    /**
     * Phoenix
     */
    Phoenix("phoenix", "Phoenix HBase数据库"),

    /**
     * Gauss
     */
    Gauss("zenith", "Gauss 数据库"),

    Sybase("sybase","sybase 数据库"),

    Derby("derby","Derby 数据库"),

    Informix("informix","informix 数据库"),

    Cloudscape("Cloudscape","cloudscape 数据库"),
    /**
     * UNKONWN DB
     */
    OTHER("other", "其他数据库");

    /**
     * 数据库名称
     */
    private final String db;
    /**
     * 描述
     */
    private final String desc;


    /**
     * 获取数据库类型
     *
     * @param dbType 数据库类型字符串
     */
    public static DatabaseType parse(String dbType) {
        for (DatabaseType type : DatabaseType.values()) {
            if (type.db.equalsIgnoreCase(dbType)) {
                return type;
            }
        }
        return OTHER;

    }
}

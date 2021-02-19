package com.flagwind.mybatis.utils;

import com.flagwind.mybatis.code.DatabaseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * @author chendb
 * @description: Jdbc 工具
 * @date 2020-04-16 15:35:43
 */
public class JdbcUtils {

    private static final Log logger = LogFactory.getLog(JdbcUtils.class);

    /**
     * 根据连接地址判断数据库类型
     *
     * @param jdbcUrl 连接地址
     * @return ignore
     */
    public static DatabaseType getDbType(String jdbcUrl) {
        Assert.hasText(jdbcUrl, "Error: The jdbcUrl is Null, Cannot read database type");
        String url = jdbcUrl.toLowerCase();
        if (url.contains(":mysql:") || url.contains(":cobar:")) {
            return DatabaseType.MySQL;
        } else if (url.contains(":mariadb:")) {
            return DatabaseType.MariaDB;
        } else if (url.contains(":oracle:")) {
            return DatabaseType.Oracle;
        } else if (url.contains(":sqlserver:") || url.contains(":microsoft:")) {
            return DatabaseType.SqlServer2005;
        } else if (url.contains(":sqlserver2012:")) {
            return DatabaseType.SqlServer;
        } else if (url.contains(":postgresql:")) {
            return DatabaseType.PostgreSQL;
        } else if (url.contains(":hsqldb:")) {
            return DatabaseType.HSQL;
        } else if (url.contains(":db2:")) {
            return DatabaseType.DB2;
        } else if (url.contains(":sqlite:")) {
            return DatabaseType.Sqlite;
        } else if (url.contains(":h2:")) {
            return DatabaseType.H2;
        } else if (url.contains(":dm:")) {
            return DatabaseType.DM;
        } else if (url.contains(":oscar:")) {
            return DatabaseType.Oscar;
        } else if (url.contains(":xugu:")) {
            return DatabaseType.XU_GU;
        } else if (url.contains(":kingbase:") || url.contains(":kingbase8:")) {
            return DatabaseType.KingbaseES;
        } else if (url.contains(":phoenix:")) {
            return DatabaseType.Phoenix;
        } else if (jdbcUrl.contains(":zenith:")) {
            return DatabaseType.Gauss;
        } else {
            logger.warn("The jdbcUrl is " + jdbcUrl + ", Mybatis Plus Cannot Read Database type or The Database's Not Supported!");
            return DatabaseType.OTHER;
        }
    }
}

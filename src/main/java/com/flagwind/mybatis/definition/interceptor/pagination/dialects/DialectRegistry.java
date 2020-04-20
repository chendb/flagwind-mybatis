
package com.flagwind.mybatis.definition.interceptor.pagination.dialects;


import com.flagwind.mybatis.code.DatabaseType;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author nieqiuqiu
 * @date 2020-01-09
 * @since 3.3.1
 */
public class DialectRegistry {

    private final Map<DatabaseType, IDialect> dialect_enum_map = new EnumMap<>(DatabaseType.class);

    public DialectRegistry() {
        dialect_enum_map.put(DatabaseType.MySQL, new MySqlDialect());
        dialect_enum_map.put(DatabaseType.MariaDB, new MariaDBDialect());
        dialect_enum_map.put(DatabaseType.Oracle, new OracleDialect());
        dialect_enum_map.put(DatabaseType.Oracle_12C, new Oracle12cDialect());
        dialect_enum_map.put(DatabaseType.DB2, new DB2Dialect());
        dialect_enum_map.put(DatabaseType.H2, new H2Dialect());
        dialect_enum_map.put(DatabaseType.HSQL, new HSQLDialect());
        dialect_enum_map.put(DatabaseType.Sqlite, new SQLiteDialect());
        dialect_enum_map.put(DatabaseType.PostgreSQL, new PostgreDialect());
        dialect_enum_map.put(DatabaseType.SqlServer2005, new SQLServer2005Dialect());
        dialect_enum_map.put(DatabaseType.SqlServer, new SQLServerDialect());
        dialect_enum_map.put(DatabaseType.DM, new DmDialect());
        dialect_enum_map.put(DatabaseType.XU_GU, new XuGuDialect());
        dialect_enum_map.put(DatabaseType.KingbaseES, new KingbaseDialect());
        dialect_enum_map.put(DatabaseType.Phoenix, new PhoenixDialect());
        dialect_enum_map.put(DatabaseType.Gauss, new GaussDialect());
    }

    public IDialect getDialect(DatabaseType dbType) {
        return dialect_enum_map.get(dbType);
    }

    public Collection<IDialect> getDialects() {
        return Collections.unmodifiableCollection(dialect_enum_map.values());
    }
}

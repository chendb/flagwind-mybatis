package com.flagwind.mybatis.code;

public enum IdentityDialect {
    DB2("VALUES IDENTITY_VAL_LOCAL()"),
    MYSQL("SELECT LAST_INSERT_ID()"),
    SQLSERVER("SELECT SCOPE_IDENTITY()"),
    CLOUDSCAPE("VALUES IDENTITY_VAL_LOCAL()"),
    DERBY("VALUES IDENTITY_VAL_LOCAL()"),
    HSQLDB("CALL IDENTITY()"),
    SYBASE("SELECT @@IDENTITY"),
    INFORMIX("select dbinfo('sqlca.sqlerrd1') from systables where tabid=1");

    private final String identityRetrievalStatement;

    IdentityDialect(String identityRetrievalStatement) {
        this.identityRetrievalStatement = identityRetrievalStatement;
    }

    public static IdentityDialect parse(String database)
    {
        IdentityDialect returnValue ;
        DatabaseType databaseType = DatabaseType.parse(database);
        switch(databaseType)
        {
            case DB2:
                returnValue = DB2;
                break;
            case SqlServer:
                returnValue = SQLSERVER;
                break;
            case Cloudscape:
                returnValue = CLOUDSCAPE;
                break;
            case Derby:
                returnValue = DERBY;
                break;
            case HSQL:
                returnValue = HSQLDB;
                break;
            case Sybase:
                returnValue = SYBASE;
                break;

            case Informix:
                returnValue = INFORMIX;
                break;
            default:
                returnValue = MYSQL;
        }

        return returnValue;
    }

    public String getIdentityRetrievalStatement() {
        return identityRetrievalStatement;
    }
}

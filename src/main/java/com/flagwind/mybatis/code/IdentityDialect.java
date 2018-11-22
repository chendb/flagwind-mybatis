package com.flagwind.mybatis.code;

public enum IdentityDialect {
    DB2("VALUES IDENTITY_VAL_LOCAL()"),
    MYSQL("SELECT LAST_INSERT_ID()"),
    SQLSERVER("SELECT SCOPE_IDENTITY()"),
    CLOUDSCAPE("VALUES IDENTITY_VAL_LOCAL()"),
    DERBY("VALUES IDENTITY_VAL_LOCAL()"),
    HSQLDB("CALL IDENTITY()"),
    SYBASE("SELECT @@IDENTITY"),
    DB2_MF("SELECT IDENTITY_VAL_LOCAL() FROM SYSIBM.SYSDUMMY1"),
    INFORMIX("select dbinfo('sqlca.sqlerrd1') from systables where tabid=1");

    private String identityRetrievalStatement;

    private IdentityDialect(String identityRetrievalStatement) {
        this.identityRetrievalStatement = identityRetrievalStatement;
    }

    public static IdentityDialect parse(String database)
    {
        IdentityDialect returnValue ;
        DialectType dialectType = DialectType.parse(database);
        switch(dialectType)
        {
            case DB2:
                returnValue = DB2;
                break;
            case MySQL:
                returnValue = MYSQL;
                break;
            case SQLServer:
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
            case DB2_MF:
                returnValue = DB2_MF;
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

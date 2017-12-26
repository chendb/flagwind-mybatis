package com.flagwind.mybatis.utils;

public class ClauseUtils {

    public static String getUpdatePartSetSql(String mapName) {
        String sql =
                "<foreach collection=\"" + mapName + "\" index=\"key\" item=\"itemValue\"  open=\"set\"  close=\"\"  separator=\",\">\n" +
                    "<if test=\"itemValue!=null\">\n" +
                        "\t${key}=#{itemValue}\n" +
                    "</if>\n" +
                    "<if test=\"itemValue==null\">\n" +
                        "\t${key}=#{itemValue,jdbcType=VARCHAR}\n" +
                     "</if>" +
                "</foreach>\n";
        return sql;
    }

    public static String getWhereSql(String clauseName,int depth) {
        depth = 3;
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"" + clauseName + " != null\">\n");
        sql.append("\t<where>\n");
        sql.append("\t\t<choose>\n");
        sql.append(getSingleClauseSql(clauseName));
        String childSql = "";
        for (int i = depth; i > 0; i--) {
            childSql = getCombineClauseSql(clauseName + i, clauseName + (i + 1), childSql, true);
        }
        sql.append(getCombineClauseSql(clauseName, clauseName + 1, childSql, true));
        sql.append(getChildClauseSql(clauseName, clauseName + 1, childSql));
        sql.append("\t\t</choose>\n");
        sql.append("\t</where>\n");
        sql.append("</if>\n");
        return sql.toString();
    }

    private static String getSingleClauseSql(String clauseName){
        String sql="<when test=\"@com.flagwind.mybatis.utils.OGNL@isSingleClause("+clauseName+")\">\n" +
                    "\t<if test=\"@com.flagwind.mybatis.utils.OGNL@isSingleValue("+clauseName+")\">  \n" +
                    "\t\t${"+clauseName+".name} ${"+clauseName+".operator.alias} #{"+clauseName+".value}\n" +
                    "\t</if>\n" +
                    "\t<if test=\"@com.flagwind.mybatis.utils.OGNL@isListValue("+clauseName+")\">\n" +
                    "\t\t${"+clauseName+".name} ${"+clauseName+".operator.alias}\n" +
                    "\t\t <foreach collection=\""+clauseName+".values\" item=\"listItem\" open=\"(\"  close=\")\" separator=\",\">\n" +
                    "\t\t\t\t#{listItem}\n" +
                    "\t\t </foreach>\n" +
                    "\t</if>\n" +
                    "\t<if test=\"@com.flagwind.mybatis.utils.OGNL@isBetweenValue("+clauseName+")\">  \n" +
                    "\t\t ${"+clauseName+".name} ${"+clauseName+".operator.alias} #{"+clauseName+".startValue} and #{"+clauseName+".endValue}\n" +
                    "\t</if>\n" +
                    "\t<if test=\"@com.flagwind.mybatis.utils.OGNL@isNullValue("+clauseName+")\">  \n" +
                    "\t\t ${"+clauseName+".name} ${"+clauseName+".operator.alias}\n" +
                    "\t</if>\n" +
                    "</when>\n";
        return sql;
    }

    private static String getChildClauseSql(String clauseName,String childClauseName,String childSql) {
        String sql =
                "<when test=\"@com.flagwind.mybatis.utils.OGNL@isChildClause(" + clauseName + ")\">\n" +
                "\t${" + clauseName + ".name} <if test=\"" + clauseName + ".included==false\"> not </if>  in  (" +
                "select ${" + clauseName + ".childField} from ${" + clauseName + ".childTable}\n" +
                "\t<where>\n" +
                        getCombineClauseSql(clauseName, childClauseName, childSql, false) +
                "\t</where>\n" +
                ")"+
                "</when>\n";
        return sql;
    }

    private static String getCombineClauseSql(String clauseName,String childClauseName,String childSql,boolean isWrapByWhen) {
        String sql =
                (isWrapByWhen ? "<when test=\"@com.flagwind.mybatis.utils.OGNL@isCombineClause(" + clauseName + ")\">\n" : "") +
                "\t<foreach collection=\"" + clauseName + "\" item=\"" + childClauseName + "\"  open=\"(\"  close=\")\" index=\"idx\"  separator=\"\">\n" +
                "\t\t<when test=\"@com.flagwind.mybatis.utils.OGNL@isSingleClause(" + childClauseName + ")\">\n" +
                "\t\t\t<if test=\"@com.flagwind.mybatis.utils.OGNL@isSingleValue(" + childClauseName + ")\">  \n" +
                "\t\t\t\t  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if>   ${" + childClauseName + ".name} ${" + childClauseName + ".operator.alias} #{" + childClauseName + ".value}\n" +
                "\t\t\t</if>\n" +
                "\t\t\t<if test=\"@com.flagwind.mybatis.utils.OGNL@isListValue(" + childClauseName + ")\">  \n" +
                "\t\t\t\t <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if>   ${" + childClauseName + ".name} ${" + childClauseName + ".operator.alias}\n" +
                "\t\t\t\t <foreach collection=\"" + childClauseName + ".values\" item=\"listItem1\" open=\"(\"  close=\")\" separator=\",\">\n" +
                "\t\t\t\t\t\t#{listItem1}\n" +
                "\t\t\t\t </foreach>\n" +
                "\t\t\t</if>\n" +
                "\t\t\t<if test=\"@com.flagwind.mybatis.utils.OGNL@isBetweenValue(" + childClauseName + ")\">  \n" +
                "\t\t\t\t  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if>   ${" + childClauseName + ".name} ${" + childClauseName + ".operator.alias} #{" + childClauseName + ".startValue} and #{" + childClauseName + ".endValue}\n" +
                "\t\t\t</if>\n" +
                "\t\t\t<if test=\"@com.flagwind.mybatis.utils.OGNL@isNullValue(" + childClauseName + ")\">  \n" +
                "\t\t\t\t  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if>   ${" + childClauseName + ".name} ${" + childClauseName + ".operator.alias}\n" +
                "\t\t\t</if>\n" +
                "\t\t</when>\n" +
                childSql +
                "\t</foreach>\n" +
                (isWrapByWhen ? "</when>\n" : "");
        return sql;
    }
}

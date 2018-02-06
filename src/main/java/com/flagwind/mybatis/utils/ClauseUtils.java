package com.flagwind.mybatis.utils;

public class ClauseUtils {


    public static String getSortingSql() {
        String sql =
                "<if test=\" _sortings!= null\">" +
                        " order by " +
                        "<foreach collection=\"_sortings\" index=\"key\" item=\"sorting\"  open=\"\"  close=\"\"  separator=\",\">" +
                            "<foreach collection=\"sorting.fields\" index=\"key\" item=\"field\"  open=\"\"  close=\"\"  separator=\",\">" +
                                "${field}" +
                            "</foreach>" +
                            "<if  test=\"@com.flagwind.mybatis.utils.OGNL@isAscending(sorting)\">" +
                                " ASC " +
                            "</if>"+
                            "<if  test=\"@com.flagwind.mybatis.utils.OGNL@isDescending(sorting)\">" +
                                " DESC " +
                            "</if>"+
                        "</foreach>" +
                  "</if>";
        return sql;
    }

    public static String getQueryFieldColumnSql(){
        String sql =
                "<foreach collection=\"_fields\" index=\"key\" item=\"field\"  open=\"\"  close=\"\"  separator=\",\">" +
                        "<if test=\"field.type==null\">" +
                            "${field.column} ${field.alias}" +
                        "</if>"+
                        "<if test=\"field.type!=null\">" +
                            " ${field.type.name}(${field.column}) ${field.alias}" +
                        "</if>"+
                "</foreach>";
        return sql;
    }

    public static String getQueryFieldGroupBySql(){
        String sql =
            "<if test=\"@com.flagwind.mybatis.utils.OGNL@hasAggregateFields(_fields)\">" +
                " group by "+
                "<foreach collection=\"_fields\" index=\"key\" item=\"field\"  open=\"\"  close=\"\"  separator=\",\">" +
                        "<if test=\"field.type==null\">" +
                            "${field.column}" +
                        "</if>"+
                "</foreach>"+
            "</if>";
        return sql;
    }


    public static String getUpdatePartSetSql(String mapName) {
        String sql =
                "<foreach collection=\"" + mapName + "\" index=\"key\" item=\"itemValue\"  open=\"set\"  close=\"\"  separator=\",\">\n" +
                    "<if test=\"itemValue!=null\">" +
                        "${key}=#{itemValue}" +
                    "</if>" +
                    "<if test=\"itemValue==null\">" +
                        "${key}=#{itemValue,jdbcType=VARCHAR}" +
                     "</if>" +
                "</foreach>";
        return sql;
    }

    public static String getWhereSql(String clauseName,int depth) {
        depth = 3;
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"" + clauseName + " != null\">");
        sql.append("<where>");
        sql.append("<choose>");
        sql.append(getSingleClauseSql(clauseName));
        String childSql = "";
        for (int i = depth; i > 0; i--) {
            childSql = getCombineClauseSql(clauseName + i, clauseName + (i + 1), childSql, true);
        }
        sql.append(getCombineClauseSql(clauseName, clauseName + 1, childSql, true));
        sql.append(getChildClauseSql(clauseName, clauseName + 1, childSql));
        sql.append("</choose>");
        sql.append("</where>");
        sql.append("</if>");
        //System.out.print(sql.toString());
        return sql.toString();
    }

    private static String getSingleClauseSql(String clauseName){
        String sql="<when test=\"@com.flagwind.mybatis.utils.OGNL@isSingleClause("+clauseName+")\">" +
                    "<if test=\"@com.flagwind.mybatis.utils.OGNL@isSingleValue("+clauseName+")\">  " +
                    "${"+clauseName+".name} ${"+clauseName+".operator.alias} #{"+clauseName+".value}" +
                    "</if>" +
                     // region in or not in 条件处理
                    "<if test=\"@com.flagwind.mybatis.utils.OGNL@isListValue("+clauseName+")\">" +
                    // region 若 list.length<1000 && operate=in 则  ${name} in ( #{value1},#{value2}.....)
                    " <if test=\"@com.flagwind.mybatis.utils.OGNL@isNotOverflow(" + clauseName + ")\"> "+
                    "${"+clauseName+".name} ${"+clauseName+".operator.alias}" +
                    " <foreach collection=\""+clauseName+".values\" item=\"listItem\" open=\"(\"  close=\")\" separator=\",\">" +
                    "#{listItem}" +
                    " </foreach>" +
                    "</if>" +
                    // endregion
                    // region 若 list.length>=1000 && operate=in 则  ${name} =  #{value}
                    " <if test=\"@com.flagwind.mybatis.utils.OGNL@isOverflowWithIn(" + clauseName + ")\"> "+
                    " <foreach collection=\"" + clauseName + ".values\" item=\"listItem\" open=\"(\"  close=\")\" separator=\"or\">" +
                    "  ${" + clauseName + ".name} = #{listItem} " +
                    " </foreach>" +
                    " </if>" +
                    // endregion
                    // region 若 list.length>=1000 && operate=not in 则  ${name} <>  #{value}
                    " <if test=\"@com.flagwind.mybatis.utils.OGNL@isOverflowWithNotIn(" + clauseName + ")\"> "+
                    " <foreach collection=\"" + clauseName + ".values\" item=\"listItem\" open=\"(\"  close=\")\" separator=\"or\">" +
                    "  ${" + clauseName + ".name} != #{listItem} " +
                    " </foreach>" +
                    " </if>" +
                    // endregion
                    "</if>" +
                    // endregion
                    "<if test=\"@com.flagwind.mybatis.utils.OGNL@isBetweenValue("+clauseName+")\">  " +
                    " ${"+clauseName+".name} ${"+clauseName+".operator.alias} #{"+clauseName+".startValue} and #{"+clauseName+".endValue}" +
                    "</if>" +
                    "<if test=\"@com.flagwind.mybatis.utils.OGNL@isNullValue("+clauseName+")\">  " +
                    " ${"+clauseName+".name} ${"+clauseName+".operator.alias}" +
                    "</if>" +
                    "</when>";
        return sql;
    }

    private static String getChildClauseSql(String clauseName,String childClauseName,String childSql) {
        String sql =
                " <when test=\"@com.flagwind.mybatis.utils.OGNL@isChildClause(" + clauseName + ")\">" +
                " ${" + clauseName + ".name} <if test=\"" + clauseName + ".included==false\"> not </if>  in  (" +
                " select ${" + clauseName + ".childField} from ${" + clauseName + ".childTable} " +
                " <where>" +
                        getCombineClauseSql(clauseName, childClauseName, childSql, false) +
                " </where>" +
                ")"+
                " </when>";
        return sql;
    }

    private static String getCombineClauseSql(String clauseName,String childClauseName,String childSql,boolean isWrapByWhen) {
        String sql =
                (isWrapByWhen ? " <when test=\"@com.flagwind.mybatis.utils.OGNL@isCombineClause(" + clauseName + ")\">" : "") +
                " <foreach collection=\"" + clauseName + "\" item=\"" + childClauseName + "\"  open=\"(\"  close=\")\" index=\"idx\"  separator=\"\">" +
                " <when test=\"@com.flagwind.mybatis.utils.OGNL@isSingleClause(" + childClauseName + ")\">" +
                // region =, <> ,like, not like,>,>=,<,<= 条件处理
                " <if test=\"@com.flagwind.mybatis.utils.OGNL@isSingleValue(" + childClauseName + ")\">  " +
                "  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if>   ${" + childClauseName + ".name} ${" + childClauseName + ".operator.alias} #{" + childClauseName + ".value}" +
                " </if>" +
                // endregion
                // region in or not in 条件处理
                " <if test=\"@com.flagwind.mybatis.utils.OGNL@isListValue(" + childClauseName + ")\">  " +
                " <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if> "+
                // region 若 list.length<1000 && operate=(in or not in) 则  ${name} in ( #{value1},#{value2}.....)
                " <if test=\"@com.flagwind.mybatis.utils.OGNL@isNotOverflow(" + childClauseName + ")\"> "+
                "  ${" + childClauseName + ".name} ${" + childClauseName + ".operator.alias}" +
                " <foreach collection=\"" + childClauseName + ".values\" item=\"listItem1\" open=\"(\"  close=\")\" separator=\",\">" +
                " #{listItem1} " +
                " </foreach>" +
                "</if>" +
                // endregion
                // region 若 list.length>=1000 && operate=in 则  ${name} =  #{value}
                " <if test=\"@com.flagwind.mybatis.utils.OGNL@isOverflowWithIn(" + childClauseName + ")\"> "+
                " <foreach collection=\"" + childClauseName + ".values\" item=\"listItem1\" open=\"(\"  close=\")\" separator=\"or\">" +
                "  ${" + childClauseName + ".name} = #{listItem1} " +
                " </foreach>" +
                " </if>" +
                // endregion
                // region 若 list.length>=1000 && operate=not in 则  ${name} <>  #{value}
                " <if test=\"@com.flagwind.mybatis.utils.OGNL@isOverflowWithNotIn(" + childClauseName + ")\"> "+
                " <foreach collection=\"" + childClauseName + ".values\" item=\"listItem1\" open=\"(\"  close=\")\" separator=\"or\">" +
                "  ${" + childClauseName + ".name} != #{listItem1} " +
                " </foreach>" +
                " </if>" +
                // endregion
                "</if>" +
                // endregion
                // region bewtween 条件处理
                "<if test=\"@com.flagwind.mybatis.utils.OGNL@isBetweenValue(" + childClauseName + ")\">  " +
                "  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if>   ${" + childClauseName + ".name} ${" + childClauseName + ".operator.alias} #{" + childClauseName + ".startValue} and #{" + childClauseName + ".endValue}" +
                "</if>" +
                // endregion
                // region Null 条件处理
                "<if test=\"@com.flagwind.mybatis.utils.OGNL@isNullValue(" + childClauseName + ")\">  " +
                "  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if>   ${" + childClauseName + ".name} ${" + childClauseName + ".operator.alias}" +
                "</if>" +
                // endregion
                "</when>" +
                childSql +
                "</foreach>" +
                (isWrapByWhen ? "</when>" : "");
        return sql;
    }
}

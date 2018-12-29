package com.flagwind.mybatis.definition.helper;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

public class ObjectSqlHelper
{

    private static final  int MAX_LEVEL=5;

    private static HashMap<String,String> TEMPLATE_SQL=new HashMap<>();
    
    // region 公共方法
    public static String getSortingSql() {
        String templateId="query_sorting";
        if(TEMPLATE_SQL.containsKey(templateId)){
            return TEMPLATE_SQL.get(templateId);
        }
        String sql =
                "<if test=\" _sorts!= null\">" +
                        " order by " +
                        "<foreach collection=\"_sorts\" index=\"key\" item=\"sorting\"  open=\"\"  close=\"\"  separator=\",\">" +
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
        TEMPLATE_SQL.put(templateId,sql);
        return sql;
    }

    public static String getQueryFieldColumnSql(){
        String templateId="query_columns";
        if(TEMPLATE_SQL.containsKey(templateId)){
            return TEMPLATE_SQL.get(templateId);
        }
        String sql =
                "<foreach collection=\"_fields\" index=\"key\" item=\"field\"  open=\"\"  close=\"\"  separator=\",\">" +
                        "<if test=\"field.type==null\">" +
                            "${field.column} ${field.alias}" +
                        "</if>"+
                        "<if test=\"field.type!=null\">" +
                            " ${field.type.name}(${field.column}) ${field.alias}" +
                        "</if>"+
                "</foreach>";
        TEMPLATE_SQL.put(templateId,sql);
        return sql;
    }

    public static String getQueryFieldGroupBySql(){
        String templateId="query_group";
        if(TEMPLATE_SQL.containsKey(templateId)){
            return TEMPLATE_SQL.get(templateId);
        }
        String sql =
            "<if test=\"@com.flagwind.mybatis.utils.OGNL@hasGroupByFields(_fields)\">" +
                " group by "+
                "<foreach collection=\"_fields\" index=\"key\" item=\"field\"  open=\"\"  close=\"\"  separator=\",\">" +
                        "<if test=\"field.type==null\">" +
                            "${field.column}" +
                        "</if>"+
                "</foreach>"+
            "</if>";
            TEMPLATE_SQL.put(templateId,sql);
        return sql;
    }


    public static String getUpdatePartSetSql(String mapName) {
        String templateId="update_"+mapName;
        if(TEMPLATE_SQL.containsKey(templateId)){
            return TEMPLATE_SQL.get(templateId);
        }
        String sql =
                "<foreach collection=\"" + mapName + "\" index=\"key\" item=\"itemValue\"  open=\"set\"  close=\"\"  separator=\",\">\n" +
                    "<if test=\"itemValue!=null\">" +
                        "${key}=#{itemValue}" +
                    "</if>" +
                    "<if test=\"itemValue==null\">" +
                        "${key}=#{itemValue,jdbcType=VARCHAR}" +
                     "</if>" +
                "</foreach>";

        TEMPLATE_SQL.put(templateId,sql);
        return sql;
    }

    public static String getWhereSql(String clauseName,int depth) {
        depth = 3;
        String templateId="where_"+clauseName+"_"+depth;
        if(TEMPLATE_SQL.containsKey(templateId)){
            return TEMPLATE_SQL.get(templateId);
        }

        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"" + clauseName + " != null\">");
        sql.append("<where>");
        sql.append("<choose>");
        sql.append(getSingleClauseSql(clauseName,true));
        sql.append(getCombineClauseSql(clauseName, true,true));
        sql.append(getChildClauseSql(clauseName,true));
        sql.append("</choose>");
        sql.append("</where>");
        sql.append("</if>");
        TEMPLATE_SQL.put(templateId,sql.toString());
        return sql.toString();
    }
    
    // endregion

    // region 私有方法

    // region SingleClause
    private static String getIfSingleValueSql(String clauseName){
        String sql=
        "<if test=\"@com.flagwind.mybatis.utils.OGNL@isSingleValue("+clauseName+")\">  " +
            "${"+clauseName+".name} ${"+clauseName+".operator.alias} #{"+clauseName+".value}" +
        "</if>" ;
        return sql;
    }

    private static String getIfListValueSql(String clauseName){
        String sql=
        "<if test=\"@com.flagwind.mybatis.utils.OGNL@isListValue("+clauseName+")\">" +
        // region 若 list.length<1000 && operate=in 则  ${name} in ( #{value1},#{value2}.....)
        "<if test=\"@com.flagwind.mybatis.utils.OGNL@isNotOverflow(" + clauseName + ")\"> "+
            "${"+clauseName+".name} ${"+clauseName+".operator.alias}" +
            " <foreach collection=\""+clauseName+".values\" item=\"listItem\" open=\"(\"  close=\")\" separator=\",\">" +
            "#{listItem}" +
            " </foreach>" +
        "</if>" +
        // endregion
        // region 若 list.length>=1000 && operate=in 则  ${name} =  #{value} or ${name} =  #{value}
        " <if test=\"@com.flagwind.mybatis.utils.OGNL@isOverflowWithIn(" + clauseName + ")\"> "+
            " <foreach collection=\"" + clauseName + ".values\" item=\"listItem\" open=\"(\"  close=\")\" separator=\"or\">" +
            "  ${" + clauseName + ".name} = #{listItem} " +
            " </foreach>" +
        " </if>" +
        // endregion
        // region 若 list.length>=1000 && operate=not in 则  ${name} <>  #{value} or ${name} <>  #{value}
        " <if test=\"@com.flagwind.mybatis.utils.OGNL@isOverflowWithNotIn(" + clauseName + ")\"> "+
            " <foreach collection=\"" + clauseName + ".values\" item=\"listItem\" open=\"(\"  close=\")\" separator=\"or\">" +
            "  ${" + clauseName + ".name} != #{listItem} " +
            " </foreach>" +
        " </if>" +
        // endregion
        "</if>" ;
        return sql;
    }

    private static String getIfBetweenValueSql(String clauseName){
        String sql=
        "<if test=\"@com.flagwind.mybatis.utils.OGNL@isBetweenValue("+clauseName+")\">  " +
        " ${"+clauseName+".name} ${"+clauseName+".operator.alias} #{"+clauseName+".startValue} and #{"+clauseName+".endValue}" +
        "</if>" ;
        return sql;
    }

    private static String getIfNullValueSql(String clauseName){
        String sql=
        "<if test=\"@com.flagwind.mybatis.utils.OGNL@isNullValue("+clauseName+")\">  " +
            " ${"+clauseName+".name} ${"+clauseName+".operator.alias} NULL " +
        "</if>" ;
        return sql;
    }

    private static String getSingleClauseSql(String clauseName,boolean isWrapByWhen){
        String sql=(isWrapByWhen ?"<when test=\"@com.flagwind.mybatis.utils.OGNL@isSingleClause("+clauseName+")\">":"") 
        +getIfSingleValueSql(clauseName)
        +getIfListValueSql(clauseName)
        +getIfBetweenValueSql(clauseName)
        +getIfNullValueSql(clauseName)
        +(isWrapByWhen ?"</when>":"");
        return sql;
    }

    // endregion

    // region ChildClause
    private static String getChildClauseSql(String clauseName,boolean isWrapByWhen) {
        String sql =
                (isWrapByWhen?" <when test=\"@com.flagwind.mybatis.utils.OGNL@isChildClause(" + clauseName + ")\">":"") +
                " ${" + clauseName + ".name} <if test=\"" + clauseName + ".included==false\"> not </if>  in  (" +
                " select ${" + clauseName + ".childField} from ${" + clauseName + ".childTable} " +
                " <where>" +
                        getCombineClauseSql(clauseName, false,false,2) +
                " </where>" +
                ")"+
                (isWrapByWhen?" </when>":"");
        return sql;
    }
    // endregion

    // region CombineClauseSql


    /**
     * 组合条件模版（使用默认递归层级）
     */
    private static String getCombineClauseSql(String clauseName,boolean isWrapByWhen,boolean isHasChildQuery) {
        int maxLevel=MAX_LEVEL;
        return getCombineClauseSql(clauseName, isWrapByWhen, isHasChildQuery, maxLevel);
    }

    /**
     * 组合条件模版（使用指定递归层级）
     */
    private static String getCombineClauseSql(String clauseName, boolean isWrapByWhen, boolean isHasChildQuery,
            int level) {
        if (isNext(clauseName)==false||level <= 0) {
            return "";
        }
        level--;
        boolean isHasCombineClause = level > 0;
        String childClauseName = getChildClauseName(clauseName);
        String sql = (isWrapByWhen
                ? " <when test=\"@com.flagwind.mybatis.utils.OGNL@isCombineClause(" + clauseName + ")\">"
                : "") + " <foreach collection=\"" + clauseName + "\" item=\"" + childClauseName
                + "\"  open=\"(\"  close=\")\" index=\"idx\"  separator=\"\">" +
                // region SingleClause
                getSingleClauseSqlWithCombine(clauseName, childClauseName) +
                // endregion

                // region ChildQuery查询

                (isHasChildQuery ? getChildClauseSqlWithCombine(clauseName, childClauseName) : "") +
                // endregion

                // region CombineClause
                (isHasCombineClause ? getCombineClauseSqlWithCombine(clauseName, childClauseName, isHasChildQuery, level): "")
                +
                // endregion
                "</foreach>" + (isWrapByWhen ? "</when>" : "");
        return sql;
    }

    /**
     * 带 and 或 or 前缀的 简单查询短句条件
     */
    private static String getSingleClauseSqlWithCombine(String clauseName,String childClauseName){

        return " <when test=\"@com.flagwind.mybatis.utils.OGNL@isSingleClause(" + childClauseName + ")\">" +
               
                "  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if> " +
            
                   getSingleClauseSql(childClauseName,false)+
                

              "</when>";
    }

    /**
     * 带 and 或 or 前缀的 子查询短句条件
     */
    private static String getChildClauseSqlWithCombine(String clauseName,String childClauseName) {
        return " <if test=\"@com.flagwind.mybatis.utils.OGNL@isChildClause(" + childClauseName + ")\">"
                +"  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if>   "
                +   getChildClauseSql(childClauseName,false)
                +"</if>";
    }

    /**
     * 带 and 或 or 前缀的 组合查询短句条件
     */
    private static String getCombineClauseSqlWithCombine(String clauseName, String childClauseName,
            boolean isHasChildQuery, int level) {
        if (isNext(childClauseName) && level > 0) {
            return "<if test=\"@com.flagwind.mybatis.utils.OGNL@isCombineClause(" + childClauseName + ")\">"
                    + "  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if>   "
                    + getCombineClauseSql(childClauseName, false, isHasChildQuery, level) + "</if>";
        }
        return "";
    }

    // endregion

    /**
     * 判断支持的最大嵌套层级
     */
    private static boolean isNext(String clauseName){
        String suffix = clauseName.substring(clauseName.length()-1);
        if(StringUtils.isNumeric(suffix)){
            return Integer.parseInt(suffix)<MAX_LEVEL;
        }else{
            return true;
        }
    }

    /**
     * 获取下级短语名
     */
    private static String getChildClauseName(String clauseName){
        String suffix = clauseName.substring(clauseName.length()-1);
        if(StringUtils.isNumeric(suffix)){
            return clauseName.substring(0,clauseName.length()-1)+(Integer.parseInt(suffix)+1);
        }else{
            return clauseName+"1";
        }
    }



    // endregion
}

package com.flagwind.mybatis.definition.builder;

import com.flagwind.mybatis.definition.Config;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * @author chenabao
 */
public class ObjectSqlBuilder {
    private Config config;

    public ObjectSqlBuilder(Config config) {
        this.config = config;
    }

    private static final int MAX_LEVEL = 5;

    private final HashMap<String, String> TEMPLATE_SQL = new HashMap<>();


    private String getOgnlMethodName(String name) {
        switch (config.getNameQuote()) {
            case 1:
                return String.format("@com.flagwind.mybatis.utils.OGNL@name1(%s)", name);
            case 2:
                return String.format("@com.flagwind.mybatis.utils.OGNL@name2(%s)", name);
            default:
                return String.format("@com.flagwind.mybatis.utils.OGNL@name(%s)", name);
        }
    }

    // region 公共方法
    public String getSortingSql() {
        String templateId = "query_sorting";
        if (TEMPLATE_SQL.containsKey(templateId)) {
            return TEMPLATE_SQL.get(templateId);
        }
        String sql =
                "<if test=\" _sorts!= null\">" +
                        "<foreach collection=\"_sorts\" index=\"key\" item=\"sorting\"  open=\" order by \"  close=\"\"  separator=\",\">" +
                        "<foreach collection=\"sorting.fields\" index=\"key\" item=\"field\"  open=\"\"  close=\"\"  separator=\",\">" +
                        "<bind name=\"__name\" value=\"" + getOgnlMethodName("field") + "\" />" +
                        "${__name}" +
                        "<if  test=\"@com.flagwind.mybatis.utils.OGNL@isAscending(sorting)\">" +
                        " ASC " +
                        "</if>" +
                        "<if  test=\"@com.flagwind.mybatis.utils.OGNL@isDescending(sorting)\">" +
                        " DESC " +
                        "</if>" +
                        "</foreach>" +
                        "</foreach>" +
                        "</if>";
        TEMPLATE_SQL.put(templateId, sql);
        return sql;
    }

    public String getQueryFieldColumnSql() {
        String templateId = "query_columns";
        if (TEMPLATE_SQL.containsKey(templateId)) {
            return TEMPLATE_SQL.get(templateId);
        }
        String sql =
                "<foreach collection=\"_fields\" index=\"key\" item=\"field\"  open=\"\"  close=\"\"  separator=\",\">" +
                        "<bind name=\"__name\" value=\"" + getOgnlMethodName("field.column") + "\" />" +
                        "<if test=\"field.type==null\">" +
                        "${__name} ${field.alias}" +
                        "</if>" +
                        "<if test=\"field.type!=null\">" +
                        " ${field.type.name}(${__name}) ${field.alias}" +
                        "</if>" +
                        "</foreach>";
        TEMPLATE_SQL.put(templateId, sql);
        return sql;
    }

    public String getQueryFieldGroupBySql() {
        String templateId = "query_group";
        if (TEMPLATE_SQL.containsKey(templateId)) {
            return TEMPLATE_SQL.get(templateId);
        }
        String sql =
                "<if test=\"@com.flagwind.mybatis.utils.OGNL@hasGroupByFields(_fields)\">" +
                        " group by " +
                        "<foreach collection=\"_fields\" index=\"key\" item=\"field\"  open=\"\"  close=\"\"  separator=\",\">" +
                        "<bind name=\"__name\" value=\"" + getOgnlMethodName("field.column") + "\" />" +
                        "<if test=\"field.type==null\">" +
                        "${__name}" +
                        "</if>" +
                        "</foreach>" +
                        "</if>";
        TEMPLATE_SQL.put(templateId, sql);
        return sql;
    }


    public String getUpdatePartSetSql(String mapName) {
        String templateId = "update_" + mapName;
        if (TEMPLATE_SQL.containsKey(templateId)) {
            return TEMPLATE_SQL.get(templateId);
        }
        String sql =
                "<foreach collection=\"" + mapName + "\" index=\"key\" item=\"itemValue\"  open=\"set\"  close=\"\"  separator=\",\">\n" +
                        "<if test=\"itemValue!=null\">" +
                        "<choose> " +
                        "<when test=\"@com.flagwind.mybatis.utils.OGNL@isCodeType(itemValue)\"> ${key}=#{itemValue.value} </when>  " +
                        "<otherwise> ${key}=#{itemValue} </otherwise> " +
                        "</choose>" +
                        "</if>" +
                        "<if test=\"itemValue==null\">" +
                        "${key}=#{itemValue,jdbcType=VARCHAR}" +
                        "</if>" +
                        "</foreach>";

        TEMPLATE_SQL.put(templateId, sql);
        return sql;
    }

    public String getWhereSql(String clauseName) {
        return getWhereSql(clauseName, 5);
    }


    private String getWhereSql(String clauseName, int depth) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"").append(clauseName).append(" != null\">");
        sql.append("<where>");
        sql.append(getClauseSql(clauseName, depth));
        sql.append("</where>");
        sql.append("</if>");

        return sql.toString();
    }

    public String getClauseSql(String clauseName) {
        return getClauseSql(clauseName, 5);
    }

    public String getClauseSql(String clauseName, int depth) {
        depth = 3;
        String templateId = clauseName + "_" + depth;
        if (TEMPLATE_SQL.containsKey(templateId)) {
            return TEMPLATE_SQL.get(templateId);
        }
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"").append(clauseName).append(" != null\">");
        sql.append("<choose>");
        sql.append(getSingleClauseSql(clauseName, true));
        sql.append(getCombineClauseSql(clauseName, true, true));
        sql.append(getChildClauseSql(clauseName, true));
        sql.append("</choose>");
        sql.append("</when>");
        sql.append("<otherwise>1=1</otherwise>");
        sql.append("</choose>");
        TEMPLATE_SQL.put(templateId, sql.toString());
        return sql.toString();
    }

    // endregion

    // region 私有方法

    // region SingleClause
    private String getIfSingleValueSql(String clauseName) {
        String sql =
                "<if test=\"@com.flagwind.mybatis.utils.OGNL@isSingleValue(" + clauseName + ")\">  " +
                        "<bind name=\"__name\" value=\"" + getOgnlMethodName(clauseName + ".name") + "\" />" +
                        "${__name} ${" + clauseName + ".operator.alias} " + getValueSql(clauseName, "value") +
                        "</if>";
        return sql;
    }

    private static String getValueSql(String clauseName, String valueName) {
        String sql =
                "<choose> " +
                        "<when test=\"@com.flagwind.mybatis.utils.OGNL@isColumn(" + clauseName + ")\"> ${" + clauseName + "." + valueName + "} </when>  " +
                        "<when test=\"@com.flagwind.mybatis.utils.OGNL@isCodeType(" + clauseName + ")\"> #{" + clauseName + "." + valueName + ".value} </when>  " +
                        "<otherwise> #{" + clauseName + "." + valueName + "} </otherwise> " +
                        "</choose>";
        return sql;
    }

    private static String getFormatValueSql(String valueName) {
        String sql =
                "<choose> " +
                        "<when test=\"@com.flagwind.mybatis.utils.OGNL@isCodeType(" + valueName + ")\"> #{" + valueName + ".value} </when>  " +
                        "<otherwise> #{" + valueName + "} </otherwise> " +
                        "</choose>";
        return sql;
    }


    private String getIfListValueSql(String clauseName) {
        String sql =
                "<if test=\"@com.flagwind.mybatis.utils.OGNL@isListValue(" + clauseName + ")\">" +
                        // region 若 list.length<1000 && operate=in 则  ${name} in ( #{value1},#{value2}.....)
                        "<if test=\"@com.flagwind.mybatis.utils.OGNL@isNotOverflow(" + clauseName + ")\"> " +
                        "<bind name=\"__name\" value=\"" + getOgnlMethodName(clauseName + ".name") + "\" />" +
                        "${__name} ${" + clauseName + ".operator.alias}" +
                        " <foreach collection=\"" + clauseName + ".values\" item=\"listItem\" open=\"(\"  close=\")\" separator=\",\">" +
                        "#{listItem}" +
                        " </foreach>" +
                        "</if>" +
                        // endregion
                        // region 若 list.length>=1000 && operate=in 则  ${name} =  #{value} or ${name} =  #{value}
                        " <if test=\"@com.flagwind.mybatis.utils.OGNL@isOverflowWithIn(" + clauseName + ")\"> " +
                        " <foreach collection=\"" + clauseName + ".values\" item=\"listItem\" open=\"(\"  close=\")\" separator=\"or\">" +
                        "  ${" + clauseName + ".name} = #{listItem} " +
                        " </foreach>" +
                        " </if>" +
                        // endregion
                        // region 若 list.length>=1000 && operate=not in 则  ${name} <>  #{value} or ${name} <>  #{value}
                        " <if test=\"@com.flagwind.mybatis.utils.OGNL@isOverflowWithNotIn(" + clauseName + ")\"> " +
                        " <foreach collection=\"" + clauseName + ".values\" item=\"listItem\" open=\"(\"  close=\")\" separator=\"and\">" +
                        "  ${" + clauseName + ".name} != #{listItem} " +
                        " </foreach>" +
                        " </if>" +
                        // endregion
                        "</if>";
        return sql;
    }

    private String getIfBetweenValueSql(String clauseName) {
        String sql =
                "<if test=\"@com.flagwind.mybatis.utils.OGNL@isBetweenValue(" + clauseName + ")\">  " +
                        "<bind name=\"__name\" value=\"" + getOgnlMethodName(clauseName + ".name") + "\" />" +
                        " ${__name} ${" + clauseName + ".operator.alias} #{" + clauseName + ".startValue} and #{" + clauseName + ".endValue}" +
                        "</if>";
        return sql;
    }

    private String getIfNullValueSql(String clauseName) {
        String sql =
                "<if test=\"@com.flagwind.mybatis.utils.OGNL@isNullValue(" + clauseName + ")\">  " +
                        "<bind name=\"__name\" value=\"" + getOgnlMethodName(clauseName + ".name") + "\" />" +
                        " ${__name} ${" + clauseName + ".operator.alias} NULL " +
                        "</if>";
        return sql;
    }

    private String getSingleClauseSql(String clauseName, boolean isWrapByWhen) {
        String sql = (isWrapByWhen ? "<when test=\"@com.flagwind.mybatis.utils.OGNL@isSingleClause(" + clauseName + ")\">" : "")
                + getIfSingleValueSql(clauseName)
                + getIfListValueSql(clauseName)
                + getIfBetweenValueSql(clauseName)
                + getIfNullValueSql(clauseName)
                + (isWrapByWhen ? "</when>" : "");
        return sql;
    }

    // endregion

    // region ChildClause
    private String getChildClauseSql(String clauseName, boolean isWrapByWhen) {
        String sql =
                (isWrapByWhen ? " <when test=\"@com.flagwind.mybatis.utils.OGNL@isChildClause(" + clauseName + ")\">" : "") +
                        "<bind name=\"__name\" value=\"" + getOgnlMethodName(clauseName + ".name") + "\" />" +
                        " ${__name} <if test=\"" + clauseName + ".included==false\"> not </if>  in  (" +
                        " select ${" + clauseName + ".childField} from ${" + clauseName + ".childTable} " +
                        " <where>" +
                        getCombineClauseSql(clauseName, false, false, 3) +
                        " </where>" +
                        ")" +
                        (isWrapByWhen ? " </when>" : "");
        return sql;
    }
    // endregion

    // region CombineClauseSql


    /**
     * 组合条件模版（使用默认递归层级）
     */
    private String getCombineClauseSql(String clauseName, boolean isWrapByWhen, boolean isHasChildQuery) {
        return getCombineClauseSql(clauseName, isWrapByWhen, isHasChildQuery, MAX_LEVEL);
    }

    /**
     * 组合条件模版（使用指定递归层级）
     */
    private String getCombineClauseSql(String clauseName, boolean isWrapByWhen, boolean isHasChildQuery,
                                       int level) {
        if (!isNext(clauseName) || level <= 0) {
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
                (isHasCombineClause ? getCombineClauseSqlWithCombine(clauseName, childClauseName, isHasChildQuery, level) : "")
                +
                // endregion
                "</foreach>" + (isWrapByWhen ? "</when>" : "");
        return sql;
    }

    /**
     * 带 and 或 or 前缀的 简单查询短句条件
     */
    private String getSingleClauseSqlWithCombine(String clauseName, String childClauseName) {

        return " <when test=\"@com.flagwind.mybatis.utils.OGNL@isSingleClause(" + childClauseName + ")\">" +
                "  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if> " +
                getSingleClauseSql(childClauseName, false) +
                "</when>";
    }

    /**
     * 带 and 或 or 前缀的 子查询短句条件
     */
    private String getChildClauseSqlWithCombine(String clauseName, String childClauseName) {
        return " <if test=\"@com.flagwind.mybatis.utils.OGNL@isChildClause(" + childClauseName + ")\">"
                + "  <if test=\"idx!=0\">${" + clauseName + ".combine.name()}</if>   "
                + getChildClauseSql(childClauseName, false)
                + "</if>";
    }

    /**
     * 带 and 或 or 前缀的 组合查询短句条件
     */
    private String getCombineClauseSqlWithCombine(String clauseName, String childClauseName,
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
    private boolean isNext(String clauseName) {
        String suffix = clauseName.substring(clauseName.length() - 1);
        if (StringUtils.isNumeric(suffix)) {
            return Integer.parseInt(suffix) < MAX_LEVEL;
        } else {
            return true;
        }
    }

    /**
     * 获取下级短语名
     */
    private String getChildClauseName(String clauseName) {
        String suffix = clauseName.substring(clauseName.length() - 1);
        if (StringUtils.isNumeric(suffix)) {
            return clauseName.substring(0, clauseName.length() - 1) + (Integer.parseInt(suffix) + 1);
        } else {
            return clauseName + "1";
        }
    }


    // endregion
}

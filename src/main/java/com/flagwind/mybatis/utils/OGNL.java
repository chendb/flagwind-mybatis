package com.flagwind.mybatis.utils;

import com.flagwind.lang.CodeType;
import com.flagwind.persistent.Functions;
import com.flagwind.persistent.QueryField;
import com.flagwind.persistent.model.*;
import com.flagwind.persistent.model.Sorting.SortingMode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * OGNL静态方法
 *
 * @author chendb
 */
public abstract class OGNL {

    public static String SINGLE_QUOTE="\"";
    public static String DOUBLE_QUOTE="`";

    public static String DOT=".";

    public static String name(Object name) {
        if (name == null) {
            return null;
        }
        String str = name.toString();
        return str.indexOf("@") >= 0 ? Functions.invoke(str) : str;
    }

    public static String name1(Object name) {
        if (name == null) {
            return null;
        }
        String str = name.toString();
        if (str.startsWith(SINGLE_QUOTE) || str.startsWith(DOUBLE_QUOTE) ) {
            return str;
        }
        if (str.indexOf(DOT) > 0) {
            String[] arr = str.split(DOT);
            if (arr.length == 2 && !StringUtils.containsAny(arr[1], SINGLE_QUOTE, SINGLE_QUOTE)) {
                return String.format("%s.`%s`", arr[0], arr[1]);
            }
        }
        return str.indexOf("@") >= 0 ? Functions.invoke(str) : String.format("`%s`", str);
    }

    public static String name2(Object name) {
        if (name == null) {
            return null;
        }
        String str = name.toString();
        if (str.startsWith(SINGLE_QUOTE) || str.startsWith(DOUBLE_QUOTE)) {
            return str;
        }
        if (str.indexOf(DOT) > 0) {
            String[] arr = str.split(DOT);
            if (arr.length == 2 && !StringUtils.containsAny(arr[1], SINGLE_QUOTE, SINGLE_QUOTE)) {
                return String.format("%s.\"%s\"", arr[0], arr[1]);
            }
        }
        return str.indexOf("@") >= 0 ? Functions.invoke(str) : String.format("\"%s\"", str);
    }
//
//    public static String clauseName(Object _clause) {
//        if (_clause instanceof ChildClause) {
//            return Functions.invoke(((ChildClause) _clause).getName());
//        } else {
//            return Functions.invoke(((SingleClause) _clause).getName());
//        }
//    }
//
//    public static String fieldColumn(Object _field) {
//        QueryField field = (QueryField) _field;
//        return Functions.invoke(field.getColumn());
//    }

    /**
     * 判断是否有聚合字段
     */
    public static boolean hasAggregateFields(Object fields) {
        if (fields != null && fields instanceof List) {
            List<QueryField> queryFields = TypeUtils.castTo(fields);
            return queryFields.stream().anyMatch(g -> g.getType() != null);
        }
        return false;
    }

    public static boolean hasGroupByFields(Object fields) {
        if (fields != null && fields instanceof List) {
            List<QueryField> queryFields = TypeUtils.castTo(fields);
            boolean flag = queryFields.stream().anyMatch(g -> g.getType() != null);
            if (flag) {
                flag = queryFields.stream().anyMatch(g -> g.getType() == null);
            }
            return flag;
        }
        return false;
    }

    /**
     * 是否为升序
     *
     * @param parameter
     */
    public static boolean isAscending(Object parameter) {
        if (parameter != null && parameter instanceof Sorting) {
            Sorting sorting = (Sorting) parameter;
            return sorting.getMode() == SortingMode.Ascending;
        }
        return true;
    }

    /**
     * 是否为将序
     */
    public static boolean isDescending(Object parameter) {
        if (parameter != null && parameter instanceof Sorting) {
            Sorting sorting = (Sorting) parameter;
            return sorting.getMode() == SortingMode.Descending;
        }
        return false;
    }

    /**
     * 是否为单条件
     *
     * @param parameter 条件短语
     */
    public static boolean isSingleClause(Object parameter) {
        if (parameter != null && parameter instanceof SingleClause) {
            return true;
        }
        return false;
    }

    /**
     * 是否为多条件
     *
     * @param parameter 条件短语
     */
    public static boolean isCombineClause(Object parameter) {
        if (parameter != null && parameter instanceof ChildClause) {
            return false;
        }

        return parameter != null && parameter instanceof CombineClause;
    }

    /**
     * 是否为子查询条件
     *
     * @param parameter 条件短语
     */
    public static boolean isChildClause(Object parameter) {
        return parameter != null && parameter instanceof ChildClause;
    }

    public static boolean isNullValue(Object parameter) {
        if (parameter != null && parameter instanceof SingleClause) {
            SingleClause clause = (SingleClause) parameter;
            switch (clause.getOperator()) {
                case Null:
                case NotNull:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public static boolean isSingleValue(Object parameter) {
        if (parameter != null && parameter instanceof SingleClause) {
            SingleClause clause = (SingleClause) parameter;
            switch (clause.getOperator()) {
                case In:
                case NotIn:
                case Null:
                case NotNull:
                case Between:
                case Child:
                    return false;
                default:
                    return true;
            }
        }
        return false;
    }

    public static boolean isListValue(Object parameter) {
        if (parameter != null && parameter instanceof SingleClause) {
            SingleClause clause = (SingleClause) parameter;
            return (clause.getOperator() == ClauseOperator.In || clause.getOperator() == ClauseOperator.NotIn);
        }
        return false;
    }

    /**
     * in or not in 操作中的values长度是否超过了数据库支持的最大升序
     */
    public static boolean isOverflow(Object parameter) {
        boolean overflow = false;
        SingleClause clause = (SingleClause) parameter;
        if (clause.getValues() != null && clause.getValues().length > 1000) {
            overflow = true;
        }
        // System.out.println("isOverflow:" + overflow);
        return overflow;

    }

    /**
     * in or not in 操作中的values长度是否没超过了数据库支持的最大升序
     */
    public static boolean isNotOverflow(Object parameter) {
        return !isOverflow(parameter);
    }

    /**
     * 超过最大长度，且为In操作
     */
    public static boolean isOverflowWithIn(Object parameter) {
        SingleClause clause = (SingleClause) parameter;
        if (!isOverflow(parameter)) {
            return false;
        }
        return (clause.getOperator() == ClauseOperator.In);
    }

    /**
     * 超过最大长度，且为NotIn操作
     */
    public static boolean isOverflowWithNotIn(Object parameter) {
        SingleClause clause = (SingleClause) parameter;
        if (!isOverflow(parameter)) {
            return false;
        }
        return (clause.getOperator() == ClauseOperator.NotIn);
    }

    public static boolean isBetweenValue(Object parameter) {
        if (parameter != null && parameter instanceof SingleClause) {
            SingleClause clause = (SingleClause) parameter;
            return (clause.getOperator() == ClauseOperator.Between);
        }
        return false;
    }


    /**
     * 判断参数是否为列 （用于组织 createTime != modifyTime 条件）
     *
     * @param parameter
     * @return
     */
    public static boolean isColumn(Object parameter) {
        SingleClause clause = (SingleClause) parameter;
        if (clause != null && clause.getParameterType() == ParameterType.Column) {
            return true;
        }
        return false;
    }

    public static boolean isCodeType(Object parameter) {
        if (parameter instanceof SingleClause) {
            SingleClause clause = (SingleClause) parameter;
            if (clause != null && clause.getValue() instanceof CodeType) {
                return true;
            }
        } else if (parameter instanceof CodeType) {
            return true;
        }
        return false;
    }

    /**
     * 判断参数是否为值（用于组织 code > 123 条件）
     *
     * @param parameter
     * @return
     */
    public static boolean isValue(Object parameter) {
        return !isColumn(parameter);
    }

}

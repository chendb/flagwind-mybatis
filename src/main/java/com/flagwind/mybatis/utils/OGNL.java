package com.flagwind.mybatis.utils;

import java.util.List;

import com.flagwind.persistent.QueryField;
import com.flagwind.persistent.model.ChildClause;
import com.flagwind.persistent.model.ClauseOperator;
import com.flagwind.persistent.model.CombineClause;
import com.flagwind.persistent.model.SingleClause;
import com.flagwind.persistent.model.Sorting;
import com.flagwind.persistent.model.Sorting.SortingMode;

/**
 * OGNL静态方法
 *
 * @author chendb
 */
public abstract class OGNL {


    /**
     * 判断是否有聚合字段
     */
    public static boolean hasAggregateFields(Object fields) {
        if (fields != null && fields instanceof List) {
            List<QueryField> queryFields = TypeUtils.castTo( fields);
            return queryFields.stream().anyMatch(g -> g.getType() != null);
        }
        return false;
    }

    /**
     * 是否为升序
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
        System.out.println("isOverflow:" + overflow);
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

}

package com.flagwind.mybatis.utils;

import com.flagwind.mybatis.common.IDynamicTableName;
import com.flagwind.persistent.model.*;

/**
 * OGNL静态方法
 *
 * @author chendb
 */
public abstract class OGNL {

    /**
     * 是否为单条件
     * @param parameter
     * @return
     */
    public static boolean isSingleClause(Object parameter) {
        if (parameter != null && parameter instanceof SingleClause) {
            return  true;
        }
        return false;
    }

    /**
     * 是否为多条件
     * @param parameter
     * @return
     */
    public static boolean isCombineClause(Object parameter) {
        if (parameter != null && parameter instanceof CombineClause) {
            if (parameter instanceof ChildClause) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 是否为子查询条件
     * @param parameter
     * @return
     */
    public static boolean isChildClause(Object parameter) {
        if (parameter != null && parameter instanceof ChildClause) {
            return true;
        }
        return false;
    }
    public static boolean isNullValue(Object parameter){
        if (parameter != null && parameter instanceof SingleClause) {
            SingleClause clause=(SingleClause)parameter;
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



    public static boolean isSingleValue(Object parameter){
        if (parameter != null && parameter instanceof SingleClause) {
            SingleClause clause=(SingleClause)parameter;
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

    public static boolean isListValue(Object parameter){
        if (parameter != null && parameter instanceof SingleClause) {
            SingleClause clause = (SingleClause) parameter;
            return (clause.getOperator() == ClauseOperator.In || clause.getOperator() == ClauseOperator.NotIn);
        }
        return false;
    }

    public static boolean isBetweenValue(Object parameter){
        if (parameter != null && parameter instanceof SingleClause) {
            SingleClause clause = (SingleClause) parameter;
            return (clause.getOperator() == ClauseOperator.Between);
        }
        return false;
    }



    /**
     * 判断参数是否支持动态表名
     *
     * @param parameter
     * @return true支持，false不支持
     */
    public static boolean isDynamicParameter(Object parameter) {
        if (parameter != null && parameter instanceof IDynamicTableName) {
            return true;
        }
        return false;
    }

    /**
     * 判断参数是否b支持动态表名
     *
     * @param parameter
     * @return true不支持，false支持
     */
    public static boolean isNotDynamicParameter(Object parameter) {
        return !isDynamicParameter(parameter);
    }

}

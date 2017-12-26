package com.flagwind.persistent.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flagwind.persistent.model.ClauseOperator;


/**
 * 
* @ClassName: ConditionOperator 
* @Description: 查询谓词
* @author chendb hbchendb1985@hotmail 
* @date 2015年9月25日 下午5:34:49 
*
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target({METHOD}) 
public @interface ConditionOperator {
	
	/**
	 * 
	* @Title: name 
	* @Description: 指定操作项名称（不指定则使用字段名称） 
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public String name() default "";
	
	
	/**
	 * 
	* @Title: operator 
	* @Description: 指定操作项谓词 
	* @param @return    设定文件 
	* @return ClauseOperator    返回类型 
	* @throws
	 */
	public ClauseOperator operator() default ClauseOperator.Equal;
}

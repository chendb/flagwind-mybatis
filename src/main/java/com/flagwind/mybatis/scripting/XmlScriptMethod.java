package com.flagwind.mybatis.scripting;

import com.flagwind.mybatis.FlagwindConfiguration;

import java.util.List;

/**
 * @author chendb
 * @description: xml 中脚本方法
 * @date 2021-03-02 14:54:50
 */
public interface XmlScriptMethod {

    String pattern();

    boolean matches(String script);

    String execute(FlagwindConfiguration configuration, String scripting, List<Class> entityClasses);
}

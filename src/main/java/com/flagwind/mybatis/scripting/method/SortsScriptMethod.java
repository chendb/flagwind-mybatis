package com.flagwind.mybatis.scripting.method;

import com.flagwind.mybatis.FlagwindConfiguration;
import com.flagwind.mybatis.definition.helper.ObjectSqlHelper;
import com.flagwind.mybatis.scripting.XmlScriptMethod;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chendb
 * @description: 返回实体类型所映射的表名，用法：${@sorts()}
 * @date 2021-03-02 15:14:15
 */
public class SortsScriptMethod implements XmlScriptMethod {

    final Pattern pattern = Pattern.compile("\\$\\{@sort\\(\\)\\}");

    @Override
    public String pattern() {
        return pattern.pattern();
    }

    @Override
    public boolean matches(String script) {

        return pattern.matcher(script).find();
    }


    @Override
    public String execute(FlagwindConfiguration configuration, String script, List<Class> entityClasses) {
        Matcher matcher = pattern.matcher(script);
        if (matcher.find()) {
            script = matcher.replaceAll(Matcher.quoteReplacement(ObjectSqlHelper.getSortingSql()));
        }
        return script;
    }
}
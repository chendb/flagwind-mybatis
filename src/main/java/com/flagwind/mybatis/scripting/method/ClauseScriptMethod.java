package com.flagwind.mybatis.scripting.method;

import com.flagwind.mybatis.FlagwindConfiguration;
import com.flagwind.mybatis.definition.helper.ObjectSqlHelper;
import com.flagwind.mybatis.scripting.XmlScriptMethod;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chendb
 * @description: ${@clause()}，返回条件短语对应用xml sql
 * @date 2021-03-02 15:18:47
 */
public class ClauseScriptMethod implements XmlScriptMethod {

    final Pattern pattern = Pattern.compile("\\$\\{@clause\\(\\)\\}");

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
            String clauseSql = ObjectSqlHelper.getClauseSql("_clause");

            script = matcher.replaceAll(Matcher.quoteReplacement(clauseSql));
        }
        return script;
    }
}

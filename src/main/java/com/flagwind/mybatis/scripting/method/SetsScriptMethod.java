package com.flagwind.mybatis.scripting.method;

import com.flagwind.mybatis.FlagwindConfiguration;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.scripting.XmlScriptMethod;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chendb
 * @description: 返回实体类型所映射的表名，用法：${@sets(#e0)}
 * @date 2021-03-02 15:14:15
 */
public class SetsScriptMethod implements XmlScriptMethod {

    final Pattern pattern = Pattern.compile("\\$\\{@sets\\(\\#e(?<classIndex>[\\d]+)\\)\\}");

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
        while (matcher.find()) {
            int classIndex = Integer.parseInt(matcher.group("classIndex"));
            if (classIndex >= entityClasses.size()) {
                throw new MapperException(entityClasses.stream().findFirst().map(s -> s.toString() + "中存在方法调用实体类型数据出现索引越界")
                        .orElse("存在方法调用实体类型数据出现索引越界"));
            }
            String sql = configuration.getSqlBuilder().getTemplateSqlBuilder()
                    .updateSetColumns(entityClasses.get(classIndex), null, false, false);
            script = matcher.replaceFirst(Matcher.quoteReplacement(sql));
            matcher = pattern.matcher(script);
        }
        return script;
    }
}

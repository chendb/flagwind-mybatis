package com.flagwind.mybatis.scripting.method;

import com.flagwind.mybatis.FlagwindConfiguration;
import com.flagwind.mybatis.definition.builder.BaseSqlBuilder;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.metadata.EntityTableFactory;
import com.flagwind.mybatis.scripting.XmlScriptMethod;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chendb
 * @description: 返回指定实体类型的主键名，示例${@Id(#e0)}
 * @date 2021-03-02 15:09:12
 */
public class IdScriptMethod implements XmlScriptMethod {

    protected final Pattern pattern = Pattern.compile("\\$\\{@id\\(\\#e(?<classIndex>[\\d]+)\\)\\}");

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
            int classIndex = Integer.parseInt(matcher.group("classIndex"));
            if (classIndex >= entityClasses.size()) {
                throw new MapperException(entityClasses.stream().findFirst().map(s -> s.toString() + "中存在方法调用实体类型数据出现索引越界")
                        .orElse("存在方法调用实体类型数据出现索引越界"));
            }
            //获取全部列
            Set<EntityColumn> columnList = EntityTableFactory.getEntityTable(entityClasses.get(classIndex)).getEntityClassPKColumns();
            if (columnList.size() == 1) {
                EntityColumn column = columnList.iterator().next();
                return BaseSqlBuilder.getColumnName(configuration.getProperties(), column);
            } else {
                throw new MapperException("实体类[" + entityClasses.get(classIndex) + "]中必须只有一个带有 @Id 注解的字段");
            }
        }
        return script;
    }
}

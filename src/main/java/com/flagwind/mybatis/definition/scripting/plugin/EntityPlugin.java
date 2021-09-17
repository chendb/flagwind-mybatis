//package com.flagwind.mybatis.definition.scripting.plugin;
//
//
//import com.flagwind.mybatis.definition.helper.TemplateSqlHelper;
//import com.flagwind.mybatis.definition.scripting.ScriptPlugin;
//import com.flagwind.mybatis.definition.scripting.annotation.Method;
//import com.flagwind.reflect.EntityTypeHolder;
//import com.flagwind.reflect.entities.EntityType;
//
//import javax.persistence.Table;
//
///**
// * 自定义公式
// *
// * @author 奔波儿灞
// * @since 1.0
// */
//public class EntityPlugin implements ScriptPlugin {
//
//    /**
//     * 实现自己的公式
//     *
//     * @param clzss 参数
//     * @return 结果
//     */
//    @Method(name = "COLUMNS", description = "description", usage = "columns(clzss,name)")
//    public String columns(Object clzss, Object alias) {
//        return TemplateSqlHelper.getBaseColumns((Class) clzss, alias == null ? null : alias.toString());
//    }
//
//    @Method(name = "TABLE", description = "description", usage = "table(clzss)")
//    public String table(Object clzss) {
//        EntityType entityType = EntityTypeHolder.getEntityType((Class) clzss);
//        String tableName = entityType.getAnnotation(Table.class).name();
//        return tableName;
//    }
//
//    @Method(name = "TEST", description = "description", usage = "TEST('ccc')")
//    public String test(Object script) {
//        return (String)script;
//    }
//}

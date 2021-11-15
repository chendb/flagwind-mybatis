package com.flagwind.mybatis;

import com.flagwind.mybatis.definition.Config;
import com.flagwind.mybatis.definition.builder.CompositeSqlBuilder;
import com.flagwind.mybatis.handlers.FlagwindEnumOrdinalTypeHandler;
import com.flagwind.mybatis.scripting.RepositoryDriver;
import lombok.Data;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author chendb
 * @description:重写Configuration
 * @date 2021-02-28 10:23:13
 */
@Data
public class FlagwindConfiguration extends Configuration {

    protected final MapperRegistry mapperRegistry;

//    protected final FormulaExecutor formulaExecutor;
    private Config properties;


    private CompositeSqlBuilder sqlBuilder;

//    static {
//        setDefaultScriptPlugin();
//    }

    public FlagwindConfiguration(Config properties) {
        super();
        this.properties = properties;
        this.mapperRegistry = new FlagwindMapperRegistry(this);
        this.mapUnderscoreToCamelCase = true;
        this.sqlBuilder = new CompositeSqlBuilder(this.properties);
//        this.formulaExecutor = new FormulaExecutor();
        this.setDefaultScriptingLanguage(RepositoryDriver.class);
        this.setDefaultEnumTypeHandler(FlagwindEnumOrdinalTypeHandler.class);
    }
//
//    public static void setDefaultScriptPlugin() {
//        FormulaExecutor.installPlugin(new EntityPlugin());
//    }

//    public String getXmlScript(String scriptText, Binding binding) {
//        return formulaExecutor.run(scriptText, binding).toString();
//    }


    @Override
    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return super.getTypeHandlerRegistry();
    }

    public CompositeSqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    @Override
    public MapperRegistry getMapperRegistry() {
        return this.mapperRegistry;
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        this.mapperRegistry.addMappers(packageName, superType);
    }

    @Override
    public void addMappers(String packageName) {
        this.mapperRegistry.addMappers(packageName);
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        this.mapperRegistry.addMapper(type);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return this.mapperRegistry.getMapper(type, sqlSession);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return this.mapperRegistry.hasMapper(type);
    }


}

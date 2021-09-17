package com.flagwind.mybatis.definition.scripting;

import com.flagwind.mybatis.definition.scripting.exception.ScriptException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FormulaExecutor {

    private final CompilerConfiguration cfg;
    private final Cache<String, Class<Script>> scriptCache;

    private final Binding emptyBinding = new Binding();

    public FormulaExecutor() {
        cfg = new CompilerConfiguration();
        cfg.setScriptBaseClass(FormulaScript.class.getName());
        scriptCache = CacheBuilder.newBuilder()
                .maximumSize(1024)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }

    /**
     * 运行脚本
     *
     * @param scriptText 公式脚本
     * @return 结果
     */
    public Object run(String scriptText) {
        return run(scriptText, emptyBinding);
    }

    /**
     * 运行脚本
     *
     * @param scriptText 公式脚本
     * @param binding    参数变量绑定
     * @return 结果
     */
    @SuppressWarnings("unchecked")
    public Object run(String scriptText, Binding binding) {
        Class<Script> scriptClass;
        try {
            scriptClass = scriptCache.get(scriptText, () -> {
                // 创建一个新的GroovyClassLoader，防止共用一个类导致无法卸载class
                GroovyClassLoader classLoader = new GroovyClassLoader(FormulaScript.class.getClassLoader(), cfg);
                return classLoader.parseClass(scriptText);
            });
        } catch (ExecutionException e) {
            throw new ScriptException("加载缓存失败", e);
        }
        Script script = InvokerHelper.createScript(scriptClass, binding);
        return script.run();
    }

    /**
     * 安装插件
     *
     * @param plugin Plugin
     */
    public static void installPlugin(ScriptPlugin plugin) {
        FormulaScript.installPlugin(plugin);
    }

    /**
     * 安装插件
     *
     * @param pluginClazz pluginClazz
     */
    public static void installPlugin(Class<? extends ScriptPlugin> pluginClazz) {
        FormulaScript.installPlugin(pluginClazz);
    }
}

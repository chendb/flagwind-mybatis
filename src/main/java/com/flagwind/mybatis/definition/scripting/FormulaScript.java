package com.flagwind.mybatis.definition.scripting;


import com.flagwind.mybatis.definition.scripting.annotation.Method;
import com.flagwind.mybatis.definition.scripting.exception.ScriptException;
import com.flagwind.mybatis.definition.scripting.exception.RegistryScriptException;
import groovy.lang.Binding;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 公式脚本，线程安全
 *
 * @author 奔波儿灞
 * @since 1.0
 */
@Slf4j
public class FormulaScript extends BaseScript {

    private static final Map<String, String> METHOD_MAPPING = new HashMap<>();
    private static final Map<String, Invocation> PLUGIN_METHOD_MAPPING = new HashMap<>();
    private static final Map<String, Method> MAPPINGS = new HashMap<>();

    private static final ReentrantReadWriteLock RWL = new ReentrantReadWriteLock();
    private static final Lock RL = RWL.readLock();
    private static final Lock WL = RWL.writeLock();

    private static final String VARIABLE_START = "#{";
    private static final String VARIABLE_END = "}";

    public FormulaScript() {

    }

    public Object getVariable(String name, Binding binding) {
        return binding.getVariable(name);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        // 解析变量
        parseVariables((Object[]) args);
        // 方法调用
        RL.lock();
        try {
            String realName = METHOD_MAPPING.get(name);
            if (realName == null) {
                Invocation invocation = PLUGIN_METHOD_MAPPING.get(name);
                if (invocation == null) {
                    throw new ScriptException("can not found method for formula: " + name);
                }
                try {
                    return invocation.invoke((Object[]) args);
                } catch (Exception e) {
                    throw new ScriptException("invoke method failed", e);
                }
            }
            return super.invokeMethod(realName, args);
        } finally {
            RL.unlock();
        }
    }

    private void parseVariables(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof String) {
                String argStr = (String) arg;
                if (argStr.startsWith(VARIABLE_START) && argStr.endsWith(VARIABLE_END)) {
                    String variable = argStr.substring(VARIABLE_START.length(), argStr.length() - 1);
                    // 从外部变量提供者获取值
                    Object value = this.getVariable(variable, getBinding());
                    if (value == null) {
                        log.warn("Variable [{}] can not find from binding context and variable supply.", variable);
                    }
                    args[i] = value;
                }
            }
        }
    }

    public static void installPlugin(Class<? extends ScriptPlugin> pluginClazz) {
        log.info("install formula plugin: {}", pluginClazz.getName());
        final ScriptPlugin instance;
        try {
            instance = pluginClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RegistryScriptException("plugin can not instantiation", e);
        }
        installPlugin(instance);
    }

    public static void installPlugin(ScriptPlugin plugin) {
        Class<? extends ScriptPlugin> pluginClazz = plugin.getClass();
        log.info("install formula plugin: {}", pluginClazz.getName());
        WL.lock();
        try {
            Arrays.stream(pluginClazz.getMethods())
                    .filter(method -> method.isAnnotationPresent(Method.class))
                    .forEach(method -> {
                        Method mapping = method.getDeclaredAnnotation(Method.class);
                        String alias = mapping.name();

                        PLUGIN_METHOD_MAPPING.put(alias, new Invocation(method, plugin));
                        MAPPINGS.put(alias, mapping);
                        log.debug("mapping {} -> {}.{}", alias, pluginClazz.getName(), method.getName());

                    });
        } finally {
            WL.unlock();
        }
    }

}

package com.flagwind.persistent;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceResolverFactory {

    // #region 单例字段
    private static ServiceResolverFactory instance;
    // #endregion

    // #region 成员字段
    private String defaultName;
    private ConcurrentHashMap<String, ServiceResolver> resolvers;
    // #endregion

    public static ServiceResolverFactory instance() {
        if (instance == null) {
            instance = new ServiceResolverFactory();
        }
        return instance;
    }




    // #region 公共方法
    public void register(String name, ServiceResolver provider) {
        this.register(name, provider, true);
    }

    public boolean register(String name, ServiceResolver resolver, boolean replaceOnExists) {

        name = StringUtils.isEmpty(name) ? "" : name.trim();

        if (replaceOnExists) {
            resolvers.put(name, resolver);
        } else {
            resolvers.putIfAbsent(name, resolver);
        }

        //返回成功
        return true;
    }


    /**
     * 注销服务供应程序
     * @param name 要注销服务供应程序的名称
     */
    public void unregister(String name) {
        name = StringUtils.isEmpty(name) ? "" : name.trim();
        resolvers.remove(name);
    }
    // #endregion

}

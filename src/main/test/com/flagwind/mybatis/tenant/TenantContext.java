package com.flagwind.mybatis.tenant;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author chendb
 * @description:
 * @date 2020-06-17 20:10:02
 */
@Slf4j
public class TenantContext {

    private static TenantContext instance;
    private Supplier<Map<String, String>> supplier;

    private TenantContext(Supplier<Map<String, String>> supplier) {
        this.supplier = supplier;
    }

    public static TenantContext instance() {
        if (instance == null) {
            instance = new TenantContext(() -> {
                HashMap<String, String> map = new HashMap<>();
                map.putIfAbsent("t", "");
                map.putIfAbsent("t1", "t");
                map.putIfAbsent("t11", "t1");
                map.putIfAbsent("t12", "t1");
                map.putIfAbsent("t2", "t");
                map.putIfAbsent("t21", "t2");
                map.putIfAbsent("t22", "t2");
                map.putIfAbsent("t3", "t");
                map.putIfAbsent("t31", "t3");
                map.putIfAbsent("t32", "t3");
                return map;
            });

        }
        return instance;
    }

    private static List<String> children(String tenantId) {
        List<String> keys = new ArrayList<>();
        try {
            if (instance == null) {
                throw new RuntimeException("租户上下文没有初始化");
            }
            Map<String, String> map = instance().supplier.get();
            map.entrySet().forEach(kv -> {
                if (StringUtils.equalsIgnoreCase(kv.getValue(), tenantId)) {
                    keys.add(kv.getKey());
                }
            });

        } catch (Exception ex) {
            throw new RuntimeException("租房缓存异常");
        }
        return keys;
    }

    private static void childrens(String tenantId, List<String> values) {
        List<String> keys = children(tenantId);
        for (String key : keys) {
            values.add(key);
            childrens(key, values);
        }
    }


    public List<String> getValues(Tenant.TenantStrategy strategy, String tenantId) {
        if (strategy.equals(Tenant.TenantStrategy.Private)) {
            return Arrays.asList(tenantId);
        }
        if (strategy.equals(Tenant.TenantStrategy.Children)) {
            List<String> values = children(tenantId);
            values.add(tenantId);
            return values;
        }
        if (strategy.equals(Tenant.TenantStrategy.Childrens)) {
            List<String> values = Arrays.asList(tenantId);
            childrens(tenantId, values);
            return values;
        }
        return Arrays.asList(tenantId);
    }
}

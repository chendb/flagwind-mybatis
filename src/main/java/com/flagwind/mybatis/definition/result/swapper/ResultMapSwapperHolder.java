package com.flagwind.mybatis.definition.result.swapper;

import org.apache.ibatis.session.Configuration;

import java.util.HashMap;
import java.util.Map;

public class ResultMapSwapperHolder {
    public static Map<String, ResultMapSwapper> CACHE = new HashMap<>();

   public static ResultMapSwapper getSwapper(Configuration configuration) {
        String id = configuration.getEnvironment().getId();
        if (!CACHE.containsKey(id)) {
            CACHE.put(id, new ResultMapSwapper(configuration));
        }
        return CACHE.get(id);
    }
}

package com.flagwind.mybatis.reflection.swapper;

import org.apache.ibatis.session.Configuration;

import java.util.HashMap;
import java.util.Map;

public class ResultMapSwapperHolder {
    public static Map<String, ResultMapSwapper> swapperMap = new HashMap<>();

   public static ResultMapSwapper getSwapper(Configuration configuration) {
        String id = configuration.getEnvironment().getId();
        if (!swapperMap.containsKey(id)) {
            swapperMap.put(id, new ResultMapSwapper(configuration));
        }
        return swapperMap.get(id);
    }
}

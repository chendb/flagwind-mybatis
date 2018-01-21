package com.flagwind.persistent;

/**
 * 实体联想提供器
 */
public interface AssociativeProvider {

    /**
     * 根据关键字联想到对象
     * @param key 联想关键字
     * @return 联想到的对象
     */
    Object associate(Object key);
}

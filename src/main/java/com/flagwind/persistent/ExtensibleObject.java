package com.flagwind.persistent;

/**
 * 可伸缩对象
 */
public interface ExtensibleObject {

    /**
     * 动态增加属性
     * @param name 属性名
     * @param value 属性值
     */
    void set(String name, Object value);

    /**
     * 获取属性值
     * @param name 属性名
     * @return 属性值
     */
    Object get(String name);

    /**
     * 是否包含指定属性名的属性
     * @param name 属性名
     * @return 是否存在
     */
    boolean contains(String name);
}

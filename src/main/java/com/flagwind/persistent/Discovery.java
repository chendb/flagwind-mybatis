package com.flagwind.persistent;

/**
 * 服务或对象查找接口
 */
public interface Discovery{

    /**
     * 根据指定服务名称获取服务实例。
     * @param  {string} name 服务名称。
     * @returns any
     */
  <T>  T  discover(String name);

    /**
     * 根据指定服务类型获取服务实例。
     * @param  {Function|string} serviceType 服务类型。
     * @returns T
     */
    <T>   T   discover(Class<?> serviceType);
}

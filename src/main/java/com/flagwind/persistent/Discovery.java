package com.flagwind.persistent;

/**
 * 服务或对象查找接口
 */
public interface Discovery{

    /**
     * 根据指定服务名称获取服务实例。
     * @param  name 服务名称。
     * @return any
     */
  <T>  T  discover(String name);

    /**
     * 根据指定服务类型获取服务实例。
     * @param  serviceType 服务类型。
     * @return T
     */
    <T> T discover(Class<?> serviceType);
}

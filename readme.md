## maven仓库发布

### 发布SNAPSHOT版本

```bash
mvn clean deploy
```

### 发布Staging版本

```bash
mvn release:clean
mvn release:prepare
mvn release:perform
```

>查看tag命名：【git tag looks like for my repository】


### 更新日志

* 2019-3-29
  
  增加自定义函数，以便适配不同数据库,目前自定义函数只支持msql和oracle两种数据库类型
    ```
    @now()                                                  // 取当前时间

    @length(name)                                           // 取字段 name 长度

    @decode(status,1:'在线',2:'离线','未知')                 // 类似case when else end
    
    @dt_format(timestramp,'yyyy-MM-dd HH:mm:ss')            // 时间格式化
    
    @dt_date(timestramp)                                    // 输出 yyyy-MM-dd 时间文档

    @year(timestramp)                                       // 输出 yyyy 时间文档

    @month(timestramp)                                      // 输出 MM 时间文档

    @day(timestramp)                                        // 输出 dd 时间文档

    @dt_time(timestramp)                                    // 输出 HH:mm:ss 时间文档

    @hour(timestramp)                                       // 输出 HH 时间文档

    @minute(timestramp)                                     // 输出 mm 时间文档

    @second(timestramp)                                     // 输出 ss 时间文档

    @substring(regionId,1,2) 或 @substring(regionId,2)      // 取region前两位 或 从第二个字符取值

    @left(name,2)                                           // 取 name 的左边两位

    @right(name,2)                                          // 取 name 的右边两位
    ```

package com.flagwind.mybatis.common;

import com.flagwind.mybatis.code.IdentityDialect;
import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.reflect.SimpleTypeUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * 通用MapperTemplate属性配置
 *
 * @author chendb
 */
public class Config {

    public static final String PREFIX  = "flagwind.mybatis";

    private String uuid;
    private String identity;
    private boolean before;
    private String  sequenceFormat;
    private String  schema;




    public String[] getMappers()
    {
        return mappers;
    }

    public void setMappers(String[] mappers)
    {
        this.mappers = mappers;
    }

    private String[] mappers;


    //使用简单类型
    private boolean useSimpleType;

    /**
     * 对于一般的getAllIfColumnNode，是否判断!=''，默认不判断
     */
    private boolean notEmpty = false;


    /**
     * 字段转换风格，默认驼峰转下划线
     */
    private Style style;

    private String dialect;

    public String getDialect(){
        return dialect;
    }

    public void setDialect(String value){
        dialect=value;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * clause查询子句内嵌套层级
     */
    private int depth;

    /**
     * 获取SelectKey的Order
     *
     *
     */
    public boolean isBefore() {
        return before;
    }

    public void setBefore(boolean before) {
        this.before = before;
    }



    /**
     * 获取主键自增回写SQL
     *
     */
    public String getIdentity() {
        if (StringUtils.isNotEmpty(this.identity)) {
            return this.identity;
        }
        //针对mysql的默认值
        return IdentityDialect.MYSQL.getIdentityRetrievalStatement();
    }

    /**
     * 主键自增回写方法,默认值MYSQL,详细说明请看文档
     *
     * @param identity
     */
    public void setIdentity(String identity) {
        IdentityDialect identityDialect = IdentityDialect.parse(identity);
        if (identityDialect != null) {
            this.identity = identityDialect.getIdentityRetrievalStatement();
        } else {
            this.identity = identity;
        }
    }

    public String getSchema() {
        return schema;
    }

    /**
     * 设置全局的schema,默认为空，如果设置了值，操作表时的sql会是schema.tablename
     * <br>如果同时设置了catalog,优先使用catalog.tablename
     *
     * @param schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 获取序列格式化模板
     *
     */
    public String getSequenceFormat() {
        if (StringUtils.isNotEmpty(this.sequenceFormat)) {
            return this.sequenceFormat;
        }
        return "{0}.nextval";
    }

    /**
     * 序列的获取规则,使用{num}格式化参数，默认值为{0}.nextval，针对Oracle
     * <br>可选参数一共3个，对应0,1,2,3分别为SequenceName，ColumnName, PropertyName，TableName
     *
     * @param sequenceFormat
     */
    public void setSequenceFormat(String sequenceFormat) {
        this.sequenceFormat = sequenceFormat;
    }

    /**
     * 获取UUID生成规则
     *
     */
    public String getUuid() {
        if (StringUtils.isNotEmpty(this.uuid)) {
            return this.uuid;
        }
        return "@java.util.UUID@randomUUID().toString().replace(\"-\", \"\")";
    }

    /**
     * 设置UUID生成策略
     * <br>配置UUID生成策略需要使用OGNL表达式
     * <br>默认值32位长度:@java.util.UUID@randomUUID().toString().replace("-", "")
     *
     * @param uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isNotEmpty() {
        return notEmpty;
    }

    public void setNotEmpty(boolean notEmpty) {
        this.notEmpty = notEmpty;
    }

    public Style getStyle() {
        return this.style == null ? Style.camelhump : this.style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }


    public boolean isUseSimpleType() {
        return useSimpleType;
    }

    public void setUseSimpleType(boolean useSimpleType) {
        this.useSimpleType = useSimpleType;
    }

    /**
     * 获取表前缀，带catalog或schema
     *
     */
    public String getPrefix() {
        if (StringUtils.isNotEmpty(this.schema)) {
            return this.schema;
        }
        return "";
    }

    /**
     * 配置属性
     *
     * @param properties
     */
    public void setProperties(String prefix,Properties properties)
    {
        if(properties == null)
        {
            //默认驼峰
            this.style = Style.camelhump;
            return;
        }
        if(StringUtils.isNotBlank(prefix))
        {
            if(!prefix.endsWith("."))
            {
                prefix += ".";
            }
        }
        else
        {
            prefix = "";
        }

        String UUID = properties.getProperty(prefix + "uuid");
        if(StringUtils.isNotEmpty(UUID))
        {
            setUuid(UUID);
        }
        String IDENTITY = properties.getProperty(prefix + "identity");
        if(StringUtils.isNotEmpty(IDENTITY))
        {
            setIdentity(IDENTITY);
        }

        String dialect = properties.getProperty(prefix + "dialect");
        if(StringUtils.isNotEmpty(dialect))
        {
            setDialect(dialect);
        }

        String depth = properties.getProperty(prefix + "depth");
        if(StringUtils.isNotEmpty(depth))
        {
            setDepth(Integer.parseInt(depth));
        }

        String seqFormat = properties.getProperty(prefix + "sequence-format");
        if(StringUtils.isNotEmpty(seqFormat))
        {
            setSequenceFormat(seqFormat);
        }

        String mapper = properties.getProperty(prefix + "mappers");
        if(StringUtils.isNotEmpty(mapper))
        {
                String[] mappers = mapper.split(",");
                setMappers(mappers);
        }
//        else
//        {
//            setMappers(new String[]{AbstractRepository.class.getName()});
//        }

        String schema = properties.getProperty(prefix + "schema");
        if(StringUtils.isNotEmpty(schema))
        {
            setSchema(schema);
        }

        String notEmpty = properties.getProperty(prefix + "not-empty");
        if(StringUtils.isNotEmpty(notEmpty))
        {
            this.notEmpty = notEmpty.equalsIgnoreCase("TRUE");
        }


        String useSimpleTypeStr = properties.getProperty(prefix + "use-simple-type");
        if(StringUtils.isNotEmpty(useSimpleTypeStr))
        {
            this.useSimpleType = useSimpleTypeStr.equalsIgnoreCase("TRUE");
        }
        //注册新的基本类型，以逗号隔开，使用全限定类名
        String simpleTypes = properties.getProperty(prefix + "simple-types");
        if(StringUtils.isNotEmpty(simpleTypes))
        {
            SimpleTypeUtils.register(simpleTypes);
        }
        String styleStr = properties.getProperty(prefix + "style");
        if(StringUtils.isNotEmpty(styleStr))
        {
            try
            {
                this.style = Style.valueOf(styleStr);
            }
            catch(IllegalArgumentException e)
            {
                throw new MapperException(styleStr + "不是合法的Style值!");
            }
        }
        else
        {
            //默认驼峰
            this.style = Style.camelhump;
        }
    }
}
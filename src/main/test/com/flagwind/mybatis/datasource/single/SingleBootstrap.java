package com.flagwind.mybatis.datasource.single;

import com.flagwind.mybatis.definition.interceptor.PaginationInterceptor;
import com.flagwind.mybatis.definition.interceptor.tenant.TenantHandler;
import com.flagwind.mybatis.definition.interceptor.tenant.TenantSqlParser;
import com.flagwind.mybatis.definition.parser.ISqlParser;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@AutoConfigureBefore(name = "com.flagwind.mybatis.spring.autoconfigure.FlagwindAutoConfiguration")
public class SingleBootstrap
{

	@Bean
	public PaginationInterceptor paginationInterceptor() {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

		// 创建SQL解析器集合
		List<ISqlParser> sqlParserList = new ArrayList<>();

		// 创建租户SQL解析器
		TenantSqlParser tenantSqlParser = new TenantSqlParser();

		// 设置租户处理器
		tenantSqlParser.setTenantHandler(new TenantHandler() {

			@Override
			public Expression getTenantId(boolean where) {
				// 设置当前租户ID，实际情况你可以从cookie、或者缓存中拿都行
				return new StringValue("2");
			}

			@Override
			public String getTenantIdColumn() {
				// 对应数据库租户ID的列名
				return "categoryId";
			}

			@Override
			public boolean doTableFilter(String tableName) {
				// 是否需要需要过滤某一张表
              /*  List<String> tableNameList = Arrays.asList("sys_user");
                if (tableNameList.contains(tableName)){
                    return true;
                }*/
				return !tableName.equalsIgnoreCase("com_role");
			}
		});

		sqlParserList.add(tenantSqlParser);
		paginationInterceptor.setSqlParserList(sqlParserList);

		return paginationInterceptor;

	}


	public static void main(String[] args)
	{
		//org.elasticsearch.transport.netty4.Netty4InternalESLogger
		SpringApplication.run(SingleBootstrap.class, args);
	}
}
package com.flagwind.mybatis.spring.autoconfigure;

import com.flagwind.mybatis.common.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 这个类存在的主要目的是方便 IDE 自动提示 mapper. 开头的配置
 *
 * @author liuzh
 * @since 2017/1/2.
 */
@ConfigurationProperties(prefix = FlagwindProperties.PREFIX)
public class FlagwindProperties extends Config
{
	private  Paginator paginator = new Paginator();


	public Paginator getPaginator()
	{
		return paginator;
	}

	public void setPaginator(Paginator paginator)
	{
		this.paginator = paginator;
	}



	public static class Paginator
	{

		private boolean asyncTotalCount = false;
		private int poolMaxSize = 0;

		public boolean isAsyncTotalCount()
		{
			return asyncTotalCount;
		}

		public void setAsyncTotalCount(boolean asyncTotalCount)
		{
			this.asyncTotalCount = asyncTotalCount;
		}

		public int getPoolMaxSize()
		{
			return poolMaxSize;
		}

		public void setPoolMaxSize(int poolMaxSize)
		{
			this.poolMaxSize = poolMaxSize;
		}
	}

}

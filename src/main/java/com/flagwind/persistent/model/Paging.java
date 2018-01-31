package com.flagwind.persistent.model;

import java.io.Serializable;


public class Paging implements Serializable {

	private static final long serialVersionUID = 754828768811350476L;

	// 总条数
	private Long totalCount;
	// 页码
	private Long pageIndex;
	// 每页显示多少条
	private long pageSize;
	private boolean enableTotalCount;



	public Paging() {
		this(1l, 10l);
	}

	public Paging(Long pageIndex, Long pageSize) {
		this(pageIndex, pageSize, 0);
	}

	public Paging(long pageIndex, long pageSize, long totalCount) {
		if (pageSize < 1) {
			pageSize = 10l;
		}

		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.pageIndex = pageIndex;
		this.enableTotalCount = true;
	}



	/** 返回每页的条数 */
	public Long getTotalCount() {
		return totalCount;
	}

	public Long getPageIndex() {
		return pageIndex;
	}

	/** 返回每页的条数 */

	public Long getPageSize() {
		return pageSize;
	}

	/** 返回总的页数 */

	public Long getPageCount() {
		if (totalCount < 1) {
			return 0l;
		}

		return Math.round(Math.ceil((double) totalCount / pageSize));
	}
	

	public boolean getEnableTotalCount() {
		return enableTotalCount;
	}

	public void setPageIndex(Long pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
	
	public void setEnableTotalCount(boolean enableTotalCount) {
		this.enableTotalCount = enableTotalCount;
	}


	@Override
	public String toString() {
		return "Paging [" +
				"totalCount=" + totalCount +
				", pageIndex=" + pageIndex +
				", pageSize=" + pageSize
				+ ", enableTotalCount=" + enableTotalCount + "]";
	}

	// region 静态构造方法

	/**
	 * 构建分页条件
	 */
	public static Paging build(Long pageIndex, Long pageSize) {
		return new Paging(pageIndex, pageSize);
	}

	/**
	 * 构建分页条件
	 */
	public static Paging build() {
		return new Paging();
	}

	// endregion

}
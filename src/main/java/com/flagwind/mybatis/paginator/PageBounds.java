package com.flagwind.mybatis.paginator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.RowBounds;

import com.flagwind.persistent.model.Sorting;

/**
 *  分页查询对象
 *  @author badqiu
 *  @author hunhun
 *  @author miemiedev
 */
public class PageBounds extends RowBounds implements Serializable {
    private static final long serialVersionUID = -6414350656252331011L;
    public final static int NO_PAGE = 1;
    /** 页号 */
    protected int pageIndex = NO_PAGE;
    /** 分页大小 */
    protected int pageSize = NO_ROW_LIMIT;
    /** 分页排序信息 */
    protected List<Sorting> orders = new ArrayList<Sorting>();
    /** 结果集是否包含TotalCount */
    protected boolean containsTotalCount;

    protected Boolean asyncTotalCount;

    public PageBounds(){
        containsTotalCount = false;
    }

    public PageBounds(RowBounds rowBounds) {
        if(rowBounds instanceof PageBounds){
            PageBounds pageBounds = (PageBounds)rowBounds;
            this.pageIndex = pageBounds.pageIndex;
            this.pageSize = pageBounds.pageSize;
            this.orders = pageBounds.orders;
            this.containsTotalCount = pageBounds.containsTotalCount;
            this.asyncTotalCount = pageBounds.asyncTotalCount;
        }else{
            this.pageIndex = (rowBounds.getOffset()/rowBounds.getLimit())+1;
            this.pageSize = rowBounds.getLimit();
        }

    }


    public PageBounds(int pageSize) {
        this.pageSize = pageSize;
        this.containsTotalCount = false;
    }

    public PageBounds(int page, int limit) {
        this(page, limit, new ArrayList<Sorting>(), true);
    }

    public PageBounds(int page, int limit, boolean containsTotalCount) {
        this(page, limit, new ArrayList<Sorting>(), containsTotalCount);
    }

 
    public PageBounds(List<Sorting> orders) {
        this(NO_PAGE, NO_ROW_LIMIT,orders ,false);
    }

 
    public PageBounds(Sorting... order) {
        this(NO_PAGE, NO_ROW_LIMIT,order);
        this.containsTotalCount = false;
    }

    public PageBounds(int page, int limit, Sorting... order) {
        this(page, limit, Arrays.asList(order), true);
    }

    public PageBounds(int page, int limit, List<Sorting> orders) {
        this(page, limit, orders, true);
    }

    public PageBounds(int pageIndex, int pageSize, List<Sorting> orders, boolean containsTotalCount) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.orders = orders;
        this.containsTotalCount = containsTotalCount;
    }


    public int getPage() {
        return pageIndex;
    }

    public void setPage(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getLimit() {
        return pageSize;
    }

    public void setLimit(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isContainsTotalCount() {
        return containsTotalCount;
    }

    public void setContainsTotalCount(boolean containsTotalCount) {
        this.containsTotalCount = containsTotalCount;
    }

    public List<Sorting> getOrders() {
        return orders;
    }

    public void setOrders(List<Sorting> orders) {
        this.orders = orders;
    }

/*    public Boolean getAsyncTotalCount() {
        return asyncTotalCount;
    }*/

    public void setAsyncTotalCount(Boolean asyncTotalCount) {
        this.asyncTotalCount = asyncTotalCount;
    }

    @Override
    public int getOffset() {
        if(pageIndex >= 1){
            return (pageIndex-1) * pageSize;
        }
        return 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PageBounds{");
        sb.append("pageIndex=").append(pageIndex);
        sb.append(", pageSize=").append(pageSize);
        sb.append(", orders=").append(orders);
        sb.append(", containsTotalCount=").append(containsTotalCount);
        sb.append('}');
        return sb.toString();
    }
}
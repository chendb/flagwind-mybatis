package com.flagwind.mybatis.definition.interceptor.pagination;

import com.flagwind.persistent.model.Sorting;
import org.apache.ibatis.session.RowBounds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PageBounds extends RowBounds implements Serializable {

    private static final long serialVersionUID = -6414350656252331011L;

    private final static int NO_PAGE = 1;
    /** 页号 */
    private int pageIndex = NO_PAGE;
    /** 分页大小 */
    private int pageSize = NO_ROW_LIMIT;
    /** 分页排序信息 */
    private List<Sorting> orders = new ArrayList<Sorting>();
    /** 结果集是否包含TotalCount */
    private boolean containsTotalCount;

    private Boolean asyncTotalCount;

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
        this(page, limit, new ArrayList<>(), containsTotalCount);
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

    @Override
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
        String sb = "PageBounds{" + "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", orders=" + orders +
                ", containsTotalCount=" + containsTotalCount +
                '}';
        return sb;
    }
}
package com.zjt.model;

/**
 * @author <a href=""mailto:Administrator@gtmap.cn></a>
 * @version 1.0, 2017/11/8
 * @description
 */

import java.io.Serializable;

/**
 * 分页对象.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Oct 6, 2012
 */
public final class Page implements Serializable {

    /**
     * 默认的序列化版本 id.
     */
    private static final long serialVersionUID = 1L;
    /**
     * 分页查询开始记录位置.
     */
    private int               begin;
    /**
     * 分页查看下结束位置.
     */
    private int               end;
    /**
     * 每页显示记录数.
     */
    private int               length           = 20;
    /**
     * 查询结果总记录数.
     */
    private int               totalRecords;
    /**
     * 当前页码.
     */
    private int               pageNo;
    /**
     * 总共页数.
     */
    private int               pageCount;

    public Page() {
    }

    /**
     * 构造函数.
     *
     * @param begin
     * @param length
     */
    public Page(int begin, int length) {
        this.begin = begin;
        this.length = length;
        this.end = this.begin + this.length;
        this.pageNo = (int) Math.floor((this.begin * 1.0d) / this.length) + 1;
    }

    /**
     * @param begin
     * @param length
     * @param totalRecords
     */
    public Page(int begin, int length, int totalRecords) {
        this(begin, length);
        this.totalRecords = totalRecords;
        setTotalRecords(totalRecords);//zjt
    }

    /**
     * 设置页数，自动计算数据范围.
     *
     * @param pageNo
     */
    public Page(int pageNo) {
        this.pageNo = pageNo;
        pageNo = pageNo > 0 ? pageNo : 1;
        this.begin = this.length * (pageNo - 1);
        this.end = this.length * pageNo;
    }



    /**
     * @return the begin
     */
    public int getBegin() {
        return begin;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end
     *            the end to set
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * @param begin
     *            the begin to set
     */
    public void setBegin(int begin) {
        this.begin = begin;
        if (this.length != 0) {
            this.pageNo = (int) Math.floor((this.begin * 1.0d) / this.length) + 1;
        }
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length
     *            the length to set
     */
    public void setLength(int length) {
        this.length = length;
        if (this.begin != 0) {
            this.pageNo = (int) Math.floor((this.begin * 1.0d) / this.length) + 1;
        }
    }

    /**
     * @return the totalRecords
     */
    public int getTotalRecords() {
        return totalRecords;
    }

    /**
     * @param totalRecords
     *            the totalRecords to set
     */
    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
        this.pageCount = (int) Math.floor((this.totalRecords * 1.0d) / this.length);
        if (this.totalRecords % this.length != 0) {
            this.pageCount++;
        }
    }

    /**
     * @return the pageNo
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * @param pageNo
     *            the pageNo to set
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
        pageNo = pageNo > 0 ? pageNo : 1;
        this.begin = this.length * (pageNo - 1);
        this.end = this.length * pageNo;
    }

    /**
     * @return the pageCount
     */
    public int getPageCount() {
        if (pageCount == 0) {
            return 1;
        }
        return pageCount;
    }

    /**
     * @param pageCount
     *            the pageCount to set
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("begin=").append(begin).append(", end=")
                .append(end).append(", length=").append(length).append(", totalRecords=").append(
                        totalRecords).append(", pageNo=").append(pageNo).append(", pageCount=")
                .append(pageCount);

        return builder.toString();
    }
}

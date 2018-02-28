package com.zjt.model;

import com.github.pagehelper.PageInfo;

import java.util.List;

public class PageRusult<T> extends PageInfo<T> {
    public PageRusult() {
    }

    public PageRusult(List<T> list) {
        super(list, 8);
    }

    private Integer code;//layui框架列表模块返回参数中必须包含code状态字段

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}

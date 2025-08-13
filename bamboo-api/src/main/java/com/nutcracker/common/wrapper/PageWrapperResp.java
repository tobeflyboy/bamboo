package com.nutcracker.common.wrapper;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 页面结果
 *
 * @author 胡桃夹子
 * @date 2025/04/03 10:30:08
 */
@ToString(callSuper = true)
@Getter
public class PageWrapperResp<T> implements Serializable {

    private static final long serialVersionUID = 2474225556241688093L;

    private final List<T> list;
    private final long total;
    private int page;
    private int size;

    public PageWrapperResp(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

    // 带分页参数的构造函数
    public PageWrapperResp(List<T> list, long total, int page, int size) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
    }

}
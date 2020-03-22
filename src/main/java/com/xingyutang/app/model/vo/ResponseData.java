package com.xingyutang.app.model.vo;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import java.util.ArrayList;
import java.util.List;

public class ResponseData {
    private int code;
    private String message;
    private Object data;

    public ResponseData(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseData ok() {
        return new ResponseData(0, null, null);
    }

    public static ResponseData ok(Object data) {
        if (data != null && data instanceof Page) {
            return new PagedResponseData(0, null, data);
        } else {
            return new ResponseData(0, null, data);
        }
    }

    public static ResponseData error(int code, String message) {
        return new ResponseData(code, message, null);
    }

    public static ResponseData error(int code, String message, Object data) {
        if (data != null && data instanceof Page) {
            return new PagedResponseData(0, null, data);
        } else {
            return new ResponseData(code, message, data);
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static class PagedResponseData extends ResponseData {
        private Integer pageNum;
        private Integer pageSize;
        private Long total;
        private Integer pages;

        public PagedResponseData(int code, String message, Object data) {
            super(code, message, data);
            Page<?> page = (Page<?>)data;
            pageNum = page.getPageNum();
            pageSize = page.getPageSize();
            total = page.getTotal();
            pages = page.getPages();
            List<Object> _data = new ArrayList<>();
            _data.addAll(page);
            this.setData(_data);

            PageHelper.clearPage();
        }

        public Integer getPageNum() {
            return pageNum;
        }

        public void setPageNum(Integer pageNum) {
            this.pageNum = pageNum;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Integer getPages() {
            return pages;
        }

        public void setPages(Integer pages) {
            this.pages = pages;
        }
    }
}

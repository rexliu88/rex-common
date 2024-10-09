package com.auxgroup.adp.commons.utils.page;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryBody<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private PageQuery pageQuery;
    private T params;

    public static <T> QueryBody<T> build(T params, PageQuery pageQuery) {
        return newBody(params, pageQuery);
    }

    private static <T> QueryBody<T> newBody(T params, PageQuery pageQuery) {
        QueryBody<T> body = new QueryBody();
        body.setParams(params);
        body.setPageQuery(pageQuery);
        return body;
    }
}

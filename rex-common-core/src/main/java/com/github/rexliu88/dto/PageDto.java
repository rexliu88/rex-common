package com.github.rexliu88.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据 查询结果 Dto
 * @param <T>   查询结果DTOList泛型
 *
 * count 记录总数
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class PageDto <T> implements Serializable {
    private List<T> data;
    private Integer count = 0;
}

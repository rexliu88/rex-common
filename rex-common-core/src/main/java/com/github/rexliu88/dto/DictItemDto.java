package com.github.rexliu88.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 字段值Dto
 *
 * id 字段值
 * name 字段名称
 */
@Data
@Accessors(chain = true)
public class DictItemDto {
    private String id;
    private String name;
}

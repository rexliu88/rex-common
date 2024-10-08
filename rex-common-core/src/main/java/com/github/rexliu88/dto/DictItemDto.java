package com.github.rexliu88.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 字段值Dto
 */
@Data
@Accessors(chain = true)
public class DictItemDto {
    private String id;
    private String name;
}

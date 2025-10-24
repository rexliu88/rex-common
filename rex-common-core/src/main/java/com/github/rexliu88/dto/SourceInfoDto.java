package com.github.rexliu88.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 来源Dto
 *
 * 来源id
 * 来源名称
 *
 * 来源项目id
 * 来源项目名称
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SourceInfoDto implements Serializable {
    String sourceId;
    String sourceName;
    String itemId;
    String itemName;
}

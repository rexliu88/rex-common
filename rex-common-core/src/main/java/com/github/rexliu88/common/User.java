package com.github.rexliu88.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录用户对象
 */
@Data
@AllArgsConstructor
public class User {
    String id;
    String name;
}

package com.auxdemo.adp.commons.group.config.component.scan.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@ConfigurationProperties(
        prefix = "aux.enums.conflict"
)
public class AuxEnumIgnoreProps {
    @Getter
    @Setter
    private Map<String, String> ignores;

    public List<String> getIgnoreClassList(String simpleName) {
        if (StrUtil.isEmpty(simpleName) || this.ignores == null) {
            return Collections.emptyList();
        }

        String ignoreValue = this.ignores.get(simpleName);
        if (StrUtil.isEmpty(ignoreValue)) {
            return Collections.emptyList();
        }

        return Arrays.asList(ignoreValue.split(","));
    }
}

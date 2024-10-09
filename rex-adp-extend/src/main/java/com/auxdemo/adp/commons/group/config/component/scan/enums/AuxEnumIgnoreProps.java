package com.auxdemo.adp.commons.group.config.component.scan.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(
        prefix = "aux.enums.conflict"
)
public class AuxEnumIgnoreProps {
    @Getter
    @Setter
    private Map<String, String> ignores;

    public List<String> getIgnoreClassList(String simpleName) {
        if (!StrUtil.isEmpty(simpleName) && this.ignores != null) {
            return (List)(StrUtil.isEmpty((String)this.ignores.get(simpleName)) ? new ArrayList() : Arrays.asList(((String)this.ignores.get(simpleName)).split(",")));
        } else {
            return new ArrayList();
        }
    }
}

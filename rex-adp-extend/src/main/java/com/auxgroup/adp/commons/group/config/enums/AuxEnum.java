package com.auxgroup.adp.commons.group.config.enums;

import lombok.Data;

import java.util.Map;
public interface AuxEnum {
    default String getCodeField() {
        return "code";
    }
    default String getNameField() {
        return "name";
    }
    default String[] getOtherPropsKeys() {
        return null;
    }

    @Data
    public static class AuxEnumProps {
        private Object code;
        private Object name;
        Map<String, Object> otherProps;

        public AuxEnumProps(Object code, Object name) {
            this.code = code;
            this.name = name;
        }

        public AuxEnumProps(Object code, Object name, Map<String, Object> otherProps) {
            this.code = code;
            this.name = name;
            if (otherProps != null && !otherProps.isEmpty()) {
                this.otherProps = otherProps;
            }
        }
    }
}

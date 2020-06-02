package com.example.demo.intercpt;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 本地线程缓存信息
 */
@Data
public class Context implements Serializable {
    private String userName;
    private String displayName;
    private String operationName;
    private String code;
    private Map<String, Object> attributes;

    public String toString() {
        String result = "Context{\n\tusername='" + this.userName + '\'' + "\n\tdisplayName='" + this.displayName + '\'' + "\n\toperationName='" + this.operationName + '\'';
        if (this.attributes != null) {
            result = result + "\n\tattributes={\n" + this.attributes.entrySet().stream().map((entry) -> {
                return "\t\t" + entry.getKey() + "='" + entry.getValue() + "'";
            }).collect(Collectors.joining("\n")) + "\n\t}";
        }

        result = result + "\n}";
        return result;
    }

}

package com.ztq.filters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 去除Json格式参数的前后空格
 */
@Component
public class StringTrimModule extends SimpleModule {

    public StringTrimModule() {

        addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
            @Override
            public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
                String value = jsonParser.getValueAsString();
                if(StringUtils.isEmpty(value)){
                    return value;
                }
                return value.trim();
            }
        });
    }
}

package com.ztq.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

/**
 * 日期时间序列化
 *
 * @Author: zhengtianqi
 * @Date: 2021/7/7
 */
public class DateSerialize1 extends JsonSerializer {
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value != null) {
            if (value.getClass().isAssignableFrom(Date.class)) {
                String dateToString = TimeUtils.convertDateToString((Date) value, TimeUtils.FORMAT2);
                jsonGenerator.writeString(dateToString);
            }
        }
    }
}

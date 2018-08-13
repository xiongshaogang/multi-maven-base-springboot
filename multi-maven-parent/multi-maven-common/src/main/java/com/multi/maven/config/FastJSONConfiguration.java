package com.multi.maven.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.Date;

/**
 * 配置fastjson替换jackson
 *
 * @author James
 */
@Configuration
public class FastJSONConfiguration {

    /**
     * 使用 @Bean 注入 FastJsonHttpMessageConverter
     *
     * @return
     */
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        //1、需要先定义一个 convert 转换消息对象；
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue, //显示null的属性
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty); //避免 $ref出现);
        ValueFilter valueFilter = new ValueFilter() {
            @Override
            public Object process(Object object, String name, Object value) {
                if (value instanceof Date) {
                    return ((Date) value).getTime();
                }
                if (value instanceof Long) {
                    return String.valueOf(value);
                }
                return value;
            }
        };
        fastJsonConfig.setSerializeFilters(valueFilter);
        //3、在 Convert 中添加配置信息;
        fastConverter.setFastJsonConfig(fastJsonConfig);
        //4、
        HttpMessageConverter<?> converter = fastConverter;
        return new HttpMessageConverters(converter);
    }
}

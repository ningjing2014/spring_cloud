package com.ln.xproject.configuration.web;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
public class MvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // JSON - FastJson
        {
            //使用jackson处理health接口数据
            MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter() {
                @Override
                public boolean canWrite(Class<?> clazz, MediaType mediaType) {
                    return clazz.equals(Health.class);
                }

                @Override
                public boolean canRead(Class<?> clazz, MediaType mediaType) {
                    return clazz.equals(Health.class);
                }

                @Override
                public boolean canRead(Type type, Class<?> contextClass,
                        MediaType mediaType) {
                    return Health.class.getTypeName().equals(type.getTypeName());
                }
            };
            converters.add(jacksonConverter);

            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);

            FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
            converter.setSupportedMediaTypes(mediaTypes);

            converters.add(converter);
        }
        super.configureMessageConverters(converters);
    }

}

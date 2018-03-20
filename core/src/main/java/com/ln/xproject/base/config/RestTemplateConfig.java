package com.ln.xproject.base.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ning on 2/15/17.
 */
@Configuration
public class RestTemplateConfig {
    /** restTemplate配置参数 */
    @Bean
    @ConfigurationProperties(prefix = "restTemplate")
    public RestTemplateParams restTemplateParams() {
        RestTemplateParams restTemplateParams = new RestTemplateParams();
        return restTemplateParams;
    }

    @Bean
    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(60000);
        factory.setConnectTimeout(10000);
        return factory;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();
        restTemplate.setRequestFactory(simpleClientHttpRequestFactory());
        return restTemplate;
    }

    public class RestTemplateParams {
        /**
         * 读取超时时间
         */
        @Getter
        @Setter
        private int readTimeout;

        /**
         * 连接超时时间
         */
        @Getter
        @Setter
        private int connectTimeout;

        public RestTemplateParams() {
            this.readTimeout = 60000;
            this.connectTimeout = 10000;
        }
    }

}

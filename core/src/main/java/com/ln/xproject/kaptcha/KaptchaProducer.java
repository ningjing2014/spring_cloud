package com.ln.xproject.kaptcha;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

@Component
public class KaptchaProducer extends DefaultKaptcha {

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put("kaptcha.border", "yes");
        props.put("kaptcha.border.color", "105,179,90");
        props.put("kaptcha.textproducer.font.color", "blue");
        props.put("kaptcha.image.width", "125");
        props.put("kaptcha.image.height", "45");
        props.put("kaptcha.textproducer.font.size", "45");
        props.put("kaptcha.session.key", "code");
        props.put("kaptcha.textproducer.char.length", "4");
        props.put("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        this.setConfig(new Config(props));
    }
    
}

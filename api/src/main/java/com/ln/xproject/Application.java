package com.ln.xproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.ibg.springboot.admin.client.SpringBootAdminClientConfigurationListener;
import com.ln.xproject.context.ContextContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class Application extends SpringBootServletInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.listeners(new SpringBootAdminClientConfigurationListener());
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        ApplicationContext ac = (ApplicationContext) SpringApplication.run(Application.class, args);
        ContextContainer.setAc(ac);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("onApplicationEvent!");
        ContextContainer.setAc(event.getApplicationContext());
    }

}

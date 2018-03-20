package com.ln.xproject.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import lombok.Getter;
import lombok.Setter;

/**
 * shiro配置信息
 */
@Configuration
public class ShiroConfig {

    /** shiro配置参数 */
    @Bean
    @ConfigurationProperties(prefix = "shiro")
    public ShiroConfigParams shiroConfigParams() {
        ShiroConfigParams shiroConfigParams = new ShiroConfigParams();
        return shiroConfigParams;
    }

    /** shiro配置 */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setLoginUrl("/noLogin");
        shiroFilter.setUnauthorizedUrl("/noAuth");
        Map<String, String> filterChainDefinitionMapping = new LinkedHashMap<String, String>();

        // 登录、退出
        filterChainDefinitionMapping.put("/login", "anon");
        filterChainDefinitionMapping.put("/logout", "anon");
        filterChainDefinitionMapping.put("/noLogin", "anon");
        filterChainDefinitionMapping.put("/loginCheck", "anon");
        filterChainDefinitionMapping.put("/sysUser/updatePassword", "anon");
        filterChainDefinitionMapping.put("/sysUser/forgetPassword", "anon");
        filterChainDefinitionMapping.put("/api/**", "anon");
        filterChainDefinitionMapping.put("/noAuth", "anon");
        filterChainDefinitionMapping.put("/sysUser/getBusinessChannel", "anon");
        filterChainDefinitionMapping.put("/sysUser/getPaySubject", "anon");
        filterChainDefinitionMapping.put("/file/upload", "anon");
        filterChainDefinitionMapping.put("/validatePic/get", "anon");
        filterChainDefinitionMapping.put("/health/**", "anon");
        filterChainDefinitionMapping.put("/info", "anon");
        filterChainDefinitionMapping.put("/version", "anon");
        filterChainDefinitionMapping.put("/error", "anon");
        //排除监控url
        String[] endpoints = { "env", "metrics", "trace", "dump", "jolokia",
                "info", "logfile", "refresh", "flyway", "liquibase", "heapdump",
                "loggers", "auditevents" };
        for (String endpoint : endpoints) {
            filterChainDefinitionMapping.put("/".concat(endpoint), "anon");
            filterChainDefinitionMapping.put("/".concat(endpoint).concat("/"), "anon");
        }
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMapping);
        shiroFilter.setSecurityManager(securityManager());

        Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
        filters.put("anon", new AnonymousFilter());
        filters.put("authc", new FormAuthenticationFilter());
        filters.put("kickout", kickoutSessionControlFilter());

        shiroFilter.setFilters(filters);
        return shiroFilter;
    }

    @Bean(name = "securityManager")
    public org.apache.shiro.mgt.SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());
        securityManager.setSessionManager(defaultWebSessionManager());
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }

    @Bean(name = "realm")
    @DependsOn("lifecycleBeanPostProcessor")
    public AuthorizingRealm realm() {
        AuthorizingRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(credentialsMatcher());
        userRealm.setAuthorizationCachingEnabled(false);
        userRealm.setAuthenticationCachingEnabled(false);
        return userRealm;
    }

    @Bean(name = "credentialsMatcher")
    public CredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(shiroConfigParams().getHashAlgorithmName());
        credentialsMatcher.setHashIterations(shiroConfigParams().getHashIterations());
        return credentialsMatcher;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public KickoutSessionControlFilter kickoutSessionControlFilter() {
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        return kickoutSessionControlFilter;
    }
    
    @Bean
    public SqlInjectAndXssFilter sqlInjectAndXssFilter() {
        SqlInjectAndXssFilter sqlInjectAndXssFilter = new SqlInjectAndXssFilter();
        return sqlInjectAndXssFilter;
    }
    
    /** shiro分布式session配置开始 */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        return redisSessionDAO;
    }

    @Bean
    public MemorySessionDAO memorySessionDAO() {
        MemorySessionDAO memorySessionDAO = new MemorySessionDAO();
        return memorySessionDAO;
    }

    @Bean
    public SimpleCookie simpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName("sid");
        simpleCookie.setPath("/");
        simpleCookie.setHttpOnly(true);
        return simpleCookie;
    }

    @Bean
    public DefaultWebSessionManager defaultWebSessionManager() {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        if (shiroConfigParams().isUseRedis()) { // 使用redis，session存放于redis
            defaultWebSessionManager.setSessionDAO(redisSessionDAO());
        } else { // 不是用redis，session存放于内存
            defaultWebSessionManager.setSessionDAO(memorySessionDAO());
        }
        defaultWebSessionManager.setSessionIdCookie(simpleCookie());
        defaultWebSessionManager.setGlobalSessionTimeout(2 * 60 * 60 * 1000);
        return defaultWebSessionManager;
    }

    @Bean
    public CacheManager cacheManager() {
        CacheManager cacheManager = null;
        if (shiroConfigParams().isUseRedis()) { // 使用redis，cache存放于redis
            cacheManager = new RedisCacheManager();
        } else { // 不是用redis，cache存放于内存
            cacheManager = new MemoryConstrainedCacheManager();
        }
        return cacheManager;
    }

    /** shiro分布式session配置结束 */

    /**
     * shiro配置参数类
     */
    public class ShiroConfigParams {
        /**
         * 密码加密方式：默认md5
         */
        @Getter
        @Setter
        private String hashAlgorithmName;

        /**
         * 密码加密次数：默认1次
         */
        @Getter
        @Setter
        private int hashIterations;

        /**
         * 是否使用redis做session共享：默认不使用
         */
        @Getter
        @Setter
        private boolean useRedis;

        public ShiroConfigParams() {
            this.hashAlgorithmName = "md5";
            this.hashIterations = 1;
            this.useRedis = false;
        }
    }
}

package com.ln.xproject.configuration.web.shiro;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ln.xproject.system.constants.PermissionType;
import com.ln.xproject.system.service.SysPermissionService;
import com.ln.xproject.system.vo.PermsVo;
import com.ln.xproject.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ning on 1/18/17.
 */
@Component
@Slf4j
public class DynamicLoadPermission {
    @Autowired
    private AbstractShiroFilter shiroFilter;
    @Autowired
    private SysPermissionService sysPermissionService;

    private static final String ROOT_URL_PERMS = "kickout,authc";

    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @PostConstruct
    public synchronized void init() {
        log.info("初始化资源权限配置开始！");
        try {
            Map<String, String> pers = loadPerms();
            DefaultFilterChainManager manager = getFilterChainManager();
            addToChain(manager, pers);
        } catch (Exception e) {
            log.error("初始化资源权限配置发生错误！系统即将退出", e);
            System.exit(-1);
        }
        log.info("reload资源权限配置结束！");
    }

    private DefaultFilterChainManager getFilterChainManager() throws Exception {
        // 获取过滤管理器
        PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                .getFilterChainResolver();
        DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
        return manager;
    }

    private void addToChain(DefaultFilterChainManager manager, Map<String, String> definitions) throws Exception {
        if (CollectionUtils.isEmpty(definitions)) {
            return;
        }
        // 移除/**的过滤器链，防止新加的权限不起作用。
        manager.getFilterChains().remove("/**");
        manager.createChain("/**", ROOT_URL_PERMS);
        // 通用权限
        for (Map.Entry<String, String> entry : definitions.entrySet()) {
            String url = entry.getKey();
            if (StringUtils.isBlank(url)) {
                continue;
            }
            String chainDefinition = entry.getValue().trim().replace(" ", "");
            manager.createChain(url, chainDefinition);
        }
        // 上面过滤器如果没法处理，而在此拦截，匹配不到，提示没有权限
        manager.createChain("/**", "perms[\"RWCCFECXOQAWDTFIMIYMLEH1C5WEU2DC\"]");

    }

    /**
     * 动态加载过滤链
     *
     * @return
     */
    public Map<String, String> loadPerms() {
        log.info("===========loadPerms============");
        Map<String, String> resources = new LinkedHashMap<String, String>();

        // 里面的键就是链接URL,值就是存在什么条件才能访问该链接
        try {
            // 获取所有Resource
            List<PermsVo> list = sysPermissionService.findPermsInfo(PermissionType.ACTION, PermissionType.FUNCTION);

            list.forEach(rcd -> {
                if (StringUtils.isNotBlank(rcd.getPermission()) && StringUtils.isNotBlank(rcd.getUrl())) {
                    resources.put(rcd.getUrl(),
                            MessageFormat.format(DynamicLoadPermission.PREMISSION_STRING, rcd.getPermission().trim()));
                }
            });

            log.debug("rersouces:" + resources);
        } catch (Exception e) {
            log.error("获取资源异常，加载过滤链失败,系统即将退出", e);
            System.exit(-1);
        }
        return resources;
    }
}

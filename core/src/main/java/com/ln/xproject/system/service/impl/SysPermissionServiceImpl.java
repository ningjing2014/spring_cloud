package com.ln.xproject.system.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.system.constants.PermissionType;
import com.ln.xproject.system.model.SysPermission;
import com.ln.xproject.system.model.SysRole;
import com.ln.xproject.system.repository.SysPermissionRepository;
import com.ln.xproject.system.service.SysPermissionService;
import com.ln.xproject.system.service.SysRolePermissionService;
import com.ln.xproject.system.service.SysUserRoleService;
import com.ln.xproject.system.vo.MenuPermVo;
import com.ln.xproject.system.vo.PermTreeVo;
import com.ln.xproject.system.vo.PermsVo;
import com.ln.xproject.util.CollectionUtils;
import com.ln.xproject.util.StringUtils;

@Service
@Transactional
public class SysPermissionServiceImpl extends BaseServiceImpl<SysPermission, SysPermissionRepository>
        implements SysPermissionService {

    public static final Long MENU_ROOT_ID = -1L;

    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    @Autowired
    @Override
    protected void setRepository(SysPermissionRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<String> findPermissionByEmail(String email) {
        return repository.findPermissionByEmail(email);
    }

    @Override
    public List<PermsVo> findPermsInfo(PermissionType... permissionType) {
        List<SysPermission> list = repository.findByTypeInOrderByOrderNum(permissionType);
        // 排序，将有统配符的放到最后面，不然后面权限过滤有问题
        list.sort(new Comparator<SysPermission>() {
            @Override
            public int compare(SysPermission o1, SysPermission o2) {
                int count1 = StringUtils.countMatches(o1.getPermission(), "*");
                int count2 = StringUtils.countMatches(o2.getPermission(), "*");
                if (count1 > count2) {
                    return 1;
                } else if (count1 < count2) {
                    return -1;

                }
                return 0;
            }

        });
        List<PermsVo> rst = new ArrayList<>();

        list.forEach(rcd -> {
            rst.add(new PermsVo(rcd.getUrl(), rcd.getPermission()));
        });
        return rst;
    }

    @Override
    public List<PermTreeVo> getPermTree() {
        return getChild(MENU_ROOT_ID, getAllSysPermission()).getChildPerms();
    }

    @Override
    public List<PermTreeVo> getAllSysPermission() {
        List<SysPermission> perms = this.repository.findByTypeInOrderByOrderNum(PermissionType.FUNCTION,
                PermissionType.MENU);
        List<PermTreeVo> total = new ArrayList<>(perms.size());
        perms.forEach(perm -> {
            total.add(buildPermTreeVo(perm));
        });
        return total;
    }

    @Override
    public List<Long> getSysPermissionByRole(Long... roleId) {
        Set<Long> set = CollectionUtils.Array2Set(roleId);
        List<MenuPermVo> menuPerms = sysRolePermissionService.getMenuPermsByRoles(set);
        return build(menuPerms);
    }

    private List<Long> build(List<MenuPermVo> menuPerms) {
        List<Long> rst = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(menuPerms)) {
            menuPerms.forEach(perm -> {
                rst.add(perm.getPermId());
            });
        }
        return rst;
    }

    @Override
    public List<MenuPermVo> getSysPermissionByUser(Long userId) {
        Set<SysRole> userRoles = this.sysUserRoleService.getUserRolelist(userId);
        Set<Long> set = buildSysRoleIds(userRoles);
        List<MenuPermVo> menuPerms = sysRolePermissionService.getMenuPermsByRoles(set);
        return menuPerms;
    }

    private Set<Long> buildSysRoleIds(Set<SysRole> userRoles) {
        Set<Long> set = new HashSet<>();
        userRoles.forEach(userRole -> {
            set.add(userRole.getId());
        });
        return set;
    }

    /**
     * 递归查找子菜单
     *
     * @param id
     *            当前菜单id
     * @param rootMenu
     *            要查找的列表
     * @return
     */
    private PermTreeVo getChild(Long id, List<PermTreeVo> rootMenu) {

        PermTreeVo currentNode = getById(id, rootMenu);
        // 子菜单
        List<PermTreeVo> childList = new ArrayList<>();

        rootMenu.forEach(menu -> {
            if (!ObjectUtils.isEmpty(menu.getParentId())) {
                if (menu.getParentId().equals(id)) {
                    childList.add(getChild(menu.getId(), rootMenu));
                }
            }
        });

        currentNode.setChildPerms(childList);
        return currentNode;
    }

    private PermTreeVo getById(Long id, List<PermTreeVo> rootMenu) {
        for (PermTreeVo menu : rootMenu) {
            if (menu.getId().equals(id)) {
                return menu;
            }
        }
        return null;
    }

    private PermTreeVo buildPermTreeVo(SysPermission sysPermission) {
        PermTreeVo ptVo = new PermTreeVo(sysPermission.getId(), sysPermission.getName(), sysPermission.getUrl(),
                sysPermission.getPermission(), sysPermission.getDescription(), sysPermission.getType().name(),
                sysPermission.getParentId(), sysPermission.getOrderNum());
        return ptVo;
    }

}

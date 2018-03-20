package com.ln.xproject.system.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.system.model.SysPermission;
import com.ln.xproject.system.model.SysRolePermission;
import com.ln.xproject.system.repository.SysRolePermissionRepository;
import com.ln.xproject.system.service.SysPermissionService;
import com.ln.xproject.system.service.SysRolePermissionService;
import com.ln.xproject.system.service.SysRoleService;
import com.ln.xproject.system.vo.MenuPermVo;
import com.ln.xproject.system.vo.PermTreeVo;
import com.ln.xproject.util.CollectionUtils;

@Service
@Transactional
public class SysRolePermissionServiceImpl extends BaseServiceImpl<SysRolePermission, SysRolePermissionRepository>
        implements SysRolePermissionService {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysPermissionService sysPermissionService;

    @Autowired
    @Override
    protected void setRepository(SysRolePermissionRepository repository) {
        super.repository = repository;
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        // 校验角色有效性
        sysRoleService.load(roleId);
        this.repository.deleteByRoleId(roleId);
    }

    @Override
    public void setRolePermission(Long roleId, Set<Long> permissionIds) {
        // 校验角色有效性
        sysRoleService.load(roleId);
        // 删除角色所有的权限
        this.deleteByRoleId(roleId);
        // 添加分配的权限
        permissionIds.forEach(rcd -> {
            this.sysPermissionService.load(rcd);
            SysRolePermission entity = new SysRolePermission();
            entity.setRoleId(roleId);
            entity.setPermissionId(rcd);
            this.save(entity);
        });
    }

    @Override
    public List<MenuPermVo> getMenuPermsByRoles(Set<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<SysPermission> permissions = this.repository.findPermissionByRoleIds(roleIds);
        List<MenuPermVo> rst = new ArrayList<>();
        permissions.forEach(rcd -> {
            rst.add(new MenuPermVo(rcd.getId(), rcd.getName(), rcd.getUrl(), rcd.getPermission(), rcd.getDescription(),
                    rcd.getType().name()));
        });
        return rst;
    }

    public void test() {
        List<PermTreeVo> perms = this.sysPermissionService.getAllSysPermission();
    }

}

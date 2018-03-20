package com.ln.xproject.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.system.model.SysRole;
import com.ln.xproject.system.model.SysUserRole;
import com.ln.xproject.system.repository.SysRoleRepository;
import com.ln.xproject.system.service.SysRolePermissionService;
import com.ln.xproject.system.service.SysRoleService;
import com.ln.xproject.system.service.SysUserRoleService;
import com.ln.xproject.system.vo.RoleVo;
import com.ln.xproject.util.CollectionUtils;

@Service
@Transactional
public class SysRoleServiceImpl extends BaseServiceImpl<SysRole, SysRoleRepository> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    @Autowired
    @Override
    protected void setRepository(SysRoleRepository repository) {
        super.repository = repository;
    }

    @Override
    public void add(String name, String description, Integer orderNum) {
        Assert.notBlank(name, "角色名称不能为空");
        Assert.notBlank(description, "角色描述不能为空");
        Assert.notNull(orderNum, "排序序号不能为空");
        SysRole role = new SysRole();
        role.setDescription(description);
        role.setName(name);
        role.setOrderNum(orderNum);
        this.save(role);
    }

    @Override
    public void update(Long roleId, String name, String description, Integer orderNum) {
        Assert.notNull(roleId, "角色主键不能为空");
        Assert.notBlank(name, "角色名称不能为空");
        Assert.notBlank(description, "角色描述不能为空");
        Assert.notNull(orderNum, "排序序号不能为空");
        SysRole role = this.load(roleId);
        role.setDescription(description);
        role.setName(name);
        role.setOrderNum(orderNum);
        this.update(role);
    }

    @Override
    public void delete(Long roleId) {
        Assert.notNull(roleId, "角色主键不能为空");
        // 查看角色是否存在
        this.load(roleId);
        // 查看是否有用户已分配该角色
        List<SysUserRole> urs = sysUserRoleService.findByRoleId(roleId);
        if (CollectionUtils.isNotEmpty(urs)) {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111010);
        }
        // 删除角色和权限关系
        sysRolePermissionService.deleteByRoleId(roleId);
        // 删除角色
        this.repository.delete(roleId);

    }

    @Override
    public Set<SysRole> getUserRolelist(Long userId) {
        Assert.notNull(userId, "用户主键不能为空");
        return repository.getUserRolelist(userId);
    }

    @Override
    public void deleteRoleByUserId(Long userId) {
        Assert.notNull(userId, "用户主键不能为空");
        repository.deleteRoleByUserId(userId);
    }

    @Override
    public List<RoleVo> getRoleList() {
        List<SysRole> roles = this.findAll();
        List<RoleVo> rst = new ArrayList<>();
        roles.forEach(role -> {
            // １是超级管理员，不应该显示
            if (role.getId() != 1L) {
                rst.add(new RoleVo(role.getId(), role.getName()));
            }
        });
        return rst;
    }
}

package com.example.demo.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.example.demo.entity.SysPermission;
import com.example.demo.model.PermissionQueryParam;

/**
 * @author lei
 * @date 2019/08/16
 */
public interface SysPermissionService {

	/**
	 * 根绝角色ids获取权限集合
	 * 
	 * @param roleIds
	 * @return
	 */
	Set<SysPermission> findByRoleIds(Set<Long> roleIds);

	/**
	 * 保存权限标识
	 * @param sysPermission
	 */
	void save(SysPermission sysPermission);

	/**
	 * 更新权限标识
	 * @param sysPermission
	 */
	void update(SysPermission sysPermission);

	/**
	 * 删除权限标识
	 * @param id
	 */
	void delete(Long id);

	/**
	 * 查询权限标识，分页
	 * @param params
	 * @return
	 */
	Page<SysPermission> findPermissions(PermissionQueryParam params);
	
	List<SysPermission> findAllPermissions();
}

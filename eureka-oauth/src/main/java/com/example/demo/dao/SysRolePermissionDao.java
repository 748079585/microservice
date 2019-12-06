package com.example.demo.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.example.demo.entity.SysRolePermission;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * @author lei
 * @date 2019/08/16
 */
public interface SysRolePermissionDao extends JpaRepository<SysRolePermission, Long> {

	/**
	 * delete by roleId
	 * @param roleId
	 */
	@Modifying
	@Transactional(rollbackFor = SQLException.class)
	void deleteByRoleId(Long roleId);

	/**
	 * delete by permissionId
	 * @param permissionId
	 */
	@Modifying
	@Transactional(rollbackFor = SQLException.class)
	void deleteByPermissionId(Long permissionId);

	/**
	 * delete by roleId and permissionId
	 * @param roleId
	 * @param permissionId
	 */
	@Modifying
	@Transactional(rollbackFor = SQLException.class)
	void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
}

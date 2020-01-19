package com.example.demo.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.SysPermission;

/**
 * @author lei
 * @date 2019/08/16
 */
public interface SysPermissionDao extends JpaRepository<SysPermission, Long>{

	/**
	 * 通过角色id，查询权限
	 * 
	 * @param roleIds
	 * @return
	 */
	@Query(value = "select p.* from sys_permission p inner join sys_role_permission rp on p.id = rp.permission_id where rp.role_id in(:ids)", nativeQuery = true)
	Set<SysPermission> findPermissionsByRoleIds(@Param("ids") Set<Long> roleIds);
	
	SysPermission findByPermission(String permission);
	
	/**
	 * 更具条件查询所有设备并分页
	 * @param specification 条件
	 * @param pageable 分页参数
	 * @return
	 */
	Page<SysPermission> findAll(Specification<SysPermission> specification, Pageable pageable);
}

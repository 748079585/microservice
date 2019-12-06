package com.example.demo.util;

import javax.annotation.PostConstruct;
import javax.xml.crypto.Data;

import com.example.demo.constans.CredentialType;
import com.example.demo.constans.UserType;
import com.example.demo.dao.SysPermissionDao;
import com.example.demo.dao.SysRoleDao;
import com.example.demo.dao.SysRolePermissionDao;
import com.example.demo.dao.SysRoleUserDao;
import com.example.demo.entity.*;
import com.example.demo.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.service.AppUserService;

import java.util.Date;
import java.util.List;

/**
 * @author lei
 * @date 2019/08/16
 */
@Component
public class DataInit {

	@Autowired
	private AppUserService appUserService;

	@Autowired
	private SysPermissionDao sysPermissionDao;

	@Autowired
	private SysRoleDao sysRoleDao;

	@Autowired
	private SysRolePermissionDao sysRolePermissionDao;

	@Autowired
	private SysRoleUserDao sysRoleUserDao;

	/**
	 * 初始化用户角色权限
	 */
	@PostConstruct
	public void setAdminUser() {
		String name = "admin";
		AppUser user = appUserService.findByName(name);
		if(user == null) {
			System.out.println("初始化用户角色权限数据");
			Date now = new Date();

			// 初始化一个管理员用户
			AppUser appUser = new AppUser();
			appUser.setUsername("admin");
			appUser.setPassword("admin");
			// 登录类型，用户名密码登录
			appUser.setType(CredentialType.USERNAME.name());
			appUser.setCreateTime(now);
			appUser.setUpdateTime(now);
			appUser.setEnabled(true);
			appUserService.addAppUser(appUser);

			// 初始化权限
			// 遍历权限枚举类中的规定好的权限，然后保存到数据库
			for (Permission p : Permission.values()){
				SysPermission sysPermission = new SysPermission();
				sysPermission.setPermission(p.getCode());
				sysPermission.setName(p.getMessage());
				sysPermission.setCreateTime(now);
				sysPermission.setUpdateTime(now);
				sysPermissionDao.save(sysPermission);
			}

			// 初始化一个超级管理员角色
			SysRole role = new SysRole();
			role.setName("超级管理员");
			role.setCode("SUPER_ADMIN");
			role.setCreateTime(now);
			role.setUpdateTime(now);
			sysRoleDao.save(role);

			// 初始化一个普通用户角色
			SysRole role1 = new SysRole();
			role1.setName("普通用户");
			role1.setCode(UserType.APP.name());
			role1.setCreateTime(now);
			role1.setUpdateTime(now);
			sysRoleDao.save(role1);

			// 初始化配置admin用户的超级管理员角色
			SysRoleUser sysRoleUser = new SysRoleUser();
			sysRoleUser.setRoleId(role.getId());
			sysRoleUser.setUserId(appUser.getId());
			sysRoleUserDao.save(sysRoleUser);

			List<SysPermission> sysPermissions = sysPermissionDao.findAll();
			for (SysPermission sp: sysPermissions) {
				// 初始化配置超级管理员角色的所有权限功能
				SysRolePermission sysRolePermission = new SysRolePermission();
				sysRolePermission.setRoleId(role.getId());
				sysRolePermission.setPermissionId(sp.getId());
				sysRolePermissionDao.save(sysRolePermission);

				// 普通用户的具体权限通过接口去设置，默认先配置只拥有查询的权限
				if(sp.getName().startsWith("查询")){
					SysRolePermission sysRolePermission1 = new SysRolePermission();
					sysRolePermission1.setRoleId(role1.getId());
					sysRolePermission1.setPermissionId(sp.getId());
					sysRolePermissionDao.save(sysRolePermission1);
				}

			}

		}

	}
	
}

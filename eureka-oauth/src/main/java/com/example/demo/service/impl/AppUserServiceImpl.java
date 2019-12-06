package com.example.demo.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.vo.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.demo.constans.UserType;
import com.example.demo.dao.AppUserDao;
import com.example.demo.dao.SysRoleDao;
import com.example.demo.dao.SysRoleUserDao;
import com.example.demo.entity.AppUser;
import com.example.demo.entity.LoginAppUser;
import com.example.demo.entity.SysPermission;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysRoleUser;
import com.example.demo.service.AppUserService;
import com.example.demo.service.SysPermissionService;
import com.example.demo.util.PhoneUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lei
 * @date 2019/08/16
 */
@Slf4j
@Service
public class AppUserServiceImpl implements AppUserService {

	@Autowired
	private AppUserDao appUserDao;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private SysPermissionService sysPermissionService;
	@Autowired
	private SysRoleUserDao userRoleDao;
	@Autowired
	private SysRoleDao sysRoleDao;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void addAppUser(AppUser appUser) {
		String username = appUser.getUsername();
		if (StringUtils.isBlank(username)) {
			throw new IllegalArgumentException("用户名不能为空");
		}

		// 防止用手机号直接当用户名，手机号要发短信验证
		if (PhoneUtil.checkPhone(username)) {
			throw new IllegalArgumentException("用户名要包含英文字符");
		}

		// 防止用邮箱直接当用户名，邮箱也要发送验证（暂未开发）
		String illegal1 = "@";
		if (username.contains(illegal1)) {
			throw new IllegalArgumentException("用户名不能包含@");
		}

		String illegal2 = "|";
		if (username.contains(illegal2)) {
			throw new IllegalArgumentException("用户名不能包含|字符");
		}

		if (StringUtils.isBlank(appUser.getPassword())) {
			throw new IllegalArgumentException("密码不能为空");
		}

		if (StringUtils.isBlank(appUser.getNickname())) {
			appUser.setNickname(username);
		}

		if (StringUtils.isBlank(appUser.getType())) {
			appUser.setType(UserType.APP.name());
		}

		appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
		appUser.setEnabled(Boolean.TRUE);
		appUser.setCreateTime(new Date());
		appUser.setUpdateTime(appUser.getCreateTime());

		appUserDao.save(appUser);
		log.info("添加用户：{}", appUser);
	}

	@Override
	public AppUser addUserInfo(UserInfo userInfo) {
		AppUser appUser = appUserDao.findByUsername(userInfo.getOpenId());
		if(appUser == null){
			appUser = new AppUser();
			// 设置openID为用户名 , 密码为空，第三方登录没有密码，但底层需要密码验证，因此在UserDetailServiceImpl中配置code为密码
			appUser.setUsername(userInfo.getOpenId());
			appUser.setSex(userInfo.getGender());
			appUser.setEnabled(true);
			Date now = new Date();
			appUser.setCreateTime(now);
			appUser.setUpdateTime(now);
			appUser.setHeadImgUrl(userInfo.getAvatar());
			appUser.setType(userInfo.getType());
			appUserDao.save(appUser);
			//  这里在先给该用户设置一个app角色
			SysRole role = sysRoleDao.findByCode(UserType.APP.name());
			userRoleDao.save(new SysRoleUser(null, appUser.getId(), role.getId()));
		}
		return appUser;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateAppUser(AppUser appUser) {
		appUser.setUpdateTime(new Date());
		Optional<AppUser> optional = appUserDao.findById(appUser.getId());
		if(optional.isPresent()){
			AppUser user = optional.get();
			if (appUser.getPassword() != null && !appUser.getPassword().equals("")) {
				user.setPassword(appUser.getPassword());
			}
			if (appUser.getNickname() != null && !appUser.getNickname().equals("")) {
				user.setNickname(appUser.getNickname());
			}
			if (appUser.getHeadImgUrl() != null && !appUser.getHeadImgUrl().equals("")) {
				user.setHeadImgUrl(appUser.getHeadImgUrl());
			}
			if (appUser.getPhone() != null && !appUser.getPhone().equals("")) {
				user.setPhone(appUser.getPhone());
			}
			if (appUser.getSex() != null) {
				user.setSex(appUser.getSex());
			}
			if (appUser.getEnabled() != null) {
				user.setEnabled(appUser.getEnabled());
			}
			appUserDao.save(appUser);
			log.info("修改用户：{}", appUser);
		}

	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public LoginAppUser findByUsername(String username) {
		AppUser appUser = appUserDao.findByUsername(username);
		if (appUser != null) {
			LoginAppUser loginAppUser = new LoginAppUser();
			BeanUtils.copyProperties(appUser, loginAppUser);

			Set<SysRole> sysRoles = sysRoleDao.findRolesByUserId(appUser.getId());
			// 设置角色
			loginAppUser.setSysRoles(sysRoles);

			if (!CollectionUtils.isEmpty(sysRoles)) {
				Set<Long> roleIds = sysRoles.parallelStream().map(SysRole::getId).collect(Collectors.toSet());
				Set<SysPermission> sysPermissions = sysPermissionService.findByRoleIds(roleIds);
				if (!CollectionUtils.isEmpty(sysPermissions)) {
					Set<String> permissions = sysPermissions.parallelStream().map(SysPermission::getPermission)
							.collect(Collectors.toSet());
					// 设置权限集合
					loginAppUser.setPermissions(permissions);
				}

			}

			return loginAppUser;
		}

		return null;
	}

	@Override
	public AppUser findById(Long id) {
		Optional<AppUser> optional = appUserDao.findById(id);
		if (!optional.isPresent()){
			return null;
		}
		return optional.get();
	}

	/**
	 * 给用户设置角色<br>
	 * 这里采用先删除老角色，再插入新角色
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void setRoleToUser(Long id, Set<Long> roleIds) {
		Optional<AppUser> optional = appUserDao.findById(id);
		if (!optional.isPresent()) {
			throw new IllegalArgumentException("用户不存在");
		} else {
			userRoleDao.deleteByUserId(id);
			if (!CollectionUtils.isEmpty(roleIds)) {
				roleIds.forEach(roleId -> {
					userRoleDao.save(new SysRoleUser(null, id, roleId));
				});
			}
			log.info("修改用户：{}的角色，{}", optional.get().getUsername(), roleIds);
		}

	}

	/**
	 * 修改密码
	 *
	 * @param id
	 * @param oldPassword
	 * @param newPassword
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updatePassword(Long id, String oldPassword, String newPassword) {
		AppUser appUser = appUserDao.findById(id).get();
		if (StringUtils.isNoneBlank(oldPassword)) {
		 // 旧密码校验
			if (!passwordEncoder.matches(oldPassword, appUser.getPassword())) { 
				throw new IllegalArgumentException("旧密码错误");
			}
		}
		appUser.setPassword(passwordEncoder.encode(newPassword)); 

		appUserDao.save(appUser);
		log.info("修改密码：{}", appUser);
	}

	@Override
	public Page<AppUser> findUsers(Map<String, Object> params) {
		Sort sort = new Sort(Direction.DESC, "id");
		PageRequest pageable = PageRequest.of(Integer.parseInt((String) params.get("start")), Integer.parseInt((String) params.get("lenth")), sort);
		Page<AppUser> list = appUserDao.findAll(pageable);
		return list;
	}

	@Override
	public Set<SysRole> findRolesByUserId(Long userId) {
		return sysRoleDao.findRolesByUserId(userId);
	}

	@Override
	public AppUser findByName(String name) {
		return appUserDao.findByUsername(name);
	}


}

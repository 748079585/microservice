package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.SysPermissionDao;
import com.example.demo.dao.SysRolePermissionDao;
import com.example.demo.entity.SysPermission;
import com.example.demo.model.PermissionQueryParam;
import com.example.demo.service.SysPermissionService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lei
 * @date 2019/08/16
 */
@Slf4j
@Service
public class SysPermissionServiceImpl implements SysPermissionService {

	@Autowired
	private SysPermissionDao sysPermissionDao;
	@Autowired
	private SysRolePermissionDao rolePermissionDao;

	@Override
	public Set<SysPermission> findByRoleIds(Set<Long> roleIds) {
		return sysPermissionDao.findPermissionsByRoleIds(roleIds);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void save(SysPermission sysPermission) {
		SysPermission permission = sysPermissionDao.findByPermission(sysPermission.getPermission());
		if (permission != null) {
			throw new IllegalArgumentException("权限标识已存在");
		}
		sysPermission.setCreateTime(new Date());
		sysPermission.setUpdateTime(sysPermission.getCreateTime());

		sysPermissionDao.save(sysPermission);
		log.info("保存权限标识：{}", sysPermission);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void update(SysPermission sysPermission) {
		log.info("修改权限标识：{}", sysPermission);
		if(sysPermission.getId() != null) {
			Optional<SysPermission> optional = sysPermissionDao.findById(sysPermission.getId());
			if (optional.isPresent()) {
				SysPermission newPermission = optional.get();
				newPermission.setName(sysPermission.getName());
				newPermission.setPermission(sysPermission.getPermission());
				newPermission.setUpdateTime(new Date());
				sysPermissionDao.save(newPermission);
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(Long id) {
		Optional<SysPermission> optional = sysPermissionDao.findById(id);
		if (!optional.isPresent()) {
			throw new IllegalArgumentException("权限标识不存在");
		}

		sysPermissionDao.deleteById(id);
		rolePermissionDao.deleteByPermissionId(id);
		log.info("删除权限标识：{}", optional.get());
	}

	@Override
	public Page<SysPermission> findPermissions(PermissionQueryParam params) {
		System.out.println("params:" + params);
		Specification<SysPermission> specification = new Specification<SysPermission>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7952569449682207566L;

			@Override
			public Predicate toPredicate(Root<SysPermission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (params.getName() != null && params.getName() != "") {
					Predicate name = cb.in(root.get("name")).value(params.getName());
					predicates.add(name);
				}
				if (params.getPermission() != null && params.getPermission() != "") {
					Predicate name = cb.in(root.get("permission")).value(params.getPermission());
					predicates.add(name);
				}
				return cb.and(predicates.toArray(new Predicate[] {}));
			}
		};
		PageRequest pageable = PageRequest.of(params.getPage() - 1, params.getLimit());
		if (params.getSort() != null && params.getSort().equals("+id")) {
			pageable = PageRequest.of(params.getPage() - 1, params.getLimit(), new Sort(Direction.ASC, "id"));
		} else if (params.getSort() != null && params.getSort().equals("-id")) {
			pageable = PageRequest.of(params.getPage() - 1, params.getLimit(), new Sort(Direction.DESC, "id"));
		}
		Page<SysPermission> findAll = sysPermissionDao.findAll(specification, pageable);
		return findAll;
	}

	@Override
	public List<SysPermission> findAllPermissions() {
		return sysPermissionDao.findAll();
	}
}

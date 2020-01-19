package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class PermissionQueryParam {

	private int page;
	
	private int limit;
	
	/**
	 * 权限表 permission 查询
	 */
	private String permission;
	
	/**
	 * 权限表 name 查询
	 */
	private String name;
	
	/**
	 * 目前只通过ID排序，前端只传 +id 、 -id
	 * +id : id 递增排序
	 * -id : id 递减排序
	 */
	private String sort;
}

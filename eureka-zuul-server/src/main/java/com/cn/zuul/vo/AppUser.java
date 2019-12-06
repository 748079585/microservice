package com.cn.zuul.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lei
 * @date 2019/08/16
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppUser implements Serializable {

	private static final long serialVersionUID = 611197991672067628L;

	private Long id;
	private String username;
	private String password;
	private String nickname;
	private String headImgUrl;
	private String phone;
	/**
	 * 普通用户性别，1为男性，2为女性
	 */
	private Integer sex;
	/**
	 * 状态
	 */
	private Boolean enabled;
	/**
	 * 用户类型
	 */
	private String type;
	private Date createTime;
	private Date updateTime;

}

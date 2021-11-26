package com.flagwind.mybatis.entity;

import com.flagwind.mybatis.entity.codes.Sex;
import com.flagwind.mybatis.entity.codes.UserType;
import com.flagwind.mybatis.tenant.Tenant;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;


@Tenant
@Data
@Entity
@Table(name = "com_user")
public class User {
	@Id
	@Column(name = "Id")
	private String id;
	@Column(name = "sex")
	private Sex sexType;

	@Column(name = "sex")
	private UserType type;

	private String name;

	private String username;

	private String password;

	private String creator;
	private String modifier;
	@Column(name = "createTime")
	private Timestamp createTime;
	@Column(name = "modifyTime")
	private Timestamp modifyTime;

	private boolean disabled;
	@Column(name = "tenantId")
	private String tenantId;


}
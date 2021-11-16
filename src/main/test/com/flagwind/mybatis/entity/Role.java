package com.flagwind.mybatis.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;


//@Tenant
@Data
@Entity
@Table(name = "com_role")
public class Role
{
	@Id
	@Column(name = "Id")
	private String id;
	@Column(name = "name")
	private String name;
	@Column(name = "description")
	private String description;
//	@Column(name = "icon")
//	private String icon;
	@Column(name = "creator")
	private String creator;
//	@Column(name = "modifier")
//	private String modifier;
	@Column(name = "createTime")
	private Timestamp createTime;
//	@Column(name = "modifyTime")
//	private Timestamp modifyTime;
	@Column(name = "disabled")
	private boolean disabled;
//	@Column(name = "tenantId")
//	private String tenantId;

}

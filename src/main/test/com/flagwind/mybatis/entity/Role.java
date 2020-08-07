package com.flagwind.mybatis.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "com_role")
public class Role
{
	@Id
	@Column(name = "Id")
	private String id;

	private String name;

	private String description;
	private String icon;

	private String creator;
	private String modifier;
	private Timestamp createTime;
	private Timestamp modifyTime;
	private boolean disabled;

}

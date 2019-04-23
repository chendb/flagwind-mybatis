package com.flagwind.mybatis.entity;

import javax.persistence.*;

@Entity
@Table(name = "com_appmenu")

public class AppMenu
{

	@Id
	@Column(name = "Id")
	@GeneratedValue(generator = "UUID")
	private String id;


	@Column(name = "Name")
	private String name;


	@Column(name = "url")
	private String url;


	@Column(name = "icon")
	private String icon;


	@Column(name = "parentId")
	private String parentId;


	@Column(name = "sort")
	private Integer sort;


	@Column(name = "permit")
	private String permit;


	@Column(name = "phonetic")
	private String phonetic;


	public String getId()
	{
		return id;
	}


	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getIcon()
	{
		return icon;
	}

	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public Integer getSort()
	{
		return sort;
	}

	public void setSort(Integer sort)
	{
		this.sort = sort;
	}

	public String getPermit()
	{
		return permit;
	}

	public void setPermit(String permit)
	{
		this.permit = permit;
	}

	public String getPhonetic()
	{
		return phonetic;
	}

	public void setPhonetic(String phonetic)
	{
		this.phonetic = phonetic;
	}
}
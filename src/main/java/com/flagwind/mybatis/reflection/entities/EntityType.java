package com.flagwind.mybatis.reflection.entities;

import java.util.ArrayList;

public class EntityType
{
	private String name;
	private Class instanceType;
	private ArrayList<EntityField> fields = new ArrayList<>();

	public EntityType(Class instanceType)
	{
		this.instanceType = instanceType;
		this.name = instanceType.getName();
	}

	public ArrayList<EntityField> getFields()
	{
		return fields;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Class getInstanceType()
	{
		return instanceType;
	}

	public void setInstanceType(Class instanceType)
	{
		this.instanceType = instanceType;
	}

	public <T> T createInstance() throws Exception
	{
		return (T) getInstanceType().newInstance();
	}
}

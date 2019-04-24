package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.IdentityDialect;
import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.reflect.entities.EntityField;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

public class GeneratedValueAnnotationProcessor implements ColumnProcessor
{
	@Override
	public void process(EntityColumn entityColumn, EntityField field, Style style)
	{
		if (field.isAnnotationPresent(GeneratedValue.class)) {
			GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
			if (generatedValue.generator().equals("UUID")) {
				entityColumn.setUuid(true);
			} else if (generatedValue.generator().equals("JDBC")) {
				entityColumn.setIdentity(true);
				entityColumn.setGenerator("JDBC");
				entityColumn.getTable().setKeyProperties(entityColumn.getProperty());
				entityColumn.getTable().setKeyColumns(entityColumn.getColumn());
			} else {
				//允许通过generator来设置获取id的sql,例如mysql=CALL IDENTITY(),hsqldb=SELECT SCOPE_IDENTITY()
				//允许通过拦截器参数设置公共的generator
				if (generatedValue.strategy() == GenerationType.IDENTITY) {
					//mysql的自动增长
					entityColumn.setIdentity(true);
					if (!generatedValue.generator().equals("")) {
						String generator = null;
						IdentityDialect identityDialect = IdentityDialect.parse(generatedValue.generator());
						if (identityDialect != null) {
							generator = identityDialect.getIdentityRetrievalStatement();
						} else {
							generator = generatedValue.generator();
						}
						entityColumn.setGenerator(generator);
					}
				} else {
					throw new MapperException(field.getName()
							+ " - 该字段@GeneratedValue配置只允许以下几种形式:" +
							"\n1.全部数据库通用的@GeneratedValue(generator=\"UUID\")" +
							"\n2.useGeneratedKeys的@GeneratedValue(generator=\\\"JDBC\\\")  " +
							"\n3.类似mysql数据库的@GeneratedValue(strategy=GenerationType.IDENTITY[,generator=\"Mysql\"])");
				}
			}
		}
	}
}
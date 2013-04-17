package com.liferay.lcs.service.test;

import org.mockito.Mockito;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author Ivica Cardic
 */
public class MockitoFactoryBean<T> implements FactoryBean<T> {

	public T getObject() throws Exception {
		return Mockito.mock(classToBeMocked);
	}

	public Class<?> getObjectType() {
		return classToBeMocked;
	}

	public boolean isSingleton() {
		return true;
	} public MockitoFactoryBean(Class<T> classToBeMocked) {
		this.classToBeMocked = classToBeMocked;
	}

	private Class<T> classToBeMocked;

}
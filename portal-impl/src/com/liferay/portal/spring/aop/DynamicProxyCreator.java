/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.aop;

import com.liferay.portal.kernel.spring.aop.InvocationHandlerFactory;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

/**
 * @author Shuyang Zhou
 */
public class DynamicProxyCreator implements BeanPostProcessor, Ordered {

	public static DynamicProxyCreator getDynamicProxyCreator() {
		return _dynamicProxyCreator;
	}

	public void clear() {
		_beanMatcherInvocationHandlerFactories.clear();
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
		throws BeansException {

		Class<?> beanClass = bean.getClass();

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		for (ObjectValuePair<BeanMatcher, InvocationHandlerFactory>
				objectValuePair : _beanMatcherInvocationHandlerFactories) {

			BeanMatcher beanMatcher = objectValuePair.getKey();

			if (!beanMatcher.match(beanClass, beanName)) {
				continue;
			}

			InvocationHandlerFactory invocationHandlerFactory =
				objectValuePair.getValue();

			InvocationHandler invocationHandler =
				invocationHandlerFactory.createInvocationHandler(bean);

			bean = ProxyUtil.newProxyInstance(
				contextClassLoader, beanClass.getInterfaces(),
				invocationHandler);
		}

		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(
		Object bean, String beanName) {

		return bean;
	}

	public static class Register {

		public Register(
			BeanMatcher beanMatcher,
			InvocationHandlerFactory invocationHandlerFactory) {

			ObjectValuePair<BeanMatcher, InvocationHandlerFactory>
				objectValuePair = new ObjectValuePair<>(
					beanMatcher, invocationHandlerFactory);

			_dynamicProxyCreator._beanMatcherInvocationHandlerFactories.add(
				objectValuePair);
		}

	}

	private static final DynamicProxyCreator _dynamicProxyCreator =
		new DynamicProxyCreator();

	private final List<ObjectValuePair<BeanMatcher, InvocationHandlerFactory>>
		_beanMatcherInvocationHandlerFactories = new ArrayList<>();

}
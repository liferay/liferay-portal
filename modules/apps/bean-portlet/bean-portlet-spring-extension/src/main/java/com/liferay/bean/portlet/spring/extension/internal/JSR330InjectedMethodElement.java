/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal;

import java.beans.PropertyDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * @author Neil Griffin
 */
public class JSR330InjectedMethodElement
	extends InjectionMetadata.InjectedElement {

	public JSR330InjectedMethodElement(
		ConfigurableListableBeanFactory configurableListableBeanFactory,
		Method method, PropertyDescriptor propertyDescriptor,
		boolean required) {

		super(method, propertyDescriptor);

		_configurableListableBeanFactory = configurableListableBeanFactory;
		_required = required;
	}

	@Override
	protected void inject(
			Object beanInstance, @Nullable String beanName,
			@Nullable PropertyValues propertyValues)
		throws Throwable {

		if (checkPropertySkipping(propertyValues)) {
			return;
		}

		Object[] dependencies = null;
		Method method = (Method)getMember();

		if (_cached) {
			dependencies = _resolveCachedDependencies(beanName);
		}
		else {
			Class<?>[] parameterTypes = method.getParameterTypes();

			dependencies = new Object[parameterTypes.length];
			DependencyDescriptor[] dependencyDescriptors =
				new DependencyDescriptor[parameterTypes.length];

			Set<String> jsr330Beans = new LinkedHashSet<>();

			Assert.state(
				_configurableListableBeanFactory != null,
				"Bean factory is unavailable");

			TypeConverter typeConverter =
				_configurableListableBeanFactory.getTypeConverter();

			for (int i = 0; i < dependencies.length; i++) {
				MethodParameter methodParameter = new MethodParameter(
					method, i);

				DependencyDescriptor dependencyDescriptor =
					new DependencyDescriptor(methodParameter, _required);

				dependencyDescriptor.setContainingClass(
					beanInstance.getClass());

				dependencyDescriptors[i] = dependencyDescriptor;

				try {
					Object dependency =
						_configurableListableBeanFactory.resolveDependency(
							dependencyDescriptor, beanName, jsr330Beans,
							typeConverter);

					if ((dependency == null) && !_required) {
						dependencies = null;

						break;
					}

					dependencies[i] = dependency;
				}
				catch (BeansException beansException) {
					throw new UnsatisfiedDependencyException(
						null, beanName, new InjectionPoint(methodParameter),
						beansException);
				}
			}

			synchronized (this) {
				if (!_cached) {
					if (dependencies != null) {
						Object[] cachedDependencies =
							new Object[parameterTypes.length];

						System.arraycopy(
							dependencyDescriptors, 0, cachedDependencies, 0,
							dependencies.length);

						AutowiredUtil.registerBeans(
							beanName, jsr330Beans,
							_configurableListableBeanFactory);

						if (jsr330Beans.size() == parameterTypes.length) {
							Iterator<String> iterator = jsr330Beans.iterator();

							for (int i = 0; i < parameterTypes.length; i++) {
								String autowiredBeanName = iterator.next();

								if (!_configurableListableBeanFactory.
										containsBean(autowiredBeanName) ||
									!_configurableListableBeanFactory.
										isTypeMatch(
											autowiredBeanName,
											parameterTypes[i])) {

									continue;
								}

								cachedDependencies[i] =
									new JSR330DependencyDescriptor(
										autowiredBeanName,
										dependencyDescriptors[i],
										parameterTypes[i]);
							}
						}

						_cachedDependencies = cachedDependencies;
					}
					else {
						_cachedDependencies = null;
					}

					_cached = true;
				}
			}
		}

		if (dependencies != null) {
			try {
				ReflectionUtils.makeAccessible(method);

				method.invoke(beanInstance, dependencies);
			}
			catch (InvocationTargetException invocationTargetException) {
				throw invocationTargetException.getTargetException();
			}
		}
	}

	private Object[] _resolveCachedDependencies(String beanName) {
		Object[] cachedDependencies = _cachedDependencies;

		if (cachedDependencies == null) {
			return null;
		}

		Object[] resolvedCachedDependencies =
			new Object[cachedDependencies.length];

		for (int i = 0; i < resolvedCachedDependencies.length; i++) {
			resolvedCachedDependencies[i] = AutowiredUtil.resolveDependency(
				_configurableListableBeanFactory, beanName,
				cachedDependencies[i]);
		}

		return resolvedCachedDependencies;
	}

	private volatile boolean _cached;
	private volatile Object[] _cachedDependencies;
	private final ConfigurableListableBeanFactory
		_configurableListableBeanFactory;
	private final boolean _required;

}
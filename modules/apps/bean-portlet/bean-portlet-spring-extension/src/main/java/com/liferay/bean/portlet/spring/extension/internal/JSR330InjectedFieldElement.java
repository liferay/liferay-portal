/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

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
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * @author Neil Griffin
 */
public class JSR330InjectedFieldElement
	extends InjectionMetadata.InjectedElement {

	public JSR330InjectedFieldElement(
		ConfigurableListableBeanFactory configurableListableBeanFactory,
		Field field, boolean required) {

		super(field, null);

		_configurableListableBeanFactory = configurableListableBeanFactory;
		_required = required;
	}

	@Override
	protected void inject(
			Object beanInstance, String beanName, PropertyValues propertyValues)
		throws Throwable {

		Field field = (Field)getMember();
		Object value = null;

		if (_cached) {
			value = AutowiredUtil.resolveDependency(
				_configurableListableBeanFactory, beanName, _cachedFieldValue);
		}
		else {
			DependencyDescriptor dependencyDescriptor =
				new DependencyDescriptor(field, _required);

			dependencyDescriptor.setContainingClass(beanInstance.getClass());

			Set<String> autowiredBeanNames = new LinkedHashSet<>();

			Assert.state(
				_configurableListableBeanFactory != null,
				"Bean factory is unavailable");

			TypeConverter typeConverter =
				_configurableListableBeanFactory.getTypeConverter();

			boolean nullInjectionPermitted = false;

			Annotation[] annotations = field.getAnnotations();

			for (Annotation annotation : annotations) {
				Class<? extends Annotation> annotationType =
					annotation.annotationType();

				String name = annotationType.getName();

				if (name.equals("jakarta.ws.rs.CookieParam") ||
					name.equals("jakarta.ws.rs.FormParam") ||
					name.equals("jakarta.ws.rs.HeaderParam") ||
					name.equals("jakarta.ws.rs.MatrixParam") ||
					name.equals("jakarta.ws.rs.PathParam") ||
					name.equals("jakarta.ws.rs.QueryParam")) {

					nullInjectionPermitted = true;

					break;
				}
			}

			try {
				value = _configurableListableBeanFactory.resolveDependency(
					dependencyDescriptor, beanName, autowiredBeanNames,
					typeConverter);
			}
			catch (BeansException beansException) {
				if (nullInjectionPermitted) {
					value = null;
				}
				else {
					throw new UnsatisfiedDependencyException(
						null, beanName, new InjectionPoint(field),
						beansException);
				}
			}

			synchronized (this) {
				if (!_cached) {
					if ((value != null) || _required) {
						_cachedFieldValue = dependencyDescriptor;
						AutowiredUtil.registerBeans(
							beanName, autowiredBeanNames,
							_configurableListableBeanFactory);

						if (autowiredBeanNames.size() == 1) {
							Iterator<String> iterator =
								autowiredBeanNames.iterator();

							String autowiredBeanName = iterator.next();

							if (_configurableListableBeanFactory.containsBean(
									autowiredBeanName) &&
								_configurableListableBeanFactory.isTypeMatch(
									autowiredBeanName, field.getType())) {

								_cachedFieldValue =
									new JSR330DependencyDescriptor(
										autowiredBeanName, dependencyDescriptor,
										field.getType());
							}
						}
					}
					else {
						_cachedFieldValue = null;
					}

					if (!nullInjectionPermitted) {
						_cached = true;
					}
				}
			}
		}

		if (value != null) {
			ReflectionUtils.makeAccessible(field);

			Class<?> valueClass = value.getClass();

			String valueClassName = valueClass.getName();

			if (valueClassName.equals(
					"org.springframework.beans.factory.support.NullBean")) {

				value = null;
			}

			field.set(beanInstance, value);
		}
	}

	private volatile boolean _cached;
	private volatile Object _cachedFieldValue;
	private final ConfigurableListableBeanFactory
		_configurableListableBeanFactory;
	private final boolean _required;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import jakarta.mvc.binding.BindingResult;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Neil Griffin
 */
public class BeanUtil {

	public static <T> List<T> getBeanInstances(
		BeanManager beanManager, Class<T> clazz, Annotation... qualifiers) {

		List<T> beanInstances = new ArrayList<>();

		if (qualifiers == null) {
			qualifiers = new Annotation[0];
		}

		Set<Bean<?>> beans = beanManager.getBeans(clazz, qualifiers);

		for (Bean<?> bean : beans) {
			beanInstances.add(
				clazz.cast(
					beanManager.getReference(
						bean, clazz,
						beanManager.createCreationalContext(bean))));
		}

		return beanInstances;
	}

	public static MutableBindingResult getMutableBindingResult(
		BeanManager beanManager) {

		Bean<?> bean = beanManager.resolve(
			beanManager.getBeans(BindingResult.class));

		BindingResult bindingResult = (BindingResult)beanManager.getReference(
			bean, BindingResult.class,
			beanManager.createCreationalContext(bean));

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(
				bindingResult.getClass());

			PropertyDescriptor[] propertyDescriptors =
				beanInfo.getPropertyDescriptors();

			Object targetInstance = null;

			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String propertyDescriptorName = propertyDescriptor.getName();

				if (propertyDescriptorName.equals("targetInstance")) {
					Method method = propertyDescriptor.getReadMethod();

					targetInstance = method.invoke(bindingResult);
				}
			}

			if (targetInstance instanceof MutableBindingResult) {
				return (MutableBindingResult)targetInstance;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(BeanUtil.class);

}
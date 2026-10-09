/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal;

import java.util.Set;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.util.Assert;

/**
 * @author Neil Griffin
 */
public class AutowiredUtil {

	public static void registerBeans(
		String beanName, Set<String> beanNames,
		ConfigurableBeanFactory configurableBeanFactory) {

		if (beanName != null) {
			for (String curBeanName : beanNames) {
				if ((configurableBeanFactory == null) ||
					!configurableBeanFactory.containsBean(curBeanName)) {

					continue;
				}

				configurableBeanFactory.registerDependentBean(
					curBeanName, beanName);
			}
		}
	}

	public static Object resolveDependency(
		AutowireCapableBeanFactory autowireCapableBeanFactory, String beanName,
		Object dependency) {

		if (dependency instanceof DependencyDescriptor) {
			DependencyDescriptor descriptor = (DependencyDescriptor)dependency;

			Assert.state(
				autowireCapableBeanFactory != null,
				"Bean factory is unavailable");

			return autowireCapableBeanFactory.resolveDependency(
				descriptor, beanName, null, null);
		}

		return dependency;
	}

}
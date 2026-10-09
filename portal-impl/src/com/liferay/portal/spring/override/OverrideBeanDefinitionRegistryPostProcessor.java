/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.override;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @author Tina Tian
 */
public class OverrideBeanDefinitionRegistryPostProcessor
	implements BeanDefinitionRegistryPostProcessor {

	public OverrideBeanDefinitionRegistryPostProcessor(Properties properties) {
		_properties = properties;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry beanDefinitionRegistry)
		throws BeansException {

		for (String key : _properties.stringPropertyNames()) {
			BeanDefinition beanDefinition =
				beanDefinitionRegistry.getBeanDefinition(key);

			if (beanDefinition == null) {
				continue;
			}

			beanDefinition.setBeanClassName(_properties.getProperty(key));
		}
	}

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory configurableListableBeanFactory)
		throws BeansException {
	}

	private final Properties _properties;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.PortletAsyncListener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.jsp.JspApplicationContext;
import jakarta.servlet.jsp.JspFactory;

import java.util.Objects;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.ServletContextAware;

/**
 * @author Neil Griffin
 */
@Configuration
public class SpringPostProcessor
	implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor,
			   ServletContextAware {

	@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry beanDefinitionRegistry)
		throws BeansException {

		String[] beanDefinitionNames =
			beanDefinitionRegistry.getBeanDefinitionNames();

		for (String beanDefinitionName : beanDefinitionNames) {
			if (beanDefinitionName.startsWith("scopedTarget.")) {
				continue;
			}

			BeanDefinition beanDefinition =
				beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);

			String beanDefinitionScope = beanDefinition.getScope();

			if (Objects.equals(beanDefinitionScope, "globalSession") ||
				Objects.equals(beanDefinitionScope, "portletRequest") ||
				Objects.equals(beanDefinitionScope, "portletAppSession") ||
				Objects.equals(beanDefinitionScope, "portletSession") ||
				Objects.equals(beanDefinitionScope, "renderState") ||
				Objects.equals(beanDefinitionScope, "request") ||
				Objects.equals(beanDefinitionScope, "session")) {

				beanDefinitionRegistry.removeBeanDefinition(beanDefinitionName);

				if (Objects.equals(beanDefinitionScope, "globalSession")) {
					beanDefinition.setScope("portletAppSession");
				}
				else if (Objects.equals(beanDefinitionScope, "request")) {
					beanDefinition.setScope("portletRequest");
				}
				else if (Objects.equals(beanDefinitionScope, "session")) {
					beanDefinition.setScope("portletSession");
				}

				BeanDefinitionHolder definitionHolder =
					new BeanDefinitionHolder(
						beanDefinition, beanDefinitionName);

				ScopeMetadata scopeMetadata = new ScopeMetadata();

				scopeMetadata.setScopeName(beanDefinitionScope);
				scopeMetadata.setScopedProxyMode(ScopedProxyMode.TARGET_CLASS);

				BeanDefinitionHolder scopedProxy =
					ScopedProxyUtils.createScopedProxy(
						definitionHolder, beanDefinitionRegistry, true);

				BeanDefinitionReaderUtils.registerBeanDefinition(
					scopedProxy, beanDefinitionRegistry);
			}
			else {
				String beanClassName = beanDefinition.getBeanClassName();

				if (beanClassName != null) {
					try {
						Class<?> beanClass = Class.forName(beanClassName);

						if (PortletAsyncListener.class.isAssignableFrom(
								beanClass)) {

							beanDefinitionRegistry.removeBeanDefinition(
								beanDefinitionName);

							beanDefinition.setLazyInit(true);

							BeanDefinitionHolder definitionHolder =
								new BeanDefinitionHolder(
									beanDefinition, beanDefinitionName);

							ScopeMetadata scopeMetadata = new ScopeMetadata();

							scopeMetadata.setScopeName(beanDefinitionScope);
							scopeMetadata.setScopedProxyMode(
								ScopedProxyMode.TARGET_CLASS);

							BeanDefinitionHolder scopedProxy =
								ScopedProxyUtils.createScopedProxy(
									definitionHolder, beanDefinitionRegistry,
									true);

							BeanDefinitionReaderUtils.registerBeanDefinition(
								scopedProxy, beanDefinitionRegistry);
						}
					}
					catch (ClassNotFoundException classNotFoundException) {
						_log.error(classNotFoundException);
					}
				}
			}
		}
	}

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory configurableListableBeanFactory)
		throws BeansException {

		DefaultListableBeanFactory defaultListableBeanFactory =
			(DefaultListableBeanFactory)configurableListableBeanFactory;

		defaultListableBeanFactory.setAutowireCandidateResolver(
			new JSR362AutowireCandidateResolver());

		_springBeanPortletExtension = new SpringBeanPortletExtension(
			_applicationContext);

		_springBeanPortletExtension.step1RegisterScopes(
			configurableListableBeanFactory);

		String[] beanDefinitionNames =
			configurableListableBeanFactory.getBeanDefinitionNames();

		for (String beanDefinitionName : beanDefinitionNames) {
			BeanDefinition beanDefinition =
				configurableListableBeanFactory.getBeanDefinition(
					beanDefinitionName);

			String beanClassName = beanDefinition.getBeanClassName();

			if (beanClassName != null) {
				_springBeanPortletExtension.step2ProcessAnnotatedType(
					beanClassName);
			}
		}

		JspFactory jspFactory = JspFactory.getDefaultFactory();

		if (jspFactory == null) {
			_log.error("Unable to register the SpringBeanELResolver with JSP");
		}
		else {
			JspApplicationContext jspApplicationContext =
				jspFactory.getJspApplicationContext(_servletContext);

			jspApplicationContext.addELResolver(
				new SpringBeanELResolver(_applicationContext));
		}

		_servletContext.setAttribute(
			SpringBeanPortletExtension.class.getName(),
			_springBeanPortletExtension);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		_applicationContext = applicationContext;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SpringPostProcessor.class);

	private ApplicationContext _applicationContext;
	private ServletContext _servletContext;
	private SpringBeanPortletExtension _springBeanPortletExtension;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.annotation.PostConstruct;

import jakarta.ws.rs.ext.ParamConverterProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Neil Griffin
 */
@Configuration
public class ParamConverterProvidersProducer
	implements ApplicationContextAware {

	@Bean
	@ParamConverterProviders
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public List<ParamConverterProvider> getParamConverterProviders() {
		return _paramConverterProviders;
	}

	@PostConstruct
	public void postConstruct() {
		Map<String, ParamConverterProvider> paramConverterProviders =
			_applicationContext.getBeansOfType(ParamConverterProvider.class);

		_paramConverterProviders = new ArrayList<>(
			paramConverterProviders.values());

		Collections.sort(
			_paramConverterProviders,
			new ParamConverterProviderPriorityComparator());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		_applicationContext = applicationContext;
	}

	private ApplicationContext _applicationContext;
	private List<ParamConverterProvider> _paramConverterProviders;

	private static class ParamConverterProviderPriorityComparator
		extends DescendingPriorityComparator<ParamConverterProvider> {

		private ParamConverterProviderPriorityComparator() {
			super(0);
		}

	}

}
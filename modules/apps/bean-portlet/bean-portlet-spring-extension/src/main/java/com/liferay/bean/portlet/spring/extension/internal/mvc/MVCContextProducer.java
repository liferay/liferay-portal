/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.annotation.PostConstruct;

import jakarta.mvc.MvcContext;
import jakarta.mvc.locale.LocaleResolver;
import jakarta.mvc.security.Encoders;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Neil Griffin
 */
@Configuration
public class MVCContextProducer implements ApplicationContextAware {

	@Bean("mvc")
	@Scope("portletRequest")
	public MvcContext getMvcContext() {
		return new MVCContextImpl(
			_configuration, _encoders, _localeResolvers, _portletContext,
			_portletRequest);
	}

	@PostConstruct
	public void postConstruct() {
		Map<String, LocaleResolver> beansOfType =
			_applicationContext.getBeansOfType(LocaleResolver.class);

		_localeResolvers = new ArrayList<>(beansOfType.values());

		Collections.sort(
			_localeResolvers, new LocaleResolverPriorityComparator());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		_applicationContext = applicationContext;
	}

	private ApplicationContext _applicationContext;

	@Autowired
	private jakarta.ws.rs.core.Configuration _configuration;

	@Autowired
	private Encoders _encoders;

	private List<LocaleResolver> _localeResolvers;

	@Autowired
	private PortletContext _portletContext;

	@Autowired
	private PortletRequest _portletRequest;

	private static class LocaleResolverPriorityComparator
		extends DescendingPriorityComparator<LocaleResolver> {

		private LocaleResolverPriorityComparator() {

			// The Javadoc for jakarta.mvc.locale.LocaleResolver states "If no
			// priority is explicitly defined, the priority is assumed to be
			// 1000."

			super(1000);
		}

	}

}
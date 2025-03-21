/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.annotation.PostConstruct;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import jakarta.mvc.MvcContext;
import jakarta.mvc.locale.LocaleResolver;
import jakarta.mvc.security.Encoders;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.annotations.PortletRequestScoped;

import jakarta.ws.rs.core.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * @author Neil Griffin
 */
@ApplicationScoped
public class MVCContextProducer {

	@Named("mvc")
	@PortletRequestScoped
	@Produces
	public MvcContext getMvcContext(
		Configuration configuration, Encoders encoders,
		PortletContext portletContext, PortletRequest portletRequest) {

		return new MVCContextImpl(
			configuration, encoders, _localeResolvers, portletContext,
			portletRequest);
	}

	@PostConstruct
	public void postConstruct() {
		_localeResolvers = BeanUtil.getBeanInstances(
			_beanManager, LocaleResolver.class);

		_localeResolvers.add(new LocaleResolverImpl());

		Collections.sort(
			_localeResolvers, new LocaleResolverPriorityComparator());
	}

	@Inject
	private BeanManager _beanManager;

	private List<LocaleResolver> _localeResolvers;

	private static class LocaleResolverPriorityComparator
		extends BaseDescendingPriorityComparator<LocaleResolver> {

		private LocaleResolverPriorityComparator() {

			// The Javadoc for jakarta.mvc.locale.LocaleResolver states "If no
			// priority is explicitly defined, the priority is assumed to be
			// 1000."

			super(1000);
		}

	}

}
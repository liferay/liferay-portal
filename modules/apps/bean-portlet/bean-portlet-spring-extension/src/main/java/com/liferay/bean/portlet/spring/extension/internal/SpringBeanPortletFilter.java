/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.annotations.PortletLifecycleFilter;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.EventFilter;
import jakarta.portlet.filter.FilterChain;
import jakarta.portlet.filter.FilterConfig;
import jakarta.portlet.filter.HeaderFilter;
import jakarta.portlet.filter.HeaderFilterChain;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import java.io.IOException;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;

/**
 * @author Neil Griffin
 */
@PortletLifecycleFilter(filterName = "springBeanPortletFilter")
public class SpringBeanPortletFilter
	implements ActionFilter, EventFilter, HeaderFilter, RenderFilter,
			   ResourceFilter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			ActionRequest actionRequest, ActionResponse actionResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		_doFilter(
			() -> filterChain.doFilter(actionRequest, actionResponse),
			actionRequest);
	}

	@Override
	public void doFilter(
			EventRequest eventRequest, EventResponse eventResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		_doFilter(
			() -> filterChain.doFilter(eventRequest, eventResponse),
			eventRequest);
	}

	@Override
	public void doFilter(
			HeaderRequest headerRequest, HeaderResponse headerResponse,
			HeaderFilterChain headerFilterChain)
		throws IOException, PortletException {

		_doFilter(
			() -> headerFilterChain.doFilter(headerRequest, headerResponse),
			headerRequest);
	}

	@Override
	public void doFilter(
			RenderRequest renderRequest, RenderResponse renderResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		_doFilter(
			() -> filterChain.doFilter(renderRequest, renderResponse),
			renderRequest);
	}

	@Override
	public void doFilter(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		_doFilter(
			() -> filterChain.doFilter(resourceRequest, resourceResponse),
			resourceRequest);
	}

	@Override
	public void init(FilterConfig filterConfig) throws PortletException {
	}

	private void _doFilter(
			FilterChainRunnable filterChainRunnable,
			PortletRequest portletRequest)
		throws IOException, PortletException {

		LocaleContext localeContext = LocaleContextHolder.getLocaleContext();

		LocaleContextHolder.setLocaleContext(
			new SimpleLocaleContext(portletRequest.getLocale()), false);

		filterChainRunnable.doFilter();

		LocaleContextHolder.setLocaleContext(localeContext, false);
	}

	@FunctionalInterface
	private interface FilterChainRunnable {

		public void doFilter() throws IOException, PortletException;

	}

}
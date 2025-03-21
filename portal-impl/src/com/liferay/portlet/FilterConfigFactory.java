/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletFilter;
import com.liferay.portlet.internal.FilterConfigImpl;

import jakarta.portlet.PortletContext;
import jakarta.portlet.filter.FilterConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class FilterConfigFactory {

	public static FilterConfig create(
		PortletFilter portletFilter, PortletContext ctx) {

		return _filterConfigFactory._create(portletFilter, ctx);
	}

	public static void destroy(PortletFilter portletFilter) {
		_filterConfigFactory._destroy(portletFilter);
	}

	private FilterConfigFactory() {
	}

	private FilterConfig _create(
		PortletFilter portletFilter, PortletContext ctx) {

		PortletApp portletApp = portletFilter.getPortletApp();

		Map<String, FilterConfig> filterConfigs = _pool.get(
			portletApp.getServletContextName());

		if (filterConfigs == null) {
			filterConfigs = new ConcurrentHashMap<>();

			_pool.put(portletApp.getServletContextName(), filterConfigs);
		}

		FilterConfig filterConfig = filterConfigs.get(
			portletFilter.getFilterName());

		if (filterConfig == null) {
			filterConfig = new FilterConfigImpl(
				portletFilter.getFilterName(), ctx,
				portletFilter.getInitParams());

			filterConfigs.put(portletFilter.getFilterName(), filterConfig);
		}

		return filterConfig;
	}

	private void _destroy(PortletFilter portletFilter) {
		PortletApp portletApp = portletFilter.getPortletApp();

		_pool.remove(portletApp.getServletContextName());
	}

	private static final FilterConfigFactory _filterConfigFactory =
		new FilterConfigFactory();

	private final Map<String, Map<String, FilterConfig>> _pool =
		new ConcurrentHashMap<>();

}
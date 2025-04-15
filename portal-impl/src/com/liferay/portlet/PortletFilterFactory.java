/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.util.InstanceFactory;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.UnavailableException;
import jakarta.portlet.filter.FilterConfig;
import jakarta.portlet.filter.PortletFilter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletFilterFactory {

	public static PortletFilter create(
			com.liferay.portal.kernel.model.PortletFilter portletFilterModel,
			PortletContext ctx)
		throws PortletException {

		return _portletFilterFactory._create(portletFilterModel, ctx);
	}

	public static void destroy(
		com.liferay.portal.kernel.model.PortletFilter portletFilterModel) {

		_portletFilterFactory._destroy(portletFilterModel);
	}

	private PortletFilterFactory() {
	}

	private PortletFilter _create(
			com.liferay.portal.kernel.model.PortletFilter portletFilterModel,
			PortletContext portletContext)
		throws PortletException {

		PortletApp portletApp = portletFilterModel.getPortletApp();

		Map<String, PortletFilter> portletFilters = _portletFilters.get(
			portletApp.getServletContextName());

		if (portletFilters == null) {
			portletFilters = new ConcurrentHashMap<>();

			_portletFilters.put(
				portletApp.getServletContextName(), portletFilters);
		}

		PortletFilter portletFilter = portletFilters.get(
			portletFilterModel.getFilterName());

		if (portletFilter != null) {
			return portletFilter;
		}

		FilterConfig filterConfig = FilterConfigFactory.create(
			portletFilterModel, portletContext);

		if (portletApp.isWARFile()) {
			PortletContextBag portletContextBag = PortletContextBagPool.get(
				portletApp.getServletContextName());

			Map<String, PortletFilter> curPortletFilters =
				portletContextBag.getPortletFilters();

			portletFilter = curPortletFilters.get(
				portletFilterModel.getFilterName());

			portletFilter = _init(
				portletFilterModel, filterConfig, portletFilter);
		}
		else {
			portletFilter = _init(portletFilterModel, filterConfig);
		}

		portletFilters.put(portletFilterModel.getFilterName(), portletFilter);

		return portletFilter;
	}

	private void _destroy(
		com.liferay.portal.kernel.model.PortletFilter portletFilterModel) {

		PortletApp portletApp = portletFilterModel.getPortletApp();

		Map<String, PortletFilter> portletFilters = _portletFilters.get(
			portletApp.getServletContextName());

		if (portletFilters == null) {
			return;
		}

		PortletFilter portletFilter = portletFilters.get(
			portletFilterModel.getFilterName());

		if (portletFilter == null) {
			return;
		}

		portletFilter.destroy();

		portletFilters.remove(portletFilterModel.getFilterName());

		FilterConfigFactory.destroy(portletFilterModel);
	}

	private PortletFilter _init(
			com.liferay.portal.kernel.model.PortletFilter portletFilterModel,
			FilterConfig filterConfig)
		throws PortletException {

		return _init(portletFilterModel, filterConfig, null);
	}

	private PortletFilter _init(
			com.liferay.portal.kernel.model.PortletFilter portletFilterModel,
			FilterConfig filterConfig, PortletFilter portletFilter)
		throws PortletException {

		try {
			if (portletFilter == null) {
				portletFilter = (PortletFilter)InstanceFactory.newInstance(
					portletFilterModel.getFilterClass());
			}

			portletFilter.init(filterConfig);
		}
		catch (PortletException portletException) {
			throw portletException;
		}
		catch (Exception exception) {
			throw new UnavailableException(exception.getMessage());
		}

		return portletFilter;
	}

	private static final PortletFilterFactory _portletFilterFactory =
		new PortletFilterFactory();

	private final Map<String, Map<String, PortletFilter>> _portletFilters =
		new ConcurrentHashMap<>();

}
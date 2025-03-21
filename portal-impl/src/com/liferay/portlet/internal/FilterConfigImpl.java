/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import jakarta.portlet.PortletContext;
import jakarta.portlet.filter.FilterConfig;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class FilterConfigImpl implements FilterConfig {

	public FilterConfigImpl(
		String filterName, PortletContext portletContext,
		Map<String, String> params) {

		_filterName = filterName;
		_portletContext = portletContext;
		_params = params;
	}

	@Override
	public String getFilterName() {
		return _filterName;
	}

	@Override
	public String getInitParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return _params.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(_params.keySet());
	}

	@Override
	public PortletContext getPortletContext() {
		return _portletContext;
	}

	private final String _filterName;
	private final Map<String, String> _params;
	private final PortletContext _portletContext;

}
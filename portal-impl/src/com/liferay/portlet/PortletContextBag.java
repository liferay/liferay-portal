/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.CustomUserAttributes;

import jakarta.portlet.PortletURLGenerationListener;
import jakarta.portlet.filter.PortletFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletContextBag {

	public PortletContextBag(String servletContextName) {
		_servletContextName = servletContextName;
	}

	public Map<String, CustomUserAttributes> getCustomUserAttributes() {
		return _customUserAttributes;
	}

	public Map<String, PortletFilter> getPortletFilters() {
		return _portletFilters;
	}

	public Map<String, PortletURLGenerationListener> getPortletURLListeners() {
		return _urlListeners;
	}

	public String getServletContextName() {
		return _servletContextName;
	}

	private final Map<String, CustomUserAttributes> _customUserAttributes =
		new HashMap<>();
	private final Map<String, PortletFilter> _portletFilters = new HashMap<>();
	private final String _servletContextName;
	private final Map<String, PortletURLGenerationListener> _urlListeners =
		new HashMap<>();

}
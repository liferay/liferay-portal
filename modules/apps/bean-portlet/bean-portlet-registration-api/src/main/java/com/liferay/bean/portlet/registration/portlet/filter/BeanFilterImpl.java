/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration.portlet.filter;

import com.liferay.portal.kernel.util.HashMapDictionary;

import jakarta.portlet.filter.PortletFilter;

import java.util.Dictionary;
import java.util.Map;
import java.util.Set;

/**
 * @author Neil Griffin
 */
public class BeanFilterImpl implements BeanFilter {

	public BeanFilterImpl(
		Class<? extends PortletFilter> filterClass, String filterName,
		Map<String, String> initParams, Set<String> lifecycles, int ordinal,
		Set<String> portletNames) {

		_filterClass = filterClass;
		_filterName = filterName;
		_initParams = initParams;
		_lifecycles = lifecycles;
		_ordinal = ordinal;
		_portletNames = portletNames;
	}

	@Override
	public Class<? extends PortletFilter> getFilterClass() {
		return _filterClass;
	}

	@Override
	public String getFilterName() {
		return _filterName;
	}

	@Override
	public Map<String, String> getInitParams() {
		return _initParams;
	}

	@Override
	public Set<String> getLifecycles() {
		return _lifecycles;
	}

	@Override
	public int getOrdinal() {
		return _ordinal;
	}

	@Override
	public Set<String> getPortletNames() {
		return _portletNames;
	}

	@Override
	public Dictionary<String, Object> toDictionary() {
		Dictionary<String, Object> dictionary = new HashMapDictionary<>();

		Set<String> lifecycles = getLifecycles();

		if (!lifecycles.isEmpty()) {
			dictionary.put("filter.lifecycles", lifecycles);
		}

		Map<String, String> initParams = getInitParams();

		for (Map.Entry<String, String> entry : initParams.entrySet()) {
			String value = entry.getValue();

			if (value != null) {
				dictionary.put(
					"jakarta.portlet.init-param.".concat(entry.getKey()),
					value);
			}
		}

		dictionary.put("service.ranking:Integer", getOrdinal());

		return dictionary;
	}

	private final Class<? extends PortletFilter> _filterClass;
	private final String _filterName;
	private final Map<String, String> _initParams;
	private final Set<String> _lifecycles;
	private final int _ordinal;
	private final Set<String> _portletNames;

}
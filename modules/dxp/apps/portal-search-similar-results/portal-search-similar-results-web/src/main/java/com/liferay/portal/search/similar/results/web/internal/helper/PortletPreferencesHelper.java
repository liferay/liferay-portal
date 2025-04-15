/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.helper;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.similar.results.web.internal.util.SearchStringUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author André de Oliveira
 */
public class PortletPreferencesHelper {

	public PortletPreferencesHelper(PortletPreferences portletPreferences) {
		_portletPreferences = portletPreferences;
	}

	public Integer getInteger(String key) {
		String stringValue = _getStringValue(key);

		if (stringValue == null) {
			return null;
		}

		return GetterUtil.getInteger(stringValue);
	}

	public int getInteger(String key, int defaultValue) {
		return GetterUtil.getInteger(_getStringValue(key), defaultValue);
	}

	public String getString(String key) {
		return _getStringValue(key);
	}

	public String getString(String key, String defaultValue) {
		String string = _getStringValue(key);

		if (string == null) {
			return defaultValue;
		}

		return string;
	}

	private String _getStringValue(String key) {
		if (_portletPreferences == null) {
			return null;
		}

		return SearchStringUtil.maybe(
			_portletPreferences.getValue(key, StringPool.BLANK));
	}

	private final PortletPreferences _portletPreferences;

}
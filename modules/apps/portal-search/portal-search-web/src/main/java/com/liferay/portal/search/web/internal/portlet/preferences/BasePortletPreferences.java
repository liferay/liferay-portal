/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet.preferences;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

/**
 * @author André de Oliveira
 */
public abstract class BasePortletPreferences {

	public BasePortletPreferences(PortletPreferences portletPreferences) {
		_portletPreferences = portletPreferences;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return GetterUtil.getBoolean(_getValue(key), defaultValue);
	}

	public int getInteger(String key, int defaultValue) {
		return GetterUtil.getInteger(_getValue(key), defaultValue);
	}

	public String getString(String key, String defaultValue) {
		return GetterUtil.getString(_getValue(key), defaultValue);
	}

	private String _getValue(String key) {
		if (_portletPreferences == null) {
			return null;
		}

		String value = _portletPreferences.getValue(key, StringPool.BLANK);

		value = StringUtil.trim(value);

		if (Validator.isBlank(value)) {
			return null;
		}

		return value;
	}

	private final PortletPreferences _portletPreferences;

}
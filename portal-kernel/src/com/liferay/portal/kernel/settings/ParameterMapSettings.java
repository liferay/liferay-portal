/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Iván Zaera
 */
public class ParameterMapSettings extends BaseSettings {

	public static final String PREFERENCES_PREFIX = "preferences--";

	public static final String SETTINGS_PREFIX = "settings--";

	public ParameterMapSettings(
		Map<String, String[]> parameterMap, Settings parentSettings) {

		super(parentSettings);

		_parameterMap = parameterMap;
	}

	public void setParameterNamePrefix(String parameterNamePrefix) {
		_parameterNamePrefix = parameterNamePrefix;
	}

	@Override
	protected String doGetValue(String key) {
		String[] values = doGetValues(key);

		if (values == null) {
			return null;
		}

		return values[0];
	}

	@Override
	protected String[] doGetValues(String key) {
		String[] values = null;

		if (Validator.isNotNull(_parameterNamePrefix)) {
			values = _parameterMap.get(_parameterNamePrefix + key);
		}

		if (values == null) {
			values = _parameterMap.get(key);
		}

		if (values == null) {
			values = _parameterMap.get(
				PREFERENCES_PREFIX + key + StringPool.DOUBLE_DASH);
		}

		if (values == null) {
			values = _parameterMap.get(
				SETTINGS_PREFIX + key + StringPool.DOUBLE_DASH);
		}

		return values;
	}

	private final Map<String, String[]> _parameterMap;
	private String _parameterNamePrefix;

}
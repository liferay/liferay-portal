/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class MockSettings extends BaseSettings {

	public MockSettings(Map<String, String> values) {
		_values = values;
	}

	@Override
	protected String doGetValue(String key) {
		return _values.get(key);
	}

	@Override
	protected String[] doGetValues(String key) {
		return StringUtil.split(_values.get(key));
	}

	private final Map<String, String> _values;

}
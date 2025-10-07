/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.props.test.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class PropsTemporarySwapper implements AutoCloseable {

	public PropsTemporarySwapper(
		String firstKey, Object firstValue, Object... keysAndValues) {

		_setTemporaryValue(firstKey, String.valueOf(firstValue));

		for (int i = 0; i < keysAndValues.length; i += 2) {
			String key = String.valueOf(keysAndValues[i]);
			String value = String.valueOf(keysAndValues[i + 1]);

			_setTemporaryValue(key, value);
		}
	}

	@Override
	public void close() {
		for (Map.Entry<String, String> entry : _oldValues.entrySet()) {
			PropsUtil.set(entry.getKey(), entry.getValue());
		}
	}

	private void _setTemporaryValue(String key, String value) {
		_oldValues.put(key, GetterUtil.getString(PropsUtil.get(key)));

		PropsUtil.set(key, value);
	}

	private final Map<String, String> _oldValues = new HashMap<>();

}
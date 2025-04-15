/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Iván Zaera
 */
public class MockPortletPreferences implements PortletPreferences {

	@Override
	public Map<String, String[]> getMap() {
		return _map;
	}

	@Override
	public Enumeration<String> getNames() {
		return Collections.enumeration(_map.keySet());
	}

	@Override
	public String getValue(String key, String defaultValue) {
		String[] values = _map.get(key);

		if (ArrayUtil.isNotEmpty(values)) {
			return values[0];
		}

		return defaultValue;
	}

	@Override
	public String[] getValues(String key, String[] defaultValues) {
		String[] values = _map.get(key);

		if (ArrayUtil.isNotEmpty(values)) {
			return values;
		}

		return defaultValues;
	}

	@Override
	public boolean isReadOnly(String key) {
		return false;
	}

	@Override
	public void reset(String key) {
		_map.remove(key);
	}

	@Override
	public void setValue(String key, String value) {
		_map.put(key, new String[] {value});
	}

	@Override
	public void setValues(String key, String[] values) {
		_map.put(key, values);
	}

	@Override
	public void store() {
	}

	private final Map<String, String[]> _map = new HashMap<>();

}
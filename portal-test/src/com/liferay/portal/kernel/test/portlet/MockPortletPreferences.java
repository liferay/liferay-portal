/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PreferencesValidator;
import jakarta.portlet.ReadOnlyException;
import jakarta.portlet.ValidatorException;

import java.io.IOException;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

/**
 * @author Dante Wang
 */
public class MockPortletPreferences implements PortletPreferences {

	@Override
	public Map<String, String[]> getMap() {
		return Collections.unmodifiableMap(_preferences);
	}

	@Override
	public Enumeration<String> getNames() {
		return Collections.enumeration(_preferences.keySet());
	}

	@Override
	public String getValue(String key, String defaultValue) {
		Assert.notNull(key, "Key must not be null");

		String[] values = _preferences.get(key);

		if (ArrayUtil.isNotEmpty(values)) {
			return values[0];
		}

		return defaultValue;
	}

	@Override
	public String[] getValues(String key, String[] defaultValues) {
		Assert.notNull(key, "Key must not be null");

		String[] values = _preferences.get(key);

		if (ArrayUtil.isNotEmpty(values)) {
			return values;
		}

		return defaultValues;
	}

	@Override
	public boolean isReadOnly(String key) {
		Assert.notNull(key, "Key must not be null");

		return _readOnlyPreferenceKeys.contains(key);
	}

	@Override
	public void reset(String key) throws ReadOnlyException {
		Assert.notNull(key, "Key must not be null");

		if (isReadOnly(key)) {
			throw new ReadOnlyException(
				"Preference '" + key + "' is read only");
		}

		_preferences.remove(key);
	}

	public void setPreferencesValidator(
		PreferencesValidator preferencesValidator) {

		_preferencesValidator = preferencesValidator;
	}

	public void setReadOnly(String key, boolean readOnly) {
		Assert.notNull(key, "Key must not be null");

		if (readOnly) {
			_readOnlyPreferenceKeys.add(key);
		}
		else {
			_readOnlyPreferenceKeys.remove(key);
		}
	}

	@Override
	public void setValue(String key, String value) throws ReadOnlyException {
		setValues(key, new String[] {value});
	}

	@Override
	public void setValues(String key, String[] values)
		throws ReadOnlyException {

		Assert.notNull(key, "Key must not be null");

		if (isReadOnly(key)) {
			throw new ReadOnlyException(
				"Preference \"" + key + "\" is read only");
		}

		_preferences.put(key, values);
	}

	@Override
	public void store() throws IOException, ValidatorException {
		if (_preferencesValidator != null) {
			_preferencesValidator.validate(this);
		}
	}

	private final Map<String, String[]> _preferences = new LinkedHashMap<>();
	private PreferencesValidator _preferencesValidator;
	private final Set<String> _readOnlyPreferenceKeys = new HashSet<>();

}
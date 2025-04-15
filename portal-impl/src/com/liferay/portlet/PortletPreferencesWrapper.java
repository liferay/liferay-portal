/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;
import jakarta.portlet.ValidatorException;

import java.io.IOException;
import java.io.Serializable;

import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletPreferencesWrapper
	implements PortletPreferences, Serializable {

	public PortletPreferencesWrapper(PortletPreferences portletPreferences) {
		_portletPreferences = portletPreferences;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortletPreferencesWrapper)) {
			return false;
		}

		PortletPreferencesWrapper portletPreferencesWrapper =
			(PortletPreferencesWrapper)object;

		if (Objects.equals(
				getPortletPreferencesImpl(),
				portletPreferencesWrapper.getPortletPreferencesImpl())) {

			return true;
		}

		return false;
	}

	@Override
	public Map<String, String[]> getMap() {
		return _portletPreferences.getMap();
	}

	@Override
	public Enumeration<String> getNames() {
		return _portletPreferences.getNames();
	}

	public PortletPreferencesImpl getPortletPreferencesImpl() {
		return (PortletPreferencesImpl)_portletPreferences;
	}

	@Override
	public String getValue(String key, String def) {
		return _portletPreferences.getValue(key, def);
	}

	@Override
	public String[] getValues(String key, String[] def) {
		return _portletPreferences.getValues(key, def);
	}

	@Override
	public int hashCode() {
		return _portletPreferences.hashCode();
	}

	@Override
	public boolean isReadOnly(String key) {
		return _portletPreferences.isReadOnly(key);
	}

	@Override
	public void reset(String key) throws ReadOnlyException {
		_portletPreferences.reset(key);
	}

	@Override
	public void setValue(String key, String value) throws ReadOnlyException {
		_portletPreferences.setValue(key, value);
	}

	@Override
	public void setValues(String key, String[] values)
		throws ReadOnlyException {

		_portletPreferences.setValues(key, values);
	}

	@Override
	public void store() throws IOException, ValidatorException {

		// PLT.17.1, clv

		throw new IllegalStateException(
			"Preferences cannot be stored inside a render call");
	}

	private final PortletPreferences _portletPreferences;

}
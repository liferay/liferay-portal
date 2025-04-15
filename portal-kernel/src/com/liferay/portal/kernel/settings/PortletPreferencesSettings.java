/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;
import jakarta.portlet.ValidatorException;

import java.io.IOException;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jorge Ferrer
 * @author Iván Zaera
 */
public class PortletPreferencesSettings extends BaseModifiableSettings {

	public PortletPreferencesSettings(PortletPreferences portletPreferences) {
		this(portletPreferences, null);
	}

	public PortletPreferencesSettings(
		PortletPreferences portletPreferences, Settings parentSettings) {

		super(parentSettings);

		_portletPreferences = portletPreferences;
	}

	@Override
	public Collection<String> getModifiedKeys() {
		Set<String> keys = new HashSet<>();

		Enumeration<String> enumeration = _portletPreferences.getNames();

		while (enumeration.hasMoreElements()) {
			keys.add(enumeration.nextElement());
		}

		return keys;
	}

	public PortletPreferences getPortletPreferences() {
		return _portletPreferences;
	}

	@Override
	public void reset(String key) {
		try {
			_portletPreferences.reset(key);
		}
		catch (ReadOnlyException readOnlyException) {
			_log.error(
				"Portlet preferences used to persist settings should never " +
					"be read only",
				readOnlyException);
		}
	}

	@Override
	public ModifiableSettings setValue(String key, String value) {
		try {
			_portletPreferences.setValue(key, value);
		}
		catch (ReadOnlyException readOnlyException) {
			_log.error(
				"Portlet preferences used to persist settings should never " +
					"be read only",
				readOnlyException);
		}

		return this;
	}

	@Override
	public ModifiableSettings setValues(String key, String[] values) {
		try {
			_portletPreferences.setValues(key, values);
		}
		catch (ReadOnlyException readOnlyException) {
			_log.error(
				"Portlet preferences used to persist settings should never " +
					"be read only",
				readOnlyException);
		}

		return this;
	}

	@Override
	public void store() throws IOException, ValidatorException {
		_portletPreferences.store();
	}

	@Override
	protected String doGetValue(String key) {
		return _portletPreferences.getValue(key, null);
	}

	@Override
	protected String[] doGetValues(String key) {
		return _portletPreferences.getValues(key, null);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferencesSettings.class);

	private final PortletPreferences _portletPreferences;

}
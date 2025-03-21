/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import jakarta.portlet.PortletPreferences;

import java.io.IOException;
import java.io.Serializable;

import java.util.Enumeration;
import java.util.Map;

/**
 * @author Alexander Chow
 */
public class PortalPreferencesWrapper
	implements Cloneable, PortletPreferences, Serializable {

	public PortalPreferencesWrapper(
		PortalPreferencesImpl portalPreferencesImpl) {

		_portalPreferencesImpl = portalPreferencesImpl;
	}

	@Override
	public PortalPreferencesWrapper clone() {
		return new PortalPreferencesWrapper(_portalPreferencesImpl.clone());
	}

	@Override
	public Map<String, String[]> getMap() {
		return _portalPreferencesImpl.getMap(null);
	}

	@Override
	public Enumeration<String> getNames() {
		return _portalPreferencesImpl.getNames(null);
	}

	public PortalPreferencesImpl getPortalPreferencesImpl() {
		return _portalPreferencesImpl;
	}

	@Override
	public String getValue(String key, String def) {
		return _portalPreferencesImpl.getValue(null, key, def);
	}

	@Override
	public String[] getValues(String key, String[] def) {
		return _portalPreferencesImpl.getValues(null, key, def);
	}

	@Override
	public boolean isReadOnly(String key) {
		return false;
	}

	@Override
	public void reset(String key) {
		_portalPreferencesImpl.reset(null, key);
	}

	@Override
	public void setValue(String key, String value) {
		_portalPreferencesImpl.setValue(null, key, value);
	}

	@Override
	public void setValues(String key, String... values) {
		_portalPreferencesImpl.setValues(null, key, values);
	}

	@Override
	public void store() throws IOException {
		_portalPreferencesImpl.store();
	}

	private final PortalPreferencesImpl _portalPreferencesImpl;

}
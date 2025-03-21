/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.settings.internal;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesServiceUtil;
import com.liferay.portal.kernel.settings.ArchivedSettings;
import com.liferay.portal.kernel.settings.BaseModifiableSettings;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.PortletPreferencesSettings;
import com.liferay.portal.kernel.util.PortletKeys;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ValidatorException;

import java.io.IOException;

import java.util.Collection;
import java.util.Date;

/**
 * @author Iván Zaera
 */
public class ArchivedSettingsImpl
	extends BaseModifiableSettings implements ArchivedSettings {

	public ArchivedSettingsImpl(PortletItem portletItem) {
		_portletItem = portletItem;
	}

	@Override
	public void delete() throws IOException {
		try {
			PortletPreferencesServiceUtil.deleteArchivedPreferences(
				_portletItem.getPortletItemId());
		}
		catch (PortalException portalException) {
			throw new IOException(
				"Unable to delete archived settings", portalException);
		}
		catch (SystemException systemException) {
			throw new IOException(
				"Unable to delete archived settings", systemException);
		}
	}

	@Override
	public Date getModifiedDate() {
		return _portletItem.getModifiedDate();
	}

	@Override
	public Collection<String> getModifiedKeys() {
		ModifiableSettings modifiableSettings = _getModifiableSettings();

		return modifiableSettings.getModifiedKeys();
	}

	@Override
	public String getName() {
		return _portletItem.getName();
	}

	@Override
	public String getUserName() {
		return _portletItem.getUserName();
	}

	@Override
	public void reset(String key) {
		ModifiableSettings modifiableSettings = _getModifiableSettings();

		modifiableSettings.reset(key);
	}

	@Override
	public ModifiableSettings setValue(String key, String value) {
		ModifiableSettings modifiableSettings = _getModifiableSettings();

		modifiableSettings.setValue(key, value);

		return this;
	}

	@Override
	public ModifiableSettings setValues(String key, String[] values) {
		ModifiableSettings modifiableSettings = _getModifiableSettings();

		modifiableSettings.setValues(key, values);

		return this;
	}

	@Override
	public void store() throws IOException, ValidatorException {
		ModifiableSettings modifiableSettings = _getModifiableSettings();

		modifiableSettings.store();
	}

	@Override
	protected String doGetValue(String key) {
		ModifiableSettings modifiableSettings = _getModifiableSettings();

		return modifiableSettings.getValue(key, null);
	}

	@Override
	protected String[] doGetValues(String key) {
		ModifiableSettings modifiableSettings = _getModifiableSettings();

		return modifiableSettings.getValues(key, null);
	}

	private ModifiableSettings _getModifiableSettings() {
		if (_portletPreferencesSettings != null) {
			return _portletPreferencesSettings;
		}

		PortletPreferences portletPreferences = null;

		try {
			long ownerId = _portletItem.getPortletItemId();
			int ownerType = PortletKeys.PREFS_OWNER_TYPE_ARCHIVED;
			long plid = 0;

			portletPreferences =
				PortletPreferencesLocalServiceUtil.getPreferences(
					_portletItem.getCompanyId(), ownerId, ownerType, plid,
					PortletIdCodec.decodePortletName(
						_portletItem.getPortletId()));
		}
		catch (SystemException systemException) {
			throw new RuntimeException(
				"Unable to load settings", systemException);
		}

		_portletPreferencesSettings = new PortletPreferencesSettings(
			portletPreferences);

		return _portletPreferencesSettings;
	}

	private final PortletItem _portletItem;
	private PortletPreferencesSettings _portletPreferencesSettings;

}
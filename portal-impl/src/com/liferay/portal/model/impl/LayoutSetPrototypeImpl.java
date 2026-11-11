/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

/**
 * @author Brian Wing Shun Chan
 * @author Ryan Park
 */
public class LayoutSetPrototypeImpl extends LayoutSetPrototypeBaseImpl {

	@Override
	public Group getGroup() throws PortalException {
		return GroupLocalServiceUtil.getLayoutSetPrototypeGroup(
			getCompanyId(), getLayoutSetPrototypeId());
	}

	@Override
	public long getGroupId() throws PortalException {
		Group group = getGroup();

		return group.getGroupId();
	}

	@Override
	public LayoutSet getLayoutSet() throws PortalException {
		return LayoutSetLocalServiceUtil.getLayoutSet(
			getGroup().getGroupId(), true);
	}

	@Override
	public UnicodeProperties getSettingsProperties() {
		if (_settingsUnicodeProperties == null) {
			_settingsUnicodeProperties = UnicodePropertiesBuilder.create(
				true
			).load(
				super.getSettings()
			).build();
		}

		return _settingsUnicodeProperties;
	}

	@Override
	public String getSettingsProperty(String key) {
		UnicodeProperties settingsUnicodeProperties = getSettingsProperties();

		return settingsUnicodeProperties.getProperty(key);
	}

	@Override
	public boolean hasSetModifiedDate() {
		return true;
	}

	@Override
	public void setSettings(String settings) {
		_settingsUnicodeProperties = null;

		super.setSettings(settings);
	}

	@Override
	public void setSettingsProperties(
		UnicodeProperties settingsUnicodeProperties) {

		_settingsUnicodeProperties = settingsUnicodeProperties;

		super.setSettings(settingsUnicodeProperties.toString());
	}

	private UnicodeProperties _settingsUnicodeProperties;

}
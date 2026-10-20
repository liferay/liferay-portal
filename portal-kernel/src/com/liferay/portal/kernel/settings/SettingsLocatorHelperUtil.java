/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Iván Zaera
 */
public class SettingsLocatorHelperUtil {

	public static Settings getCompanyConfigurationBeanSettings(
		long companyId, String configurationPid, Settings parentSettings) {

		SettingsLocatorHelper settingsLocatorHelper =
			_settingsLocatorHelperSnapshot.get();

		return settingsLocatorHelper.getCompanyConfigurationBeanSettings(
			companyId, configurationPid, parentSettings);
	}

	public static Settings getGroupConfigurationBeanSettings(
		long groupId, String configurationPid, Settings parentSettings) {

		SettingsLocatorHelper settingsLocatorHelper =
			_settingsLocatorHelperSnapshot.get();

		return settingsLocatorHelper.getGroupConfigurationBeanSettings(
			groupId, configurationPid, parentSettings);
	}

	public static Settings getPortletInstanceConfigurationBeanSettings(
		String portletId, String configurationPid, Settings parentSettings) {

		SettingsLocatorHelper settingsLocatorHelper =
			_settingsLocatorHelperSnapshot.get();

		return settingsLocatorHelper.
			getPortletInstanceConfigurationBeanSettings(
				portletId, configurationPid, parentSettings);
	}

	public static SettingsDescriptor getSettingsDescriptor(String settingsId) {
		SettingsLocatorHelper settingsLocatorHelper =
			_settingsLocatorHelperSnapshot.get();

		return settingsLocatorHelper.getSettingsDescriptor(settingsId);
	}

	public static SettingsLocatorHelper getSettingsLocatorHelper() {
		return _settingsLocatorHelperSnapshot.get();
	}

	private static final Snapshot<SettingsLocatorHelper>
		_settingsLocatorHelperSnapshot = new Snapshot<>(
			SettingsLocatorHelperUtil.class, SettingsLocatorHelper.class);

}
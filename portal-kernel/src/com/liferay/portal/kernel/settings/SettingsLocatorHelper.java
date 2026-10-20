/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import com.liferay.portal.kernel.settings.definition.ConfigurationPidMapping;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Iván Zaera
 */
@ProviderType
public interface SettingsLocatorHelper {

	public Settings getCompanyConfigurationBeanSettings(
		long companyId, String configurationPid, Settings parentSettings);

	public Settings getCompanyPortletPreferencesSettings(
		long companyId, String settingsId, Settings parentSettings);

	public Settings getConfigurationBeanSettings(String configurationPid);

	public ConfigurationPidMapping getConfigurationPidMapping(
		String configurationId);

	public Settings getGroupConfigurationBeanSettings(
		long groupId, String configurationPid, Settings parentSettings);

	public Settings getGroupPortletPreferencesSettings(
		long groupId, String settingsId, Settings parentSettings);

	public Settings getPortalPreferencesSettings(
		long companyId, Settings parentSettings);

	public Settings getPortletInstanceConfigurationBeanSettings(
		String portletId, String configurationPid, Settings parentSettings);

	public SettingsDescriptor getSettingsDescriptor(String settingsId);

}
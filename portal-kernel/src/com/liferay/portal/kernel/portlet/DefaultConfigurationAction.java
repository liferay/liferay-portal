/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.settings.PortletPreferencesSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ReadOnlyException;

/**
 * @author Iván Zaera
 */
public class DefaultConfigurationAction
	extends BaseJSPSettingsConfigurationAction
	implements ConfigurationAction, ResourceServingConfigurationAction {

	public DefaultConfigurationAction() {
		setParameterNamePrefix("preferences--");
	}

	@Override
	protected Settings getSettings(ActionRequest actionRequest) {
		return new PortletPreferencesSettings(actionRequest.getPreferences());
	}

	protected void postProcess(
			long companyId, PortletRequest portletRequest,
			PortletPreferences portletPreferences)
		throws PortalException {
	}

	@Override
	protected void postProcess(
			long companyId, PortletRequest portletRequest, Settings settings)
		throws PortalException {

		PortletPreferencesSettings portletPreferencesSettings =
			(PortletPreferencesSettings)settings;

		postProcess(
			companyId, portletRequest,
			portletPreferencesSettings.getPortletPreferences());
	}

	protected void removeDefaultValue(
		PortletRequest portletRequest, PortletPreferences portletPreferences,
		String key, String defaultValue) {

		String value = getParameter(portletRequest, key);

		if (defaultValue.equals(value) ||
			StringUtil.equalsIgnoreBreakLine(defaultValue, value)) {

			try {
				portletPreferences.reset(key);
			}
			catch (ReadOnlyException readOnlyException) {
				throw new SystemException(readOnlyException);
			}
		}
	}

	@Override
	protected void updateMultiValuedKeys(ActionRequest actionRequest) {

		// Legacy configuration actions that are not based on Settings must
		// ignore this method to avoid failures due to multi valued keys not
		// registering with SettingsConfigurationAction

	}

}
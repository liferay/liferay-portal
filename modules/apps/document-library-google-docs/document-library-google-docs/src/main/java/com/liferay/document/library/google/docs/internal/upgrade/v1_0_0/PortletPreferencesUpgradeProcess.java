/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.google.docs.internal.upgrade.v1_0_0;

import com.liferay.document.library.google.drive.configuration.DLGoogleDriveCompanyConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

/**
 * @author Alejandro Tardín
 */
public class PortletPreferencesUpgradeProcess extends UpgradeProcess {

	public PortletPreferencesUpgradeProcess(
		ConfigurationProvider configurationProvider, PrefsProps prefsProps) {

		_configurationProvider = configurationProvider;
		_prefsProps = prefsProps;
	}

	@Override
	protected void doUpgrade() throws Exception {
		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				DLGoogleDriveCompanyConfiguration
					dlGoogleDriveCompanyConfiguration =
						_configurationProvider.getCompanyConfiguration(
							DLGoogleDriveCompanyConfiguration.class, companyId);

				PortletPreferences portletPreferences =
					_prefsProps.getPreferences(companyId);

				String apiKey = portletPreferences.getValue(
					"googleAppsAPIKey", StringPool.BLANK);
				String clientId = portletPreferences.getValue(
					"googleClientId", StringPool.BLANK);

				if (Validator.isNotNull(apiKey) &&
					Validator.isNotNull(clientId) &&
					Validator.isNull(
						dlGoogleDriveCompanyConfiguration.clientId()) &&
					Validator.isNull(
						dlGoogleDriveCompanyConfiguration.clientSecret())) {

					_configurationProvider.saveCompanyConfiguration(
						DLGoogleDriveCompanyConfiguration.class, companyId,
						HashMapDictionaryBuilder.<String, Object>put(
							"clientId", clientId
						).put(
							"pickerAPIKey", apiKey
						).build());
				}
			});
	}

	private final ConfigurationProvider _configurationProvider;
	private final PrefsProps _prefsProps;

}
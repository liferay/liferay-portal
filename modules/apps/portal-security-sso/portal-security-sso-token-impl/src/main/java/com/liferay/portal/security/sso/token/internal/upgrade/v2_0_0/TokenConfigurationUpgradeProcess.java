/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.token.internal.upgrade.v2_0_0;

import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.security.sso.token.constants.LegacyTokenPropsKeys;
import com.liferay.portal.security.sso.token.constants.TokenConfigurationKeys;
import com.liferay.portal.security.sso.token.constants.TokenConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christopher Kian
 */
public class TokenConfigurationUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeConfiguration();
	}

	private void _storeSettings(
			long companyId, String settingsId, Map<String, String> map)
		throws Exception {

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new CompanyServiceSettingsLocator(companyId, settingsId));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String name = entry.getKey();

			String value = entry.getValue();

			String oldValue = settings.getValue(name, null);

			if (value.equals(oldValue)) {
				continue;
			}

			modifiableSettings.setValue(name, value);
		}

		modifiableSettings.store();
	}

	private void _upgradeConfiguration() throws Exception {
		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				Map<String, String> map = new HashMap<>();

				for (String[] renamePropertykeys :
						_RENAME_PROPERTY_KEYS_ARRAY) {

					String propertyValue = PrefsPropsUtil.getString(
						companyId, renamePropertykeys[0]);

					if (propertyValue == null) {
						continue;
					}

					map.put(renamePropertykeys[1], propertyValue);
				}

				if (!map.isEmpty()) {
					_storeSettings(companyId, TokenConstants.SERVICE_NAME, map);
				}

				CompanyLocalServiceUtil.removePreferences(
					companyId,
					ArrayUtil.append(
						LegacyTokenPropsKeys.SHIBBOLETH_KEYS,
						LegacyTokenPropsKeys.SITEMINDER_KEYS));
			});
	}

	private static final String[][] _RENAME_PROPERTY_KEYS_ARRAY = {
		{
			LegacyTokenPropsKeys.SHIBBOLETH_AUTH_ENABLED,
			TokenConfigurationKeys.AUTH_ENABLED
		},
		{
			LegacyTokenPropsKeys.SHIBBOLETH_IMPORT_FROM_LDAP,
			TokenConfigurationKeys.IMPORT_FROM_LDAP
		},
		{
			LegacyTokenPropsKeys.SHIBBOLETH_LOGOUT_URL,
			TokenConfigurationKeys.LOGOUT_REDIRECT_URL
		},
		{
			LegacyTokenPropsKeys.SHIBBOLETH_USER_HEADER,
			TokenConfigurationKeys.USER_TOKEN_NAME
		},
		{
			LegacyTokenPropsKeys.SITEMINDER_AUTH_ENABLED,
			TokenConfigurationKeys.AUTH_ENABLED
		},
		{
			LegacyTokenPropsKeys.SITEMINDER_IMPORT_FROM_LDAP,
			TokenConfigurationKeys.IMPORT_FROM_LDAP
		},
		{
			LegacyTokenPropsKeys.SITEMINDER_USER_HEADER,
			TokenConfigurationKeys.USER_TOKEN_NAME
		}
	};

}
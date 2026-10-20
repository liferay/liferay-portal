/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsException;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PrefsPropsUtil;

import jakarta.portlet.ValidatorException;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael C. Han
 */
public abstract class BaseCompanySettingsVerifyProcess extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		verifyProperties();
	}

	protected abstract CompanyLocalService getCompanyLocalService();

	protected abstract Set<String> getLegacyPropertyKeys();

	protected Map<String, String> getPropertyValues(long companyId) {
		Map<String, String> map = new HashMap<>();

		for (String[] renamePropertykeys : getRenamePropertyKeysArray()) {
			String propertyValue = PrefsPropsUtil.getString(
				companyId, renamePropertykeys[0]);

			if (propertyValue == null) {
				continue;
			}

			map.put(renamePropertykeys[1], propertyValue);
		}

		return map;
	}

	protected String[][] getRenamePropertyKeysArray() {
		return new String[0][0];
	}

	protected abstract String getSettingsId();

	protected void storeSettings(
			long companyId, String settingsId, Map<String, String> map)
		throws IOException, SettingsException, ValidatorException {

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

	protected void verifyProperties() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			CompanyLocalService companyLocalService = getCompanyLocalService();

			companyLocalService.forEachCompanyId(
				companyId -> {
					Map<String, String> map = getPropertyValues(companyId);

					if (!map.isEmpty()) {
						storeSettings(companyId, getSettingsId(), map);
					}

					Set<String> keys = getLegacyPropertyKeys();

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Removing preference keys ", keys,
								" for company ", companyId));
					}

					companyLocalService.removePreferences(
						companyId, keys.toArray(new String[0]));
				});
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseCompanySettingsVerifyProcess.class);

}
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
import com.liferay.portal.kernel.settings.SettingsDescriptor;
import com.liferay.portal.kernel.settings.SettingsException;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PrefsPropsUtil;

import jakarta.portlet.ValidatorException;

import java.io.IOException;

import java.util.Dictionary;
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

	protected Dictionary<String, String> getPropertyValues(long companyId) {
		Dictionary<String, String> dictionary = new HashMapDictionary<>();

		for (String[] renamePropertykeys : getRenamePropertyKeysArray()) {
			String propertyValue = PrefsPropsUtil.getString(
				companyId, renamePropertykeys[0]);

			if (propertyValue != null) {
				dictionary.put(renamePropertykeys[1], propertyValue);
			}
		}

		return dictionary;
	}

	protected String[][] getRenamePropertyKeysArray() {
		return new String[0][0];
	}

	protected abstract String getSettingsId();

	protected abstract SettingsLocatorHelper getSettingsLocatorHelper();

	protected void storeSettings(
			long companyId, String settingsId,
			Dictionary<String, String> dictionary)
		throws IOException, SettingsException, ValidatorException {

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new CompanyServiceSettingsLocator(companyId, settingsId));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		SettingsLocatorHelper settingsLocatorHelper =
			getSettingsLocatorHelper();

		SettingsDescriptor settingsDescriptor =
			settingsLocatorHelper.getSettingsDescriptor(settingsId);

		for (String name : settingsDescriptor.getAllKeys()) {
			String value = dictionary.get(name);

			if (value == null) {
				continue;
			}

			String oldValue = settings.getValue(name, null);

			if (!value.equals(oldValue)) {
				modifiableSettings.setValue(name, value);
			}
		}

		modifiableSettings.store();
	}

	protected void verifyProperties() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			CompanyLocalService companyLocalService = getCompanyLocalService();

			companyLocalService.forEachCompanyId(
				companyId -> {
					Dictionary<String, String> dictionary = getPropertyValues(
						companyId);

					if (!dictionary.isEmpty()) {
						storeSettings(companyId, getSettingsId(), dictionary);
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
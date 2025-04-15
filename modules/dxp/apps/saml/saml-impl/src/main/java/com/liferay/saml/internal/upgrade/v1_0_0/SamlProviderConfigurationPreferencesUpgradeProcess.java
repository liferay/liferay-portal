/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.upgrade.v1_0_0;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.internal.constants.LegacySamlPropsKeys;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.portlet.PortletPreferences;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Stian Sigvartsen
 * @author Tomas Polesovsky
 */
public class SamlProviderConfigurationPreferencesUpgradeProcess
	extends BaseUpgradeSaml {

	public SamlProviderConfigurationPreferencesUpgradeProcess(
		CompanyLocalService companyLocalService, PrefsProps prefsProps,
		Props props,
		SamlProviderConfigurationHelper samlProviderConfigurationHelper) {

		_companyLocalService = companyLocalService;
		_prefsProps = prefsProps;
		_props = props;
		_samlProviderConfigurationHelper = samlProviderConfigurationHelper;
	}

	public Set<String> migrateSAMLProviderConfigurationPreferences(
			long companyId)
		throws Exception {

		String prefsPropsFilterString = null;
		Filter propsFilter = null;

		PortletPreferences portletPreferences = _prefsProps.getPreferences(
			companyId);

		String entityId = portletPreferences.getValue(
			LegacySamlPropsKeys.SAML_ENTITY_ID, null);

		if (entityId == null) {
			entityId = _props.get(LegacySamlPropsKeys.SAML_ENTITY_ID);
		}

		if (Validator.isNotNull(entityId)) {
			prefsPropsFilterString = "[" + entityId + "]";
			propsFilter = new Filter(entityId);
		}

		Set<String> migratedPrefsPropsKeys = new HashSet<>();
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		for (String key : LegacySamlPropsKeys.SAML_KEYS_PREFS_PROPS) {
			if (ArrayUtil.contains(
					LegacySamlPropsKeys.SAML_KEYS_DEPRECATED, key)) {

				continue;
			}

			String value = null;

			if ((prefsPropsFilterString != null) &&
				ArrayUtil.contains(
					LegacySamlPropsKeys.SAML_KEYS_FILTERED, key)) {

				String prefsPropsKey = key + prefsPropsFilterString;

				value = portletPreferences.getValue(prefsPropsKey, null);

				if (value != null) {
					migratedPrefsPropsKeys.add(prefsPropsKey);
				}
			}

			if (value == null) {
				value = portletPreferences.getValue(key, null);

				if (value != null) {
					migratedPrefsPropsKeys.add(key);
				}
			}

			if (value == null) {
				value = getPropsValue(_props, key, propsFilter);
			}

			if (value == null) {
				continue;
			}

			if (!Objects.equals(value, getDefaultValue(key))) {
				unicodeProperties.put(key, value);
			}
		}

		if (!migratedPrefsPropsKeys.isEmpty()) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						companyId)) {

				_samlProviderConfigurationHelper.updateProperties(
					unicodeProperties);
			}
		}

		return migratedPrefsPropsKeys;
	}

	public void migrateSAMLProviderConfigurationSystemPreferences()
		throws Exception {

		Filter filter = null;

		String entityId = _props.get(LegacySamlPropsKeys.SAML_ENTITY_ID);

		if (Validator.isNotNull(entityId)) {
			filter = new Filter(entityId);
		}

		UnicodeProperties unicodeProperties = new UnicodeProperties();

		for (String key : LegacySamlPropsKeys.SAML_KEYS_PREFS_PROPS) {
			if (ArrayUtil.contains(
					LegacySamlPropsKeys.SAML_KEYS_DEPRECATED, key)) {

				continue;
			}

			String value = getPropsValue(_props, key, filter);

			if (value == null) {
				continue;
			}

			if (!Objects.equals(value, getDefaultValue(key))) {
				unicodeProperties.put(key, value);
			}
		}

		if (!unicodeProperties.isEmpty()) {
			_samlProviderConfigurationHelper.updateProperties(
				unicodeProperties);
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_companyLocalService.forEachCompanyId(
				companyId -> {
					Set<String> migratedPrefsPropsKeys =
						migrateSAMLProviderConfigurationPreferences(companyId);

					if (migratedPrefsPropsKeys.isEmpty()) {
						return;
					}

					_companyLocalService.removePreferences(
						companyId,
						migratedPrefsPropsKeys.toArray(new String[0]));
				});

			migrateSAMLProviderConfigurationSystemPreferences();
		}
	}

	private final CompanyLocalService _companyLocalService;
	private final PrefsProps _prefsProps;
	private final Props _props;
	private final SamlProviderConfigurationHelper
		_samlProviderConfigurationHelper;

}
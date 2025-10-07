/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.upgrade.v1_0_0;

import com.liferay.organizations.internal.configuration.OrganizationTypeConfiguration;
import com.liferay.organizations.internal.constants.LegacyOrganizationTypesKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Drew Brokke
 */
public class OrganizationTypesConfigurationUpgradeProcess
	extends UpgradeProcess {

	public OrganizationTypesConfigurationUpgradeProcess(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Override
	protected void doUpgrade() throws Exception {
		for (String organizationType :
				PropsUtil.getArray(
					LegacyOrganizationTypesKeys.ORGANIZATIONS_TYPES)) {

			_upgradeOrganizationTypeConfiguration(organizationType);
		}
	}

	private String _getPropertyName(
		String basePropertyName, String organizationType) {

		return StringBundler.concat(
			basePropertyName, "[", organizationType, "]");
	}

	private void _upgradeOrganizationTypeConfiguration(String organizationType)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(&(", ConfigurationAdmin.SERVICE_FACTORYPID, "=", _FACTORY_PID,
				")(name=", organizationType, "))"));

		if (configurations != null) {
			return;
		}

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		if (!organizationType.equals(_organizationTypeConfiguration.name())) {
			properties.put("name", organizationType);
		}

		Filter filter = new Filter(organizationType);

		if (PropsUtil.contains(
				_getPropertyName(
					LegacyOrganizationTypesKeys.ORGANIZATIONS_CHILDREN_TYPES,
					organizationType))) {

			String[] childrenTypes = PropsUtil.getArray(
				LegacyOrganizationTypesKeys.ORGANIZATIONS_CHILDREN_TYPES,
				filter);

			if (!Arrays.equals(
					childrenTypes,
					_organizationTypeConfiguration.childrenTypes())) {

				properties.put("childrenTypes", childrenTypes);
			}
		}

		boolean defaultCountryEnabled =
			_organizationTypeConfiguration.countryEnabled();

		boolean countryEnabled = GetterUtil.getBoolean(
			PropsUtil.get(
				LegacyOrganizationTypesKeys.ORGANIZATIONS_COUNTRY_ENABLED,
				filter),
			defaultCountryEnabled);

		if (countryEnabled != defaultCountryEnabled) {
			properties.put("countryEnabled", countryEnabled);
		}

		boolean defaultCountryRequired =
			_organizationTypeConfiguration.countryRequired();

		boolean countryRequired = GetterUtil.getBoolean(
			PropsUtil.get(
				LegacyOrganizationTypesKeys.ORGANIZATIONS_COUNTRY_REQUIRED,
				filter),
			defaultCountryRequired);

		if (countryRequired != defaultCountryRequired) {
			properties.put("countryRequired", countryRequired);
		}

		boolean defaultRootable = _organizationTypeConfiguration.rootable();

		boolean rootable = GetterUtil.getBoolean(
			PropsUtil.get(
				LegacyOrganizationTypesKeys.ORGANIZATIONS_ROOTABLE, filter),
			defaultRootable);

		if (rootable != defaultRootable) {
			properties.put("rootable", rootable);
		}

		if (properties.isEmpty()) {
			return;
		}

		if (Validator.isNull(properties.get("name"))) {
			properties.put("name", organizationType);
		}

		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				_FACTORY_PID, StringPool.QUESTION);

		configuration.update(properties);
	}

	private static final String _FACTORY_PID =
		OrganizationTypeConfiguration.class.getName();

	private final ConfigurationAdmin _configurationAdmin;
	private final OrganizationTypeConfiguration _organizationTypeConfiguration =
		ConfigurableUtil.createConfigurable(
			OrganizationTypeConfiguration.class,
			new HashMapDictionary<String, Object>());

}
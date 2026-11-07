/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.upgrade.v2_13_1;

import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author David Truong
 */
public class CTConflictConfigurationUpgradeProcess extends UpgradeProcess {

	public CTConflictConfigurationUpgradeProcess(
		CompanyLocalService companyLocalService,
		ConfigurationAdmin configurationAdmin,
		ConfigurationProvider configurationProvider) {

		_companyLocalService = companyLocalService;
		_configurationAdmin = configurationAdmin;
		_configurationProvider = configurationProvider;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeCompanyScopedConfigurations();
		_upgradeSystemConfigurations();
	}

	private Dictionary<String, Object> _mergeProperties(
		Dictionary<String, Object> ctConflictConfigurationProperties,
		Dictionary<String, Object> ctSettingsConfigurationProperties) {

		if (ctSettingsConfigurationProperties == null) {
			ctSettingsConfigurationProperties = new HashMapDictionary<>();
		}

		ctSettingsConfigurationProperties.put(
			"modificationDeletionConflictCheckEnabled",
			ctConflictConfigurationProperties.get(
				"modificationDeletionConflictCheckEnabled"));

		ctSettingsConfigurationProperties.put(
			"schemaVersionCheckEnabled",
			ctConflictConfigurationProperties.get("schemaVersionCheckEnabled"));

		return ctSettingsConfigurationProperties;
	}

	private void _upgradeCompanyScopedConfigurations() throws Exception {
		Configuration[] ctConflictConfigurations =
			_configurationAdmin.listConfigurations(
				String.format(
					"(%s=%s.scoped)", ConfigurationAdmin.SERVICE_FACTORYPID,
					_CLASS_NAME));

		if (ctConflictConfigurations == null) {
			return;
		}

		for (Configuration ctConflictConfiguration : ctConflictConfigurations) {
			Dictionary<String, Object> ctConflictConfigurationProperties =
				ctConflictConfiguration.getProperties();

			if (ctConflictConfigurationProperties == null) {
				continue;
			}

			long companyId = GetterUtil.getLong(
				ctConflictConfigurationProperties.get(
					ExtendedObjectClassDefinition.Scope.COMPANY.
						getPropertyKey()));

			if (_companyLocalService.fetchCompany(companyId) == null) {
				continue;
			}

			Configuration[] ctSettingsConfigurations =
				_configurationAdmin.listConfigurations(
					String.format(
						"(&(%s=%s.scoped)(%s=%d))",
						ConfigurationAdmin.SERVICE_FACTORYPID,
						CTSettingsConfiguration.class.getName(),
						ExtendedObjectClassDefinition.Scope.COMPANY.
							getPropertyKey(),
						companyId));

			Dictionary<String, Object> ctSettingsConfigurationProperties = null;

			if (ctSettingsConfigurations != null) {
				Configuration ctSettingsConfiguration =
					ctSettingsConfigurations[0];

				ctSettingsConfigurationProperties =
					ctSettingsConfiguration.getProperties();
			}

			_configurationProvider.saveCompanyConfiguration(
				CTSettingsConfiguration.class, companyId,
				_mergeProperties(
					ctConflictConfigurationProperties,
					ctSettingsConfigurationProperties));

			ctConflictConfiguration.delete();
		}
	}

	private void _upgradeSystemConfigurations() throws Exception {
		Configuration[] ctConflictConfigurations =
			_configurationAdmin.listConfigurations(
				String.format("(%s=%s)", Constants.SERVICE_PID, _CLASS_NAME));

		if (ctConflictConfigurations == null) {
			return;
		}

		Configuration ctConflictConfiguration = ctConflictConfigurations[0];

		Dictionary<String, Object> ctConflictConfigurationProperties =
			ctConflictConfiguration.getProperties();

		if (ctConflictConfigurationProperties == null) {
			return;
		}

		Configuration ctSettingsConfiguration =
			_configurationAdmin.getConfiguration(
				CTSettingsConfiguration.class.getName(), StringPool.QUESTION);

		Dictionary<String, Object> ctSettingsConfigurationProperties =
			ctSettingsConfiguration.getProperties();

		_configurationProvider.saveSystemConfiguration(
			CTSettingsConfiguration.class,
			_mergeProperties(
				ctConflictConfigurationProperties,
				ctSettingsConfigurationProperties));

		ctConflictConfiguration.delete();
	}

	private static final String _CLASS_NAME =
		"com.liferay.change.tracking.configuration.CTConflictConfiguration";

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationAdmin _configurationAdmin;
	private final ConfigurationProvider _configurationProvider;

}
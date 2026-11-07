/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.upgrade.v1_0_2;

import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.staging.configuration.StagingConfiguration;

import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Tamas Molnar
 */
public class ExportImportServiceConfigurationUpgradeProcess
	extends UpgradeProcess {

	public ExportImportServiceConfigurationUpgradeProcess(
		CompanyLocalService companyLocalService,
		ConfigurationAdmin configurationAdmin,
		ConfigurationProvider configurationProvider) {

		_companyLocalService = companyLocalService;
		_configurationAdmin = configurationAdmin;
		_configurationProvider = configurationProvider;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeExportImportServiceConfiguration();
	}

	private void _upgradeExportImportServiceConfiguration() throws Exception {
		Configuration[] exportImportServiceConfigurations =
			_configurationAdmin.listConfigurations(
				"(service.pid=" +
					ExportImportServiceConfiguration.class.getName() + "*)");

		if (exportImportServiceConfigurations == null) {
			return;
		}

		for (Configuration exportImportServiceConfiguration :
				exportImportServiceConfigurations) {

			Dictionary<String, Object> exportImportProperties =
				exportImportServiceConfiguration.getProperties();

			if (exportImportProperties == null) {
				continue;
			}

			Dictionary<String, Object> stagingProperties =
				HashMapDictionaryBuilder.<String, Object>put(
					"stagingDeleteTempLAROnFailure",
					GetterUtil.getBoolean(
						exportImportProperties.remove(
							"stagingDeleteTempLAROnFailure"),
						true)
				).put(
					"stagingDeleteTempLAROnSuccess",
					GetterUtil.getBoolean(
						exportImportProperties.remove(
							"stagingDeleteTempLarOnSuccess"),
						true)
				).put(
					"stagingUseVirtualHostForRemoteSite",
					GetterUtil.getBoolean(
						exportImportProperties.remove(
							"stagingUseVirtualHostForRemoteSite"))
				).build();

			exportImportServiceConfiguration.update(exportImportProperties);

			long companyId = GetterUtil.getLong(
				exportImportProperties.get("companyId"));

			if (companyId != 0) {
				if (_companyLocalService.fetchCompany(companyId) == null) {
					continue;
				}

				stagingProperties.put("companyId", companyId);

				_configurationProvider.saveCompanyConfiguration(
					StagingConfiguration.class, companyId, stagingProperties);
			}
			else {
				_configurationProvider.saveSystemConfiguration(
					StagingConfiguration.class, stagingProperties);
			}
		}
	}

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationAdmin _configurationAdmin;
	private final ConfigurationProvider _configurationProvider;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal.upgrade.v1_0_0;

import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;

import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Christian Moura
 */
public class AuditConfigurationUpgradeProcess extends UpgradeProcess {

	public AuditConfigurationUpgradeProcess(
		CompanyLocalService companyLocalService,
		ConfigurationAdmin configurationAdmin,
		ConfigurationProvider configurationProvider) {

		_companyLocalService = companyLocalService;
		_configurationAdmin = configurationAdmin;
		_configurationProvider = configurationProvider;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getSystemScopedFilterString(
				AuditConfiguration.class.getName()));

		if (ArrayUtil.isEmpty(configurations)) {
			return;
		}

		Dictionary<String, Object> properties =
			configurations[0].getProperties();

		if (properties == null) {
			return;
		}

		AuditConfiguration defaultAuditConfiguration =
			ConfigurableUtil.createConfigurable(
				AuditConfiguration.class, new HashMapDictionary<>());

		boolean defaultEnabled = defaultAuditConfiguration.enabled();

		boolean enabled = GetterUtil.getBoolean(
			properties.get("enabled"), defaultEnabled);

		if (enabled == defaultEnabled) {
			return;
		}

		_companyLocalService.forEachCompanyId(
			companyId -> {
				if (_hasCompanyConfiguration(companyId)) {
					return;
				}

				_configurationProvider.saveCompanyConfiguration(
					AuditConfiguration.class, companyId,
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", enabled
					).build());
			});
	}

	private boolean _hasCompanyConfiguration(long companyId) throws Exception {
		Company company = _companyLocalService.getCompany(companyId);

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				companyId, AuditConfiguration.class.getName(),
				company.getWebId()));

		return ArrayUtil.isNotEmpty(configurations);
	}

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationAdmin _configurationAdmin;
	private final ConfigurationProvider _configurationProvider;

}
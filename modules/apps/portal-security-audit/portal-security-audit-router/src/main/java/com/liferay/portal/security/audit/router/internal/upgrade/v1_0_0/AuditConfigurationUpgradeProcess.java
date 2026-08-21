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
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

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
		_upgradeAuditConfiguration();
		_upgradePersistentAuditMessageProcessorConfiguration();
	}

	private Dictionary<String, Object> _getSystemProperties(Class<?> clazz)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getSystemScopedFilterString(
				clazz.getName()));

		if (ArrayUtil.isEmpty(configurations)) {
			return null;
		}

		return configurations[0].getProperties();
	}

	private boolean _hasCompanyConfiguration(Class<?> clazz, long companyId)
		throws Exception {

		Company company = _companyLocalService.getCompany(companyId);

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getCompanyScopedFilterString(
				companyId, clazz.getName(), company.getWebId()));

		return ArrayUtil.isNotEmpty(configurations);
	}

	private void _saveCompanyConfigurations(
			Class<?> clazz, Map<String, Object> properties)
		throws Exception {

		if (properties.isEmpty()) {
			return;
		}

		_companyLocalService.forEachCompanyId(
			companyId -> {
				if (_hasCompanyConfiguration(clazz, companyId)) {
					return;
				}

				_configurationProvider.saveCompanyConfiguration(
					clazz, companyId, new HashMapDictionary<>(properties));
			});
	}

	private void _upgradeAuditConfiguration() throws Exception {
		Dictionary<String, Object> systemProperties = _getSystemProperties(
			AuditConfiguration.class);

		if (systemProperties == null) {
			return;
		}

		AuditConfiguration defaultAuditConfiguration =
			ConfigurableUtil.createConfigurable(
				AuditConfiguration.class, new HashMapDictionary<>());

		Map<String, Object> properties = new HashMap<>();

		boolean enabled = GetterUtil.getBoolean(
			systemProperties.get("enabled"),
			defaultAuditConfiguration.enabled());

		if (enabled != defaultAuditConfiguration.enabled()) {
			properties.put("enabled", enabled);
		}

		_saveCompanyConfigurations(AuditConfiguration.class, properties);
	}

	private void _upgradePersistentAuditMessageProcessorConfiguration()
		throws Exception {

		Dictionary<String, Object> systemProperties = _getSystemProperties(
			PersistentAuditMessageProcessorConfiguration.class);

		if (systemProperties == null) {
			return;
		}

		PersistentAuditMessageProcessorConfiguration
			defaultPersistentAuditMessageProcessorConfiguration =
				ConfigurableUtil.createConfigurable(
					PersistentAuditMessageProcessorConfiguration.class,
					new HashMapDictionary<>());

		Map<String, Object> properties = new HashMap<>();

		int bufferSize = GetterUtil.getInteger(
			systemProperties.get("bufferSize"),
			defaultPersistentAuditMessageProcessorConfiguration.bufferSize());

		if (bufferSize !=
				defaultPersistentAuditMessageProcessorConfiguration.
					bufferSize()) {

			properties.put("bufferSize", bufferSize);
		}

		boolean enabled = GetterUtil.getBoolean(
			systemProperties.get("enabled"),
			defaultPersistentAuditMessageProcessorConfiguration.enabled());

		if (enabled !=
				defaultPersistentAuditMessageProcessorConfiguration.enabled()) {

			properties.put("enabled", enabled);
		}

		long flushInterval = GetterUtil.getLong(
			systemProperties.get("flushInterval"),
			defaultPersistentAuditMessageProcessorConfiguration.
				flushInterval());

		if (flushInterval !=
				defaultPersistentAuditMessageProcessorConfiguration.
					flushInterval()) {

			properties.put("flushInterval", flushInterval);
		}

		_saveCompanyConfigurations(
			PersistentAuditMessageProcessorConfiguration.class, properties);
	}

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationAdmin _configurationAdmin;
	private final ConfigurationProvider _configurationProvider;

}
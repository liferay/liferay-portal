/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.internal.operation;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.instances.internal.configuration.ExportPortalInstanceConfiguration;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.felix.cm.file.ConfigurationHandler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mariano Álvaro Sáiz
 */
@Component(
	configurationPid = "com.liferay.portal.instances.internal.configuration.ExportPortalInstanceConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class ExportPortalInstanceOperation extends BasePortalInstanceOperation {

	@Override
	public String getOperationCompletedMessage(long companyId) {
		return "Portal instance with company ID " + companyId +
			" exported successfully";
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		onPortalInstance(
			() -> {
				FeatureFlagManagerUtil.checkEnabled("LPD-11342");

				ExportPortalInstanceConfiguration
					exportPortalInstanceConfiguration =
						ConfigurableUtil.createConfigurable(
							ExportPortalInstanceConfiguration.class,
							properties);

				long companyId =
					exportPortalInstanceConfiguration.exportCompanyId();

				if (_companyLocalService.fetchCompany(companyId) == null) {
					_log.error(
						"Portal instance with company ID " + companyId +
							" does not exist");

					return null;
				}

				Company company = _companyLocalService.exportCompany(companyId);

				_exportConfigurations(companyId);

				return company;
			},
			properties);
	}

	private void _exportConfigurations(long companyId) throws Exception {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		List<ScopedConfiguration> scopedConfigurations = new ArrayList<>();

		Map<String, String> configurations = DBPartitionUtil.getConfigurations(
			CompanyConstants.SYSTEM);

		for (Map.Entry<String, String> entry : configurations.entrySet()) {
			String dictionaryString = entry.getValue();

			ScopedConfiguration scopedConfiguration = _getScopedConfiguration(
				entry.getKey(), dictionaryString);

			if (scopedConfiguration == null) {
				continue;
			}

			if (Objects.equals(
					scopedConfiguration.getScope(),
					ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE)) {

				scopedConfigurations.add(scopedConfiguration);

				continue;
			}

			if (_isApplicable(companyId, scopedConfiguration)) {
				scopedConfigurations.add(scopedConfiguration);
			}
		}

		for (ScopedConfiguration scopedConfiguration : scopedConfigurations) {
			DBPartitionUtil.exportConfiguration(
				companyId, scopedConfiguration.getConfigurationId(),
				scopedConfiguration.getDictionaryString());
		}
	}

	private ScopedConfiguration _getScopedConfiguration(
			String configurationId, String dictionaryString)
		throws Exception {

		Dictionary<String, String> dictionary = ConfigurationHandler.read(
			new UnsyncByteArrayInputStream(
				dictionaryString.getBytes(StringPool.UTF8)));

		Object value = dictionary.get(
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey());

		if (value != null) {
			return new ScopedConfiguration(
				configurationId, dictionaryString, GetterUtil.getLong(value),
				ExtendedObjectClassDefinition.Scope.COMPANY);
		}

		value = dictionary.get(
			ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey());

		if (value != null) {
			return new ScopedConfiguration(
				configurationId, dictionaryString, GetterUtil.getLong(value),
				ExtendedObjectClassDefinition.Scope.GROUP);
		}

		value = dictionary.get(
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey());

		if (value != null) {
			return new ScopedConfiguration(
				configurationId, dictionaryString, GetterUtil.getString(value),
				ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE);
		}

		return null;
	}

	private boolean _isApplicable(
			long companyId, ScopedConfiguration scopedConfiguration)
		throws Exception {

		if (Objects.equals(
				scopedConfiguration.getScope(),
				ExtendedObjectClassDefinition.Scope.COMPANY)) {

			if (companyId == (long)scopedConfiguration.getScopePK()) {
				return true;
			}

			return false;
		}

		if (Objects.equals(
				scopedConfiguration.getScope(),
				ExtendedObjectClassDefinition.Scope.GROUP)) {

			Group group = _groupLocalService.getGroup(
				(long)scopedConfiguration.getScopePK());

			if (group.getCompanyId() == companyId) {
				return true;
			}

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportPortalInstanceOperation.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	private class ScopedConfiguration {

		public ScopedConfiguration(
			String configurationId, String dictionaryString,
			Serializable scopePK, ExtendedObjectClassDefinition.Scope scope) {

			_configurationId = configurationId;
			_dictionaryString = dictionaryString;
			_scopePK = scopePK;
			_scope = scope;
		}

		public String getConfigurationId() {
			return _configurationId;
		}

		public String getDictionaryString() {
			return _dictionaryString;
		}

		public ExtendedObjectClassDefinition.Scope getScope() {
			return _scope;
		}

		public Object getScopePK() {
			return _scopePK;
		}

		private final String _configurationId;
		private final String _dictionaryString;
		private final ExtendedObjectClassDefinition.Scope _scope;
		private final Object _scopePK;

	}

}
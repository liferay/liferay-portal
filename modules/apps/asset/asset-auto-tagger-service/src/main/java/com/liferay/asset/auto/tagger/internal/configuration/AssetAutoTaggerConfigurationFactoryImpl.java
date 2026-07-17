/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.internal.configuration;

import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration;
import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfigurationFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	configurationPid = "com.liferay.asset.auto.tagger.internal.configuration.AssetAutoTaggerSystemConfiguration",
	service = AssetAutoTaggerConfigurationFactory.class
)
public class AssetAutoTaggerConfigurationFactoryImpl
	implements AssetAutoTaggerConfigurationFactory {

	@Override
	public AssetAutoTaggerConfiguration getCompanyAssetAutoTaggerConfiguration(
		Company company) {

		try {
			return new CompanyAssetAutoTaggerConfiguration(company);
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);

			return getSystemAssetAutoTaggerConfiguration();
		}
	}

	@Override
	public AssetAutoTaggerConfiguration getGroupAssetAutoTaggerConfiguration(
		Group group) {

		try {
			return new GroupAssetAutoTaggerConfiguration(group);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return getSystemAssetAutoTaggerConfiguration();
		}
	}

	@Override
	public AssetAutoTaggerConfiguration
		getSystemAssetAutoTaggerConfiguration() {

		return new AssetAutoTaggerConfiguration() {

			@Override
			public int getMaximumNumberOfTagsPerAsset() {
				return _assetAutoTaggerSystemConfiguration.
					maximumNumberOfTagsPerAsset();
			}

			@Override
			public boolean isAvailable() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return _assetAutoTaggerSystemConfiguration.enabled();
			}

			@Override
			public boolean isUpdateAutoTags() {
				return false;
			}

		};
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_assetAutoTaggerSystemConfiguration =
			ConfigurableUtil.createConfigurable(
				AssetAutoTaggerSystemConfiguration.class, properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetAutoTaggerConfigurationFactoryImpl.class);

	private volatile AssetAutoTaggerSystemConfiguration
		_assetAutoTaggerSystemConfiguration;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private class CompanyAssetAutoTaggerConfiguration
		implements AssetAutoTaggerConfiguration {

		public CompanyAssetAutoTaggerConfiguration(Company company)
			throws ConfigurationException {

			_assetAutoTaggerCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AssetAutoTaggerCompanyConfiguration.class,
					company.getCompanyId());
		}

		@Override
		public int getMaximumNumberOfTagsPerAsset() {
			int companyMaximumNumberOfTagsPerAsset =
				_assetAutoTaggerCompanyConfiguration.
					maximumNumberOfTagsPerAsset();

			int systemMaximumNumberOfTagsPerAsset =
				_assetAutoTaggerSystemConfiguration.
					maximumNumberOfTagsPerAsset();

			if ((systemMaximumNumberOfTagsPerAsset > 0) &&
				((companyMaximumNumberOfTagsPerAsset == 0) ||
				 (systemMaximumNumberOfTagsPerAsset <
					 companyMaximumNumberOfTagsPerAsset))) {

				return systemMaximumNumberOfTagsPerAsset;
			}

			return companyMaximumNumberOfTagsPerAsset;
		}

		@Override
		public boolean isAvailable() {
			return _assetAutoTaggerSystemConfiguration.enabled();
		}

		@Override
		public boolean isEnabled() {
			if (!_assetAutoTaggerSystemConfiguration.enabled()) {
				return false;
			}

			return _assetAutoTaggerCompanyConfiguration.enabled();
		}

		@Override
		public boolean isUpdateAutoTags() {
			return _assetAutoTaggerCompanyConfiguration.updateAutoTags();
		}

		private final AssetAutoTaggerCompanyConfiguration
			_assetAutoTaggerCompanyConfiguration;

	}

	private class GroupAssetAutoTaggerConfiguration
		implements AssetAutoTaggerConfiguration {

		public GroupAssetAutoTaggerConfiguration(Group group)
			throws PortalException {

			_group = group;

			_assetAutoTaggerConfiguration =
				new CompanyAssetAutoTaggerConfiguration(
					_companyLocalService.getCompany(group.getCompanyId()));
		}

		@Override
		public int getMaximumNumberOfTagsPerAsset() {
			return _assetAutoTaggerConfiguration.
				getMaximumNumberOfTagsPerAsset();
		}

		@Override
		public boolean isAvailable() {
			return _assetAutoTaggerConfiguration.isEnabled();
		}

		@Override
		public boolean isEnabled() {
			try {
				if (!_assetAutoTaggerConfiguration.isEnabled()) {
					return false;
				}

				UnicodeProperties typeSettingsUnicodeProperties =
					_group.getTypeSettingsProperties();

				String assetAutoTaggingEnabledString =
					typeSettingsUnicodeProperties.get(
						"assetAutoTaggingEnabled");

				if (assetAutoTaggingEnabledString != null) {
					return GetterUtil.getBoolean(assetAutoTaggingEnabledString);
				}

				AssetAutoTaggerGroupConfiguration
					assetAutoTaggerGroupConfiguration =
						_configurationProvider.getGroupConfiguration(
							AssetAutoTaggerGroupConfiguration.class,
							_group.getCompanyId(), _group.getGroupId());

				return assetAutoTaggerGroupConfiguration.enabled();
			}
			catch (ConfigurationException configurationException) {
				if (_log.isDebugEnabled()) {
					_log.debug(configurationException);
				}

				return _assetAutoTaggerConfiguration.isEnabled();
			}
		}

		@Override
		public boolean isUpdateAutoTags() {
			return _assetAutoTaggerConfiguration.isUpdateAutoTags();
		}

		private final AssetAutoTaggerConfiguration
			_assetAutoTaggerConfiguration;
		private final Group _group;

	}

}
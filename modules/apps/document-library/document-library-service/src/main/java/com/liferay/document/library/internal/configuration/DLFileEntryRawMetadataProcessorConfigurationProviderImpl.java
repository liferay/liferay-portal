/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration;

import com.liferay.document.library.configuration.DLFileEntryRawMetadataProcessorConfiguration;
import com.liferay.document.library.configuration.DLFileEntryRawMetadataProcessorConfigurationProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = DLFileEntryRawMetadataProcessorConfigurationProvider.class)
public class DLFileEntryRawMetadataProcessorConfigurationProviderImpl
	implements DLFileEntryRawMetadataProcessorConfigurationProvider {

	@Override
	public String[] getCompanyExcludedMimeTypes(long companyId)
		throws ConfigurationException {

		DLFileEntryRawMetadataProcessorConfiguration
			dlFileEntryRawMetadataProcessorConfiguration =
				_configurationProvider.getCompanyConfiguration(
					DLFileEntryRawMetadataProcessorConfiguration.class,
					companyId);

		String[] excludedMimeTypes =
			dlFileEntryRawMetadataProcessorConfiguration.excludedMimeTypes();

		if (excludedMimeTypes == null) {
			excludedMimeTypes = StringPool.EMPTY_ARRAY;
		}

		return ArrayUtil.append(
			getSystemExcludedMimeTypes(), excludedMimeTypes);
	}

	@Override
	public String[] getGroupExcludedMimeTypes(long groupId)
		throws ConfigurationException {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return getCompanyExcludedMimeTypes(
				CompanyThreadLocal.getCompanyId());
		}

		DLFileEntryRawMetadataProcessorConfiguration
			dlFileEntryRawMetadataProcessorConfiguration =
				_configurationProvider.getGroupConfiguration(
					DLFileEntryRawMetadataProcessorConfiguration.class,
					group.getGroupId());

		String[] excludedMimeTypes =
			dlFileEntryRawMetadataProcessorConfiguration.excludedMimeTypes();

		if (excludedMimeTypes == null) {
			excludedMimeTypes = StringPool.EMPTY_ARRAY;
		}

		return ArrayUtil.append(
			getCompanyExcludedMimeTypes(CompanyThreadLocal.getCompanyId()),
			excludedMimeTypes);
	}

	@Override
	public String[] getSystemExcludedMimeTypes() throws ConfigurationException {
		DLFileEntryRawMetadataProcessorConfiguration
			dlFileEntryRawMetadataProcessorConfiguration =
				_configurationProvider.getSystemConfiguration(
					DLFileEntryRawMetadataProcessorConfiguration.class);

		String[] excludedMimeTypes =
			dlFileEntryRawMetadataProcessorConfiguration.excludedMimeTypes();

		if (excludedMimeTypes == null) {
			excludedMimeTypes = StringPool.EMPTY_ARRAY;
		}

		return excludedMimeTypes;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

}
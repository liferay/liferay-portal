/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.configuration;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Stefano Motta
 */
@ProviderType
public interface DLFileEntryRawMetadataProcessorConfigurationProvider {

	public String[] getCompanyExcludedMimeTypes(long companyId)
		throws ConfigurationException;

	public String[] getGroupExcludedMimeTypes(long groupId)
		throws ConfigurationException;

	public String[] getSystemExcludedMimeTypes() throws ConfigurationException;

}
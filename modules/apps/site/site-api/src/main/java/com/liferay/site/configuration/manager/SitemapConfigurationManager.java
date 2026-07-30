/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.configuration.manager;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Lourdes Fernández Besada
 */
@ProviderType
public interface SitemapConfigurationManager {

	public Long[] getCompanySitemapGroupIds(long companyId) throws Exception;

	public Long[] getCompanySitemapObjectDefinitionIds(long companyId)
		throws ConfigurationException;

	public String getXMLSitemapIndexMode(long companyId)
		throws ConfigurationException;

	public String getXMLSitemapRegenerationDayOfWeek(long companyId)
		throws ConfigurationException;

	public long getXMLSitemapRegenerationDelay(long companyId)
		throws ConfigurationException;

	public String getXMLSitemapRegenerationFrequency(long companyId)
		throws ConfigurationException;

	public String getXMLSitemapRegenerationTime(long companyId)
		throws ConfigurationException;

	public String getXMLSitemapRegenerationTimeZoneId(long companyId)
		throws ConfigurationException;

	public boolean includeCategoriesCompanyEnabled(long companyId)
		throws ConfigurationException;

	public boolean includeCategoriesGroupEnabled(long companyId, long groupId)
		throws ConfigurationException;

	public boolean includePagesCompanyEnabled(long companyId)
		throws ConfigurationException;

	public boolean includePagesGroupEnabled(long companyId, long groupId)
		throws ConfigurationException;

	public boolean includeWebContentCompanyEnabled(long companyId)
		throws ConfigurationException;

	public boolean includeWebContentGroupEnabled(long companyId, long groupId)
		throws ConfigurationException;

	public boolean isCachedGenerationCompanyEnabled(long companyId)
		throws ConfigurationException;

	public boolean isIndexModeAssetTypeCompanyEnabled(long companyId)
		throws ConfigurationException;

	public boolean isObjectDefinitionCompanyIncluded(
			long companyId, String objectDefinitionId)
		throws ConfigurationException;

	public boolean isXMLSitemapIndexCompanyEnabled(long companyId)
		throws ConfigurationException;

	public void saveSitemapCompanyConfiguration(
			boolean cachedGenerationEnabled, long companyId,
			long[] companySitemapGroupIds,
			long[] companySitemapObjectDefinitionIds, boolean includeCategories,
			boolean includePages, boolean includeWebContent,
			boolean xmlSitemapIndexEnabled, String xmlSitemapIndexMode,
			String xmlSitemapRegenerationDayOfWeek,
			String xmlSitemapRegenerationFrequency,
			String xmlSitemapRegenerationTime,
			String xmlSitemapRegenerationTimeZoneId)
		throws ConfigurationException;

	public void saveSitemapGroupConfiguration(
			long groupId, boolean includeCategories, boolean includePages,
			boolean includeWebContent)
		throws ConfigurationException;

}
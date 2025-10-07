/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.helper;

import aQute.bnd.annotation.ProviderType;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPQuery;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.List;
import java.util.Locale;

/**
 * @author Marco Leo
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@ProviderType
public interface CPDefinitionHelper {

	public CPCatalogEntry getCPCatalogEntry(Document document, Locale locale);

	public CPCatalogEntry getCPCatalogEntry(
			long commerceAccountId, long groupId, long cpDefinitionId,
			Locale locale)
		throws PortalException;

	public CPCatalogEntry getCPCatalogEntry(
			long commerceAccountId, long groupId, long cpDefinitionId,
			Locale locale, boolean secure)
		throws PortalException;

	public String getDefaultImageFileURL(
			long commerceAccountId, long cpDefinitionId)
		throws PortalException;

	public String getFriendlyURL(long cpDefinitionId, ThemeDisplay themeDisplay)
		throws PortalException;

	public CPDataSourceResult search(
			long groupId, SearchContext searchContext, CPQuery cpQuery,
			int start, int end)
		throws PortalException;

	public long searchCount(
			long groupId, SearchContext searchContext, CPQuery cpQuery)
		throws PortalException;

	public long searchCount(
			long[] groupIds, SearchContext searchContext, CPQuery cpQuery)
		throws PortalException;

	public List<CPDefinition> searchCPDefinitions(
			long groupId, SearchContext searchContext, CPQuery cpQuery,
			int start, int end)
		throws PortalException;

	public List<CPDefinition> searchCPDefinitions(
			long[] groupIds, SearchContext searchContext, CPQuery cpQuery,
			int start, int end)
		throws PortalException;

}
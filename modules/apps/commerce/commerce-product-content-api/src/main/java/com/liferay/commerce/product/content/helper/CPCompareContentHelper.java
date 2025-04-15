/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.helper;

import aQute.bnd.annotation.ProviderType;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Alessio Antonio Rendina
 */
@ProviderType
public interface CPCompareContentHelper {

	public Set<CPSpecificationOption> getCategorizedCPSpecificationOptions(
			CPDataSourceResult cpDataSourceResult)
		throws PortalException;

	public String getCompareContentPortletNamespace();

	public String getCompareProductsURL(ThemeDisplay themeDisplay)
		throws PortalException;

	public List<CPCatalogEntry> getCPCatalogEntries(
			long groupId, long commerceAccountId,
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public Set<String> getCPDefinitionOptionRelNames(
			CPDataSourceResult cpDataSourceResult, Locale locale)
		throws PortalException;

	public String getCPDefinitionOptionValueRels(
			CPCatalogEntry cpCatalogEntry, String cpDefinitionOptionRelName,
			Locale locale)
		throws PortalException;

	public String getCPDefinitionSpecificationOptionValue(
		long cpDefinitionId, long cpSpecificationOptionId, Locale locale);

	public List<CPOptionCategory> getCPOptionCategories(long groupId);

	public Set<CPSpecificationOption> getCPSpecificationOptions(
			CPDataSourceResult cpDataSourceResult)
		throws PortalException;

	public String getDefaultImageFileURL(
			long commerceAccountId, long cpDefinitionId)
		throws PortalException;

	public String getDeleteCompareProductURL(
		long cpDefinitionId, RenderRequest renderRequest,
		RenderResponse renderResponse);

	public String getDimensionCPMeasurementUnitName(
		long groupId, Locale locale);

	public int getProductsLimit(PortletDisplay portletDisplay)
		throws PortalException;

	public boolean hasCategorizedCPDefinitionSpecificationOptionValues(
			CPDataSourceResult cpDataSourceResult, long cpOptionCategoryId)
		throws PortalException;

}
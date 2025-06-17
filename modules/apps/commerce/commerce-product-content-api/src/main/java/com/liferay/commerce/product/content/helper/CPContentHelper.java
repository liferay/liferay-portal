/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.helper;

import aQute.bnd.annotation.ProviderType;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.content.render.CPContentRenderer;
import com.liferay.commerce.product.content.util.CPMedia;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.type.CPType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 * @author Ivica Cardic
 */
@ProviderType
public interface CPContentHelper {

	public JSONObject getAvailabilityContentContributorValueJSONObject(
			CPCatalogEntry cpCatalogEntry,
			HttpServletRequest httpServletRequest)
		throws Exception;

	public String getAvailabilityEstimateLabel(
			HttpServletRequest httpServletRequest)
		throws Exception;

	public String getAvailabilityLabel(HttpServletRequest httpServletRequest)
		throws Exception;

	public List<CPDefinitionSpecificationOptionValue>
			getCategorizedCPDefinitionSpecificationOptionValues(
				long cpDefinitionId, long cpOptionCategoryId)
		throws PortalException;

	public CPCatalogEntry getCPCatalogEntry(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public JSONObject getCPContentContributorValueJSONObject(
			String contributorKey, CPCatalogEntry cpCatalogEntry,
			HttpServletRequest httpServletRequest)
		throws Exception;

	public JSONObject getCPContentContributorValueJSONObject(
			String contributorKey, HttpServletRequest httpServletRequest)
		throws Exception;

	public String getCPContentRendererKey(
		String type, RenderRequest renderRequest);

	public List<CPContentRenderer> getCPContentRenderers(String cpType);

	public String getCPDefinitionCDNURL(
			long cpDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception;

	public FileVersion getCPDefinitionImageFileVersion(
			long cpDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception;

	public List<CPDefinitionSpecificationOptionValue>
			getCPDefinitionSpecificationOptionValues(long cpDefinitionId)
		throws PortalException;

	public List<CPMedia> getCPMedias(
			long cpDefinitionId, ThemeDisplay themeDisplay)
		throws PortalException;

	public List<CPOptionCategory> getCPOptionCategories(long companyId);

	public List<CPType> getCPTypes();

	public CPInstance getDefaultCPInstance(CPCatalogEntry cpCatalogEntry)
		throws Exception;

	public CPInstance getDefaultCPInstance(
			HttpServletRequest httpServletRequest)
		throws Exception;

	public CPSku getDefaultCPSku(CPCatalogEntry cpCatalogEntry)
		throws Exception;

	public String getDefaultImageFileURL(
			long commerceAccountId, long cpDefinitionId)
		throws PortalException;

	public String getDownloadFileEntryURL(
			FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getFriendlyURL(
			CPCatalogEntry cpCatalogEntry, ThemeDisplay themeDisplay)
		throws PortalException;

	public List<CPMedia> getImages(
			long cpDefinitionId, boolean gallery, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getImageURL(FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws Exception;

	public String getIncomingQuantityLabel(
			long companyId, Locale locale, String sku, String unitOfMeasureKey,
			User user)
		throws PortalException;

	public BigDecimal getMinOrderQuantity(long cpDefinitionId);

	public String getReplacementCommerceProductFriendlyURL(
			CPSku cpSku, ThemeDisplay themeDisplay)
		throws PortalException;

	public String getStockQuantity(HttpServletRequest httpServletRequest)
		throws Exception;

	public String getStockQuantityLabel(HttpServletRequest httpServletRequest)
		throws Exception;

	public String getSubscriptionInfoLabel(
			HttpServletRequest httpServletRequest)
		throws Exception;

	public ResourceURL getViewAttachmentURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException;

	public boolean hasChildCPDefinitions(long cpDefinitionId);

	public boolean hasCPDefinitionOptionRels(long cpDefinitionId);

	public boolean hasCPDefinitionSpecificationOptionValues(long cpDefinitionId)
		throws PortalException;

	public boolean hasMultipleCPSkus(CPCatalogEntry cpCatalogEntry)
		throws Exception;

	public boolean hasReplacement(
			CPSku cpSku, HttpServletRequest httpServletRequest)
		throws Exception;

	public boolean hasRequiredCPDefinitionOptionRels(long cpDefinitionId);

	public boolean isDirectReplacement(CPSku cpSku) throws Exception;

	public boolean isInWishList(
			CPSku cpSku, CPCatalogEntry cpCatalogEntry,
			ThemeDisplay themeDisplay)
		throws Exception;

	public void renderCPType(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

	public void renderOptions(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.frontend.data.set.provider;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.product.asset.categories.web.internal.constants.CommerceProductAssetCategoriesFDSNames;
import com.liferay.commerce.product.asset.categories.web.internal.model.CategoryDisplayPage;
import com.liferay.commerce.product.constants.CPDisplayLayoutConstants;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDisplayLayoutService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceProductAssetCategoriesFDSNames.CATEGORY_DISPLAY_PAGES,
	service = FDSDataProvider.class
)
public class CommerceCategoryDisplayPageFDSDataProvider
	implements FDSDataProvider<CategoryDisplayPage> {

	@Override
	public List<CategoryDisplayPage> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		List<CategoryDisplayPage> categoryDisplayPages = new ArrayList<>();

		BaseModelSearchResult<CPDisplayLayout>
			cpDisplayLayoutBaseModelSearchResult =
				_cpDisplayLayoutService.searchCPDisplayLayout(
					commerceChannel.getCompanyId(),
					commerceChannel.getSiteGroupId(),
					AssetCategory.class.getName(),
					CPDisplayLayoutConstants.TYPE_LAYOUT,
					fdsKeywords.getKeywords(), fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition(), sort);

		for (CPDisplayLayout cpDisplayLayout :
				cpDisplayLayoutBaseModelSearchResult.getBaseModels()) {

			categoryDisplayPages.add(
				new CategoryDisplayPage(
					cpDisplayLayout.getCPDisplayLayoutId(),
					_getCategoryName(cpDisplayLayout),
					_getLayout(cpDisplayLayout, themeDisplay.getLanguageId())));
		}

		return categoryDisplayPages;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		BaseModelSearchResult<CPDisplayLayout>
			cpDisplayLayoutBaseModelSearchResult =
				_cpDisplayLayoutService.searchCPDisplayLayout(
					commerceChannel.getCompanyId(),
					commerceChannel.getSiteGroupId(),
					AssetCategory.class.getName(),
					CPDisplayLayoutConstants.TYPE_LAYOUT,
					fdsKeywords.getKeywords(), 0, 0, null);

		return cpDisplayLayoutBaseModelSearchResult.getLength();
	}

	private String _getCategoryName(CPDisplayLayout cpDisplayLayout) {
		AssetCategory assetCategory = cpDisplayLayout.fetchAssetCategory();

		if (assetCategory == null) {
			return StringPool.BLANK;
		}

		return assetCategory.getName();
	}

	private String _getLayout(
		CPDisplayLayout cpDisplayLayout, String languageId) {

		Layout layout = cpDisplayLayout.fetchLayout();

		if (layout == null) {
			return StringPool.BLANK;
		}

		return layout.getName(languageId);
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CPDisplayLayoutService _cpDisplayLayoutService;

}
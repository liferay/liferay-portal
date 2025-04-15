/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.provider;

import com.liferay.commerce.product.definitions.web.internal.constants.CommerceProductFDSNames;
import com.liferay.commerce.product.definitions.web.internal.model.ProductDisplayPage;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDisplayLayoutService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_DISPLAY_PAGES,
	service = FDSDataProvider.class
)
public class CommerceProductDisplayPageFDSDataProvider
	implements FDSDataProvider<ProductDisplayPage> {

	@Override
	public List<ProductDisplayPage> getItems(
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

		List<ProductDisplayPage> productDisplayPages = new ArrayList<>();

		BaseModelSearchResult<CPDisplayLayout>
			cpDisplayLayoutBaseModelSearchResult =
				_cpDisplayLayoutService.searchCPDisplayLayout(
					commerceChannel.getCompanyId(),
					commerceChannel.getSiteGroupId(),
					CPDefinition.class.getName(), null,
					fdsKeywords.getKeywords(), fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition(), sort);

		for (CPDisplayLayout cpDisplayLayout :
				cpDisplayLayoutBaseModelSearchResult.getBaseModels()) {

			productDisplayPages.add(
				new ProductDisplayPage(
					_getName(
						commerceChannel, cpDisplayLayout,
						themeDisplay.getLanguageId()),
					cpDisplayLayout.getCPDisplayLayoutId(),
					_getProductName(
						cpDisplayLayout, themeDisplay.getLanguageId()),
					_getType(cpDisplayLayout, themeDisplay.getLocale())));
		}

		return productDisplayPages;
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
					CPDefinition.class.getName(), null,
					fdsKeywords.getKeywords(), 0, 0, null);

		return cpDisplayLayoutBaseModelSearchResult.getLength();
	}

	private String _getName(
		CommerceChannel commerceChannel, CPDisplayLayout cpDisplayLayout,
		String languageId) {

		if (Validator.isNotNull(cpDisplayLayout.getLayoutUuid())) {
			Layout layout = cpDisplayLayout.fetchLayout();

			if (layout == null) {
				return StringPool.BLANK;
			}

			return layout.getName(languageId);
		}
		else if (Validator.isNotNull(
					cpDisplayLayout.getLayoutPageTemplateEntryUuid())) {

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.
					fetchLayoutPageTemplateEntryByUuidAndGroupId(
						cpDisplayLayout.getLayoutPageTemplateEntryUuid(),
						commerceChannel.getSiteGroupId());

			if (layoutPageTemplateEntry == null) {
				return StringPool.BLANK;
			}

			return layoutPageTemplateEntry.getName();
		}

		return StringPool.BLANK;
	}

	private String _getProductName(
		CPDisplayLayout cpDisplayLayout, String languageId) {

		CPDefinition cpDefinition = cpDisplayLayout.fetchCPDefinition();

		if (cpDefinition == null) {
			return StringPool.BLANK;
		}

		return cpDefinition.getName(languageId);
	}

	private String _getType(CPDisplayLayout cpDisplayLayout, Locale locale) {
		if (Validator.isNotNull(cpDisplayLayout.getLayoutUuid())) {
			return _language.get(locale, "layout");
		}
		else if (Validator.isNotNull(
					cpDisplayLayout.getLayoutPageTemplateEntryUuid())) {

			return _language.get(locale, "display-page-template");
		}

		return StringPool.BLANK;
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CPDisplayLayoutService _cpDisplayLayoutService;

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

}
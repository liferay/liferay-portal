/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.site.provider;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPQuery;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.helper.CPDefinitionHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.provider.SitemapURLProvider;
import com.liferay.site.provider.helper.SitemapURLProviderHelper;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(service = SitemapURLProvider.class)
public class CPDefinitionSitemapURLProvider implements SitemapURLProvider {

	@Override
	public String getClassName() {
		return CPDefinition.class.getName();
	}

	@Override
	public void visitLayout(
			Element element, String layoutUuid, LayoutSet layoutSet,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layoutUuid, layoutSet.getGroupId(), layoutSet.isPrivateLayout());

		if (layout == null) {
			return;
		}

		if (SitemapURLProviderUtil.hasPortletId(
				layout, CPPortletKeys.CP_CONTENT_WEB)) {

			long groupId =
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(
						layoutSet.getGroupId());

			AccountEntry accountEntry =
				_commerceAccountHelper.getCurrentAccountEntry(
					groupId, themeDisplay.getRequest());

			SearchContext searchContext = new SearchContext();

			searchContext.setAttributes(
				HashMapBuilder.<String, Serializable>put(
					Field.STATUS, WorkflowConstants.STATUS_APPROVED
				).put(
					"commerceAccountGroupIds",
					_accountGroupLocalService.getAccountGroupIds(
						accountEntry.getAccountEntryId())
				).put(
					"commerceChannelGroupId", groupId
				).build());
			searchContext.setCompanyId(themeDisplay.getCompanyId());

			CPQuery cpQuery = new CPQuery();

			cpQuery.setOrderByCol1("title");
			cpQuery.setOrderByCol2("modifiedDate");
			cpQuery.setOrderByType1("ASC");
			cpQuery.setOrderByType2("DESC");

			CPDataSourceResult cpDataSourceResult = _cpDefinitionHelper.search(
				groupId, searchContext, cpQuery, -1, -1);

			for (CPCatalogEntry cpCatalogEntry :
					cpDataSourceResult.getCPCatalogEntries()) {

				visitLayout(
					element, layout, cpCatalogEntry.getCPDefinitionId(),
					themeDisplay);
			}
		}
	}

	@Override
	public void visitLayoutSet(
			Element element, LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {
	}

	protected void visitLayout(
			Element element, Layout layout, long cpDefinitionId,
			ThemeDisplay themeDisplay)
		throws PortalException {

		if (layout.isSystem() ||
			_sitemapURLProviderHelper.isExcludeLayoutFromSitemap(layout)) {

			return;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String currentSiteURL = _portal.getGroupFriendlyURL(
			layout.getLayoutSet(), themeDisplay, false, false);
		String urlSeparator = _cpFriendlyURL.getProductURLSeparator(
			themeDisplay.getCompanyId());

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(CProduct.class),
				cpDefinition.getCProductId());

		currentSiteURL = StringBundler.concat(
			currentSiteURL, urlSeparator, friendlyURLEntry.getUrlTitle());

		Map<Locale, String> alternateFriendlyURLs =
			SitemapURLProviderUtil.getAlternateFriendlyURLs(
				_portal.getAlternateURLs(
					currentSiteURL, themeDisplay, layout,
					_language.getAvailableLocales(layout.getGroupId())),
				friendlyURLEntry.getFriendlyURLEntryId(),
				_friendlyURLEntryLocalService);

		String productFriendlyURL = alternateFriendlyURLs.get(
			_portal.getLocale(themeDisplay.getRequest()));

		for (String alternateFriendlyURL : alternateFriendlyURLs.values()) {
			_sitemapManager.addURLElement(
				element, alternateFriendlyURL, typeSettingsUnicodeProperties,
				layout.getModifiedDate(), productFriendlyURL,
				alternateFriendlyURLs);
		}
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPFriendlyURL _cpFriendlyURL;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SitemapManager _sitemapManager;

	@Reference
	private SitemapURLProviderHelper _sitemapURLProviderHelper;

}
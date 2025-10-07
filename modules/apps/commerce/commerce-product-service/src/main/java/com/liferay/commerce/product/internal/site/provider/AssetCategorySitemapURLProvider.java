/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.site.provider;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.provider.SitemapURLProvider;
import com.liferay.site.provider.helper.SitemapURLProviderHelper;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(service = SitemapURLProvider.class)
public class AssetCategorySitemapURLProvider implements SitemapURLProvider {

	@Override
	public String getClassName() {
		return AssetCategory.class.getName();
	}

	@Override
	public boolean isInclude(long companyId, long groupId)
		throws PortalException {

		return _sitemapConfigurationManager.includeCategoriesGroupEnabled(
			companyId, groupId);
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
				layout, CPPortletKeys.CP_CATEGORY_CONTENT_WEB)) {

			Group group = layoutSet.getGroup();

			Company company = _companyLocalService.getCompany(
				group.getCompanyId());

			List<AssetVocabulary> assetVocabularies =
				_assetVocabularyService.getGroupVocabularies(
					company.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null);

			for (AssetVocabulary assetVocabulary : assetVocabularies) {
				if (assetVocabulary.getVisibilityType() !=
						AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL) {

					List<AssetCategory> assetCategories =
						_assetCategoryService.getVocabularyCategories(
							assetVocabulary.getVocabularyId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

					for (AssetCategory assetCategory : assetCategories) {
						visitLayout(
							element, layout, assetCategory.getCategoryId(),
							themeDisplay);
					}
				}
			}
		}
	}

	@Override
	public void visitLayoutSet(
			Element element, LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {
	}

	protected void visitLayout(
			Element element, Layout layout, long assetCategoryId,
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

		String urlSeparator = _cpFriendlyURL.getAssetCategoryURLSeparator(
			themeDisplay.getCompanyId());

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(AssetCategory.class), assetCategoryId);

		currentSiteURL = StringBundler.concat(
			currentSiteURL, urlSeparator, friendlyURLEntry.getUrlTitle());

		Map<Locale, String> alternateFriendlyURLs =
			SitemapURLProviderUtil.getAlternateFriendlyURLs(
				_portal.getAlternateURLs(
					currentSiteURL, themeDisplay, layout,
					_language.getAvailableLocales(layout.getGroupId())),
				friendlyURLEntry.getFriendlyURLEntryId(),
				_friendlyURLEntryLocalService);

		String categoryFriendlyURL = alternateFriendlyURLs.get(
			_portal.getLocale(themeDisplay.getRequest()));

		for (String alternateFriendlyURL : alternateFriendlyURLs.values()) {
			_sitemapManager.addURLElement(
				element, alternateFriendlyURL, typeSettingsUnicodeProperties,
				layout.getModifiedDate(), categoryFriendlyURL,
				alternateFriendlyURLs);
		}
	}

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference
	private CompanyLocalService _companyLocalService;

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
	private SitemapConfigurationManager _sitemapConfigurationManager;

	@Reference
	private SitemapManager _sitemapManager;

	@Reference
	private SitemapURLProviderHelper _sitemapURLProviderHelper;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.product.asset.categories.web.internal.constants.CPAssetCategoriesWebKeys;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "screen.navigation.entry.order:Integer=20",
	service = ScreenNavigationEntry.class
)
public class CategoryCPFriendlyURLScreenNavigationEntry
	extends CategoryCPFriendlyURLScreenNavigationCategory
	implements ScreenNavigationEntry<AssetCategory> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, AssetCategory assetCategory) {
		if (assetCategory == null) {
			return false;
		}

		return true;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		AssetCategory assetCategory = null;
		long categoryId = ParamUtil.getLong(httpServletRequest, "categoryId");
		long classNameId = _portal.getClassNameId(AssetCategory.class);
		String commerceFriendlyURLBase = StringPool.BLANK;
		String commerceFriendlyURLSeparator = StringPool.BLANK;
		String siteFriendlyURLBase = StringPool.BLANK;
		String siteFriendlyURLSeparator = StringPool.BLANK;
		String titleMapAsXML = StringPool.BLANK;
		String urlTitle = StringPool.BLANK;

		try {
			assetCategory = _assetCategoryService.fetchCategory(categoryId);

			if (assetCategory != null) {
				FriendlyURLEntry friendlyURLEntry =
					_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
						classNameId, categoryId);

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				Locale siteDefaultLocale = themeDisplay.getSiteDefaultLocale();

				titleMapAsXML = friendlyURLEntry.getUrlTitleMapAsXML();
				urlTitle = friendlyURLEntry.getUrlTitle(
					LocaleUtil.toLanguageId(siteDefaultLocale));

				commerceFriendlyURLSeparator =
					_cpFriendlyURL.getAssetCategoryURLSeparator(
						themeDisplay.getCompanyId());

				String groupFriendlyURL = _getGroupFriendlyURL(themeDisplay);

				commerceFriendlyURLBase =
					groupFriendlyURL + commerceFriendlyURLSeparator;

				LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
					_layoutDisplayPageProviderRegistry.
						getLayoutDisplayPageProviderByClassName(
							themeDisplay.getCompanyId(),
							AssetCategory.class.getName());

				siteFriendlyURLBase = _getSiteFriendlyURLBase(
					assetCategory, groupFriendlyURL, layoutDisplayPageProvider,
					siteDefaultLocale);
				siteFriendlyURLSeparator =
					layoutDisplayPageProvider.getURLSeparator();
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		httpServletRequest.setAttribute(
			CPAssetCategoriesWebKeys.ASSET_CATEGORY, assetCategory);
		httpServletRequest.setAttribute(
			CPAssetCategoriesWebKeys.COMMERCE_FRIENDLY_URL_BASE,
			commerceFriendlyURLBase);
		httpServletRequest.setAttribute(
			CPAssetCategoriesWebKeys.COMMERCE_FRIENDLY_URL_SEPARATOR,
			commerceFriendlyURLSeparator);
		httpServletRequest.setAttribute(
			CPAssetCategoriesWebKeys.SITE_FRIENDLY_URL_BASE,
			siteFriendlyURLBase);
		httpServletRequest.setAttribute(
			CPAssetCategoriesWebKeys.SITE_FRIENDLY_URL_SEPARATOR,
			siteFriendlyURLSeparator);
		httpServletRequest.setAttribute(
			CPAssetCategoriesWebKeys.TITLE_MAP_AS_XML, titleMapAsXML);
		httpServletRequest.setAttribute(
			CPAssetCategoriesWebKeys.URL_TITLE, urlTitle);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/friendly_url.jsp");
	}

	private String _getGroupFriendlyURL(ThemeDisplay themeDisplay)
		throws PortalException {

		Group group = _groupLocalService.getGroup(
			themeDisplay.getScopeGroupId());

		return _portal.getGroupFriendlyURL(
			group.getPublicLayoutSet(), themeDisplay, false, false);
	}

	private String _getSiteFriendlyURLBase(
		AssetCategory assetCategory, String groupFriendlyURL,
		LayoutDisplayPageProvider<?> layoutDisplayPageProvider,
		Locale siteDefaultLocale) {

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					AssetCategory.class.getName(),
					assetCategory.getCategoryId()));

		String urlTitle = layoutDisplayPageObjectProvider.getURLTitle(
			siteDefaultLocale);

		return groupFriendlyURL + layoutDisplayPageProvider.getURLSeparator() +
			urlTitle.substring(0, urlTitle.lastIndexOf(CharPool.SLASH) + 1);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CategoryCPFriendlyURLScreenNavigationEntry.class);

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private CPFriendlyURL _cpFriendlyURL;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.asset.categories.web)"
	)
	private ServletContext _servletContext;

}
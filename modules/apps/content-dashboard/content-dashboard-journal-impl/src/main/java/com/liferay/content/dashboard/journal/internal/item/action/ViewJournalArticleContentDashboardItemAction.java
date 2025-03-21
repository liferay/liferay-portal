/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.action;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Cristina González
 */
public class ViewJournalArticleContentDashboardItemAction
	implements ContentDashboardItemAction {

	public ViewJournalArticleContentDashboardItemAction(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		HttpServletRequest httpServletRequest, JournalArticle journalArticle,
		Language language,
		LayoutDisplayPageProviderRegistry layoutDisplayPageProviderRegistry,
		LayoutLocalService layoutLocalService,
		LayoutSEOLinkManager layoutSEOLinkManager, Portal portal) {

		_httpServletRequest = httpServletRequest;
		_journalArticle = journalArticle;
		_language = language;
		_layoutDisplayPageProviderRegistry = layoutDisplayPageProviderRegistry;
		_layoutLocalService = layoutLocalService;
		_layoutSEOLinkManager = layoutSEOLinkManager;
		_portal = portal;
	}

	@Override
	public String getIcon() {
		return "view";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "view");
	}

	@Override
	public String getName() {
		return "view";
	}

	@Override
	public Type getType() {
		return Type.VIEW;
	}

	@Override
	public String getURL() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _getViewURL(themeDisplay.getLocale(), themeDisplay);
	}

	@Override
	public String getURL(Locale locale) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _getViewURL(locale, themeDisplay);
	}

	private Layout _getLayout(
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider) {

		if (layoutDisplayPageObjectProvider == null) {
			return null;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
				layoutDisplayPageObjectProvider.getGroupId(),
				layoutDisplayPageObjectProvider.getClassNameId(),
				layoutDisplayPageObjectProvider.getClassPK(),
				layoutDisplayPageObjectProvider.getClassTypeId());

		if (layoutPageTemplateEntry == null) {
			return null;
		}

		return _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());
	}

	private LayoutDisplayPageObjectProvider<JournalArticle>
		_getLayoutDisplayPageObjectProvider(JournalArticle journalArticle) {

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					JournalArticle.class.getName());

		if (layoutDisplayPageProvider == null) {
			return null;
		}

		return (LayoutDisplayPageObjectProvider<JournalArticle>)
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey()));
	}

	private String _getLocalizedURL(
		Locale locale, List<LayoutSEOLink> localizedLayoutSEOLinks) {

		List<LayoutSEOLink> layoutSEOLinks = new ArrayList<>();

		ListUtil.filter(
			localizedLayoutSEOLinks, layoutSEOLinks,
			seoLink -> Objects.equals(
				LocaleUtil.toW3cLanguageId(locale), seoLink.getHrefLang()));

		LayoutSEOLink localizedLayoutSEOLink = layoutSEOLinks.get(0);

		return localizedLayoutSEOLink.getHref();
	}

	private String _getViewURL(Locale locale, ThemeDisplay themeDisplay) {
		if (themeDisplay == null) {
			return StringPool.BLANK;
		}

		Layout layout = _getLayout(
			_getLayoutDisplayPageObjectProvider(_journalArticle));

		if (layout == null) {
			return StringPool.BLANK;
		}

		String url = null;

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		LayoutDisplayPageObjectProvider<?>
			initialLayoutDisplayPageObjectProvider =
				(LayoutDisplayPageObjectProvider<?>)
					httpServletRequest.getAttribute(
						LayoutDisplayPageWebKeys.
							LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		httpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			_getLayoutDisplayPageObjectProvider(_journalArticle));

		try {
			url = _getLocalizedURL(
				locale,
				_layoutSEOLinkManager.getLocalizedLayoutSEOLinks(
					layout,
					_portal.getSiteDefaultLocale(
						_portal.getScopeGroupId(_httpServletRequest)),
					_portal.getCanonicalURL(
						_portal.getCurrentCompleteURL(httpServletRequest),
						themeDisplay, layout, false, false),
					Collections.singleton(locale)));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
		finally {
			httpServletRequest.setAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
				initialLayoutDisplayPageObjectProvider);
		}

		if (url == null) {
			return StringPool.BLANK;
		}

		String backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		if (Validator.isNotNull(backURL)) {
			return HttpComponentsUtil.addParameters(
				url, "p_l_back_url", backURL, "p_l_back_url_title",
				portletDisplay.getPortletDisplayName());
		}

		return HttpComponentsUtil.addParameters(
			url, "p_l_back_url", themeDisplay.getURLCurrent(),
			"p_l_back_url_title", portletDisplay.getPortletDisplayName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewJournalArticleContentDashboardItemAction.class);

	private final HttpServletRequest _httpServletRequest;
	private final JournalArticle _journalArticle;
	private final Language _language;
	private final LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutSEOLinkManager _layoutSEOLinkManager;
	private final Portal _portal;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.site.provider;

import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.util.LayoutTypeControllerTracker;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.provider.SitemapURLProvider;
import com.liferay.site.provider.helper.SitemapURLProviderHelper;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = SitemapURLProvider.class)
public class LayoutSitemapURLProvider implements SitemapURLProvider {

	@Override
	public String getClassName() {
		return Layout.class.getName();
	}

	@Override
	public boolean isInclude(long companyId, long groupId)
		throws PortalException {

		return _sitemapConfigurationManager.includePagesGroupEnabled(
			companyId, groupId);
	}

	@Override
	public void visitLayout(
			Element element, String layoutUuid, LayoutSet layoutSet,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = _layoutLocalService.getLayoutByUuidAndGroupId(
			layoutUuid, layoutSet.getGroupId(), layoutSet.isPrivateLayout());

		visitLayout(element, layout, themeDisplay);
	}

	@Override
	public void visitLayoutSet(
			Element element, LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {

		if (layoutSet.isPrivateLayout()) {
			return;
		}

		Map<String, LayoutTypeController> layoutTypeControllers =
			LayoutTypeControllerTracker.getLayoutTypeControllers();

		for (Map.Entry<String, LayoutTypeController> entry :
				layoutTypeControllers.entrySet()) {

			LayoutTypeController layoutTypeController = entry.getValue();

			if (!layoutTypeController.isSitemapable()) {
				continue;
			}

			int start = QueryUtil.ALL_POS;
			int end = QueryUtil.ALL_POS;

			int count = _layoutService.getLayoutsCount(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				entry.getKey());

			if (count > SitemapManager.MAXIMUM_ENTRIES) {
				start = count - SitemapManager.MAXIMUM_ENTRIES;
				end = count;
			}

			List<Layout> layouts = _layoutService.getLayouts(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				entry.getKey(), start, end);

			for (Layout layout : layouts) {
				visitLayout(element, layout, themeDisplay);
			}
		}
	}

	protected void visitLayout(
			Element element, Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		if (layout.isSystem() ||
			_sitemapURLProviderHelper.isExcludeLayoutFromSitemap(layout)) {

			return;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String layoutFullURL = _portal.getCanonicalURL(
			_portal.getLayoutFullURL(layout, themeDisplay), themeDisplay,
			layout);

		Map<Locale, String> alternateURLs = _portal.getAlternateURLs(
			layoutFullURL, themeDisplay, layout,
			_getAvailableLocales(
				layout,
				_language.getAvailableLocales(themeDisplay.getScopeGroupId())));

		for (String alternateURL : alternateURLs.values()) {
			_sitemapManager.addURLElement(
				element, alternateURL, typeSettingsUnicodeProperties,
				layout.getModifiedDate(), layoutFullURL, alternateURLs);
		}
	}

	private Set<Locale> _getAvailableLocales(
			Layout layout, Set<Locale> siteAvailableLocales)
		throws PortalException {

		Set<Locale> availableLocales = new HashSet<>();

		if (SetUtil.isEmpty(siteAvailableLocales)) {
			return availableLocales;
		}

		InfoItemLanguagesProvider<Layout> infoItemLanguagesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemLanguagesProvider.class, Layout.class.getName());

		for (String availableLanguageId :
				infoItemLanguagesProvider.getAvailableLanguageIds(layout)) {

			Locale locale = LocaleUtil.fromLanguageId(availableLanguageId);

			if (siteAvailableLocales.contains(locale)) {
				availableLocales.add(
					LocaleUtil.fromLanguageId(availableLanguageId));
			}
		}

		return availableLocales;
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Portal _portal;

	@Reference
	private SitemapConfigurationManager _sitemapConfigurationManager;

	@Reference
	private SitemapManager _sitemapManager;

	@Reference
	private SitemapURLProviderHelper _sitemapURLProviderHelper;

}
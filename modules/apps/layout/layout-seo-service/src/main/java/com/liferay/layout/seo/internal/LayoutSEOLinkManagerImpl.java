/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.internal;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.layout.seo.canonical.url.LayoutSEOCanonicalURLProvider;
import com.liferay.layout.seo.internal.configuration.LayoutSEOGeneralGroupConfiguration;
import com.liferay.layout.seo.internal.util.AlternateURLMapperProvider;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = LayoutSEOLinkManager.class)
public class LayoutSEOLinkManagerImpl implements LayoutSEOLinkManager {

	@Override
	public LayoutSEOLink getCanonicalLayoutSEOLink(
			Layout layout, Locale locale, String canonicalURL,
			ThemeDisplay themeDisplay)
		throws PortalException {

		return new LayoutSEOLinkImpl(
			HtmlUtil.escapeAttribute(
				_layoutSEOCanonicalURLProvider.getCanonicalURL(
					layout, locale, canonicalURL, themeDisplay)),
			null, LayoutSEOLink.Relationship.CANONICAL);
	}

	@Override
	public String getFullPageTitle(
			Layout layout, String portletId, String tilesTitle,
			ListMergeable<String> titleListMergeable,
			ListMergeable<String> subtitleListMergeable, String companyName,
			Locale locale)
		throws PortalException {

		String layoutTitle = _getPageTitle(
			layout, portletId, tilesTitle, titleListMergeable,
			subtitleListMergeable, locale);

		String suffix = _getPageTitleSuffix(layout, companyName);

		if (Validator.isNotNull(suffix)) {
			return _merge(layoutTitle, suffix);
		}

		return layoutTitle;
	}

	@Override
	public List<LayoutSEOLink> getLocalizedLayoutSEOLinks(
			Layout layout, Locale locale, String canonicalURL,
			Set<Locale> availableLocales)
		throws PortalException {

		ThemeDisplay themeDisplay = _getThemeDisplay();

		AlternateURLMapperProvider.AlternateURLMapper alternateURLMapper =
			_alternateURLMapperProvider.getAlternateURLMapper(
				_getHttpServletRequest());

		Map<Locale, String> alternateURLs = alternateURLMapper.getAlternateURLs(
			canonicalURL, themeDisplay, layout, availableLocales);

		List<LayoutSEOLink> layoutSEOLinks = new ArrayList<>(
			availableLocales.size() + 2);

		layoutSEOLinks.add(
			getCanonicalLayoutSEOLink(
				layout, locale, canonicalURL, themeDisplay));

		Locale siteDefaultLocale = _portal.getSiteDefaultLocale(
			layout.getGroupId());

		Map<Locale, String> alternateCanonicalURLs = _getAlternateCanonicalURLs(
			alternateURLs, layout, siteDefaultLocale);

		for (Map.Entry<Locale, String> entry :
				alternateCanonicalURLs.entrySet()) {

			layoutSEOLinks.add(
				new LayoutSEOLinkImpl(
					HtmlUtil.escapeAttribute(entry.getValue()),
					LocaleUtil.toW3cLanguageId(entry.getKey()),
					LayoutSEOLink.Relationship.ALTERNATE));
		}

		String defaultLocaleURL = alternateCanonicalURLs.get(siteDefaultLocale);

		if (defaultLocaleURL == null) {
			return layoutSEOLinks;
		}

		layoutSEOLinks.add(
			new LayoutSEOLinkImpl(
				HtmlUtil.escapeAttribute(defaultLocaleURL), "x-default",
				LayoutSEOLink.Relationship.ALTERNATE));

		return layoutSEOLinks;
	}

	@Override
	public String getPageTitle(
			Layout layout, String portletId, String tilesTitle,
			ListMergeable<String> titleListMergeable,
			ListMergeable<String> subtitleListMergeable, Locale locale)
		throws PortalException {

		return HtmlUtil.escape(
			_getPageTitle(
				layout, portletId, tilesTitle, titleListMergeable,
				subtitleListMergeable, locale));
	}

	@Override
	public String getPageTitleSuffix(Layout layout, String companyName)
		throws PortalException {

		return HtmlUtil.escape(_getPageTitleSuffix(layout, companyName));
	}

	@Activate
	protected void activate() {
		_alternateURLMapperProvider = new AlternateURLMapperProvider(
			_assetDisplayPageFriendlyURLProvider, _classNameLocalService,
			_portal);
	}

	@Deactivate
	protected void deactivate() {
		_alternateURLMapperProvider = null;
	}

	private Map<Locale, String> _getAlternateCanonicalURLs(
		Map<Locale, String> alternateURLs, Layout layout,
		Locale siteDefaultLocale) {

		LayoutSEOEntry layoutSEOEntry =
			_layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId());

		if ((layoutSEOEntry == null) ||
			!layoutSEOEntry.isCanonicalURLEnabled()) {

			return alternateURLs;
		}

		Map<Locale, String> alternateCanonicalURLs = new HashMap<>();

		try {
			siteDefaultLocale = _portal.getSiteDefaultLocale(
				layout.getGroupId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		String currentCanonicalURL = layoutSEOEntry.getCanonicalURL(
			siteDefaultLocale);

		for (Map.Entry<Locale, String> entry : alternateURLs.entrySet()) {
			Locale locale = entry.getKey();

			String alternateCanonicalURL = layoutSEOEntry.getCanonicalURL(
				locale);

			if (!locale.equals(siteDefaultLocale) &&
				currentCanonicalURL.equals(alternateCanonicalURL)) {

				alternateCanonicalURL = entry.getValue();
			}

			alternateCanonicalURLs.put(locale, alternateCanonicalURL);
		}

		return alternateCanonicalURLs;
	}

	private HttpServletRequest _getHttpServletRequest() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return serviceContext.getRequest();
		}

		return null;
	}

	private String _getPageTitle(
			Layout layout, String portletId, String tilesTitle,
			ListMergeable<String> titleListMergeable,
			ListMergeable<String> subtitleListMergeable, Locale locale)
		throws PortalException {

		if (Validator.isNotNull(portletId) && layout.isSystem() &&
			!layout.isTypeControlPanel() &&
			StringUtil.equals(layout.getFriendlyURL(), "/manage")) {

			return _portal.getPortletTitle(portletId, locale);
		}

		if (Validator.isNotNull(tilesTitle)) {
			return _language.get(locale, tilesTitle);
		}

		if (subtitleListMergeable == null) {
			return _getTitle(layout, titleListMergeable, locale);
		}

		return _merge(
			subtitleListMergeable.mergeToString(_SEPARATOR),
			_getTitle(layout, titleListMergeable, locale));
	}

	private String _getPageTitleSuffix(Layout layout, String companyName)
		throws PortalException {

		LayoutSEOGeneralGroupConfiguration layoutSEOGeneralGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				LayoutSEOGeneralGroupConfiguration.class, layout.getGroupId());

		if (!layoutSEOGeneralGroupConfiguration.includeInstanceName() &&
			!layoutSEOGeneralGroupConfiguration.includeSiteName()) {

			return StringPool.BLANK;
		}

		if (layoutSEOGeneralGroupConfiguration.includeInstanceName() &&
			layoutSEOGeneralGroupConfiguration.includeSiteName()) {

			Group group = layout.getGroup();

			if (group.isControlPanel() || group.isLayoutPrototype() ||
				StringUtil.equals(companyName, group.getDescriptiveName())) {

				return companyName;
			}

			return _merge(group.getDescriptiveName(), companyName);
		}

		if (layoutSEOGeneralGroupConfiguration.includeInstanceName()) {
			return companyName;
		}

		Group group = layout.getGroup();

		return group.getDescriptiveName();
	}

	private ThemeDisplay _getThemeDisplay() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return serviceContext.getThemeDisplay();
		}

		return null;
	}

	private String _getTitle(
			Layout layout, ListMergeable<String> titleListMergeable,
			Locale locale)
		throws PortalException {

		Group group = layout.getGroup();

		if (group.isLayoutPrototype()) {
			return group.getDescriptiveName(locale);
		}

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		if (Validator.isNull(PropsValues.LAYOUT_FRIENDLY_URL_PAGE_NOT_FOUND) &&
			(httpServletRequest != null) &&
			SessionErrors.contains(
				httpServletRequest, NoSuchLayoutException.class)) {

			if (titleListMergeable == null) {
				titleListMergeable = new ListMergeable<>();
			}

			titleListMergeable.add(_language.get(locale, "status"));
		}

		if (titleListMergeable != null) {
			return titleListMergeable.mergeToString(_SEPARATOR);
		}

		return layout.getHTMLTitle(_language.getLanguageId(locale));
	}

	private String _merge(String... strings) {
		return StringUtil.merge(strings, _SEPARATOR);
	}

	private static final String _SEPARATOR = " - ";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSEOLinkManagerImpl.class);

	private AlternateURLMapperProvider _alternateURLMapperProvider;

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference
	private LayoutSEOCanonicalURLProvider _layoutSEOCanonicalURLProvider;

	@Reference
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Reference
	private Portal _portal;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.internal.canonical.url;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.layout.seo.canonical.url.LayoutSEOCanonicalURLProvider;
import com.liferay.layout.seo.contributor.LayoutSEOCanonicalURLContributor;
import com.liferay.layout.seo.internal.configuration.LayoutSEOCompanyConfiguration;
import com.liferay.layout.seo.internal.util.AlternateURLMapperProvider;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = LayoutSEOCanonicalURLProvider.class)
public class LayoutSEOCanonicalURLProviderImpl
	implements LayoutSEOCanonicalURLProvider {

	@Override
	public String getCanonicalURL(
			Layout layout, Locale locale, String canonicalURL,
			ThemeDisplay themeDisplay)
		throws PortalException {

		String layoutCanonicalURL = _getLayoutCanonicalURL(locale, layout);

		if (Validator.isNull(layoutCanonicalURL)) {
			layoutCanonicalURL = _getDefaultCanonicalURL(
				layout, locale, canonicalURL, themeDisplay);
		}

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-71164")) {

			layoutCanonicalURL = _appendContributedParameters(
				layoutCanonicalURL, layout);

			return HttpComponentsUtil.sortParameters(layoutCanonicalURL);
		}

		return layoutCanonicalURL;
	}

	@Override
	public Map<Locale, String> getCanonicalURLMap(
			Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		AlternateURLMapperProvider.AlternateURLMapper alternateURLMapper =
			_alternateURLMapperProvider.getAlternateURLMapper(
				_getHttpServletRequest());

		Map<Locale, String> alternateURLs = alternateURLMapper.getAlternateURLs(
			_portal.getCanonicalURL(
				_portal.getLayoutFullURL(layout, themeDisplay), themeDisplay,
				layout, false, false),
			themeDisplay, layout,
			_language.getAvailableLocales(layout.getGroupId()));

		LayoutSEOEntry layoutSEOEntry =
			_layoutSEOEntryLocalService.fetchLayoutSEOEntry(layout);

		if ((layoutSEOEntry == null) ||
			(layoutSEOEntry.getGroupId() != layout.getGroupId()) ||
			!layoutSEOEntry.isCanonicalURLEnabled()) {

			return alternateURLs;
		}

		return HashMapBuilder.create(
			alternateURLs
		).putAll(
			layoutSEOEntry.getCanonicalURLMap()
		).build();
	}

	@Override
	public String getDefaultCanonicalURL(
			Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		String canonicalURL = _portal.getCanonicalURL(
			_portal.getLayoutFullURL(layout, themeDisplay), themeDisplay,
			layout, false, false);

		return _getDefaultCanonicalURL(
			layout, themeDisplay.getLocale(), canonicalURL, themeDisplay);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_alternateURLMapperProvider = new AlternateURLMapperProvider(
			_assetDisplayPageFriendlyURLProvider, _classNameLocalService,
			_portal);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, LayoutSEOCanonicalURLContributor.class,
			"jakarta.portlet.name");
	}

	@Deactivate
	protected void deactivate() {
		_alternateURLMapperProvider = null;

		_serviceTrackerMap.close();
	}

	private String _appendContributedParameters(
		String canonicalURL, Layout layout) {

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		if ((httpServletRequest == null) || !layout.isTypePortlet()) {
			return canonicalURL;
		}

		Map<String, String[]> parameters = new HashMap<>();

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		for (Portlet portlet : layoutTypePortlet.getAllPortlets(false)) {
			LayoutSEOCanonicalURLContributor layoutSEOCanonicalURLContributor =
				_serviceTrackerMap.getService(portlet.getRootPortletId());

			if (layoutSEOCanonicalURLContributor == null) {
				continue;
			}

			parameters.putAll(
				layoutSEOCanonicalURLContributor.contributeURLParameters(
					httpServletRequest, layout.getPlid(),
					portlet.getPortletId()));
		}

		if (MapUtil.isEmpty(parameters)) {
			return canonicalURL;
		}

		String queryString = HttpComponentsUtil.parameterMapToString(
			parameters, false);

		if (canonicalURL.contains(StringPool.QUESTION)) {
			return canonicalURL + StringPool.AMPERSAND + queryString;
		}

		return canonicalURL + StringPool.QUESTION + queryString;
	}

	private String _getDefaultCanonicalURL(
			Layout layout, Locale locale, String canonicalURL,
			ThemeDisplay themeDisplay)
		throws PortalException {

		LayoutSEOCompanyConfiguration layoutSEOCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				LayoutSEOCompanyConfiguration.class, layout.getCompanyId());

		AlternateURLMapperProvider.AlternateURLMapper alternateURLMapper =
			_alternateURLMapperProvider.getAlternateURLMapper(
				_getHttpServletRequest());

		if (Objects.equals(
				layoutSEOCompanyConfiguration.canonicalURL(),
				"default-language-url")) {

			return alternateURLMapper.getAlternateURL(
				canonicalURL, themeDisplay, LocaleUtil.getSiteDefault(),
				layout);
		}

		Set<Locale> availableLocales = _language.getAvailableLocales(
			layout.getGroupId());

		if (!availableLocales.contains(locale)) {
			locale = LocaleUtil.getSiteDefault();
		}

		return alternateURLMapper.getAlternateURL(
			canonicalURL, themeDisplay, locale, layout);
	}

	private HttpServletRequest _getHttpServletRequest() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return serviceContext.getRequest();
		}

		return null;
	}

	private String _getLayoutCanonicalURL(Locale locale, Layout layout) {
		LayoutSEOEntry layoutSEOEntry =
			_layoutSEOEntryLocalService.fetchLayoutSEOEntry(layout);

		if ((layoutSEOEntry == null) ||
			(layoutSEOEntry.getGroupId() != layout.getGroupId()) ||
			!layoutSEOEntry.isCanonicalURLEnabled()) {

			return StringPool.BLANK;
		}

		return layoutSEOEntry.getCanonicalURL(locale);
	}

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
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, LayoutSEOCanonicalURLContributor>
		_serviceTrackerMap;

}
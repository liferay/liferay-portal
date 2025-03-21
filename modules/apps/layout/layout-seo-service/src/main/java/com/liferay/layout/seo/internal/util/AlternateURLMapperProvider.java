/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.internal.util;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Lourdes Fernández Besada
 */
public class AlternateURLMapperProvider {

	public AlternateURLMapperProvider(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		ClassNameLocalService classNameLocalService, Portal portal) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_classNameLocalService = classNameLocalService;
		_portal = portal;
	}

	public AlternateURLMapperProvider.AlternateURLMapper getAlternateURLMapper(
		HttpServletRequest httpServletRequest) {

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		if ((layoutDisplayPageObjectProvider != null) &&
			AssetDisplayPageUtil.hasAssetDisplayPage(
				layoutDisplayPageObjectProvider.getGroupId(),
				layoutDisplayPageObjectProvider.getClassNameId(),
				layoutDisplayPageObjectProvider.getClassPK(),
				layoutDisplayPageObjectProvider.getClassTypeId())) {

			return new AlternateURLMapperProvider.
				AssetDisplayPageAlternateURLMapper(
					_assetDisplayPageFriendlyURLProvider,
					_classNameLocalService, layoutDisplayPageObjectProvider,
					_portal);
		}

		return new AlternateURLMapperProvider.DefaultPageAlternateURLMapper(
			_portal);
	}

	public static class AssetDisplayPageAlternateURLMapper
		implements AlternateURLMapperProvider.AlternateURLMapper {

		@Override
		public String getAlternateURL(
				String canonicalURL, ThemeDisplay themeDisplay, Locale locale,
				Layout layout)
			throws PortalException {

			return _getAlternateURL(
				canonicalURL, _getPortalURL(themeDisplay), themeDisplay, locale,
				layout);
		}

		@Override
		public Map<Locale, String> getAlternateURLs(
				String canonicalURL, ThemeDisplay themeDisplay, Layout layout,
				Set<Locale> locales)
			throws PortalException {

			Map<Locale, String> alternateURLs = new HashMap<>();

			String portalURL = _getPortalURL(themeDisplay);

			for (Locale locale : locales) {
				alternateURLs.put(
					locale,
					_getAlternateURL(
						canonicalURL, portalURL, themeDisplay, locale, layout));
			}

			return alternateURLs;
		}

		protected AssetDisplayPageAlternateURLMapper(
			AssetDisplayPageFriendlyURLProvider
				assetDisplayPageFriendlyURLProvider,
			ClassNameLocalService classNameLocalService,
			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider,
			Portal portal) {

			_assetDisplayPageFriendlyURLProvider =
				assetDisplayPageFriendlyURLProvider;
			_classNameLocalService = classNameLocalService;
			_layoutDisplayPageObjectProvider = layoutDisplayPageObjectProvider;
			_portal = portal;
		}

		private String _getAlternateURL(
				String canonicalURL, String portalURL,
				ThemeDisplay themeDisplay, Locale locale, Layout layout)
			throws PortalException {

			return _portal.getAlternateURL(
				_getCanonicalFriendlyURL(
					canonicalURL, portalURL, locale, themeDisplay),
				themeDisplay, locale, layout);
		}

		private String _getCanonicalFriendlyURL(
				String defaultURL, String portalURL, Locale locale,
				ThemeDisplay themeDisplay)
			throws PortalException {

			String friendlyURL = _getMappedFriendlyURL(
				defaultURL, locale, themeDisplay);

			if (friendlyURL.startsWith(Http.HTTP)) {
				return friendlyURL;
			}

			return portalURL.concat(friendlyURL);
		}

		private String _getMappedFriendlyURL(
				String url, Locale locale, ThemeDisplay themeDisplay)
			throws PortalException {

			if (_layoutDisplayPageObjectProvider == null) {
				return url;
			}

			return _assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					_layoutDisplayPageObjectProvider.getClassName(),
					new ClassPKInfoItemIdentifier(
						_layoutDisplayPageObjectProvider.getClassPK())),
				locale, themeDisplay);
		}

		private String _getPortalURL(ThemeDisplay themeDisplay) {
			TreeMap<String, String> virtualHostnames =
				_portal.getVirtualHostnames(themeDisplay.getLayoutSet());

			String virtualHostname = null;

			if (!virtualHostnames.isEmpty()) {
				virtualHostname = virtualHostnames.firstKey();
			}

			if (Validator.isNull(virtualHostname)) {
				virtualHostname = "localhost";

				Company company = themeDisplay.getCompany();

				if ((company != null) &&
					Validator.isNotNull(company.getVirtualHostname())) {

					virtualHostname = company.getVirtualHostname();
				}
			}

			return _portal.getPortalURL(
				virtualHostname, themeDisplay.getServerPort(),
				themeDisplay.isSecure());
		}

		private final AssetDisplayPageFriendlyURLProvider
			_assetDisplayPageFriendlyURLProvider;
		private final ClassNameLocalService _classNameLocalService;
		private final LayoutDisplayPageObjectProvider<?>
			_layoutDisplayPageObjectProvider;
		private final Portal _portal;

	}

	public static class DefaultPageAlternateURLMapper
		implements AlternateURLMapperProvider.AlternateURLMapper {

		@Override
		public String getAlternateURL(
				String canonicalURL, ThemeDisplay themeDisplay, Locale locale,
				Layout layout)
			throws PortalException {

			return _portal.getAlternateURL(
				canonicalURL, themeDisplay, locale, layout);
		}

		protected DefaultPageAlternateURLMapper(Portal portal) {
			_portal = portal;
		}

		private final Portal _portal;

	}

	public interface AlternateURLMapper {

		public String getAlternateURL(
				String canonicalURL, ThemeDisplay themeDisplay, Locale locale,
				Layout layout)
			throws PortalException;

		public default Map<Locale, String> getAlternateURLs(
				String canonicalURL, ThemeDisplay themeDisplay, Layout layout,
				Set<Locale> locales)
			throws PortalException {

			Map<Locale, String> alternateURLs = new HashMap<>();

			for (Locale locale : locales) {
				alternateURLs.put(
					locale,
					getAlternateURL(
						canonicalURL, themeDisplay, locale, layout));
			}

			return alternateURLs;
		}

	}

	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private ClassNameLocalService _classNameLocalService;
	private final Portal _portal;

}
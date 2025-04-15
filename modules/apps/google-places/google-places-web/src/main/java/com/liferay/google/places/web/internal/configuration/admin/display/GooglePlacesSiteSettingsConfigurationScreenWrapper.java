/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.google.places.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.google.places.constants.GooglePlacesWebKeys;
import com.liferay.google.places.util.GooglePlacesUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rodrigo Paulino
 */
@Component(service = ConfigurationScreen.class)
public class GooglePlacesSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new GooglePlacesSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.google.places.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class GooglePlacesSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "maps";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/google_places.jsp";
		}

		@Override
		public String getKey() {
			return "google-places-site-settings";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "google-places");
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			SiteSettingsConfigurationScreenContributor.super.setAttributes(
				httpServletRequest, httpServletResponse);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Group group = themeDisplay.getSiteGroup();

			httpServletRequest.setAttribute(
				GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY,
				GooglePlacesUtil.getGooglePlacesAPIKey(
					themeDisplay.getCompanyId(), group.getGroupId(),
					_groupLocalService));
		}

	}

}
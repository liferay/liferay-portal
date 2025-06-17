/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.sitemap.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.sitemap.web.internal.display.context.SitemapCompanyConfigurationDisplayContext;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = ConfigurationScreen.class)
public class SitemapPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new SitemapPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.site.sitemap.web)")
	private ServletContext _servletContext;

	@Reference
	private SitemapConfigurationManager _sitemapConfigurationManager;

	private class SitemapPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "seo";
		}

		@Override
		public String getJspPath() {
			return "/configuration/sitemap_company_configuration.jsp";
		}

		@Override
		public String getKey() {
			return "sitemap-company-configuration";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "xml-sitemap");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/site_sitemap/save_company_configuration";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public boolean isVisible() {
			return true;
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			PortalSettingsConfigurationScreenContributor.super.setAttributes(
				httpServletRequest, httpServletResponse);

			httpServletRequest.setAttribute(
				SitemapCompanyConfigurationDisplayContext.class.getName(),
				new SitemapCompanyConfigurationDisplayContext(
					_groupLocalService, _itemSelector,
					_portal.getLiferayPortletRequest(
						(PortletRequest)httpServletRequest.getAttribute(
							JavaConstants.JAKARTA_PORTLET_REQUEST)),
					_portal.getLiferayPortletResponse(
						(PortletResponse)httpServletRequest.getAttribute(
							JavaConstants.JAKARTA_PORTLET_RESPONSE)),
					_sitemapConfigurationManager,
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY)));
		}

	}

}
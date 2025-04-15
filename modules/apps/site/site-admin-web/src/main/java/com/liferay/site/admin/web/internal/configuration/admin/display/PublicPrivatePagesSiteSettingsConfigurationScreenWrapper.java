/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ConfigurationScreen.class)
public class PublicPrivatePagesSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new PublicPrivatePagesSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.site.admin.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class PublicPrivatePagesSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "pages";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/public_private_pages.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-public-private-pages";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "pages");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/site_admin/edit_pages";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public boolean isVisible(Group group) {
			if ((group != null) && group.isCompany()) {
				return false;
			}

			if ((group != null) && group.isPrivateLayoutsEnabled()) {
				return true;
			}

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			HttpServletRequest httpServletRequest = themeDisplay.getRequest();

			long layoutSetPrototypeId = ParamUtil.getLong(
				httpServletRequest, "layoutSetPrototypeId");

			if (layoutSetPrototypeId > 0) {
				LayoutSetPrototype layoutSetPrototype =
					_layoutSetPrototypeLocalService.fetchLayoutSetPrototype(
						layoutSetPrototypeId);

				if (layoutSetPrototype != null) {
					return true;
				}
			}

			return false;
		}

	}

}
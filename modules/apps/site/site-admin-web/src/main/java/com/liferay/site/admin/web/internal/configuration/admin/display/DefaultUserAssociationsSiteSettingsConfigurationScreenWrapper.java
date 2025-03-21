/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.site.admin.web.internal.display.context.DefaultUserAssociationsDisplayContext;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ConfigurationScreen.class)
public class DefaultUserAssociationsSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new DefaultUserAssociationsSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.site.admin.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class
		DefaultUserAssociationsSiteSettingsConfigurationScreenContributor
			implements SiteSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "users";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/default_user_associations.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-default-user-associations";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "default-user-associations");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/site_admin/edit_default_user_associations";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public boolean isVisible(Group group) {
			return !group.isCompany();
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			SiteSettingsConfigurationScreenContributor.super.setAttributes(
				httpServletRequest, httpServletResponse);

			httpServletRequest.setAttribute(
				DefaultUserAssociationsDisplayContext.class.getName(),
				new DefaultUserAssociationsDisplayContext(
					httpServletRequest, _itemSelector));
		}

	}

}
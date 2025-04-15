/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ConfigurationScreen.class)
public class AnalyticsSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new AnalyticsSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.site.admin.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class AnalyticsSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "analytics";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/analytics.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-analytics";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "analytics");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/site_admin/edit_analytics";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public boolean isVisible(Group group) {
			if (group.isCompany()) {
				return false;
			}

			String[] analyticsTypes = PrefsPropsUtil.getStringArray(
				group.getCompanyId(), PropsKeys.ADMIN_ANALYTICS_TYPES,
				StringPool.NEW_LINE);

			if (analyticsTypes.length == 0) {
				return false;
			}

			return true;
		}

	}

}
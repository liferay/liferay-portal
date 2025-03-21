/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ConfigurationScreen.class)
public class CustomFieldsSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new CustomFieldsSiteSettingsConfigurationScreenContributor());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CustomFieldsSiteSettingsConfigurationScreenWrapper.class);

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.site.admin.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class CustomFieldsSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "other";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/custom_fields.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-custom-fields";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "custom-fields");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/site_admin/edit_custom_fields";
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

			boolean hasCustomAttributesAvailable = false;

			try {
				hasCustomAttributesAvailable =
					CustomAttributesUtil.hasCustomAttributes(
						group.getCompanyId(), Group.class.getName(),
						group.getGroupId(), null);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			return hasCustomAttributesAvailable;
		}

	}

}
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
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;
import com.liferay.sites.kernel.util.Sites;

import jakarta.servlet.ServletContext;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

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

	private static final String[] _GOOGLE_ANALYTICS_KEYS = {
		"googleAnalytics4CustomConfiguration", "googleAnalytics4Id",
		"googleAnalyticsCreateCustomConfiguration",
		"googleAnalyticsCustomConfiguration", "googleAnalyticsId"
	};

	@Reference
	private GroupLocalService _groupLocalService;

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
		public Dictionary<String, Object> exportProperties(
			Serializable scopePK) {

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			for (Map.Entry<String, String> entry :
					typeSettingsUnicodeProperties.entrySet()) {

				if (!_isAnalyticsKey(entry.getKey())) {
					continue;
				}

				properties.put(entry.getKey(), entry.getValue());
			}

			return properties;
		}

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
		public void importProperties(
				Dictionary<String, Object> properties, Serializable scopePK)
			throws Exception {

			UnicodeProperties analyticsUnicodeProperties =
				new UnicodeProperties();

			Enumeration<String> enumeration = properties.keys();

			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();

				if (!_isAnalyticsKey(key)) {
					continue;
				}

				analyticsUnicodeProperties.put(
					key, String.valueOf(properties.get(key)));
			}

			if (analyticsUnicodeProperties.isEmpty()) {
				return;
			}

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.putAll(analyticsUnicodeProperties);

			_groupLocalService.updateGroup(
				group.getGroupId(), typeSettingsUnicodeProperties.toString());
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

		private boolean _isAnalyticsKey(String key) {
			if (ArrayUtil.contains(_GOOGLE_ANALYTICS_KEYS, key) ||
				StringUtil.startsWith(key, Sites.ANALYTICS_PREFIX)) {

				return true;
			}

			return false;
		}

	}

}
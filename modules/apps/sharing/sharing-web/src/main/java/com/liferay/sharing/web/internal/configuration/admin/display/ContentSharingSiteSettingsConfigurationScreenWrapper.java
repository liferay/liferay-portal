/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;
import com.liferay.sites.kernel.util.Sites;

import jakarta.servlet.ServletContext;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ConfigurationScreen.class)
public class ContentSharingSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new ContentSharingSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.sharing.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class ContentSharingSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public Dictionary<String, Object> exportProperties(
			Serializable scopePK) {

			return HashMapDictionaryBuilder.<String, Object>put(
				"contentSharingWithChildrenEnabled",
				() -> {
					Group group = _groupLocalService.fetchGroup((long)scopePK);

					return group.getTypeSettingsProperty(
						"contentSharingWithChildrenEnabled");
				}
			).build();
		}

		@Override
		public String getCategoryKey() {
			return "sharing";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/content_sharing.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-content-sharing";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "content-sharing");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/sharing/edit_content_sharing";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void importProperties(
				Dictionary<String, Object> properties, Serializable scopePK)
			throws Exception {

			Object contentSharingWithChildrenEnabled = properties.get(
				"contentSharingWithChildrenEnabled");

			if (contentSharingWithChildrenEnabled == null) {
				return;
			}

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.put(
				"contentSharingWithChildrenEnabled",
				String.valueOf(
					GetterUtil.getInteger(
						contentSharingWithChildrenEnabled,
						Sites.CONTENT_SHARING_WITH_CHILDREN_DEFAULT_VALUE)));

			_groupLocalService.updateGroup(
				group.getGroupId(), typeSettingsUnicodeProperties.toString());
		}

		@Override
		public boolean isVisible(Group group) {
			int contentSharingWithChildrenEnabled = PrefsPropsUtil.getInteger(
				group.getCompanyId(),
				PropsKeys.SITES_CONTENT_SHARING_WITH_CHILDREN_ENABLED);

			if (contentSharingWithChildrenEnabled == 0) {
				return false;
			}

			return true;
		}

	}

}
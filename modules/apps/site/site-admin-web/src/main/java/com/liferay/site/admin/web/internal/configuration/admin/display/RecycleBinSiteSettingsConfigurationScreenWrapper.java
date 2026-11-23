/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

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
public class RecycleBinSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new RecycleBinSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.site.admin.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class RecycleBinSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public Dictionary<String, Object> exportProperties(
			Serializable scopePK) {

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			return HashMapDictionaryBuilder.<String, Object>put(
				"trashEnabled",
				() -> group.getTypeSettingsProperty("trashEnabled")
			).put(
				"trashEntriesMaxAge",
				() -> group.getTypeSettingsProperty("trashEntriesMaxAge")
			).build();
		}

		@Override
		public String getCategoryKey() {
			return "recycle-bin";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/recycle_bin.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-recycle-bin";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "recycle-bin");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/site_admin/edit_recycle_bin";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void importProperties(
				Dictionary<String, Object> properties, Serializable scopePK)
			throws Exception {

			Object trashEnabled = properties.get("trashEnabled");
			Object trashEntriesMaxAge = properties.get("trashEntriesMaxAge");

			if ((trashEnabled == null) && (trashEntriesMaxAge == null)) {
				return;
			}

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			if (trashEnabled != null) {
				typeSettingsUnicodeProperties.setProperty(
					"trashEnabled",
					String.valueOf(GetterUtil.getBoolean(trashEnabled, true)));
			}

			if (trashEntriesMaxAge != null) {
				int groupTrashEntriesMaxAge = GetterUtil.getInteger(
					trashEntriesMaxAge);

				int companyTrashEntriesMaxAge = PrefsPropsUtil.getInteger(
					group.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE);

				if ((groupTrashEntriesMaxAge <= 0) ||
					(groupTrashEntriesMaxAge == companyTrashEntriesMaxAge)) {

					typeSettingsUnicodeProperties.remove("trashEntriesMaxAge");
				}
				else {
					typeSettingsUnicodeProperties.setProperty(
						"trashEntriesMaxAge",
						String.valueOf(groupTrashEntriesMaxAge));
				}
			}

			_groupService.updateGroup(
				group.getGroupId(), typeSettingsUnicodeProperties.toString());
		}

		@Override
		public boolean isVisible(Group group) {
			return PrefsPropsUtil.getBoolean(
				group.getCompanyId(), PropsKeys.TRASH_ENABLED);
		}

	}

}
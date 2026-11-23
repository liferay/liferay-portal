/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.map.constants.MapProviderWebKeys;
import com.liferay.map.util.MapProviderHelperUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ConfigurationScreen.class)
public class MapsSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new MapsSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.site.admin.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class MapsSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public Dictionary<String, Object> exportProperties(
			Serializable scopePK) {

			return HashMapDictionaryBuilder.<String, Object>put(
				MapProviderWebKeys.MAP_PROVIDER_KEY,
				() -> {
					Group group = _groupLocalService.fetchGroup((long)scopePK);

					return group.getTypeSettingsProperty(
						MapProviderWebKeys.MAP_PROVIDER_KEY);
				}
			).build();
		}

		@Override
		public String getCategoryKey() {
			return "maps";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/maps.jsp";
		}

		@Override
		public String getKey() {
			return "site-settings-maps";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "maps");
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void importProperties(
				Dictionary<String, Object> properties, Serializable scopePK)
			throws Exception {

			String mapProviderKey = GetterUtil.getString(
				properties.get(MapProviderWebKeys.MAP_PROVIDER_KEY), null);

			if (mapProviderKey == null) {
				return;
			}

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			if (group.hasStagingGroup()) {
				_updateMapProviderKey(group.getStagingGroup(), mapProviderKey);
			}

			_updateMapProviderKey(group, mapProviderKey);
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

			Group siteGroup = themeDisplay.getSiteGroup();

			Group liveGroup = null;

			if (siteGroup.isStagingGroup()) {
				liveGroup = siteGroup.getLiveGroup();
			}
			else {
				liveGroup = siteGroup;
			}

			httpServletRequest.setAttribute(
				MapProviderWebKeys.MAP_PROVIDER_KEY,
				MapProviderHelperUtil.getMapProviderKey(
					_groupLocalService, themeDisplay.getCompanyId(),
					liveGroup.getGroupId()));
		}

		private void _updateMapProviderKey(Group group, String mapProviderKey)
			throws Exception {

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.put(
				MapProviderWebKeys.MAP_PROVIDER_KEY, mapProviderKey);

			_groupLocalService.updateGroup(
				group.getGroupId(), typeSettingsUnicodeProperties.toString());
		}

	}

}
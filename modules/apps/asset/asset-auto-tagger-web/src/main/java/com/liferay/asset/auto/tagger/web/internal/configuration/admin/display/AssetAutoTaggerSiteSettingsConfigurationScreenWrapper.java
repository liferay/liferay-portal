/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.web.internal.configuration.admin.display;

import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration;
import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfigurationFactory;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
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
public class AssetAutoTaggerSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new AssetAutoTaggerSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private AssetAutoTaggerConfigurationFactory
		_assetAutoTaggerConfigurationFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.asset.auto.tagger.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	private class AssetAutoTaggerSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public Dictionary<String, Object> exportProperties(
			Serializable scopePK) {

			return HashMapDictionaryBuilder.<String, Object>put(
				"assetAutoTaggingEnabled",
				() -> {
					Group group = _groupLocalService.fetchGroup((long)scopePK);

					return group.getTypeSettingsProperty(
						"assetAutoTaggingEnabled");
				}
			).build();
		}

		@Override
		public String getCategoryKey() {
			return "assets";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/asset_auto_tagger.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-asset-auto-tagger";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "asset-auto-tagging");
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void importProperties(
				Dictionary<String, Object> properties, Serializable scopePK)
			throws Exception {

			Object assetAutoTaggingEnabled = properties.get(
				"assetAutoTaggingEnabled");

			if (assetAutoTaggingEnabled == null) {
				return;
			}

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.put(
				"assetAutoTaggingEnabled",
				String.valueOf(GetterUtil.getBoolean(assetAutoTaggingEnabled)));

			_groupLocalService.updateGroup(
				group.getGroupId(), typeSettingsUnicodeProperties.toString());
		}

		@Override
		public boolean isVisible(Group group) {
			AssetAutoTaggerConfiguration assetAutoTaggerConfiguration =
				_assetAutoTaggerConfigurationFactory.
					getGroupAssetAutoTaggerConfiguration(group);

			return assetAutoTaggerConfiguration.isAvailable();
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

			httpServletRequest.setAttribute(
				AssetAutoTaggerConfiguration.class.getName(),
				_assetAutoTaggerConfigurationFactory.
					getGroupAssetAutoTaggerConfiguration(
						themeDisplay.getSiteGroup()));
		}

	}

}
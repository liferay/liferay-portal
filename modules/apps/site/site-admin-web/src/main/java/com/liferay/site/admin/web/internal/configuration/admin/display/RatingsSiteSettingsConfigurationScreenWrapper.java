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
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.ratings.kernel.RatingsType;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionUtil;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionValues;
import com.liferay.ratings.kernel.transformer.RatingsDataTransformerUtil;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ConfigurationScreen.class)
public class RatingsSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new RatingsSiteSettingsConfigurationScreenContributor());
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

	private class RatingsSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public Dictionary<String, Object> exportProperties(
			Serializable scopePK) {

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			Map<String, PortletRatingsDefinitionValues>
				portletRatingsDefinitionValuesMap =
					PortletRatingsDefinitionUtil.
						getPortletRatingsDefinitionValuesMap();

			for (String className :
					portletRatingsDefinitionValuesMap.keySet()) {

				String propertyKey = RatingsDataTransformerUtil.getPropertyKey(
					className);

				String ratingsTypeString = group.getTypeSettingsProperty(
					propertyKey);

				if (ratingsTypeString == null) {
					continue;
				}

				properties.put(propertyKey, ratingsTypeString);
			}

			return properties;
		}

		@Override
		public String getCategoryKey() {
			return "community-tools";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/ratings.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-ratings";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "ratings");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/site_admin/edit_ratings";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void importProperties(
				Dictionary<String, Object> properties, Serializable scopePK)
			throws Exception {

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			Map<String, PortletRatingsDefinitionValues>
				portletRatingsDefinitionValuesMap =
					PortletRatingsDefinitionUtil.
						getPortletRatingsDefinitionValuesMap();

			for (String className :
					portletRatingsDefinitionValuesMap.keySet()) {

				String propertyKey = RatingsDataTransformerUtil.getPropertyKey(
					className);

				String ratingsTypeString = GetterUtil.getString(
					properties.get(propertyKey), null);

				if (ratingsTypeString == null) {
					continue;
				}

				if (RatingsType.isValid(ratingsTypeString)) {
					typeSettingsUnicodeProperties.put(
						propertyKey, ratingsTypeString);
				}
				else {
					typeSettingsUnicodeProperties.remove(propertyKey);
				}
			}

			_groupService.updateGroup(
				group.getGroupId(), typeSettingsUnicodeProperties.toString());
		}

		@Override
		public boolean isVisible(Group group) {
			if (group.isCompany()) {
				return false;
			}

			Map<String, PortletRatingsDefinitionValues>
				portletRatingsDefinitionValuesMap =
					PortletRatingsDefinitionUtil.
						getPortletRatingsDefinitionValuesMap();

			return !portletRatingsDefinitionValuesMap.isEmpty();
		}

	}

}
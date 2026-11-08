/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ConfigurationScreen.class)
public class CategorizationSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new CategorizationSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

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

	private class CategorizationSiteSettingsConfigurationScreenContributor
		implements SiteSettingsConfigurationScreenContributor {

		@Override
		public Dictionary<String, Object> exportProperties(
			Serializable scopePK) {

			String[] assetTagNames = _assetTagLocalService.getTagNames(
				Group.class.getName(), (long)scopePK);

			if (assetTagNames.length == 0) {
				return new HashMapDictionary<>();
			}

			return HashMapDictionaryBuilder.<String, Object>put(
				"assetTagNames", ListUtil.fromArray(assetTagNames)
			).build();
		}

		@Override
		public String getCategoryKey() {
			return "assets";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/categorization.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-categorization";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "categorization");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/site_admin/edit_categorization";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void importProperties(
				Dictionary<String, Object> properties, Serializable scopePK)
			throws Exception {

			List<String> assetTagNames = (List<String>)properties.get(
				"assetTagNames");

			if (assetTagNames == null) {
				return;
			}

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAssetCategoryIds(
				_assetCategoryLocalService.getCategoryIds(
					Group.class.getName(), group.getGroupId()));
			serviceContext.setAssetTagNames(
				ArrayUtil.toStringArray(assetTagNames));
			serviceContext.setUserId(PrincipalThreadLocal.getUserId());

			_groupService.updateGroup(
				group.getGroupId(), group.getParentGroupId(),
				group.getNameMap(), group.getDescriptionMap(), group.getType(),
				null, group.isManualMembership(),
				group.getMembershipRestriction(), group.getFriendlyURL(),
				group.isInheritContent(), group.isActive(), serviceContext);
		}

		@Override
		public boolean isVisible(Group group) {
			return !group.isCompany();
		}

	}

}
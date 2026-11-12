/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.asset.kernel.exception.NoSuchCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

			return HashMapDictionaryBuilder.<String, Object>put(
				"assetCategories",
				() -> {
					List<AssetCategory> assetCategories =
						_assetCategoryLocalService.getCategories(
							Group.class.getName(), (long)scopePK);

					if (assetCategories.isEmpty()) {
						return null;
					}

					return TransformUtil.transform(
						assetCategories,
						assetCategory -> HashMapBuilder.<String, Object>put(
							"externalReferenceCode",
							assetCategory.getExternalReferenceCode()
						).put(
							"scopeExternalReferenceCode",
							() -> ScopeUtil.getItemScopeExternalReferenceCode(
								assetCategory.getGroupId(), (long)scopePK)
						).build());
				}
			).put(
				"assetTagNames",
				() -> {
					String[] assetTagNames = _assetTagLocalService.getTagNames(
						Group.class.getName(), (long)scopePK);

					if (assetTagNames.length == 0) {
						return null;
					}

					return ListUtil.fromArray(assetTagNames);
				}
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

			List<Map<String, Object>> assetCategoryMaps =
				(List<Map<String, Object>>)properties.get("assetCategories");
			List<String> assetTagNames = (List<String>)properties.get(
				"assetTagNames");

			if ((assetCategoryMaps == null) && (assetTagNames == null)) {
				return;
			}

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			ServiceContext serviceContext = new ServiceContext();

			if (assetCategoryMaps == null) {
				serviceContext.setAssetCategoryIds(
					_assetCategoryLocalService.getCategoryIds(
						Group.class.getName(), group.getGroupId()));
			}
			else {
				serviceContext.setAssetCategoryIds(
					TransformUtil.unsafeTransformToLongArray(
						assetCategoryMaps,
						assetCategoryMap -> _getAssetCategoryId(
							GetterUtil.getString(
								assetCategoryMap.get("externalReferenceCode")),
							group,
							GetterUtil.getString(
								assetCategoryMap.get(
									"scopeExternalReferenceCode")))));
			}

			if (assetTagNames == null) {
				serviceContext.setAssetTagNames(
					_assetTagLocalService.getTagNames(
						Group.class.getName(), group.getGroupId()));
			}
			else {
				serviceContext.setAssetTagNames(
					ArrayUtil.toStringArray(assetTagNames));
			}

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

		private long _getAssetCategoryId(
				String externalReferenceCode, Group group,
				String scopeExternalReferenceCode)
			throws Exception {

			if (Validator.isNull(scopeExternalReferenceCode)) {
				scopeExternalReferenceCode = group.getExternalReferenceCode();
			}

			Long groupId = ScopeUtil.getItemGroupId(
				group.getCompanyId(), scopeExternalReferenceCode,
				group.getGroupId());

			if (groupId != null) {
				AssetCategory assetCategory =
					_assetCategoryLocalService.
						fetchAssetCategoryByExternalReferenceCode(
							externalReferenceCode, groupId);

				if (assetCategory != null) {
					return assetCategory.getCategoryId();
				}
			}

			throw new NoSuchCategoryException(
				StringBundler.concat(
					"No asset category exists with external reference code ",
					externalReferenceCode,
					" in the site or asset library with external reference ",
					"code ", scopeExternalReferenceCode));
		}

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.initializer.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(service = AssetCategoriesImporter.class)
public class AssetCategoriesImporter {

	public List<AssetCategory> importAssetCategories(
			JSONArray jsonArray, String assetVocabularyName,
			ClassLoader classLoader, String imageDependenciesPath,
			long scopeGroupId, long userId)
		throws Exception {

		return importAssetCategories(
			jsonArray, assetVocabularyName, classLoader, imageDependenciesPath,
			scopeGroupId, userId, false);
	}

	public List<AssetCategory> importAssetCategories(
			JSONArray jsonArray, String assetVocabularyName,
			ClassLoader classLoader, String imageDependenciesPath,
			long scopeGroupId, long userId, boolean addGuestPermissions)
		throws Exception {

		User user = _userLocalService.getUser(userId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGuestPermissions(addGuestPermissions);
		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setScopeGroupId(scopeGroupId);
		serviceContext.setUserId(userId);

		List<AssetCategory> assetCategories = new ArrayList<>(
			jsonArray.length());

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			assetVocabularyName, serviceContext);

		updateAssetVocabularyPermissions(assetVocabulary);

		for (int i = 0; i < jsonArray.length(); i++) {

			// Asset category

			String title = null;
			String imageFileName = null;

			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (jsonObject != null) {
				title = jsonObject.getString("title");
				imageFileName = jsonObject.getString("image");
			}
			else {
				title = jsonArray.getString(i);
			}

			AssetCategory assetCategory = _addAssetCategory(
				assetVocabulary.getVocabularyId(), title, classLoader,
				imageDependenciesPath, imageFileName, serviceContext);

			assetCategories.add(assetCategory);

			// Permissions

			if (jsonObject == null) {
				continue;
			}

			JSONArray permissionsJSONArray = jsonObject.getJSONArray(
				"permissions");

			if ((permissionsJSONArray != null) &&
				(permissionsJSONArray.length() > 0)) {

				updatePermissions(
					assetCategory.getCompanyId(),
					assetCategory.getModelClassName(),
					String.valueOf(assetCategory.getCategoryId()),
					permissionsJSONArray);
			}
		}

		return assetCategories;
	}

	protected void updateAssetVocabularyPermissions(
			AssetVocabulary assetVocabulary)
		throws PortalException {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		jsonObject.put(
			"roleName", "User"
		).put(
			"scope", 4
		);

		JSONArray actionIdsJSONArray = _jsonFactory.createJSONArray();

		actionIdsJSONArray.put("VIEW");

		jsonObject.put("actionIds", actionIdsJSONArray);

		jsonArray.put(jsonObject);

		updatePermissions(
			assetVocabulary.getCompanyId(), assetVocabulary.getModelClassName(),
			String.valueOf(assetVocabulary.getVocabularyId()), jsonArray);
	}

	protected void updatePermissions(
			long companyId, String name, String primKey, JSONArray jsonArray)
		throws PortalException {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			int scope = jsonObject.getInt("scope");

			String roleName = jsonObject.getString("roleName");

			Role role = _roleLocalService.getRole(companyId, roleName);

			String[] actionIds = new String[0];

			JSONArray actionIdsJSONArray = jsonObject.getJSONArray("actionIds");

			if (actionIdsJSONArray != null) {
				for (int j = 0; j < actionIdsJSONArray.length(); j++) {
					actionIds = ArrayUtil.append(
						actionIds, actionIdsJSONArray.getString(j));
				}
			}

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, name, scope, primKey, role.getRoleId(), actionIds);
		}
	}

	private AssetCategory _addAssetCategory(
			long assetVocabularyId, String title, ClassLoader classLoader,
			String imageDependenciesPath, String imageFileName,
			ServiceContext serviceContext)
		throws Exception {

		// Asset category

		AssetCategory assetCategory = _assetCategoryLocalService.fetchCategory(
			serviceContext.getScopeGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, title,
			assetVocabularyId);

		if (assetCategory == null) {
			Map<Locale, String> titleMap = Collections.singletonMap(
				LocaleUtil.getSiteDefault(), title);

			assetCategory = _assetCategoryLocalService.addCategory(
				null, serviceContext.getUserId(),
				serviceContext.getScopeGroupId(),
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, titleMap,
				null, assetVocabularyId, false, new String[0], serviceContext);
		}

		// Commerce product friendly URL entry

		Group companyGroup = _groupLocalService.getCompanyGroup(
			assetCategory.getCompanyId());

		long classNameId = _portal.getClassNameId(AssetCategory.class);

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				companyGroup.getGroupId(), classNameId,
				assetCategory.getCategoryId());

		if (friendlyURLEntries.isEmpty()) {
			Map<String, String> urlTitleMap = _getUniqueUrlTitles(
				assetCategory);

			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				companyGroup.getGroupId(), classNameId,
				assetCategory.getCategoryId(), urlTitleMap, serviceContext);
		}

		// Commerce product attachment file entry

		if (Validator.isNotNull(imageFileName)) {
			_cpAttachmentFileEntryCreator.addCPAttachmentFileEntry(
				assetCategory, classLoader, imageDependenciesPath,
				imageFileName, 0, CPAttachmentFileEntryConstants.TYPE_IMAGE,
				serviceContext.getScopeGroupId(), serviceContext.getUserId());
		}

		Role siteMemberRole = _roleLocalService.getRole(
			serviceContext.getCompanyId(), RoleConstants.SITE_MEMBER);

		_resourcePermissionLocalService.setResourcePermissions(
			serviceContext.getCompanyId(), AssetCategory.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(assetCategory.getCategoryId()),
			siteMemberRole.getRoleId(), new String[] {ActionKeys.VIEW});

		return assetCategory;
	}

	private AssetVocabulary _addAssetVocabulary(
			String name, ServiceContext serviceContext)
		throws Exception {

		String vocabularyName = name;

		if (name != null) {
			vocabularyName = name.trim();

			vocabularyName = StringUtil.toLowerCase(vocabularyName);
		}

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), vocabularyName);

		if (assetVocabulary == null) {
			assetVocabulary = _assetVocabularyLocalService.addVocabulary(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				name, serviceContext);
		}

		Role siteMemberRole = _roleLocalService.getRole(
			serviceContext.getCompanyId(), RoleConstants.SITE_MEMBER);

		_resourcePermissionLocalService.setResourcePermissions(
			serviceContext.getCompanyId(), AssetVocabulary.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(assetVocabulary.getVocabularyId()),
			siteMemberRole.getRoleId(), new String[] {ActionKeys.VIEW});

		return assetVocabulary;
	}

	private long _getParentClassPK(AssetCategory assetCategory) {
		if (assetCategory.getParentCategoryId() ==
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

			return assetCategory.getVocabularyId();
		}

		return assetCategory.getParentCategoryId();
	}

	private Map<String, String> _getUniqueUrlTitles(AssetCategory assetCategory)
		throws Exception {

		Map<String, String> urlTitleMap = new HashMap<>();

		boolean featureFlagEnabled = FeatureFlagManagerUtil.isEnabled(
			assetCategory.getCompanyId(), "LPD-70396");

		Map<Locale, String> titleMap = assetCategory.getTitleMap();

		for (Map.Entry<Locale, String> titleEntry : titleMap.entrySet()) {
			Group companyGroup = _groupLocalService.getCompanyGroup(
				assetCategory.getCompanyId());

			String urlTitle = null;

			if (featureFlagEnabled) {
				urlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
					companyGroup.getGroupId(),
					_portal.getClassNameId(AssetCategory.class),
					_getParentClassPK(assetCategory),
					assetCategory.getCategoryId(), titleEntry.getValue(), null);
			}
			else {
				urlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
					companyGroup.getGroupId(),
					_portal.getClassNameId(AssetCategory.class),
					assetCategory.getCategoryId(), titleEntry.getValue(), null);
			}

			urlTitleMap.put(
				LocaleUtil.toLanguageId(titleEntry.getKey()), urlTitle);
		}

		return urlTitleMap;
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private CPAttachmentFileEntryCreator _cpAttachmentFileEntryCreator;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
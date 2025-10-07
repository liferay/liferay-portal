/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.permission;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author Eduardo Lundgren
 */
public class AssetCategoryPermission {

	public static void check(
			PermissionChecker permissionChecker, AssetCategory category,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, category, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, AssetCategory.class.getName(),
				category.getCategoryId(), actionId);
		}
	}

	public static void check(
			PermissionChecker permissionChecker, long groupId, long categoryId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, groupId, categoryId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, AssetCategory.class.getName(), categoryId,
				actionId);
		}
	}

	public static void check(
			PermissionChecker permissionChecker, long categoryId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, categoryId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, AssetCategory.class.getName(), categoryId,
				actionId);
		}
	}

	public static boolean contains(
			PermissionChecker permissionChecker, AssetCategory category,
			String actionId)
		throws PortalException {

		if (actionId.equals(ActionKeys.VIEW) &&
			(category.getVocabularyId() !=
				AssetVocabularyConstants.EMPTY_VOCABULARY_ID) &&
			!AssetVocabularyPermission.contains(
				permissionChecker, category.getVocabularyId(),
				ActionKeys.VIEW)) {

			return false;
		}

		if (actionId.equals(ActionKeys.VIEW) &&
			PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {

			while (true) {
				if (!_hasPermission(permissionChecker, category, actionId)) {
					return false;
				}

				long parentCategoryId = category.getParentCategoryId();

				if ((parentCategoryId ==
						AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) ||
					(parentCategoryId ==
						AssetCategoryConstants.EMPTY_PARENT_CATEGORY_ID)) {

					break;
				}

				category = AssetCategoryLocalServiceUtil.getCategory(
					parentCategoryId);
			}

			if (category.getVocabularyId() !=
					AssetVocabularyConstants.EMPTY_VOCABULARY_ID) {

				return AssetVocabularyPermission.contains(
					permissionChecker, category.getVocabularyId(), actionId);
			}
		}

		return _hasPermission(permissionChecker, category, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long groupId, long categoryId,
			String actionId)
		throws PortalException {

		if ((categoryId == AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) ||
			(categoryId == AssetCategoryConstants.EMPTY_PARENT_CATEGORY_ID)) {

			return AssetCategoriesPermission.contains(
				permissionChecker, groupId, actionId);
		}

		return contains(permissionChecker, categoryId, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long categoryId,
			String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			AssetCategoryLocalServiceUtil.getCategory(categoryId), actionId);
	}

	private static boolean _hasPermission(
		PermissionChecker permissionChecker, AssetCategory category,
		String actionId) {

		if (permissionChecker.hasOwnerPermission(
				category.getCompanyId(), AssetCategory.class.getName(),
				category.getCategoryId(), category.getUserId(), actionId) ||
			permissionChecker.hasPermission(
				category.getGroupId(), AssetCategory.class.getName(),
				category.getCategoryId(), actionId)) {

			return true;
		}

		return false;
	}

}
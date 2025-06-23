/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.Scope;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.function.Function;

/**
 * @author Lourdes Fernández Besada
 */
public class AssetUtil {

	public static ItemExternalReference[] getKeywordItemExternalReferences(
		String className, long classPK, long groupId) {

		List<AssetTag> assetTags = AssetTagLocalServiceUtil.getTags(
			className, classPK);

		return _getItemExternalReferences(
			AssetTag::getExternalReferenceCode, AssetTag::getGroupId, groupId,
			AssetTag.class.getName(), assetTags);
	}

	public static ItemExternalReference[]
		getTaxonomyCategoryItemExternalReferences(
			String className, long classPK, long groupId) {

		List<AssetCategory> assetCategories =
			AssetCategoryLocalServiceUtil.getCategories(className, classPK);

		return _getItemExternalReferences(
			AssetCategory::getExternalReferenceCode, AssetCategory::getGroupId,
			groupId, AssetCategory.class.getName(), assetCategories);
	}

	private static <T> ItemExternalReference[] _getItemExternalReferences(
		Function<T, String> getExternalReferenceCodeFunction,
		Function<T, Long> getGroupIdFunction, long groupId,
		String itemClassName, List<T> items) {

		if (ListUtil.isEmpty(items)) {
			return new ItemExternalReference[0];
		}

		return TransformUtil.unsafeTransformToArray(
			items,
			item -> new ItemExternalReference() {
				{
					setClassName(() -> itemClassName);
					setExternalReferenceCode(
						() -> getExternalReferenceCodeFunction.apply(item));
					setScope(
						() -> _getScope(
							groupId, getGroupIdFunction.apply(item)));
				}
			},
			ItemExternalReference.class);
	}

	private static Scope _getScope(long groupId, long scopeGroupId)
		throws Exception {

		if (groupId == scopeGroupId) {
			return null;
		}

		Group group = GroupLocalServiceUtil.getGroup(scopeGroupId);

		return new Scope() {
			{
				setExternalReferenceCode(group::getExternalReferenceCode);
				setType(
					() -> {
						if (group.getType() == GroupConstants.TYPE_DEPOT) {
							return Scope.Type.ASSET_LIBRARY;
						}

						return Scope.Type.SITE;
					});
			}
		};
	}

}
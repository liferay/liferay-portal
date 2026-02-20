/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "bulk.selection.action.key=edit.object.categories",
	service = BulkSelectionAction.class
)
public class EditObjectCategoriesBulkSelectionAction
	extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws Exception {

		if (!(object instanceof ObjectEntry)) {
			return;
		}

		ObjectEntry objectEntry = (ObjectEntry)object;

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			objectEntry.getModelClassName(), objectEntry.getObjectEntryId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		if ((assetEntry == null) ||
			!ModelResourcePermissionUtil.contains(
				permissionChecker, assetEntry.getGroupId(),
				assetEntry.getClassName(), assetEntry.getClassPK(),
				ActionKeys.UPDATE)) {

			return;
		}

		long[] newCategoryIds = new long[0];

		Set<Long> toAddCategoryIds = _toLongSet(inputMap, "toAddCategoryIds");

		if (SetUtil.isNotEmpty(toAddCategoryIds)) {
			newCategoryIds = ArrayUtil.toLongArray(toAddCategoryIds);
		}

		if (MapUtil.getBoolean(inputMap, "append")) {
			Set<Long> currentCategoryIds = SetUtil.fromArray(
				assetEntry.getCategoryIds());

			Set<Long> toRemoveCategoryIds = _toLongSet(
				inputMap, "toRemoveCategoryIds");

			currentCategoryIds.removeAll(toRemoveCategoryIds);

			currentCategoryIds.addAll(toAddCategoryIds);

			newCategoryIds = ArrayUtil.toLongArray(currentCategoryIds);
		}

		_assetEntryLocalService.updateEntry(
			assetEntry.getUserId(), assetEntry.getGroupId(),
			assetEntry.getClassName(), assetEntry.getClassPK(), newCategoryIds,
			assetEntry.getTagNames());
	}

	private Set<Long> _toLongSet(Map<String, Serializable> map, String key) {
		try {
			Serializable values = map.get(key);

			if (values instanceof Long[]) {
				return SetUtil.fromArray((Long[])values);
			}

			Set<Long> set = new HashSet<>();

			for (Integer value : (Integer[])values) {
				set.add(value.longValue());
			}

			return set;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return SetUtil.fromArray(new Long[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditObjectCategoriesBulkSelectionAction.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection.action;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.AssetHelper;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "bulk.selection.action.key=edit.object.tags",
	service = BulkSelectionAction.class
)
public class EditObjectTagsBulkSelectionAction
	implements BulkSelectionAction<Object> {

	@Override
	public void execute(
			User user, BulkSelection<Object> bulkSelection,
			Map<String, Serializable> inputMap)
		throws Exception {

		Set<String> toAddTagNames = _toStringSet(inputMap, "toAddTagNames");
		Set<String> toRemoveTagNames = _toStringSet(
			inputMap, "toRemoveTagNames");

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		bulkSelection.forEach(
			object -> {
				try {
					if (object instanceof ObjectEntry) {
						ObjectEntry objectEntry = (ObjectEntry)object;

						AssetEntry assetEntry =
							_assetEntryLocalService.fetchEntry(
								objectEntry.getModelClassName(),
								objectEntry.getObjectEntryId());

						if (!_hasEditPermission(
								assetEntry, permissionChecker)) {

							return;
						}

						String[] newTagNames = new String[0];

						if (SetUtil.isNotEmpty(toAddTagNames)) {
							newTagNames = (String[])inputMap.get(
								"toAddTagNames");
						}

						if (MapUtil.getBoolean(inputMap, "append")) {
							Set<String> currentTagNames = SetUtil.fromArray(
								assetEntry.getTagNames());

							currentTagNames.removeAll(toRemoveTagNames);

							currentTagNames.addAll(toAddTagNames);

							currentTagNames.removeIf(
								tagName -> !_assetHelper.isValidWord(tagName));

							newTagNames = currentTagNames.toArray(
								new String[0]);
						}

						_assetEntryLocalService.updateEntry(
							assetEntry.getUserId(), assetEntry.getGroupId(),
							assetEntry.getClassName(), assetEntry.getClassPK(),
							assetEntry.getCategoryIds(), newTagNames);
					}
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(portalException);
					}
				}
			});
	}

	private boolean _hasEditPermission(
			AssetEntry assetEntry, PermissionChecker permissionChecker)
		throws PortalException {

		AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

		if (assetRenderer != null) {
			return assetRenderer.hasEditPermission(permissionChecker);
		}

		return ModelResourcePermissionUtil.contains(
			permissionChecker, assetEntry.getGroupId(),
			assetEntry.getClassName(), assetEntry.getClassPK(),
			ActionKeys.UPDATE);
	}

	private Set<String> _toStringSet(
		Map<String, Serializable> map, String key) {

		try {
			return SetUtil.fromArray(
				(String[])map.getOrDefault(key, new String[0]));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return SetUtil.fromArray(new String[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditObjectTagsBulkSelectionAction.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetHelper _assetHelper;

}
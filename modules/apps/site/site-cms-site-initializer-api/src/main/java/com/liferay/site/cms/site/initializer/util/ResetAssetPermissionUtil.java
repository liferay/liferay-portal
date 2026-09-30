/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Balazs Breier
 */
public class ResetAssetPermissionUtil {

	public static void executeResetAssetPermission(
			String className, long classPK,
			FilterFactory<Predicate> filterFactory,
			GroupLocalService groupLocalService,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryFolderLocalService objectEntryFolderLocalService,
			ModelResourcePermission<ObjectEntryFolder>
				objectEntryFolderModelResourcePermission,
			ObjectEntryLocalService objectEntryLocalService,
			ResourcePermissionLocalService resourcePermissionLocalService,
			RoleLocalService roleLocalService)
		throws Exception {

		if (StringUtil.startsWith(
				className, ObjectDefinition.class.getName())) {

			ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
				classPK);

			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.getObjectDefinition(
					objectEntry.getObjectDefinitionId());

			ModelResourcePermission<?> modelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					objectDefinition.getClassName());

			modelResourcePermission.check(
				PermissionThreadLocal.getPermissionChecker(),
				objectEntry.getObjectEntryId(), ActionKeys.PERMISSIONS);

			ObjectEntryFolder rootObjectEntryFolder = _getRootObjectEntryFolder(
				objectEntry, objectEntryFolderLocalService);

			if (rootObjectEntryFolder == null) {
				return;
			}

			JSONObject jsonObject = _getCMSDefaultPermissionJSONObject(
				objectEntry.getGroupId(), groupLocalService,
				objectEntry.getObjectEntryFolderId(), filterFactory,
				objectEntryFolderLocalService);

			JSONObject objectEntryJSONObject = jsonObject.getJSONObject(
				rootObjectEntryFolder.getExternalReferenceCode());

			if (objectEntryJSONObject == null) {
				return;
			}

			_setResourcePermissions(
				objectEntry.getModelClassName(), objectEntry.getCompanyId(),
				objectEntryJSONObject, objectEntry.getObjectEntryId(),
				resourcePermissionLocalService, roleLocalService);
		}
		else if (Objects.equals(className, ObjectEntryFolder.class.getName())) {
			ObjectEntryFolder objectEntryFolder =
				objectEntryFolderLocalService.getObjectEntryFolder(classPK);

			objectEntryFolderModelResourcePermission.check(
				PermissionThreadLocal.getPermissionChecker(),
				objectEntryFolder.getObjectEntryFolderId(),
				ActionKeys.PERMISSIONS);

			JSONObject jsonObject = _getCMSDefaultPermissionJSONObject(
				objectEntryFolder.getGroupId(), groupLocalService,
				objectEntryFolder.getParentObjectEntryFolderId(), filterFactory,
				objectEntryFolderLocalService);

			_setResourcePermissions(
				objectEntryFolder.getModelClassName(),
				objectEntryFolder.getCompanyId(),
				jsonObject.getJSONObject("OBJECT_ENTRY_FOLDERS"),
				objectEntryFolder.getObjectEntryFolderId(),
				resourcePermissionLocalService, roleLocalService);
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	private static JSONObject _getCMSDefaultPermissionJSONObject(
			long groupId, GroupLocalService groupLocalService,
			long objectEntryFolderId, FilterFactory<Predicate> filterFactory,
			ObjectEntryFolderLocalService objectEntryFolderLocalService)
		throws Exception {

		if (objectEntryFolderId != 0) {
			ObjectEntryFolder objectEntryFolder =
				objectEntryFolderLocalService.getObjectEntryFolder(
					objectEntryFolderId);

			JSONObject jsonObject = CMSDefaultPermissionUtil.getJSONObject(
				objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
				objectEntryFolder.getExternalReferenceCode(),
				objectEntryFolder.getModelClassName(), filterFactory);

			if ((jsonObject != null) && !JSONUtil.isEmpty(jsonObject)) {
				return jsonObject;
			}
		}

		Group group = groupLocalService.getGroup(groupId);

		return CMSDefaultPermissionUtil.getJSONObject(
			group.getCompanyId(), group.getCreatorUserId(),
			group.getExternalReferenceCode(), DepotEntry.class.getName(),
			filterFactory);
	}

	private static ObjectEntryFolder _getRootObjectEntryFolder(
		ObjectEntry objectEntry,
		ObjectEntryFolderLocalService objectEntryFolderLocalService) {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntry.getObjectEntryFolderId());

		if (objectEntryFolder == null) {
			return null;
		}

		if (Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS) ||
			Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			return objectEntryFolder;
		}

		String[] parts = StringUtil.split(
			objectEntryFolder.getTreePath(), CharPool.SLASH);

		if (parts.length <= 2) {
			return null;
		}

		return objectEntryFolderLocalService.fetchObjectEntryFolder(
			GetterUtil.getLong(parts[1]));
	}

	private static void _setResourcePermissions(
			String className, long companyId, JSONObject jsonObject,
			long primKey,
			ResourcePermissionLocalService resourcePermissionLocalService,
			RoleLocalService roleLocalService)
		throws Exception {

		resourcePermissionLocalService.deleteResourcePermissions(
			companyId, className, ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(primKey));

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			className);

		for (String key : jsonObject.keySet()) {
			JSONArray jsonArray = jsonObject.getJSONArray(key);

			if ((jsonArray == null) || JSONUtil.isEmpty(jsonArray)) {
				continue;
			}

			Role role = roleLocalService.fetchRole(companyId, key);

			if (role == null) {
				continue;
			}

			resourcePermissionLocalService.setResourcePermissions(
				companyId, className, ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(primKey), role.getRoleId(),
				ArrayUtil.filter(
					JSONUtil.toStringArray(jsonArray),
					action -> resourceActions.contains(action)));
		}
	}

}
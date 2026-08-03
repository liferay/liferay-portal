/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Stefano Motta
 */
public class CMSDefaultPermissionUtil {

	public static void addCMSDefaultPermissions(
			ObjectEntryFolder objectEntryFolder,
			FilterFactory<Predicate> filterFactory)
		throws PortalException {

		JSONObject defaultPermissionsJSONObject =
			getCMSDefaultPermissionJSONObject(objectEntryFolder, filterFactory);

		if ((defaultPermissionsJSONObject == null) ||
			JSONUtil.isEmpty(defaultPermissionsJSONObject)) {

			return;
		}

		if (!Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS) &&
			!Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			addOrUpdateObjectEntry(
				null, objectEntryFolder.getCompanyId(),
				objectEntryFolder.getUserId(),
				objectEntryFolder.getExternalReferenceCode(),
				objectEntryFolder.getModelClassName(),
				defaultPermissionsJSONObject, objectEntryFolder.getGroupId(),
				objectEntryFolder.getTreePath());
		}

		JSONObject objectEntryFoldersJSONObject =
			defaultPermissionsJSONObject.getJSONObject("OBJECT_ENTRY_FOLDERS");

		if (objectEntryFoldersJSONObject == null) {
			return;
		}

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			ObjectEntryFolder.class.getName());

		List<Role> roles = RoleLocalServiceUtil.getGroupRolesAndTeamRoles(
			objectEntryFolder.getCompanyId(), null,
			Arrays.asList(
				RoleConstants.ADMINISTRATOR,
				DepotRolesConstants.ASSET_LIBRARY_OWNER),
			null, null,
			new int[] {RoleConstants.TYPE_REGULAR, RoleConstants.TYPE_DEPOT},
			null, 0, 0, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (Role role : roles) {
			String[] actionIds = JSONUtil.toStringArray(
				objectEntryFoldersJSONObject.getJSONArray(role.getName()));

			if (objectEntryFolder.getParentObjectEntryFolderId() ==
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

				actionIds = ArrayUtil.remove(actionIds, ActionKeys.DELETE);
			}

			ResourcePermissionLocalServiceUtil.setResourcePermissions(
				objectEntryFolder.getCompanyId(),
				ObjectEntryFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
				role.getRoleId(),
				ArrayUtil.filter(actionIds, resourceActions::contains));
		}
	}

	public static ObjectEntry addOrUpdateObjectEntry(
			String externalReferenceCode, long companyId, long userId,
			String classExternalReferenceCode, String className,
			JSONObject defaultPermissionsJSONObject, long depotGroupId,
			String treePath)
		throws PortalException {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", companyId);

		return ObjectEntryLocalServiceUtil.addOrUpdateObjectEntry(
			externalReferenceCode, 0, userId,
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"classExternalReferenceCode", classExternalReferenceCode
			).put(
				"className", className
			).put(
				"defaultPermissions", defaultPermissionsJSONObject.toString()
			).put(
				"depotGroupId", depotGroupId
			).put(
				"treePath", treePath
			).build(),
			new ServiceContext());
	}

	public static ObjectEntry fetchObjectEntry(
			long companyId, long userId, String classExternalReferenceCode,
			String className, FilterFactory<Predicate> filterFactory)
		throws PortalException {

		return _fetchObjectEntry(
			companyId, userId,
			StringBundler.concat(
				"(classExternalReferenceCode eq '", classExternalReferenceCode,
				"') and (className eq '", className, "')"),
			filterFactory);
	}

	public static ObjectEntry fetchObjectEntryByDepotGroupId(
			long companyId, long userId, long depotGroupId, String className,
			FilterFactory<Predicate> filterFactory)
		throws PortalException {

		return _fetchObjectEntry(
			companyId, userId,
			StringBundler.concat(
				"(depotGroupId eq ", depotGroupId, ") and (className eq '",
				className, "')"),
			filterFactory);
	}

	public static JSONObject getCMSDefaultPermissionJSONObject(
			ObjectEntryFolder objectEntryFolder,
			FilterFactory<Predicate> filterFactory)
		throws PortalException {

		// A company scoped folder (group id 0) has no depot group and
		// therefore no depot level default permissions to inherit.

		if (objectEntryFolder.getGroupId() == 0) {
			return null;
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION",
					objectEntryFolder.getCompanyId());

		if (objectDefinition == null) {
			return null;
		}

		if (objectEntryFolder.getParentObjectEntryFolderId() !=
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

			ObjectEntryFolder parentObjectEntryFolder =
				ObjectEntryFolderLocalServiceUtil.getObjectEntryFolder(
					objectEntryFolder.getParentObjectEntryFolderId());

			JSONObject jsonObject = getJSONObject(
				parentObjectEntryFolder.getCompanyId(),
				parentObjectEntryFolder.getUserId(),
				parentObjectEntryFolder.getExternalReferenceCode(),
				parentObjectEntryFolder.getModelClassName(), filterFactory);

			if ((jsonObject != null) && !JSONUtil.isEmpty(jsonObject)) {
				return jsonObject;
			}
		}

		Group group = GroupLocalServiceUtil.getGroup(
			objectEntryFolder.getGroupId());

		return getJSONObject(
			group.getCompanyId(), group.getCreatorUserId(),
			group.getExternalReferenceCode(), DepotEntry.class.getName(),
			filterFactory);
	}

	public static JSONObject getJSONObject(
			long companyId, long userId, String classExternalReferenceCode,
			String className, FilterFactory<Predicate> filterFactory)
		throws PortalException {

		if (classExternalReferenceCode.equals(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS) ||
			classExternalReferenceCode.equals(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			return JSONFactoryUtil.createJSONObject();
		}

		ObjectEntry objectEntry = fetchObjectEntry(
			companyId, userId, classExternalReferenceCode, className,
			filterFactory);

		if (objectEntry == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		Map<String, Serializable> values = objectEntry.getValues();

		return JSONFactoryUtil.createJSONObject(
			String.valueOf(values.getOrDefault("defaultPermissions", "{}")));
	}

	public static void updateClassExternalReferenceCode(
			ObjectEntry objectEntry, String classExternalReferenceCode,
			long userId)
		throws PortalException {

		Map<String, Serializable> values = objectEntry.getValues();

		if (Objects.equals(
				values.get("classExternalReferenceCode"),
				classExternalReferenceCode)) {

			return;
		}

		ObjectEntryLocalServiceUtil.updateObjectEntry(
			userId, objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>putAll(
				values
			).put(
				"classExternalReferenceCode", classExternalReferenceCode
			).build(),
			new ServiceContext());
	}

	private static ObjectEntry _fetchObjectEntry(
			long companyId, long userId, String filterString,
			FilterFactory<Predicate> filterFactory)
		throws PortalException {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", companyId);

		Predicate predicate = filterFactory.create(
			filterString, objectDefinition);

		List<Long> primaryKeys = ObjectEntryLocalServiceUtil.getPrimaryKeys(
			new Long[0], companyId, userId,
			objectDefinition.getObjectDefinitionId(), predicate, false, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (ListUtil.isEmpty(primaryKeys)) {
			return null;
		}

		return ObjectEntryLocalServiceUtil.fetchObjectEntry(primaryKeys.get(0));
	}

}
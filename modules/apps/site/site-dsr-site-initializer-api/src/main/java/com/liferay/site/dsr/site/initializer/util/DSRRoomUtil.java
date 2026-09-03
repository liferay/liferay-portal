/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Objects;

/**
 * @author Balazs Breier
 */
public class DSRRoomUtil {

	public static void checkPermission(
			ObjectEntry objectEntry, PermissionChecker permissionChecker,
			String actionId)
		throws PortalException {

		if (!isReadOnly(objectEntry, permissionChecker)) {
			return;
		}

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		throw new PrincipalException.MustHavePermission(
			permissionChecker, objectDefinition.getClassName(),
			objectEntry.getObjectEntryId(), actionId);
	}

	public static String[] getGroupIds(
			String[] groupIds, PermissionChecker permissionChecker)
		throws PortalException {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", permissionChecker.getCompanyId());

		if (objectDefinition == null) {
			return new String[0];
		}

		return TransformUtil.transformToArray(
			GroupServiceUtil.search(
				permissionChecker.getCompanyId(),
				new long[] {
					ClassNameLocalServiceUtil.getClassNameId(
						objectDefinition.getClassName())
				},
				null,
				LinkedHashMapBuilder.<String, Object>put(
					"actionId", ActionKeys.VIEW
				).put(
					"active", Boolean.TRUE
				).put(
					"site", Boolean.TRUE
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			group -> {
				if (!permissionChecker.isCompanyAdmin() &&
					!permissionChecker.isGroupOwner(group.getGroupId())) {

					return null;
				}

				String groupId = String.valueOf(group.getGroupId());

				if (ArrayUtil.isNotEmpty(groupIds) &&
					!ArrayUtil.contains(groupIds, groupId)) {

					return null;
				}

				return groupId;
			},
			String.class);
	}

	public static boolean isArchived(ObjectEntry objectEntry) {
		if (MapUtil.getInteger(objectEntry.getValues(), "roomStatus") ==
				WorkflowConstants.STATUS_INACTIVE) {

			return true;
		}

		return false;
	}

	public static boolean isReadOnly(
		long groupId, PermissionChecker permissionChecker) {

		if ((permissionChecker == null) || permissionChecker.isCompanyAdmin()) {
			return false;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", group.getCompanyId());

		if ((objectDefinition == null) ||
			!Objects.equals(
				group.getClassName(), objectDefinition.getClassName())) {

			return false;
		}

		ObjectEntry objectEntry = ObjectEntryLocalServiceUtil.fetchObjectEntry(
			group.getClassPK());

		if (objectEntry == null) {
			return false;
		}

		return isReadOnly(objectEntry, permissionChecker);
	}

	public static boolean isReadOnly(
		ObjectEntry objectEntry, PermissionChecker permissionChecker) {

		if ((permissionChecker == null) || permissionChecker.isCompanyAdmin()) {
			return false;
		}

		return isArchived(objectEntry);
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.security.permission.resource;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tancredi Covioli
 */
public class DSRDefaultPermissionObjectEntryModelResourcePermission
	implements ModelResourcePermission<ObjectEntry> {

	public DSRDefaultPermissionObjectEntryModelResourcePermission(
		ModelResourcePermission<ObjectEntry> modelResourcePermission,
		ObjectEntryLocalService objectEntryLocalService) {

		_modelResourcePermission = modelResourcePermission;
		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long primaryKey,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, primaryKey, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, _modelResourcePermission.getModelName(),
				primaryKey, actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, ObjectEntry objectEntry,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, objectEntry, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, _modelResourcePermission.getModelName(),
				objectEntry.getObjectEntryId(), actionId);
		}
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long primaryKey,
			String actionId)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			primaryKey);

		if (objectEntry == null) {
			return false;
		}

		return contains(permissionChecker, objectEntry, actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, ObjectEntry objectEntry,
			String actionId)
		throws PortalException {

		if (Objects.equals(actionId, ActionKeys.VIEW) ||
			(MapUtil.getInteger(objectEntry.getValues(), "roomStatus") !=
				WorkflowConstants.STATUS_INACTIVE)) {

			return _contains(permissionChecker, objectEntry, actionId);
		}

		if (permissionChecker.isCompanyAdmin()) {
			return true;
		}

		long siteId = MapUtil.getLong(objectEntry.getValues(), "siteId");

		if ((siteId > 0) && permissionChecker.isGroupOwner(siteId) &&
			_archivedRoomOwnerAllowedActionIds.contains(actionId)) {

			return true;
		}

		return false;
	}

	@Override
	public String getModelName() {
		return _modelResourcePermission.getModelName();
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return _modelResourcePermission.getPortletResourcePermission();
	}

	private boolean _contains(
			PermissionChecker permissionChecker, ObjectEntry objectEntry,
			String actionId)
		throws PortalException {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (permissionChecker.hasOwnerPermission(
				permissionChecker.getCompanyId(),
				objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
				objectEntry.getUserId(), actionId) ||
			permissionChecker.hasPermission(
				objectEntry.getGroupId(), objectDefinition.getClassName(),
				objectEntry.getObjectEntryId(), actionId)) {

			return true;
		}

		if (actionId.equals(ActionKeys.ADD_DISCUSSION) ||
			actionId.equals(ActionKeys.VIEW)) {

			long siteId = MapUtil.getLong(objectEntry.getValues(), "siteId");

			if ((siteId > 0) && permissionChecker.isGroupMember(siteId)) {
				return true;
			}
		}

		return _modelResourcePermission.contains(
			permissionChecker, objectEntry, actionId);
	}

	private static final Set<String> _archivedRoomOwnerAllowedActionIds =
		new HashSet<>(
			Arrays.asList(
				ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE,
				ActionKeys.DELETE_DISCUSSION, ActionKeys.UPDATE,
				ActionKeys.UPDATE_DISCUSSION));

	private final ModelResourcePermission<ObjectEntry> _modelResourcePermission;
	private final ObjectEntryLocalService _objectEntryLocalService;

}
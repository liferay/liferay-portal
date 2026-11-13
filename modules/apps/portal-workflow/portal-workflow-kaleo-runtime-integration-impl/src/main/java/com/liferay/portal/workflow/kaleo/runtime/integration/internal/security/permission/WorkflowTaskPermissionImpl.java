/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.integration.internal.security.permission;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskAssignee;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;
import com.liferay.portal.workflow.security.permission.WorkflowTaskPermission;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = WorkflowTaskPermission.class)
public class WorkflowTaskPermissionImpl implements WorkflowTaskPermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, WorkflowTask workflowTask,
			long groupId)
		throws PortalException {

		if (!contains(permissionChecker, workflowTask, groupId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, WorkflowTask.class.getName(),
				workflowTask.getWorkflowTaskId(), ActionKeys.VIEW);
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, WorkflowTask workflowTask,
		long groupId) {

		if (permissionChecker.isOmniadmin() ||
			permissionChecker.isCompanyAdmin()) {

			return true;
		}

		long[] roleIds = getRoleIds(groupId, permissionChecker);

		for (WorkflowTaskAssignee workflowTaskAssignee :
				workflowTask.getWorkflowTaskAssignees()) {

			if (_isWorkflowTaskAssignableToRoles(
					workflowTaskAssignee, roleIds) ||
				_isWorkflowTaskAssignableToUser(
					workflowTaskAssignee, permissionChecker.getUserId())) {

				return true;
			}
		}

		boolean notifiableUser = false;

		try {
			notifiableUser =
				_kaleoTaskInstanceTokenLocalService.isNotifiableUser(
					permissionChecker.getUserId(),
					workflowTask.getWorkflowTaskId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		if ((notifiableUser ||
			 (workflowTask.isCompleted() &&
			  (workflowTask.getAssigneeUserId() ==
				  permissionChecker.getUserId()))) &&
			hasAssetViewPermission(workflowTask, permissionChecker)) {

			return true;
		}

		return false;
	}

	protected long[] getRoleIds(
		long groupId, PermissionChecker permissionChecker) {

		long[] roleIds = permissionChecker.getRoleIds(
			permissionChecker.getUserId(), groupId);

		try {
			List<Group> groups = new ArrayList<>();

			if (groupId != WorkflowConstants.DEFAULT_GROUP_ID) {
				Group group = _groupLocalService.getGroup(groupId);

				if (group.isOrganization()) {
					groups.addAll(_getAncestorOrganizationGroups(group));
				}

				if (group.isSite()) {
					groups.addAll(_getAncestorGroups(group));
				}
			}

			for (Group group : groups) {
				long[] roleIdArray = permissionChecker.getRoleIds(
					permissionChecker.getUserId(), group.getGroupId());

				roleIds = ArrayUtil.append(roleIds, roleIdArray);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return roleIds;
	}

	protected boolean hasAssetViewPermission(
		WorkflowTask workflowTask, PermissionChecker permissionChecker) {

		Map<String, Serializable> optionalAttributes =
			workflowTask.getOptionalAttributes();

		String className = MapUtil.getString(
			optionalAttributes, WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME);

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(className);

		if (workflowHandler == null) {
			return false;
		}

		long classPK = MapUtil.getLong(
			optionalAttributes, WorkflowConstants.CONTEXT_ENTRY_CLASS_PK);

		try {
			AssetRenderer<?> assetRenderer = workflowHandler.getAssetRenderer(
				classPK);

			if (assetRenderer == null) {
				return false;
			}

			return assetRenderer.hasViewPermission(permissionChecker);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	private List<Group> _getAncestorGroups(Group group) throws PortalException {
		List<Group> groups = new ArrayList<>();

		for (Group ancestorGroup : group.getAncestors()) {
			groups.add(ancestorGroup);
		}

		return groups;
	}

	private List<Group> _getAncestorOrganizationGroups(Group group)
		throws PortalException {

		List<Group> groups = new ArrayList<>();

		Organization organization = _organizationLocalService.getOrganization(
			group.getOrganizationId());

		for (Organization ancestorOrganization : organization.getAncestors()) {
			groups.add(ancestorOrganization.getGroup());
		}

		return groups;
	}

	private boolean _isWorkflowTaskAssignableToRoles(
		WorkflowTaskAssignee workflowTaskAssignee, long[] roleIds) {

		String assigneeClassName = workflowTaskAssignee.getAssigneeClassName();

		if (!assigneeClassName.equals(Role.class.getName())) {
			return false;
		}

		return ArrayUtil.contains(
			roleIds, workflowTaskAssignee.getAssigneeClassPK());
	}

	private boolean _isWorkflowTaskAssignableToUser(
		WorkflowTaskAssignee workflowTaskAssignee, long userId) {

		String assigneeClassName = workflowTaskAssignee.getAssigneeClassName();

		if (!assigneeClassName.equals(User.class.getName())) {
			return false;
		}

		if (workflowTaskAssignee.getAssigneeClassPK() == userId) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowTaskPermissionImpl.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

}
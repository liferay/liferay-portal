/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.assignment;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.KaleoTaskAssignmentFactory;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.assignment.KaleoTaskAssignmentSelector;
import com.liferay.portal.workflow.kaleo.runtime.util.validator.GroupAwareRoleValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "assignee.class.name=com.liferay.portal.kernel.model.Role",
	service = KaleoTaskAssignmentSelector.class
)
public class GroupAwareRoleKaleoTaskAssignmentSelector
	implements KaleoTaskAssignmentSelector {

	@Override
	public Collection<KaleoTaskAssignment> getKaleoTaskAssignments(
			KaleoTaskAssignment kaleoTaskAssignment,
			ExecutionContext executionContext)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		return _createKaleoTaskAssigments(
			kaleoInstanceToken.getGroupId(),
			_roleLocalService.getRole(
				kaleoTaskAssignment.getAssigneeClassPK()));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, GroupAwareRoleValidator.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private List<KaleoTaskAssignment> _createKaleoTaskAssigments(
			long groupId, Role role)
		throws PortalException {

		List<Long> groupIds = new ArrayList<>();

		Group group = null;

		if (groupId != WorkflowConstants.DEFAULT_GROUP_ID) {
			group = _groupLocalService.getGroup(groupId);

			if (group.isOrganization()) {
				groupIds.addAll(_getAncestorOrganizationGroupIds(group, role));
			}

			if (group.isSite()) {
				groupIds.addAll(_getAncestorGroupIds(group, role));
			}

			if (group.isLayout()) {
				group = _groupLocalService.getGroup(group.getParentGroupId());
			}
		}

		if (_isValidAssignment(group, role)) {
			groupIds.add(groupId);
		}

		return _createKaleoTaskAssigments(role, groupIds);
	}

	private List<KaleoTaskAssignment> _createKaleoTaskAssigments(
			Role role, List<Long> groupIds)
		throws PortalException {

		return TransformUtil.transform(
			groupIds, groupId -> _createKaleoTaskAssignment(role, groupId));
	}

	private KaleoTaskAssignment _createKaleoTaskAssignment(
		Role role, long groupId) {

		KaleoTaskAssignment kaleoTaskAssignment =
			_kaleoTaskAssignmentFactory.createKaleoTaskAssignment();

		kaleoTaskAssignment.setGroupId(groupId);
		kaleoTaskAssignment.setAssigneeClassName(Role.class.getName());
		kaleoTaskAssignment.setAssigneeClassPK(role.getRoleId());

		return kaleoTaskAssignment;
	}

	private List<Long> _getAncestorGroupIds(Group group, Role role)
		throws PortalException {

		List<Long> groupIds = new ArrayList<>();

		for (Group ancestorGroup : group.getAncestors()) {
			if (!_isValidAssignment(group, role)) {
				continue;
			}

			groupIds.add(ancestorGroup.getGroupId());
		}

		return groupIds;
	}

	private List<Long> _getAncestorOrganizationGroupIds(Group group, Role role)
		throws PortalException {

		List<Long> groupIds = new ArrayList<>();

		Organization organization = _organizationLocalService.getOrganization(
			group.getOrganizationId());

		for (Organization ancestorOrganization : organization.getAncestors()) {
			if (!_isValidAssignment(group, role)) {
				continue;
			}

			groupIds.add(ancestorOrganization.getGroupId());
		}

		return groupIds;
	}

	private boolean _isValidAssignment(Group group, Role role)
		throws PortalException {

		if ((group != null) && group.isDepot() &&
			(role.getType() == RoleConstants.TYPE_DEPOT)) {

			return true;
		}
		else if (role.getType() == RoleConstants.TYPE_REGULAR) {
			return true;
		}
		else if ((group != null) && group.isOrganization() &&
				 (role.getType() == RoleConstants.TYPE_ORGANIZATION)) {

			return true;
		}
		else if ((group != null) && group.isSite() &&
				 (role.getType() == RoleConstants.TYPE_SITE)) {

			return true;
		}

		for (GroupAwareRoleValidator groupAwareRoleValidator :
				_serviceTrackerList) {

			if (groupAwareRoleValidator.isValidGroup(group, role)) {
				return true;
			}
		}

		return false;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private KaleoTaskAssignmentFactory _kaleoTaskAssignmentFactory;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	private ServiceTrackerList<GroupAwareRoleValidator> _serviceTrackerList;

}
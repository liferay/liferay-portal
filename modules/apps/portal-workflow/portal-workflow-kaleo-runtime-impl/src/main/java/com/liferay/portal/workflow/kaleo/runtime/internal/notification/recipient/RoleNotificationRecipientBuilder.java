/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.notification.recipient;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.configuration.WorkflowDefinitionConfiguration;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationRecipient;
import com.liferay.portal.workflow.kaleo.runtime.notification.recipient.NotificationRecipientBuilder;
import com.liferay.portal.workflow.kaleo.runtime.util.validator.GroupAwareRoleValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.workflow.configuration.WorkflowDefinitionConfiguration",
	property = "recipient.type=ROLE",
	service = NotificationRecipientBuilder.class
)
public class RoleNotificationRecipientBuilder
	implements NotificationRecipientBuilder {

	@Override
	public void processKaleoNotificationRecipient(
			Set<NotificationRecipient> notificationRecipients,
			KaleoNotificationRecipient kaleoNotificationRecipient,
			NotificationReceptionType notificationReceptionType,
			ExecutionContext executionContext)
		throws Exception {

		long roleId = kaleoNotificationRecipient.getRecipientClassPK();

		addRoleRecipientAddresses(
			notificationRecipients, roleLocalService.getRole(roleId),
			notificationReceptionType, executionContext);
	}

	@Override
	public void processKaleoTaskAssignmentInstance(
			Set<NotificationRecipient> notificationRecipients,
			KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance,
			NotificationReceptionType notificationReceptionType,
			ExecutionContext executionContext)
		throws Exception {

		long roleId = kaleoTaskAssignmentInstance.getAssigneeClassPK();

		addRoleRecipientAddresses(
			notificationRecipients, roleLocalService.getRole(roleId),
			notificationReceptionType, executionContext);
	}

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, GroupAwareRoleValidator.class);
		_workflowDefinitionConfiguration = ConfigurableUtil.createConfigurable(
			WorkflowDefinitionConfiguration.class, properties);
	}

	protected void addRoleRecipientAddresses(
			Set<NotificationRecipient> notificationRecipients, Role role,
			NotificationReceptionType notificationReceptionType,
			ExecutionContext executionContext)
		throws Exception {

		for (User user : _getRoleUsers(role, executionContext)) {
			if (!user.isActive() ||
				_isSelfAssignedUser(executionContext, user)) {

				continue;
			}

			notificationRecipients.add(
				new NotificationRecipient(user, notificationReceptionType));
		}
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected OrganizationLocalService organizationLocalService;

	@Reference
	protected RoleLocalService roleLocalService;

	@Reference
	protected UserGroupGroupRoleLocalService userGroupGroupRoleLocalService;

	@Reference
	protected UserGroupRoleLocalService userGroupRoleLocalService;

	@Reference
	protected UserLocalService userLocalService;

	private List<Long> _getAncestorGroupIds(Group group, Role role)
		throws Exception {

		List<Long> groupIds = new ArrayList<>();

		for (Group ancestorGroup : group.getAncestors()) {
			if (!_isValidGroup(group, role)) {
				continue;
			}

			groupIds.add(ancestorGroup.getGroupId());
		}

		return groupIds;
	}

	private List<Long> _getAncestorOrganizationGroupIds(Group group, Role role)
		throws Exception {

		List<Long> groupIds = new ArrayList<>();

		Organization organization = organizationLocalService.getOrganization(
			group.getOrganizationId());

		for (Organization ancestorOrganization : organization.getAncestors()) {
			if (!_isValidGroup(group, role)) {
				continue;
			}

			groupIds.add(ancestorOrganization.getGroupId());
		}

		return groupIds;
	}

	private List<Long> _getGroupIds(long groupId, Role role) throws Exception {
		List<Long> groupIds = new ArrayList<>();

		if (groupId != WorkflowConstants.DEFAULT_GROUP_ID) {
			Group group = groupLocalService.getGroup(groupId);

			if (group.isOrganization()) {
				groupIds.addAll(_getAncestorOrganizationGroupIds(group, role));
			}

			if (group.isSite() && !_isPreventNotifyingAncestorSites()) {
				groupIds.addAll(_getAncestorGroupIds(group, role));
			}

			if (_isValidGroup(group, role)) {
				groupIds.add(groupId);
			}
		}

		return groupIds;
	}

	private List<User> _getRoleUsers(
			Role role, ExecutionContext executionContext)
		throws Exception {

		long roleId = role.getRoleId();

		if (role.getType() == RoleConstants.TYPE_REGULAR) {
			return userLocalService.getInheritedRoleUsers(
				roleId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		List<Long> groupIds = _getGroupIds(
			kaleoInstanceToken.getGroupId(), role);

		List<User> users = new ArrayList<>();

		for (Long groupId : groupIds) {
			List<UserGroupRole> userGroupRoles =
				userGroupRoleLocalService.getUserGroupRolesByGroupAndRole(
					groupId, roleId);

			for (UserGroupRole userGroupRole : userGroupRoles) {
				users.add(userGroupRole.getUser());
			}

			List<UserGroupGroupRole> userGroupGroupRoles =
				userGroupGroupRoleLocalService.
					getUserGroupGroupRolesByGroupAndRole(groupId, roleId);

			for (UserGroupGroupRole userGroupGroupRole : userGroupGroupRoles) {
				users.addAll(
					userLocalService.getUserGroupUsers(
						userGroupGroupRole.getUserGroupId()));
			}

			if (Objects.equals(
					role.getName(), DepotRolesConstants.ASSET_LIBRARY_MEMBER) ||
				Objects.equals(role.getName(), RoleConstants.SITE_MEMBER)) {

				users.addAll(
					userLocalService.getGroupUsers(
						groupId, WorkflowConstants.STATUS_APPROVED, null));
			}

			if (!Objects.equals(
					role.getName(), RoleConstants.ORGANIZATION_USER)) {

				continue;
			}

			Group group = groupLocalService.getGroup(groupId);

			if (!group.isOrganization()) {
				continue;
			}

			long organizationId = group.getClassPK();

			users.addAll(userLocalService.getOrganizationUsers(organizationId));
		}

		return users;
	}

	private boolean _isPreventNotifyingAncestorSites() {
		return _workflowDefinitionConfiguration.preventNotifyingAncestorSites();
	}

	private boolean _isSelfAssignedUser(
		ExecutionContext executionContext, User user) {

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			executionContext.getKaleoTaskInstanceToken();

		if (kaleoTaskInstanceToken == null) {
			return false;
		}

		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance =
			kaleoTaskInstanceToken.getFirstKaleoTaskAssignmentInstance();

		if ((user.getUserId() == kaleoTaskAssignmentInstance.getUserId()) &&
			(user.getUserId() ==
				kaleoTaskAssignmentInstance.getAssigneeClassPK())) {

			return true;
		}

		return false;
	}

	private boolean _isValidGroup(Group group, Role role) throws Exception {
		if ((group != null) && group.isDepot() &&
			(role.getType() == RoleConstants.TYPE_DEPOT)) {

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

	private volatile ServiceTrackerList<GroupAwareRoleValidator>
		_serviceTrackerList;
	private volatile WorkflowDefinitionConfiguration
		_workflowDefinitionConfiguration;

}
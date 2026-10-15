/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.helper;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.PublicationRoleConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.on.demand.user.ticket.generator.CTOnDemandUserTicketGenerator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = PublicationHelper.class)
public class PublicationHelper {

	public void addCollaborators(
			CTCollection ctCollection, long[] publicationsUserRoleUserIds,
			int[] roleValues, long[] userIds)
		throws PortalException {

		if (publicationsUserRoleUserIds.length > 0) {
			Role publicationsUserRole = _roleLocalService.getRole(
				ctCollection.getCompanyId(), RoleConstants.PUBLICATIONS_USER);

			_userLocalService.addRoleUsers(
				publicationsUserRole.getRoleId(), publicationsUserRoleUserIds);
		}

		Group group = getOrAddGroup(ctCollection);

		for (int i = 0; i < userIds.length; i++) {
			User user = _userLocalService.fetchUser(userIds[i]);

			if ((user == null) || (roleValues[i] < 0)) {
				continue;
			}

			Role role = getPublicationRole(
				ctCollection.getCompanyId(), roleValues[i]);

			if (_userGroupRoleLocalService.hasUserGroupRole(
					user.getUserId(), group.getGroupId(), role.getRoleId())) {

				continue;
			}

			_userGroupRoleLocalService.addUserGroupRole(
				user.getUserId(), group.getGroupId(), role.getRoleId());
		}
	}

	public Group getOrAddGroup(CTCollection ctCollection)
		throws PortalException {

		Group group = _groupLocalService.fetchGroup(
			ctCollection.getCompanyId(),
			_portal.getClassNameId(CTCollection.class),
			ctCollection.getCtCollectionId());

		if (group != null) {
			return group;
		}

		return _groupLocalService.addGroup(
			StringPool.BLANK, ctCollection.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			CTCollection.class.getName(), ctCollection.getCtCollectionId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), ctCollection.getName()
			).build(),
			null, GroupConstants.TYPE_SITE_PRIVATE, null, false,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false, false,
			true, null);
	}

	public Role getPublicationRole(long companyId, int roleValue)
		throws PortalException {

		String name = PublicationRoleConstants.getRoleName(roleValue);

		Role role = _roleLocalService.fetchRole(companyId, name);

		if (role != null) {
			return role;
		}

		role = _roleLocalService.addRole(
			null, _userLocalService.getGuestUserId(companyId), null, 0, name,
			null, null, RoleConstants.TYPE_PUBLICATIONS, null, null);

		for (String actionId : _getModelResourceActions(roleValue)) {
			_resourcePermissionLocalService.addResourcePermission(
				companyId, CTCollection.class.getName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				role.getRoleId(), actionId);
		}

		return role;
	}

	public String getShareURL(
			long ctCollectionId, PortletRequest portletRequest)
		throws PortalException {

		Ticket ticket = _ctOnDemandUserTicketGenerator.generate(ctCollectionId);

		if (ticket == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_portal.getPortalURL(portletRequest));
		sb.append(Portal.PATH_MODULE);
		sb.append("/change_tracking/review_changes?ticketKey=");
		sb.append(ticket.getKey());

		return sb.toString();
	}

	private String[] _getModelResourceActions(int role) {
		if (role == PublicationRoleConstants.ROLE_ADMIN) {
			return new String[] {
				ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.VIEW, CTActionKeys.INVITE_USERS, CTActionKeys.PUBLISH
			};
		}
		else if (role == PublicationRoleConstants.ROLE_EDITOR) {
			return new String[] {ActionKeys.UPDATE, ActionKeys.VIEW};
		}
		else if (role == PublicationRoleConstants.ROLE_PUBLISHER) {
			return new String[] {
				ActionKeys.UPDATE, ActionKeys.VIEW, CTActionKeys.PUBLISH
			};
		}

		return new String[] {ActionKeys.VIEW};
	}

	@Reference
	private CTOnDemandUserTicketGenerator _ctOnDemandUserTicketGenerator;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
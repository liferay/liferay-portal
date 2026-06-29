/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.internal.resource.v1_0;

import com.liferay.headless.dsr.dto.v1_0.InvitedMember;
import com.liferay.headless.dsr.resource.v1_0.InvitedMemberResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.RoleAssignmentException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.site.dsr.site.initializer.constants.DSRRoleConstants;
import com.liferay.site.dsr.site.initializer.constants.DSRTicketConstants;
import com.liferay.site.dsr.site.initializer.util.DSRRoomUtil;

import java.util.Date;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stefano Motta
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/invited-member.properties",
	scope = ServiceScope.PROTOTYPE, service = InvitedMemberResource.class
)
public class InvitedMemberResourceImpl extends BaseInvitedMemberResourceImpl {

	@Override
	public void deleteRoomInvitedMember(Long roomId, Long invitedMemberId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-66359")) {

			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _getObjectEntry(roomId);

		DSRRoomUtil.checkPermission(
			objectEntry, PermissionThreadLocal.getPermissionChecker(),
			ActionKeys.UPDATE);

		Ticket ticket = _getTicket(
			_groupService.getGroup(
				MapUtil.getLong(objectEntry.getValues(), "siteId")),
			objectEntry, invitedMemberId);

		_ticketLocalService.deleteTicket(ticket.getTicketId());
	}

	@Override
	public Page<InvitedMember> getRoomInvitedMembersPage(Long roomId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-66359")) {

			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _getObjectEntry(roomId);

		Group group = _groupService.getGroup(
			MapUtil.getLong(objectEntry.getValues(), "siteId"));

		return Page.of(
			transform(
				_ticketLocalService.getTickets(
					group.getCompanyId(), Group.class.getName(),
					group.getGroupId(), DSRTicketConstants.TYPE_INVITE_MEMBER),
				this::_toInvitedMember));
	}

	@Override
	public InvitedMember patchRoomInvitedMember(
			Long roomId, Long invitedMemberId, InvitedMember invitedMember)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-66359")) {

			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _getObjectEntry(roomId);

		DSRRoomUtil.checkPermission(
			objectEntry, PermissionThreadLocal.getPermissionChecker(),
			ActionKeys.UPDATE);

		Group group = _groupService.getGroup(
			MapUtil.getLong(objectEntry.getValues(), "siteId"));

		Ticket ticket = _getTicket(group, objectEntry, invitedMemberId);

		if ((invitedMember.getMembershipExpirationDate() == null) &&
			(invitedMember.getRoleKey() == null)) {

			return _toInvitedMember(ticket);
		}

		_checkPermission(group, invitedMember.getRoleKey());

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			ticket.getExtraInfo());

		if (invitedMember.getMembershipExpirationDate() != null) {
			Date membershipExpirationDate =
				invitedMember.getMembershipExpirationDate();

			jsonObject.put(
				"membershipExpirationDate", membershipExpirationDate.getTime());
		}
		else {
			jsonObject.remove("membershipExpirationDate");
		}

		if (invitedMember.getRoleKey() != null) {
			jsonObject.put("roleKey", invitedMember.getRoleKey());
		}

		ticket.setExtraInfo(jsonObject.toString());

		ticket = _ticketLocalService.updateTicket(ticket);

		return _toInvitedMember(ticket);
	}

	private void _checkPermission(Group group, String roleKey)
		throws Exception {

		if (!Objects.equals(
				roleKey, DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR)) {

			return;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isGroupAdmin(group.getGroupId()) &&
			!permissionChecker.isGroupOwner(group.getGroupId())) {

			throw new RoleAssignmentException();
		}
	}

	private ObjectEntry _getObjectEntry(long roomId) throws Exception {
		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(roomId);

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				objectDefinition.getClassName());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!modelResourcePermission.contains(
				permissionChecker, objectEntry, ActionKeys.UPDATE)) {

			GroupPermissionUtil.check(
				permissionChecker,
				MapUtil.getLong(objectEntry.getValues(), "siteId"),
				ActionKeys.ASSIGN_MEMBERS);
		}

		return objectEntry;
	}

	private Ticket _getTicket(
			Group group, ObjectEntry objectEntry, long ticketId)
		throws Exception {

		Ticket ticket = _ticketLocalService.getTicket(ticketId);

		if (!Objects.equals(Group.class.getName(), ticket.getClassName()) ||
			(group.getGroupId() != ticket.getClassPK())) {

			throw new NoSuchModelException();
		}

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				objectDefinition.getClassName());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (modelResourcePermission.contains(
				permissionChecker, objectEntry, ActionKeys.UPDATE)) {

			return ticket;
		}

		if (GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.ASSIGN_MEMBERS)) {

			long ownerId = 0;

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				ticket.getExtraInfo());

			if ((jsonObject != null) && !jsonObject.isNull("ownerId")) {
				ownerId = jsonObject.getLong("ownerId");
			}

			if (ownerId == contextUser.getUserId()) {
				return ticket;
			}
		}

		throw new PrincipalException.MustHavePermission(
			contextUser.getUserId(), ObjectEntry.class.getName(),
			objectEntry.getObjectEntryId(), ActionKeys.UPDATE);
	}

	private InvitedMember _toInvitedMember(Ticket ticket) throws Exception {
		return _invitedMemberDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				true, null, _dtoConverterRegistry, contextUser.getUserId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			ticket);
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupService _groupService;

	@Reference(
		target = "(component.name=com.liferay.headless.dsr.internal.dto.v1_0.converter.InvitedMemberDTOConverter)"
	)
	private DTOConverter<Ticket, InvitedMember> _invitedMemberDTOConverter;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private TicketLocalService _ticketLocalService;

}
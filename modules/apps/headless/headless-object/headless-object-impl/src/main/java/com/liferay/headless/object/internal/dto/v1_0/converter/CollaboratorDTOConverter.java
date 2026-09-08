/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.internal.dto.v1_0.converter;

import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.object.dto.v1_0.Collaborator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.object.dto.v1_0.Collaborator"
	},
	service = DTOConverter.class
)
public class CollaboratorDTOConverter
	implements DTOConverter<SharingEntry, Collaborator> {

	@Override
	public String getContentType() {
		return Collaborator.class.getSimpleName();
	}

	@Override
	public Collaborator toDTO(
			DTOConverterContext dtoConverterContext, SharingEntry sharingEntry)
		throws Exception {

		String ticketEmailAddress = _fetchEmailAddress(
			_fetchTicket(sharingEntry));

		User user = _fetchUser(sharingEntry);

		boolean hasViewPermission = _hasViewPermission(user);

		UserGroup userGroup = _fetchUserGroup(sharingEntry);

		return new Collaborator() {
			{
				setActionIds(
					() -> TransformUtil.transformToArray(
						SharingEntryAction.getSharingEntryActions(
							sharingEntry.getActionIds()),
						SharingEntryAction::getActionId, String.class));
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.getUser(sharingEntry.getUserId())));
				setDateExpired(sharingEntry::getExpirationDate);
				setEmailAddress(
					() -> {
						if (ticketEmailAddress != null) {
							return ticketEmailAddress;
						}

						if (user != null) {
							return user.getEmailAddress();
						}

						return null;
					});
				setExternalReferenceCode(
					() -> {
						if (user != null) {
							if (!hasViewPermission) {
								return null;
							}

							return user.getExternalReferenceCode();
						}

						if (userGroup != null) {
							return userGroup.getExternalReferenceCode();
						}

						return null;
					});
				setId(
					() -> {
						if ((user != null) && hasViewPermission) {
							return user.getUserId();
						}

						if (userGroup != null) {
							return userGroup.getUserGroupId();
						}

						return null;
					});
				setName(
					() -> {
						if (user != null) {
							if (!hasViewPermission) {
								return user.getEmailAddress();
							}

							return user.getFullName();
						}

						if (userGroup != null) {
							return userGroup.getName();
						}

						return ticketEmailAddress;
					});
				setPortrait(
					() -> {
						if ((user == null) || !hasViewPermission ||
							(user.getPortraitId() == 0)) {

							return null;
						}

						ThemeDisplay themeDisplay = new ThemeDisplay() {
							{
								setPathImage(_portal.getPathImage());
							}
						};

						return user.getPortraitURL(themeDisplay);
					});
				setShare(sharingEntry::isShareable);
				setType(
					() -> {
						if ((user != null) && hasViewPermission) {
							return "User";
						}

						if (userGroup != null) {
							return "UserGroup";
						}

						return "Email";
					});
			}
		};
	}

	private String _fetchEmailAddress(Ticket ticket) {
		if (ticket == null) {
			return null;
		}

		return ticket.getEmailAddress();
	}

	private Ticket _fetchTicket(SharingEntry sharingEntry) {
		if (sharingEntry.getToTicketId() > 0) {
			return _ticketLocalService.fetchTicket(
				sharingEntry.getToTicketId());
		}

		return null;
	}

	private User _fetchUser(SharingEntry sharingEntry) {
		if (sharingEntry.getToUserId() > 0) {
			return _userLocalService.fetchUser(sharingEntry.getToUserId());
		}

		return null;
	}

	private UserGroup _fetchUserGroup(SharingEntry sharingEntry) {
		if (sharingEntry.getToUserGroupId() > 0) {
			return _userGroupLocalService.fetchUserGroup(
				sharingEntry.getToUserGroupId());
		}

		return null;
	}

	private boolean _hasViewPermission(User user) throws Exception {
		if (user == null) {
			return false;
		}

		return UserPermissionUtil.contains(
			GuestOrUserUtil.getPermissionChecker(), user.getUserId(),
			ActionKeys.VIEW);
	}

	@Reference
	private Portal _portal;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
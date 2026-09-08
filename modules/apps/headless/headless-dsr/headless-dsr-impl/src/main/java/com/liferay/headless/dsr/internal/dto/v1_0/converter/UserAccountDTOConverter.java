/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.internal.dto.v1_0.converter;

import com.liferay.headless.dsr.dto.v1_0.UserAccount;
import com.liferay.headless.dsr.internal.util.TicketUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"application.name=Liferay.Headless.DSR", "default=true",
		"dto.class.name=com.liferay.headless.dsr.dto.v1_0.UserAccount",
		"version=v1.0"
	},
	service = DTOConverter.class
)
public class UserAccountDTOConverter
	implements DTOConverter<User, UserAccount> {

	@Override
	public String getContentType() {
		return User.class.getSimpleName();
	}

	@Override
	public UserAccount toDTO(DTOConverterContext dtoConverterContext, User user)
		throws Exception {

		if (!(dtoConverterContext instanceof UserAccountDTOConverterContext) ||
			(user == null)) {

			return null;
		}

		UserAccountDTOConverterContext userAccountDTOConverterContext =
			(UserAccountDTOConverterContext)dtoConverterContext;

		return new UserAccount() {
			{
				setAlternateName(user::getScreenName);
				setEmailAddress(user::getEmailAddress);
				setExternalReferenceCode(user::getExternalReferenceCode);
				setId(user::getUserId);
				setMembershipExpirationDate(
					() -> {
						Ticket ticket = TicketUtil.fetchExpireMembershipTicket(
							userAccountDTOConverterContext.getGroupId(),
							_jsonFactory, _ticketLocalService,
							user.getUserId());

						if (ticket == null) {
							return null;
						}

						return ticket.getExpirationDate();
					});
				setName(user::getFullName);
				setRoleKey(
					() -> {
						List<UserGroupRole> userGroupRoles =
							_userGroupRoleLocalService.getUserGroupRoles(
								user.getUserId(),
								userAccountDTOConverterContext.getGroupId());

						if (userGroupRoles.isEmpty()) {
							return null;
						}

						UserGroupRole userGroupRole = userGroupRoles.get(0);

						Role role = userGroupRole.getRole();

						return role.getName();
					});
			}
		};
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}
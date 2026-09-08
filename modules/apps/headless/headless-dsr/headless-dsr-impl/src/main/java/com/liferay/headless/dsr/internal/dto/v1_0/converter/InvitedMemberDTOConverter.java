/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.internal.dto.v1_0.converter;

import com.liferay.headless.dsr.dto.v1_0.InvitedMember;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"application.name=Liferay.Headless.DSR", "default=true",
		"dto.class.name=com.liferay.headless.dsr.dto.v1_0.InvitedMember",
		"version=v1.0"
	},
	service = DTOConverter.class
)
public class InvitedMemberDTOConverter
	implements DTOConverter<Ticket, InvitedMember> {

	@Override
	public String getContentType() {
		return Ticket.class.getSimpleName();
	}

	@Override
	public InvitedMember toDTO(
			DTOConverterContext dtoConverterContext, Ticket ticket)
		throws Exception {

		if (ticket == null) {
			return null;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			ticket.getExtraInfo());

		String emailAddress = jsonObject.getString("emailAddress");

		User user = _userLocalService.fetchUserByEmailAddress(
			ticket.getCompanyId(), emailAddress);

		if (user != null) {
			return null;
		}

		return new InvitedMember() {
			{
				setEmailAddress(() -> jsonObject.getString("emailAddress"));
				setId(ticket::getTicketId);
				setMembershipExpirationDate(
					() -> {
						if (jsonObject.isNull("membershipExpirationDate")) {
							return null;
						}

						return new Date(
							jsonObject.getLong("membershipExpirationDate"));
					});
				setOwnerId(
					() -> {
						if (jsonObject.isNull("ownerId")) {
							return null;
						}

						return jsonObject.getLong("ownerId");
					});
				setRoleKey(() -> jsonObject.getString("roleKey"));
			}
		};
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private UserLocalService _userLocalService;

}
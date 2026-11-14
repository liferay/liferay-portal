/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.headless.admin.user.dto.v1_0.Ticket;
import com.liferay.headless.admin.user.resource.v1_0.TicketResource;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.util.Time;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/ticket.properties",
	scope = ServiceScope.PROTOTYPE, service = TicketResource.class
)
public class TicketResourceImpl extends BaseTicketResourceImpl {

	@Override
	public Ticket getUserAccountEmailVerificationTicket(Long userAccountId)
		throws Exception {

		return _getTicket(TicketConstants.TYPE_EMAIL_ADDRESS, userAccountId);
	}

	@Override
	public Ticket getUserAccountPasswordResetTicket(Long userAccountId)
		throws Exception {

		if (userAccountId == contextUser.getUserId()) {
			throw new PrincipalException(
				"User account " + userAccountId +
					" cannot get their own password reset ticket");
		}

		return _getTicket(TicketConstants.TYPE_PASSWORD, userAccountId);
	}

	private Ticket _getTicket(int type, Long userAccountId) throws Exception {
		User user = _userLocalService.getUser(userAccountId);

		UserPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(), userAccountId,
			ActionKeys.UPDATE);

		Date expirationDate = null;

		PasswordPolicy passwordPolicy = user.getPasswordPolicy();

		if ((passwordPolicy != null) &&
			(passwordPolicy.getResetTicketMaxAge() > 0)) {

			expirationDate = new Date(
				System.currentTimeMillis() +
					(passwordPolicy.getResetTicketMaxAge() * 1000));
		}
		else {
			expirationDate = new Date(System.currentTimeMillis() + Time.DAY);
		}

		com.liferay.portal.kernel.model.Ticket ticket =
			_ticketLocalService.addTicket(
				user.getCompanyId(), User.class.getName(), userAccountId, type,
				null, null, expirationDate,
				ServiceContextThreadLocal.getServiceContext());

		String unencryptedKey = ticket.getKey();

		ticket.setKey(PasswordEncryptorUtil.encrypt(ticket.getKey()));

		_ticketLocalService.updateTicket(ticket);

		return _toTicket(ticket, unencryptedKey);
	}

	private Ticket _toTicket(
			com.liferay.portal.kernel.model.Ticket ticket,
			String unencryptedKey)
		throws Exception {

		return new Ticket() {
			{
				setExpirationDate(ticket::getExpirationDate);
				setExtraInfo(ticket::getExtraInfo);
				setId(ticket::getTicketId);
				setKey(() -> unencryptedKey);
			}
		};
	}

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
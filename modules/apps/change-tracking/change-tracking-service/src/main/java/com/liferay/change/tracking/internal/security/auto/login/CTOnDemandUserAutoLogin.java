/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.security.auto.login;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = AutoLogin.class)
public class CTOnDemandUserAutoLogin extends BaseAutoLogin {

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String portletId = ParamUtil.getString(httpServletRequest, "p_p_id");

		if (!Objects.equals(portletId, CTPortletKeys.PUBLICATIONS)) {
			return null;
		}

		Ticket ticket = _getTicket(httpServletRequest);

		if (ticket == null) {
			return null;
		}

		User user = _userLocalService.getUser(
			Long.valueOf(ticket.getExtraInfo()));

		if ((user != null) && !user.isOnDemandUser()) {
			return null;
		}

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}

	private Ticket _getTicket(HttpServletRequest httpServletRequest) {
		String ticketKey = ParamUtil.getString(httpServletRequest, "ticketKey");

		if (Validator.isNull(ticketKey)) {
			return null;
		}

		try {
			Ticket ticket = _ticketLocalService.fetchTicket(ticketKey);

			if ((ticket == null) ||
				(ticket.getType() !=
					TicketConstants.TYPE_ON_DEMAND_USER_LOGIN)) {

				return null;
			}

			if (!ticket.isExpired()) {
				return ticket;
			}

			_ticketLocalService.deleteTicket(ticket);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTOnDemandUserAutoLogin.class);

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
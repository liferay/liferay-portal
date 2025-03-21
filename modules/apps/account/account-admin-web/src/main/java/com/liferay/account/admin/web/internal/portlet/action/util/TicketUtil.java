/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action.util;

import com.liferay.account.constants.AccountTicketConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

/**
 * @author Pei-Jung Lan
 */
public class TicketUtil {

	public static Ticket getTicket(
		PortletRequest portletRequest, TicketLocalService ticketLocalService) {

		String ticketKey = ParamUtil.getString(portletRequest, "ticketKey");

		if (Validator.isNull(ticketKey)) {
			return null;
		}

		try {
			Ticket ticket = ticketLocalService.fetchTicket(ticketKey);

			if ((ticket == null) ||
				(ticket.getType() !=
					AccountTicketConstants.TYPE_USER_INVITATION)) {

				return null;
			}

			if (!ticket.isExpired()) {
				return ticket;
			}

			ticketLocalService.deleteTicket(ticket);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(TicketUtil.class);

}
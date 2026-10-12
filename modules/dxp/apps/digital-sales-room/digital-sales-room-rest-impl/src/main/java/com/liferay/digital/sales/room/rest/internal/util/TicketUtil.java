/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.rest.internal.util;

import com.liferay.digital.sales.room.constants.DSRTicketConstants;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.service.TicketLocalService;

/**
 * @author Stefano Motta
 */
public class TicketUtil {

	public static Ticket fetchExpireMembershipTicket(
			long groupId, JSONFactory jsonFactory,
			TicketLocalService ticketLocalService, long userId)
		throws Exception {

		for (Ticket ticket :
				ticketLocalService.getTickets(
					Group.class.getName(), groupId,
					DSRTicketConstants.TYPE_EXPIRE_MEMBERSHIP)) {

			JSONObject jsonObject = jsonFactory.createJSONObject(
				ticket.getExtraInfo());

			if (jsonObject.getLong("userId") == userId) {
				return ticket;
			}
		}

		return null;
	}

}
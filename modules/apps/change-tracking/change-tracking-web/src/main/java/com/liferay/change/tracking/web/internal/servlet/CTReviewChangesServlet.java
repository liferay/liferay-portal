/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.servlet;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.portal.kernel.exception.NoSuchTicketException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.change.tracking.web.internal.servlet.CTReviewChangesServlet",
		"osgi.http.whiteboard.servlet.pattern=/change_tracking/review_changes",
		"servlet.init.httpMethods=GET"
	},
	service = Servlet.class
)
public class CTReviewChangesServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		super.service(httpServletRequest, httpServletResponse);
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		Ticket ticket = _getTicket(httpServletRequest);

		if (ticket == null) {
			SessionErrors.add(httpServletRequest, NoSuchTicketException.class);

			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			httpServletResponse.sendRedirect(
				Portal.PATH_MAIN + "/portal/status");

			return;
		}

		try {
			Group group = _groupLocalService.getGroup(
				ticket.getCompanyId(), GroupConstants.CONTROL_PANEL);

			String portletNamespace = _portal.getPortletNamespace(
				CTPortletKeys.PUBLICATIONS);

			httpServletResponse.sendRedirect(
				_portal.getControlPanelFullURL(
					group.getGroupId(), CTPortletKeys.PUBLICATIONS,
					HashMapBuilder.put(
						portletNamespace.concat("mvcRenderCommandName"),
						new String[] {"/change_tracking/view_changes"}
					).put(
						portletNamespace.concat("ctCollectionId"),
						new String[] {String.valueOf(ticket.getClassPK())}
					).put(
						"ticketKey", new String[] {ticket.getKey()}
					).build()));
		}
		catch (PortalException portalException) {
			_portal.sendError(
				portalException, httpServletRequest, httpServletResponse);
		}
	}

	private Ticket _getTicket(HttpServletRequest httpServletRequest) {
		String ticketKey = ParamUtil.getString(httpServletRequest, "ticketKey");

		if (Validator.isNull(ticketKey)) {
			return null;
		}

		Ticket ticket = _ticketLocalService.fetchTicket(ticketKey);

		if ((ticket == null) ||
			(ticket.getType() != TicketConstants.TYPE_ON_DEMAND_USER_LOGIN)) {

			return null;
		}

		if (!ticket.isExpired()) {
			return ticket;
		}

		return null;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private TicketLocalService _ticketLocalService;

}
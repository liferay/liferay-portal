/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.TicketLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.constants.ActionConstants;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author Mika Koivisto
 */
public class VerifyEmailAddressAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		if (Validator.isNull(cmd)) {
			return actionMapping.getActionForward(
				"portal.verify_email_address");
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isSignedIn() && cmd.equals(Constants.SEND)) {
			sendEmailAddressVerification(
				httpServletRequest, httpServletResponse, themeDisplay);

			return actionMapping.getActionForward(
				"portal.verify_email_address");
		}

		try {
			verifyEmailAddress(
				httpServletRequest, httpServletResponse, themeDisplay);

			if (!themeDisplay.isSignedIn()) {
				PortletURL portletURL = PortletURLFactoryUtil.create(
					httpServletRequest, PortletKeys.LOGIN,
					PortletRequest.RENDER_PHASE);

				httpServletResponse.sendRedirect(portletURL.toString());

				return null;
			}

			return actionMapping.getActionForward(
				ActionConstants.COMMON_REFERER_JSP);
		}
		catch (Exception exception) {
			if (exception instanceof PortalException ||
				exception instanceof SystemException) {

				SessionErrors.add(httpServletRequest, exception.getClass());

				return actionMapping.getActionForward(
					"portal.verify_email_address");
			}

			PortalUtil.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
	}

	protected void sendEmailAddressVerification(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, ThemeDisplay themeDisplay)
		throws Exception {

		User user = themeDisplay.getUser();

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		List<Ticket> tickets = TicketLocalServiceUtil.getTickets(
			themeDisplay.getCompanyId(), User.class.getName(), user.getUserId(),
			TicketConstants.TYPE_EMAIL_ADDRESS);

		if (ListUtil.isEmpty(tickets)) {
			UserLocalServiceUtil.sendEmailAddressVerification(
				user, user.getEmailAddress(), serviceContext);
		}
		else {
			Ticket ticket = tickets.get(0);

			UserLocalServiceUtil.sendEmailAddressVerification(
				user, ticket.getExtraInfo(), serviceContext);
		}
	}

	protected void verifyEmailAddress(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, ThemeDisplay themeDisplay)
		throws Exception {

		AuthTokenUtil.checkCSRFToken(
			httpServletRequest, VerifyEmailAddressAction.class.getName());

		String ticketKey = ParamUtil.getString(httpServletRequest, "ticketKey");

		UserLocalServiceUtil.verifyEmailAddress(ticketKey);
	}

}
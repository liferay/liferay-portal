/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.action;

import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.portal.action.UpdatePasswordActionUtil;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alvaro Saugar
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LoginPortletKeys.CREATE_ACCOUNT,
		"jakarta.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"jakarta.portlet.name=" + LoginPortletKeys.FORGOT_PASSWORD,
		"jakarta.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/update_password"
	},
	service = MVCActionCommand.class
)
public class UpdatePasswordMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Ticket ticket = UpdatePasswordActionUtil.getTicket(
			ParamUtil.getString(actionRequest, "ticketId"),
			ParamUtil.getString(actionRequest, "ticketKey"));

		actionRequest.setAttribute(WebKeys.TICKET, ticket);

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (Validator.isNull(cmd)) {
			UpdatePasswordActionUtil.verifyUser(httpServletRequest, ticket);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			UpdatePasswordActionUtil.updatePassword(
				UpdatePasswordMVCActionCommand.class.getName(),
				httpServletRequest,
				_portal.getHttpServletResponse(actionResponse),
				actionRequest.getParameter("password1"),
				actionRequest.getParameter("password2"),
				ParamUtil.getString(actionRequest, "redirect"),
				actionResponse::sendRedirect, themeDisplay, ticket);
		}
		catch (Exception exception) {
			if (exception instanceof UserPasswordException) {
				SessionErrors.add(actionRequest, exception.getClass());
			}
			else if (exception instanceof NoSuchUserException ||
					 exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}

			LiferayPortletRequest liferayPortletRequest =
				_portal.getLiferayPortletRequest(actionRequest);

			Layout layout =
				_layoutUtilityPageEntryLayoutProvider.
					getDefaultLayoutUtilityPageEntryLayout(
						themeDisplay.getScopeGroupId(),
						LayoutUtilityPageEntryConstants.TYPE_FORGOT_PASSWORD);

			if (layout == null) {
				layout = (Layout)actionRequest.getAttribute(WebKeys.LAYOUT);
			}

			String ticketId = ParamUtil.getString(actionRequest, "ticketId");
			String ticketKey = ParamUtil.getString(actionRequest, "ticketKey");

			PortletURL portletURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					actionRequest, LoginPortletKeys.UPDATE_PASSWORD, layout,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/login/update_password"
			).setParameter(
				"saveLastPath", false
			).setPortletMode(
				PortletMode.VIEW
			).setWindowState(
				WindowState.MAXIMIZED
			).buildPortletURL();

			String portletName = liferayPortletRequest.getPortletName();

			if (portletName.equals(LoginPortletKeys.UPDATE_PASSWORD)) {
				if (layout.isTypeUtility()) {
					portletURL.setWindowState(WindowState.NORMAL);
				}
				else {
					portletURL.setWindowState(WindowState.MAXIMIZED);
				}
			}
			else {
				portletURL.setWindowState(actionRequest.getWindowState());
			}

			String portletURLString = portletURL.toString();

			portletURLString = HttpComponentsUtil.setParameter(
				portletURLString, "ticketId", ticketId);
			portletURLString = HttpComponentsUtil.setParameter(
				portletURLString, "ticketKey", ticketKey);

			actionResponse.sendRedirect(portletURLString);
		}
	}

	@Reference
	private LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;

	@Reference
	private Portal _portal;

}
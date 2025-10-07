/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.kernel.provider.util.LayoutUtilityPageEntryLayoutProviderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Mika Koivisto
 */
public class UpdatePasswordAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		Ticket ticket = UpdatePasswordActionUtil.getTicket(
			ParamUtil.getString(httpServletRequest, "ticketId"),
			ParamUtil.getString(httpServletRequest, "ticketKey"));

		if ((ticket != null) &&
			StringUtil.equals(
				httpServletRequest.getMethod(), HttpMethods.GET)) {

			resendAsPost(httpServletRequest, httpServletResponse);

			return null;
		}

		httpServletRequest.setAttribute(WebKeys.TICKET, ticket);

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		if (Validator.isNull(cmd)) {
			User user = UpdatePasswordActionUtil.verifyUser(
				httpServletRequest, ticket);

			if (user == null) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				Layout layout =
					LayoutUtilityPageEntryLayoutProviderUtil.
						getDefaultLayoutUtilityPageEntryLayout(
							themeDisplay.getScopeGroupId(),
							LayoutUtilityPageEntryConstants.
								TYPE_FORGOT_PASSWORD);

				if (layout != null) {
					return actionMapping.getActionForward(
						"portal.update_password_utility_page");
				}
			}

			return actionMapping.getActionForward("portal.update_password");
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			UpdatePasswordActionUtil.updatePassword(
				UpdatePasswordAction.class.getName(), httpServletRequest,
				httpServletResponse,
				httpServletRequest.getParameter("password1"),
				httpServletRequest.getParameter("password2"),
				ParamUtil.getString(httpServletRequest, WebKeys.REFERER),
				httpServletResponse::sendRedirect, themeDisplay, ticket);

			return null;
		}
		catch (Exception exception) {
			if (exception instanceof UserPasswordException) {
				SessionErrors.add(
					httpServletRequest, exception.getClass(), exception);

				return actionMapping.getActionForward("portal.update_password");
			}
			else if (exception instanceof NoSuchUserException ||
					 exception instanceof PrincipalException) {

				SessionErrors.add(httpServletRequest, exception.getClass());

				return actionMapping.getActionForward("portal.error");
			}

			PortalUtil.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
	}

	protected void resendAsPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletResponse.setHeader(
			"Cache-Control", "no-cache, no-store, must-revalidate");
		httpServletResponse.setHeader("Expires", "0");
		httpServletResponse.setHeader("Pragma", "no-cache");

		PrintWriter printWriter = httpServletResponse.getWriter();

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		StringBundler sb = new StringBundler(8 + (parameterMap.size() * 5));

		sb.append("<html><body onload=\"document.fm.submit();\">");
		sb.append("<form action=\"");
		sb.append(PortalUtil.getPortalURL(httpServletRequest));
		sb.append(PortalUtil.getPathContext());
		sb.append("/c/portal/update_password\" method=\"post\" name=\"fm\">");

		for (String name : parameterMap.keySet()) {
			String value = ParamUtil.getString(httpServletRequest, name);

			sb.append("<input name=\"");
			sb.append(HtmlUtil.escapeAttribute(name));
			sb.append("\" type=\"hidden\" value=\"");
			sb.append(HtmlUtil.escapeAttribute(value));
			sb.append("\"/>");
		}

		sb.append("<noscript>");
		sb.append("<input type=\"submit\" value=\"Please continue here...\"/>");
		sb.append("</noscript></form></body></html>");

		printWriter.write(sb.toString());

		printWriter.close();
	}

}
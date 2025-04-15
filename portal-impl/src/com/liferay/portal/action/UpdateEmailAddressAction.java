/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.constants.ActionConstants;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;
import com.liferay.portlet.admin.util.AdminUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Julio Camarero
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 */
public class UpdateEmailAddressAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		if (Validator.isNull(cmd)) {
			return actionMapping.getActionForward(
				"portal.update_email_address");
		}

		try {
			updateEmailAddress(httpServletRequest);

			return actionMapping.getActionForward(
				ActionConstants.COMMON_REFERER_JSP);
		}
		catch (Exception exception) {
			if (exception instanceof UserEmailAddressException) {
				SessionErrors.add(httpServletRequest, exception.getClass());

				return actionMapping.getActionForward(
					"portal.update_email_address");
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

	protected void updateEmailAddress(HttpServletRequest httpServletRequest)
		throws Exception {

		AuthTokenUtil.checkCSRFToken(
			httpServletRequest, UpdateEmailAddressAction.class.getName());

		long userId = PortalUtil.getUserId(httpServletRequest);

		String password = AdminUtil.getUpdateUserPassword(
			httpServletRequest, userId);

		String emailAddress1 = ParamUtil.getString(
			httpServletRequest, "emailAddress1");
		String emailAddress2 = ParamUtil.getString(
			httpServletRequest, "emailAddress2");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		UserServiceUtil.updateEmailAddress(
			userId, password, emailAddress1, emailAddress2, serviceContext);
	}

}
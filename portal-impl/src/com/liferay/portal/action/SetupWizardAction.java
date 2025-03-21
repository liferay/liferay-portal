/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.setup.SetupWizardUtil;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLException;

/**
 * @author Manuel de la Peña
 * @author Brian Wing Shun Chan
 */
public class SetupWizardAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!PropsValues.SETUP_WIZARD_ENABLED) {
			httpServletResponse.sendRedirect(themeDisplay.getPathMain());
		}

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		try {
			if (Validator.isNull(cmd)) {
				return actionMapping.getActionForward("portal.setup_wizard");
			}
			else if (cmd.equals(Constants.TRANSLATE)) {
				SetupWizardUtil.updateLanguage(
					httpServletRequest, httpServletResponse);

				return actionMapping.getActionForward("portal.setup_wizard");
			}
			else if (cmd.equals(Constants.TEST)) {
				testDatabase(httpServletRequest, httpServletResponse);

				return null;
			}
			else if (cmd.equals(Constants.UPDATE)) {
				SetupWizardUtil.updateSetup(
					httpServletRequest, httpServletResponse);

				if (ParamUtil.getBoolean(
						httpServletRequest, "defaultDatabase")) {

					PropsValues.SETUP_WIZARD_ENABLED = false;
				}
			}

			httpServletResponse.sendRedirect(
				themeDisplay.getPathMain() + "/portal/setup_wizard");

			return null;
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				SessionErrors.add(httpServletRequest, exception.getClass());

				return actionMapping.getActionForward("portal.setup_wizard");
			}

			PortalUtil.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
	}

	protected void putMessage(
		HttpServletRequest httpServletRequest, JSONObject jsonObject,
		String key, Object... arguments) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String message = themeDisplay.translate(key, arguments);

		jsonObject.put("message", message);
	}

	protected void testDatabase(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		try {
			SetupWizardUtil.testDatabase(httpServletRequest);

			jsonObject.put("success", true);

			putMessage(
				httpServletRequest, jsonObject,
				"database-connection-was-established-successfully");
		}
		catch (ClassNotFoundException classNotFoundException) {
			putMessage(
				httpServletRequest, jsonObject,
				"database-driver-x-is-not-present",
				classNotFoundException.getLocalizedMessage());
		}
		catch (SQLException sqlException) {
			if (_log.isDebugEnabled()) {
				_log.debug(sqlException);
			}

			putMessage(
				httpServletRequest, jsonObject,
				"database-connection-could-not-be-established");
		}

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setHeader(
			HttpHeaders.CACHE_CONTROL,
			HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);

		ServletResponseUtil.write(httpServletResponse, jsonObject.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SetupWizardAction.class);

}
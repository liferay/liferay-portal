/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action;

import com.liferay.launch.exception.LaunchSetScheduleException;
import com.liferay.launch.set.scheduler.LaunchSetScheduler;
import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LaunchPortletKeys.LAUNCH,
		"mvc.command.name=/launch/schedule_launch"
	},
	service = MVCActionCommand.class
)
public class ScheduleLaunchMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			_launchSetScheduler.schedulePublish(
				ParamUtil.getLong(actionRequest, "launchSetId"),
				themeDisplay.getUserId(),
				new Date(ParamUtil.getLong(actionRequest, "publishTime")));
		}
		catch (Exception exception) {
			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				_getErrorJSONObject(actionRequest, exception));

			return;
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse,
			JSONUtil.put(
				"redirect",
				_portal.escapeRedirect(
					ParamUtil.getString(actionRequest, "redirect"))));
	}

	private JSONObject _getErrorJSONObject(
		ActionRequest actionRequest, Exception exception) {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		if (exception instanceof LaunchSetScheduleException.MustBeOpen) {
			return JSONUtil.put(
				"error",
				_language.get(
					httpServletRequest, "only-open-launches-can-be-scheduled"));
		}

		if (exception instanceof
				LaunchSetScheduleException.MustHaveFutureDate) {

			return JSONUtil.put(
				"validationError",
				_language.get(
					httpServletRequest,
					"the-publish-time-must-be-in-the-future"));
		}

		if (exception instanceof PrincipalException) {
			return JSONUtil.put(
				"error",
				_language.get(
					httpServletRequest,
					"you-do-not-have-the-required-permissions"));
		}

		if (_log.isWarnEnabled()) {
			_log.warn(exception);
		}

		return JSONUtil.put(
			"error",
			_language.get(httpServletRequest, "an-unexpected-error-occurred"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ScheduleLaunchMVCActionCommand.class);

	@Reference
	private Language _language;

	@Reference
	private LaunchSetScheduler _launchSetScheduler;

	@Reference
	private Portal _portal;

}
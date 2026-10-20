/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action;

import com.liferay.launch.set.scheduler.LaunchSetScheduler;
import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

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
public class ScheduleLaunchMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			return super.processAction(actionRequest, actionResponse);
		}
		catch (PortletException portletException) {
			if (!(portletException.getCause() instanceof PortalException)) {
				throw portletException;
			}

			try {
				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					_getErrorJSONObject(
						actionRequest,
						(PortalException)portletException.getCause()));
			}
			catch (IOException ioException) {
				throw new PortletException(ioException);
			}
		}

		return false;
	}

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		Date publishDate = new Date(
			ParamUtil.getLong(actionRequest, "publishTime"));

		if (!publishDate.after(new Date())) {
			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"validationError",
					_language.get(
						_portal.getHttpServletRequest(actionRequest),
						"the-publish-time-must-be-in-the-future")));

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_launchSetScheduler.schedulePublish(
			ParamUtil.getLong(actionRequest, "launchSetId"),
			themeDisplay.getUserId(), publishDate);

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
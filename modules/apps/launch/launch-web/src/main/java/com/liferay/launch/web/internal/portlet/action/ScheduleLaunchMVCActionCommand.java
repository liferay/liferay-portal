/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action;

import com.liferay.launch.set.publisher.LaunchSetPublisher;
import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

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
public class ScheduleLaunchMVCActionCommand extends BaseLaunchMVCActionCommand {

	@Override
	protected void doLaunchCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		long launchSetId = ParamUtil.getLong(actionRequest, "launchSetId");

		_objectEntryService.checkModelResourcePermission(
			_objectEntryLocalService.getObjectEntry(launchSetId),
			ActionKeys.UPDATE);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			_launchSetPublisher.schedulePublish(
				new Date(ParamUtil.getLong(actionRequest, "publishTime")),
				launchSetId, themeDisplay.getUserId());
		}
		catch (SchedulerException schedulerException) {
			if (_log.isDebugEnabled()) {
				_log.debug(schedulerException);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"validationError",
					language.get(
						portal.getHttpServletRequest(actionRequest),
						"the-date-and-time-must-be-at-least-one-minute-in-" +
							"the-future")));

			return;
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse,
			JSONUtil.put(
				"redirect",
				portal.escapeRedirect(
					ParamUtil.getString(actionRequest, "redirect"))));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ScheduleLaunchMVCActionCommand.class);

	@Reference
	private LaunchSetPublisher _launchSetPublisher;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}
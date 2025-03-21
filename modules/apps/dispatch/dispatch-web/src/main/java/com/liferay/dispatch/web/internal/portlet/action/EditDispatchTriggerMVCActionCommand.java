/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.portlet.action;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.executor.DispatchTaskExecutorRegistry;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.service.DispatchTriggerService;
import com.liferay.dispatch.web.internal.security.permisison.resource.DispatchTriggerPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Calendar;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DispatchPortletKeys.DISPATCH,
		"mvc.command.name=/dispatch/edit_dispatch_trigger"
	},
	service = MVCActionCommand.class
)
public class EditDispatchTriggerMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (Objects.equals(cmd, Constants.ADD) ||
				Objects.equals(cmd, Constants.UPDATE)) {

				_updateDispatchTrigger(actionRequest);
			}
			else if (Objects.equals(cmd, Constants.DELETE)) {
				_deleteDispatchTrigger(actionRequest);
			}
			else if (Objects.equals(cmd, "runProcess")) {
				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(actionResponse);

				httpServletResponse.setContentType(
					ContentTypes.APPLICATION_JSON);

				_writeJSON(actionResponse, _runProcess(actionRequest));

				hideDefaultSuccessMessage(actionRequest);
			}
			else if (Objects.equals(cmd, "schedule")) {
				_scheduleDispatchTrigger(actionRequest);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			SessionErrors.add(actionRequest, exception.getClass());
		}
	}

	private void _checkPermission(
			ActionRequest actionRequest, long dispatchTriggerId)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DispatchTriggerPermission.contains(
			themeDisplay.getPermissionChecker(), dispatchTriggerId,
			ActionKeys.UPDATE);
	}

	private void _deleteDispatchTrigger(ActionRequest actionRequest)
		throws PortalException {

		long[] deleteDispatchTriggerIds = null;

		long dispatchTriggerId = ParamUtil.getLong(
			actionRequest, "dispatchTriggerId");

		if (dispatchTriggerId > 0) {
			deleteDispatchTriggerIds = new long[] {dispatchTriggerId};
		}
		else {
			deleteDispatchTriggerIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long deleteDispatchTriggerId : deleteDispatchTriggerIds) {
			_dispatchTriggerService.deleteDispatchTrigger(
				deleteDispatchTriggerId);
		}
	}

	private DispatchTaskClusterMode _getDispatchTaskClusterMode(
			long dispatchTaskId,
			DispatchTaskClusterMode dispatchTaskClusterMode)
		throws PortalException {

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.getDispatchTrigger(dispatchTaskId);

		if (_dispatchTaskExecutorRegistry.isClusterModeSingle(
				dispatchTrigger.getDispatchTaskExecutorType())) {

			return DispatchTaskClusterMode.SINGLE_NODE_PERSISTED;
		}

		return dispatchTaskClusterMode;
	}

	private JSONObject _runProcess(ActionRequest actionRequest)
		throws PortalException {

		long dispatchTriggerId = ParamUtil.getLong(
			actionRequest, "dispatchTriggerId");

		_checkPermission(actionRequest, dispatchTriggerId);

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			_sendMessage(dispatchTriggerId);
		}
		catch (Exception exception) {
			hideDefaultErrorMessage(actionRequest);

			_log.error(exception);

			jsonObject.put(
				"error", exception.getMessage()
			).put(
				"success", false
			);
		}

		jsonObject.put("success", true);

		return jsonObject;
	}

	private void _scheduleDispatchTrigger(ActionRequest actionRequest)
		throws PortalException {

		long dispatchTriggerId = ParamUtil.getLong(
			actionRequest, "dispatchTriggerId");

		boolean active = ParamUtil.getBoolean(actionRequest, "active");
		String cronExpression = ParamUtil.getString(
			actionRequest, "cronExpression");
		DispatchTaskClusterMode dispatchTaskClusterMode =
			_getDispatchTaskClusterMode(
				dispatchTriggerId,
				DispatchTaskClusterMode.valueOf(
					ParamUtil.getInteger(
						actionRequest, "dispatchTaskClusterMode")));
		int endDateMonth = ParamUtil.getInteger(actionRequest, "endDateMonth");
		int endDateDay = ParamUtil.getInteger(actionRequest, "endDateDay");
		int endDateYear = ParamUtil.getInteger(actionRequest, "endDateYear");
		int endDateHour = ParamUtil.getInteger(actionRequest, "endDateHour");
		int endDateMinute = ParamUtil.getInteger(
			actionRequest, "endDateMinute");

		int endDateAmPm = ParamUtil.getInteger(actionRequest, "endDateAmPm");

		if (endDateAmPm == Calendar.PM) {
			endDateHour += 12;
		}

		boolean neverEnd = ParamUtil.getBoolean(actionRequest, "neverEnd");
		boolean overlapAllowed = ParamUtil.getBoolean(
			actionRequest, "overlapAllowed");

		int startDateMonth = ParamUtil.getInteger(
			actionRequest, "startDateMonth");
		int startDateDay = ParamUtil.getInteger(actionRequest, "startDateDay");
		int startDateYear = ParamUtil.getInteger(
			actionRequest, "startDateYear");
		int startDateHour = ParamUtil.getInteger(
			actionRequest, "startDateHour");
		int startDateMinute = ParamUtil.getInteger(
			actionRequest, "startDateMinute");

		int startDateAmPm = ParamUtil.getInteger(
			actionRequest, "startDateAmPm");

		if (startDateAmPm == Calendar.PM) {
			startDateHour += 12;
		}

		String timeZoneId = ParamUtil.getString(actionRequest, "timeZoneId");

		_dispatchTriggerService.updateDispatchTrigger(
			dispatchTriggerId, active, cronExpression, dispatchTaskClusterMode,
			endDateMonth, endDateDay, endDateYear, endDateHour, endDateMinute,
			neverEnd, overlapAllowed, startDateMonth, startDateDay,
			startDateYear, startDateHour, startDateMinute, timeZoneId);
	}

	private void _sendMessage(long dispatchTriggerId) {
		Message message = new Message();

		message.setPayload(
			JSONUtil.put(
				"dispatchTriggerId", dispatchTriggerId
			).toString());

		_messageBus.sendMessage(
			DispatchConstants.EXECUTOR_DESTINATION_NAME, message);
	}

	private DispatchTrigger _updateDispatchTrigger(ActionRequest actionRequest)
		throws Exception {

		long dispatchTriggerId = ParamUtil.getLong(
			actionRequest, "dispatchTriggerId");

		String name = ParamUtil.getString(actionRequest, "name");

		UnicodeProperties dispatchTaskSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				ParamUtil.getString(actionRequest, "dispatchTaskSettings")
			).build();

		DispatchTrigger dispatchTrigger = null;

		if (dispatchTriggerId > 0) {
			dispatchTrigger = _dispatchTriggerService.updateDispatchTrigger(
				dispatchTriggerId, dispatchTaskSettingsUnicodeProperties, name);
		}
		else {
			String dispatchTaskExecutorType = ParamUtil.getString(
				actionRequest, "dispatchTaskExecutorType");

			dispatchTrigger = _dispatchTriggerService.addDispatchTrigger(
				null, _portal.getUserId(actionRequest),
				dispatchTaskExecutorType, dispatchTaskSettingsUnicodeProperties,
				name);
		}

		return dispatchTrigger;
	}

	private void _writeJSON(ActionResponse actionResponse, Object object)
		throws IOException {

		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(actionResponse);

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		ServletResponseUtil.write(httpServletResponse, object.toString());

		httpServletResponse.flushBuffer();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditDispatchTriggerMVCActionCommand.class);

	@Reference
	private DispatchTaskExecutorRegistry _dispatchTaskExecutorRegistry;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Reference
	private DispatchTriggerService _dispatchTriggerService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private Portal _portal;

}
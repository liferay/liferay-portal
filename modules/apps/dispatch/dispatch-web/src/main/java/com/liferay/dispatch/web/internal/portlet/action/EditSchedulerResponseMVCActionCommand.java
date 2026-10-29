/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.portlet.action;

import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.scheduler.SchedulerResponseManager;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DispatchPortletKeys.DISPATCH,
		"mvc.command.name=/dispatch/edit_scheduler_response"
	},
	service = MVCActionCommand.class
)
public class EditSchedulerResponseMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		String groupName = ParamUtil.getString(actionRequest, "groupName");
		String jobName = ParamUtil.getString(actionRequest, "jobName");
		StorageType storageType = StorageType.valueOf(
			ParamUtil.getString(actionRequest, "storageType"));

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (Objects.equals(cmd, "pause")) {
			_schedulerResponseManager.pause(
				permissionChecker, jobName, groupName, storageType);
		}
		else if (Objects.equals(cmd, "resume")) {
			_schedulerResponseManager.resume(
				permissionChecker, jobName, groupName, storageType);
		}
		else if (Objects.equals(cmd, "runScheduledJob")) {
			_schedulerResponseManager.run(
				permissionChecker, jobName, groupName, storageType);
		}
	}

	@Reference
	private SchedulerResponseManager _schedulerResponseManager;

}
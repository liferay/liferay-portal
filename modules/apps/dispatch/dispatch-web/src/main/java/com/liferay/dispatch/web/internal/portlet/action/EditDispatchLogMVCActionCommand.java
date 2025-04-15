/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.portlet.action;

import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.service.DispatchLogService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author guywandji
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DispatchPortletKeys.DISPATCH,
		"mvc.command.name=/dispatch/edit_dispatch_log"
	},
	service = MVCActionCommand.class
)
public class EditDispatchLogMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (Objects.equals(cmd, Constants.DELETE)) {
				_deleteDispatchLog(actionRequest);
			}
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");
		}
	}

	private void _deleteDispatchLog(ActionRequest actionRequest)
		throws PortalException {

		long[] deleteDispatchLogIds = null;

		long dispatchLogId = ParamUtil.getLong(actionRequest, "dispatchLogId");

		if (dispatchLogId > 0) {
			deleteDispatchLogIds = new long[] {dispatchLogId};
		}
		else {
			deleteDispatchLogIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "deleteDispatchLogIds"), 0L);
		}

		for (long deleteDispatchLogId : deleteDispatchLogIds) {
			_dispatchLogService.deleteDispatchLog(deleteDispatchLogId);
		}
	}

	@Reference
	private DispatchLogService _dispatchLogService;

}
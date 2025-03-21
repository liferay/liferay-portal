/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KaleoFormsPortletKeys.KALEO_FORMS_ADMIN,
		"mvc.command.name=/kaleo_forms_admin/delete_kaleo_process"
	},
	service = MVCActionCommand.class
)
public class DeleteKaleoProcessMVCActionCommand
	extends BaseKaleoFormsMVCActionCommand {

	/**
	 * Deletes the <code>KaleoProcess</code> (in the
	 * <code>com.liferay.portal.workflow.kaleo.forms.api</code> module)
	 * associated with the Kaleo process IDs from the action request.
	 *
	 * @param  actionRequest the request from which to get the request
	 *         parameters
	 * @param  actionResponse the response to receive the render parameters
	 * @throws Exception if an exception occurred
	 */
	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] kaleoProcessIds = _getKaleoProcessIds(actionRequest);

		for (final long kaleoProcessId : kaleoProcessIds) {
			kaleoProcessService.deleteKaleoProcess(kaleoProcessId);
		}
	}

	/**
	 * Returns an array of the Kaleo process IDs in the action request.
	 *
	 * @param  actionRequest the request from which to get the request
	 *         parameters
	 * @return an array of the Kaleo process IDs
	 */
	private long[] _getKaleoProcessIds(ActionRequest actionRequest) {
		long kaleoProcessId = ParamUtil.getLong(
			actionRequest, "kaleoProcessId");

		if (kaleoProcessId > 0) {
			return new long[] {kaleoProcessId};
		}

		return StringUtil.split(
			ParamUtil.getString(actionRequest, "kaleoProcessIds"), 0L);
	}

}
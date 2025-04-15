/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;
import com.liferay.portal.reports.engine.console.service.EntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gavin Wan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
		"mvc.command.name=/reports_admin/unschedule_report_request"
	},
	service = MVCActionCommand.class
)
public class UnscheduleReportRequestMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long entryId = ParamUtil.getLong(actionRequest, "entryId");

		_entryService.unscheduleEntry(entryId);
	}

	@Reference
	private EntryService _entryService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.portlet.action;

import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.service.DDLRecordService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS,
		"jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
		"mvc.command.name=/dynamic_data_lists/delete_record"
	},
	service = MVCActionCommand.class
)
public class DeleteRecordMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] recordIds = _getRecordIds(actionRequest);

		for (long recordId : recordIds) {
			_ddlRecordService.deleteRecord(recordId);
		}
	}

	private long[] _getRecordIds(ActionRequest actionRequest) {
		return StringUtil.split(
			ParamUtil.getString(actionRequest, "recordIds"), 0L);
	}

	@Reference
	private DDLRecordService _ddlRecordService;

}
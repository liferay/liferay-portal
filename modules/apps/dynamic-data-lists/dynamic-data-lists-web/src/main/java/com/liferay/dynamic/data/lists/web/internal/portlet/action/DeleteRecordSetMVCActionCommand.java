/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.portlet.action;

import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.service.DDLRecordSetService;
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
		"mvc.command.name=/dynamic_data_lists/delete_record_set"
	},
	service = MVCActionCommand.class
)
public class DeleteRecordSetMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] recordSetIds = _getRecordSetIds(actionRequest);

		for (long recordSetId : recordSetIds) {
			_ddlRecordSetService.deleteRecordSet(recordSetId);
		}
	}

	private long[] _getRecordSetIds(ActionRequest actionRequest) {
		long recordSetId = ParamUtil.getLong(actionRequest, "recordSetId");

		if (recordSetId > 0) {
			return new long[] {recordSetId};
		}

		return StringUtil.split(
			ParamUtil.getString(actionRequest, "recordSetIds"), 0L);
	}

	@Reference
	private DDLRecordSetService _ddlRecordSetService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/delete_form_instance_record"
	},
	service = MVCActionCommand.class
)
public class DeleteFormInstanceRecordMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteFormInstanceRecordIds = null;

		long formInstanceRecordId = ParamUtil.getLong(
			actionRequest, "formInstanceRecordId");

		if (formInstanceRecordId > 0) {
			deleteFormInstanceRecordIds = new long[] {formInstanceRecordId};
		}
		else {
			deleteFormInstanceRecordIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteFormInstanceRecordIds"),
				0L);
		}

		for (long deleteFormInstanceRecordId : deleteFormInstanceRecordIds) {
			_ddmFormInstanceRecordService.deleteFormInstanceRecord(
				deleteFormInstanceRecordId);
		}
	}

	@Reference
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

}
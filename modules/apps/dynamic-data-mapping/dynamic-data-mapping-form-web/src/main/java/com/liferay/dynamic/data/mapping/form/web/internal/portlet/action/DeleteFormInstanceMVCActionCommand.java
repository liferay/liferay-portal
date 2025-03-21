/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/delete_form_instance"
	},
	service = MVCActionCommand.class
)
public class DeleteFormInstanceMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteFormInstanceIds = null;

		long formInstanceId = ParamUtil.getLong(
			actionRequest, "formInstanceId");

		if (formInstanceId > 0) {
			deleteFormInstanceIds = new long[] {formInstanceId};
		}
		else {
			deleteFormInstanceIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "deleteFormInstanceIds"),
				0L);
		}

		for (long deleteFormInstanceId : deleteFormInstanceIds) {
			_deleteFormInstance(deleteFormInstanceId);
		}
	}

	private void _deleteFormInstance(long formInstanceId) throws Exception {
		_ddmFormInstanceService.deleteFormInstance(formInstanceId);
	}

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

}
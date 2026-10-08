/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

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
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long formInstanceRecordId = ParamUtil.getLong(
			actionRequest, "formInstanceRecordId");

		if (formInstanceRecordId > 0) {
			_ddmFormInstanceRecordService.deleteFormInstanceRecord(
				formInstanceRecordId);

			return;
		}

		for (long deleteFormInstanceRecordId :
				ParamUtil.getLongValues(
					actionRequest, "deleteFormInstanceRecordIds")) {

			try {
				_ddmFormInstanceRecordService.deleteFormInstanceRecord(
					deleteFormInstanceRecordId);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteFormInstanceRecordMVCActionCommand.class);

	@Reference
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

}
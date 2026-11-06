/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.BulkSelectionRunner;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collections;

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

		if (ParamUtil.getBoolean(actionRequest, "selectAll")) {
			_deleteFormInstanceRecordsInBackground(
				actionRequest, actionResponse);

			return;
		}

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

	private void _deleteFormInstanceRecordsInBackground(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long formInstanceId = ParamUtil.getLong(
			actionRequest, "formInstanceId");

		_ddmFormInstanceModelResourcePermission.check(
			themeDisplay.getPermissionChecker(), formInstanceId,
			ActionKeys.DELETE);

		_bulkSelectionRunner.run(
			themeDisplay.getUser(),
			_ddmFormInstanceRecordBulkSelectionFactory.create(
				HashMapBuilder.put(
					"createDate",
					new String[] {String.valueOf(System.currentTimeMillis())}
				).put(
					"formInstanceId",
					new String[] {String.valueOf(formInstanceId)}
				).build()),
			_deleteDDMFormInstanceRecordBulkSelectionAction,
			Collections.emptyMap());

		hideDefaultSuccessMessage(actionRequest);

		SessionMessages.add(
			actionRequest, "deleteFormInstanceRecordsInBackground");

		sendRedirect(actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteFormInstanceRecordMVCActionCommand.class);

	@Reference
	private BulkSelectionRunner _bulkSelectionRunner;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstance)"
	)
	private ModelResourcePermission<DDMFormInstance>
		_ddmFormInstanceModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord)"
	)
	private BulkSelectionFactory<DDMFormInstanceRecord>
		_ddmFormInstanceRecordBulkSelectionFactory;

	@Reference
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

	@Reference(
		target = "(bulk.selection.action.key=delete.ddm.form.instance.record)"
	)
	private BulkSelectionAction<DDMFormInstanceRecord>
		_deleteDDMFormInstanceRecordBulkSelectionAction;

}
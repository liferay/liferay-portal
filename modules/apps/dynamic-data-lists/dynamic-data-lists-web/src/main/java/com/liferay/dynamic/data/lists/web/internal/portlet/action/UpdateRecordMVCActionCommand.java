/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.portlet.action;

import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLRecordConstants;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS,
		"jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
		"mvc.command.name=/dynamic_data_lists/update_record"
	},
	service = MVCActionCommand.class
)
public class UpdateRecordMVCActionCommand extends AddRecordMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long recordId = ParamUtil.getLong(actionRequest, "recordId");

		DDMFormValues ddmFormValues = getDDMFormValues(actionRequest);
		boolean majorVersion = ParamUtil.getBoolean(
			actionRequest, "majorVersion");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDLRecord.class.getName(), actionRequest);

		ddlRecordService.updateRecord(
			recordId, majorVersion, DDLRecordConstants.DISPLAY_INDEX_DEFAULT,
			ddmFormValues, serviceContext);

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");

		if (Validator.isNotNull(portletResource)) {
			MultiSessionMessages.add(
				actionRequest, portletResource + "requestProcessed");

			sendRedirect(actionRequest, actionResponse);
		}
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.portlet.action;

import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS,
		"jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
		"mvc.command.name=/dynamic_data_lists/update_record_set"
	},
	service = MVCActionCommand.class
)
public class UpdateRecordSetMVCActionCommand
	extends AddRecordSetMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		DDLRecordSet recordSet = _updateRecordSet(actionRequest);

		updateWorkflowDefinitionLink(actionRequest, recordSet);

		updatePortletPreferences(actionRequest, recordSet);
	}

	private DDLRecordSet _updateRecordSet(ActionRequest actionRequest)
		throws Exception {

		long recordSetId = ParamUtil.getLong(actionRequest, "recordSetId");

		long ddmStructureId = ParamUtil.getLong(
			actionRequest, "ddmStructureId");
		Map<Locale, String> nameMap = localization.getLocalizationMap(
			actionRequest, "name");
		Map<Locale, String> descriptionMap = localization.getLocalizationMap(
			actionRequest, "description");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDLRecordSet.class.getName(), actionRequest);

		return ddlRecordSetService.updateRecordSet(
			recordSetId, ddmStructureId, nameMap, descriptionMap,
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT, serviceContext);
	}

}
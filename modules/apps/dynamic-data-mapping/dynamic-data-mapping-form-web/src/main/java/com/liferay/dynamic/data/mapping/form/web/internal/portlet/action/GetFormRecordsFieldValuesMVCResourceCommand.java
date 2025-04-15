/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.util.DDMFormReportDataUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/get_form_records_field_values"
	},
	service = MVCResourceCommand.class
)
public class GetFormRecordsFieldValuesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			_getFieldValuesJSONArray(
				_portal.getHttpServletRequest(resourceRequest)));
	}

	private JSONArray _getFieldValuesJSONArray(
			HttpServletRequest httpServletRequest)
		throws Exception {

		String fieldName = ParamUtil.getString(httpServletRequest, "fieldName");

		BaseModelSearchResult<DDMFormInstanceRecord> baseModelSearchResult =
			_ddmFormInstanceRecordLocalService.searchFormInstanceRecords(
				ParamUtil.getLong(httpServletRequest, "formInstanceId"),
				new String[] {fieldName}, WorkflowConstants.STATUS_APPROVED,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, true));

		return DDMFormReportDataUtil.getFieldValuesJSONArray(
			baseModelSearchResult.getBaseModels(), fieldName);
	}

	@Reference
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Reference
	private Portal _portal;

}
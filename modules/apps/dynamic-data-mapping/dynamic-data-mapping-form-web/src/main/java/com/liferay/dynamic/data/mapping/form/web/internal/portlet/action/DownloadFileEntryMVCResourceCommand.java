/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
		"mvc.command.name=/dynamic_data_mapping_form/download_file_entry"
	},
	service = MVCResourceCommand.class
)
public class DownloadFileEntryMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long fileEntryId = 0;

		DDMFormInstanceRecord ddmFormInstanceRecord =
			_ddmFormInstanceRecordService.getFormInstanceRecord(
				ParamUtil.getLong(resourceRequest, "ddmFormInstanceRecordId"));

		DDMFormValues ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormFieldValuesMap.get(
					ParamUtil.getString(resourceRequest, "ddmFormFieldName"))) {

			Value value = ddmFormFieldValue.getValue();

			if (value == null) {
				continue;
			}

			JSONObject valueJSONObject = _jsonFactory.createJSONObject(
				value.getString(value.getDefaultLocale()));

			if (valueJSONObject.isNull("fileEntryId")) {
				continue;
			}

			if (Objects.equals(
					valueJSONObject.getLong("fileEntryId"),
					ParamUtil.getLong(resourceRequest, "fileEntryId"))) {

				fileEntryId = valueJSONObject.getLong("fileEntryId");

				break;
			}
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, fileEntry.getFileName(),
			fileEntry.getContentStream(),
			MimeTypesUtil.getContentType(fileEntry.getFileName()));
	}

	@Reference
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}
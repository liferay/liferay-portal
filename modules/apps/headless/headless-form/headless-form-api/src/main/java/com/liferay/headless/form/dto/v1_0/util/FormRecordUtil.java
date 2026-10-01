/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.dto.v1_0.util;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.headless.form.dto.v1_0.FormDocument;
import com.liferay.headless.form.dto.v1_0.FormFieldValue;
import com.liferay.headless.form.dto.v1_0.FormRecord;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Locale;

/**
 * @author Victor Oliveira
 */
public class FormRecordUtil {

	public static FormRecord toFormRecord(
			DDMFormInstanceRecord ddmFormInstanceRecord,
			DLAppService dlAppService, DLURLHelper dlurlHelper, Locale locale,
			Portal portal, UserLocalService userLocalService)
		throws Exception {

		return new FormRecord() {
			{
				setCreator(
					() -> CreatorUtil.toCreator(
						portal,
						userLocalService.fetchUser(
							ddmFormInstanceRecord.getUserId())));
				setDateCreated(ddmFormInstanceRecord::getCreateDate);
				setDateModified(ddmFormInstanceRecord::getModifiedDate);
				setDatePublished(ddmFormInstanceRecord::getLastPublishDate);
				setDraft(
					() ->
						ddmFormInstanceRecord.getStatus() ==
							WorkflowConstants.STATUS_DRAFT);
				setFormFieldValues(
					() -> {
						DDMFormValues ddmFormValues =
							ddmFormInstanceRecord.getDDMFormValues();

						return TransformUtil.transformToArray(
							ddmFormValues.getDDMFormFieldValues(),
							ddmFormFieldValue -> {
								Value localizedValue =
									ddmFormFieldValue.getValue();

								if (localizedValue == null) {
									return null;
								}

								return new FormFieldValue() {
									{
										setFormDocument(
											() -> _toFormDocument(
												dlAppService, dlurlHelper,
												locale, localizedValue));
										setName(ddmFormFieldValue::getName);
										setValue(
											() -> localizedValue.getString(
												locale));
									}
								};
							},
							FormFieldValue.class);
					});
				setId(ddmFormInstanceRecord::getFormInstanceRecordId);
			}
		};
	}

	private static FormDocument _toFormDocument(
			DLAppService dlAppService, DLURLHelper dlurlHelper, Locale locale,
			Value localizedValue)
		throws Exception {

		FileEntry fileEntry = null;

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				localizedValue.getString(locale));

			long fileEntryId = jsonObject.getLong("fileEntryId", 0);

			if (fileEntryId > 0) {
				fileEntry = dlAppService.getFileEntry(fileEntryId);
			}
		}
		catch (JSONException jsonException) {
			if (_log.isWarnEnabled()) {
				_log.warn(jsonException);
			}
		}

		if (fileEntry == null) {
			return null;
		}

		return FormDocumentUtil.toFormDocument(dlurlHelper, fileEntry);
	}

	private static final Log _log = LogFactoryUtil.getLog(FormRecordUtil.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.constants.DDMFormInstanceReportConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.dynamic.data.mapping.report.DDMFormFieldTypeReportProcessor;
import com.liferay.dynamic.data.mapping.report.DDMFormFieldTypeReportProcessorRegistry;
import com.liferay.dynamic.data.mapping.service.base.DDMFormInstanceReportLocalServiceBaseImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstancePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceRecordVersionPersistence;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport",
	service = AopService.class
)
public class DDMFormInstanceReportLocalServiceImpl
	extends DDMFormInstanceReportLocalServiceBaseImpl {

	@Override
	public DDMFormInstanceReport addFormInstanceReport(long formInstanceId)
		throws PortalException {

		DDMFormInstanceReport ddmFormInstanceReport =
			ddmFormInstanceReportPersistence.create(
				counterLocalService.increment());

		DDMFormInstance ddmFormInstance =
			_ddmFormInstancePersistence.findByPrimaryKey(formInstanceId);

		ddmFormInstanceReport.setGroupId(ddmFormInstance.getGroupId());
		ddmFormInstanceReport.setCompanyId(ddmFormInstance.getCompanyId());

		ddmFormInstanceReport.setCreateDate(new Date());
		ddmFormInstanceReport.setFormInstanceId(
			ddmFormInstance.getFormInstanceId());

		return ddmFormInstanceReportPersistence.update(ddmFormInstanceReport);
	}

	@Override
	public DDMFormInstanceReport getFormInstanceReportByFormInstanceId(
			long formInstanceId)
		throws PortalException {

		return ddmFormInstanceReportPersistence.findByFormInstanceId(
			formInstanceId);
	}

	@Override
	public void processFormInstanceReportEvent(
		long formInstanceReportId, long formInstanceRecordVersionId,
		String ddmFormInstanceReportEvent) {

		try {
			ddmFormInstanceReportLocalService.updateFormInstanceReport(
				formInstanceReportId, formInstanceRecordVersionId,
				ddmFormInstanceReportEvent);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to update dynamic data mapping form instance ",
						"report ", formInstanceReportId),
					exception);
			}
		}
	}

	@Override
	public DDMFormInstanceReport updateFormInstanceReport(
			long formInstanceReportId, long formInstanceRecordVersionId,
			String ddmFormInstanceReportEvent)
		throws PortalException {

		DDMFormInstanceReport ddmFormInstanceReport =
			ddmFormInstanceReportPersistence.findByPrimaryKey(
				formInstanceReportId);

		ddmFormInstanceReport.setModifiedDate(new Date());
		ddmFormInstanceReport.setData(
			_getData(formInstanceRecordVersionId, ddmFormInstanceReportEvent));

		return ddmFormInstanceReportPersistence.update(ddmFormInstanceReport);
	}

	private String _getData(
			long formInstanceRecordVersionId, String ddmFormInstanceReportEvent)
		throws PortalException {

		try {
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
				_ddmFormInstanceRecordVersionPersistence.findByPrimaryKey(
					formInstanceRecordVersionId);

			DDMFormInstanceReport ddmFormInstanceReport =
				ddmFormInstanceReportPersistence.findByFormInstanceId(
					ddmFormInstanceRecordVersion.getFormInstanceId());

			JSONObject ddmFormInstanceReportDataJSONObject =
				_jsonFactory.createJSONObject(ddmFormInstanceReport.getData());

			DDMFormValues ddmFormValues =
				ddmFormInstanceRecordVersion.getDDMFormValues();

			for (DDMFormFieldValue ddmFormFieldValue :
					ddmFormValues.getDDMFormFieldValues()) {

				_processDDMFormFieldValue(
					ddmFormFieldValue, ddmFormInstanceRecordVersion,
					ddmFormInstanceReportDataJSONObject,
					ddmFormInstanceReportEvent);

				for (DDMFormFieldValue nestedDDMFormFieldValue :
						ddmFormFieldValue.getNestedDDMFormFieldValues()) {

					_processDDMFormFieldValue(
						nestedDDMFormFieldValue, ddmFormInstanceRecordVersion,
						ddmFormInstanceReportDataJSONObject,
						ddmFormInstanceReportEvent);
				}
			}

			int totalItems = ddmFormInstanceReportDataJSONObject.getInt(
				"totalItems");

			if (ddmFormInstanceReportEvent.equals(
					DDMFormInstanceReportConstants.EVENT_ADD_RECORD_VERSION)) {

				totalItems++;
			}
			else if (ddmFormInstanceReportEvent.equals(
						DDMFormInstanceReportConstants.
							EVENT_DELETE_RECORD_VERSION)) {

				totalItems--;
			}

			ddmFormInstanceReportDataJSONObject.put("totalItems", totalItems);

			return ddmFormInstanceReportDataJSONObject.toString();
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to process data for form instance record version " +
					formInstanceRecordVersionId,
				exception);
		}
	}

	private void _processDDMFormFieldValue(
			DDMFormFieldValue ddmFormFieldValue,
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion,
			JSONObject ddmFormInstanceReportDataJSONObject,
			String ddmFormInstanceReportEvent)
		throws Exception, JSONException {

		DDMFormFieldTypeReportProcessor ddmFormFieldTypeReportProcessor =
			_ddmFormFieldTypeReportProcessorRegistry.
				getDDMFormFieldTypeReportProcessor(ddmFormFieldValue.getType());

		if (ddmFormFieldTypeReportProcessor != null) {
			String fieldName = ddmFormFieldValue.getName();

			JSONObject fieldJSONObject =
				ddmFormInstanceReportDataJSONObject.getJSONObject(fieldName);

			if (fieldJSONObject == null) {
				fieldJSONObject = JSONUtil.put(
					"type", ddmFormFieldValue.getType()
				).put(
					"values", _jsonFactory.createJSONObject()
				);
			}

			JSONObject processedFieldJSONObject =
				ddmFormFieldTypeReportProcessor.process(
					ddmFormFieldValue,
					_jsonFactory.createJSONObject(fieldJSONObject.toString()),
					ddmFormInstanceRecordVersion.getFormInstanceRecordId(),
					ddmFormInstanceReportEvent);

			ddmFormInstanceReportDataJSONObject.put(
				fieldName, processedFieldJSONObject);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstanceReportLocalServiceImpl.class);

	@Reference
	private DDMFormFieldTypeReportProcessorRegistry
		_ddmFormFieldTypeReportProcessorRegistry;

	@Reference
	private DDMFormInstancePersistence _ddmFormInstancePersistence;

	@Reference
	private DDMFormInstanceRecordVersionPersistence
		_ddmFormInstanceRecordVersionPersistence;

	@Reference
	private JSONFactory _jsonFactory;

}
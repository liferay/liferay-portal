/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.report;

import com.liferay.dynamic.data.mapping.constants.DDMFormInstanceReportConstants;
import com.liferay.dynamic.data.mapping.internal.report.util.NumericDDMFormFieldTypeReportUtil;
import com.liferay.dynamic.data.mapping.internal.util.DDMFormInstanceRecordThreadLocal;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.report.DDMFormFieldTypeReportProcessor;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcos Martins
 */
@Component(
	property = "ddm.form.field.type.name=numeric",
	service = DDMFormFieldTypeReportProcessor.class
)
public class NumericDDMFormFieldTypeReportProcessor
	extends TextDDMFormFieldTypeReportProcessor {

	public NumericDDMFormFieldTypeReportProcessor() {
	}

	public NumericDDMFormFieldTypeReportProcessor(JSONFactory jsonFactory) {
		super(jsonFactory);
	}

	@Override
	public JSONObject process(
			DDMFormFieldValue ddmFormFieldValue, JSONObject fieldJSONObject,
			long formInstanceRecordId, String ddmFormInstanceReportEvent)
		throws Exception {

		JSONObject jsonObject = super.process(
			ddmFormFieldValue, fieldJSONObject, formInstanceRecordId,
			ddmFormInstanceReportEvent);

		BigDecimal valueBigDecimal =
			NumericDDMFormFieldTypeReportUtil.getValueBigDecimal(
				ddmFormFieldValue);

		if (valueBigDecimal == null) {
			return jsonObject;
		}

		JSONObject summaryJSONObject = jsonObject.getJSONObject("summary");

		if (summaryJSONObject == null) {
			summaryJSONObject = jsonFactory.createJSONObject();
		}

		BigDecimal sumBigDecimal = new BigDecimal(
			summaryJSONObject.getString("sum", "0"));

		if (ddmFormInstanceReportEvent.equals(
				DDMFormInstanceReportConstants.EVENT_ADD_RECORD_VERSION)) {

			BigDecimal maxValueBigDecimal = null;

			if (summaryJSONObject.has("max")) {
				maxValueBigDecimal = new BigDecimal(
					summaryJSONObject.getString("max"));
			}

			if ((maxValueBigDecimal == null) ||
				(valueBigDecimal.compareTo(maxValueBigDecimal) == 1)) {

				summaryJSONObject.put(
					"max", valueBigDecimal.toString()
				).put(
					"maxCount", 1
				);
			}
			else if (valueBigDecimal.compareTo(maxValueBigDecimal) == 0) {
				summaryJSONObject.put(
					"maxCount", summaryJSONObject.getInt("maxCount") + 1);
			}

			BigDecimal minValueBigDecimal = null;

			if (summaryJSONObject.has("min")) {
				minValueBigDecimal = new BigDecimal(
					summaryJSONObject.getString("min"));
			}

			if ((minValueBigDecimal == null) ||
				(valueBigDecimal.compareTo(minValueBigDecimal) == -1)) {

				summaryJSONObject.put(
					"min", valueBigDecimal.toString()
				).put(
					"minCount", 1
				);
			}
			else if (valueBigDecimal.compareTo(minValueBigDecimal) == 0) {
				summaryJSONObject.put(
					"minCount", summaryJSONObject.getInt("minCount") + 1);
			}

			sumBigDecimal = sumBigDecimal.add(valueBigDecimal);
		}
		else if (ddmFormInstanceReportEvent.equals(
					DDMFormInstanceReportConstants.
						EVENT_DELETE_RECORD_VERSION)) {

			boolean lastMaxValueDeleted = _isLastValue(
				"max", summaryJSONObject, valueBigDecimal);
			boolean lastMinValueDeleted = _isLastValue(
				"min", summaryJSONObject, valueBigDecimal);

			_decrementCount("max", summaryJSONObject, valueBigDecimal);
			_decrementCount("min", summaryJSONObject, valueBigDecimal);

			if ((lastMaxValueDeleted || lastMinValueDeleted) &&
				!DDMFormInstanceRecordThreadLocal.isDeleteInProcess()) {

				jsonObject.put("summary", summaryJSONObject);

				DDMFormInstanceRecord ddmFormInstanceRecord =
					ddmFormInstanceRecordLocalService.getFormInstanceRecord(
						formInstanceRecordId);

				DDMFormInstance ddmFormInstance =
					ddmFormInstanceRecord.getFormInstance();

				NumericDDMFormFieldTypeReportUtil.updateMaxAndMin(
					ddmFormFieldValue.getName(),
					NumericDDMFormFieldTypeReportUtil.
						getLatestApprovedDDMFormValuesList(
							TransformUtil.transform(
								ddmFormInstance.getFormInstanceRecords(),
								currentDDMFormInstanceRecord -> {
									if (formInstanceRecordId ==
											currentDDMFormInstanceRecord.
												getFormInstanceRecordId()) {

										return null;
									}

									return currentDDMFormInstanceRecord;
								})),
					jsonObject);

				if (!jsonObject.has("summary")) {
					return jsonObject;
				}
			}

			sumBigDecimal = sumBigDecimal.subtract(valueBigDecimal);
		}

		BigDecimal averageBigDecimal = new BigDecimal("0.0");

		if (jsonObject.getInt("totalEntries") > 0) {
			averageBigDecimal = sumBigDecimal.divide(
				new BigDecimal(jsonObject.getInt("totalEntries")), 10,
				RoundingMode.HALF_UP);
		}

		summaryJSONObject.put(
			"average", formatBigDecimal(averageBigDecimal)
		).put(
			"sum", sumBigDecimal.toString()
		);

		jsonObject.put("summary", summaryJSONObject);

		return jsonObject;
	}

	protected String formatBigDecimal(BigDecimal bigDecimal) {
		StringBundler sb = new StringBundler(3);

		String bigDecimalString = bigDecimal.toString();

		String[] bigDecimalStringParts = bigDecimalString.split("\\.");

		sb.append(bigDecimalStringParts[0]);

		if (bigDecimalStringParts.length > 1) {
			String decimalPart = bigDecimalStringParts[1];

			int decimalPartIndex = decimalPart.length() - 1;

			while ((decimalPartIndex > 0) &&
				   (decimalPart.charAt(decimalPartIndex) == '0')) {

				decimalPartIndex--;
			}

			if (decimalPartIndex > 0) {
				decimalPart = decimalPart.substring(0, decimalPartIndex + 1);

				sb.append(StringPool.PERIOD);
				sb.append(decimalPart);
			}
		}

		return sb.toString();
	}

	private void _decrementCount(
		String key, JSONObject summaryJSONObject, BigDecimal valueBigDecimal) {

		if (!summaryJSONObject.has(key)) {
			return;
		}

		BigDecimal bigDecimal = new BigDecimal(
			summaryJSONObject.getString(key));

		if (valueBigDecimal.compareTo(bigDecimal) != 0) {
			return;
		}

		int count = summaryJSONObject.getInt(key + "Count");

		if (count > 1) {
			summaryJSONObject.put(key + "Count", count - 1);
		}
	}

	private boolean _isLastValue(
		String key, JSONObject summaryJSONObject, BigDecimal valueBigDecimal) {

		if (!summaryJSONObject.has(key)) {
			return true;
		}

		BigDecimal bigDecimal = new BigDecimal(
			summaryJSONObject.getString(key));

		if ((valueBigDecimal.compareTo(bigDecimal) == 0) &&
			(summaryJSONObject.getInt(key + "Count") <= 1)) {

			return true;
		}

		return false;
	}

}
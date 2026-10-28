/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.report.util;

import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceRecordVersionException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.math.BigDecimal;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Akhash Ramprakash
 */
public class NumericDDMFormFieldTypeReportUtil {

	public static List<DDMFormValues> getLatestApprovedDDMFormValuesList(
		List<DDMFormInstanceRecord> ddmFormInstanceRecords) {

		return TransformUtil.transform(
			ddmFormInstanceRecords,
			ddmFormInstanceRecord -> {
				try {
					DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
						DDMFormInstanceRecordVersionLocalServiceUtil.
							getLatestFormInstanceRecordVersion(
								ddmFormInstanceRecord.getFormInstanceRecordId(),
								WorkflowConstants.STATUS_APPROVED);

					return ddmFormInstanceRecordVersion.getDDMFormValues();
				}
				catch (NoSuchFormInstanceRecordVersionException
							noSuchFormInstanceRecordVersionException) {

					if (_log.isDebugEnabled()) {
						_log.debug(noSuchFormInstanceRecordVersionException);
					}
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(portalException);
					}
				}

				return null;
			});
	}

	public static BigDecimal getValueBigDecimal(
		DDMFormFieldValue ddmFormFieldValue) {

		try {
			Value value = ddmFormFieldValue.getValue();

			Locale defaultLocale = value.getDefaultLocale();

			String valueString = value.getString(defaultLocale);

			if (Validator.isNull(valueString)) {
				return null;
			}

			DecimalFormat decimalFormat =
				(DecimalFormat)NumberFormat.getNumberInstance(defaultLocale);

			decimalFormat.setParseBigDecimal(true);

			return new BigDecimal(
				String.valueOf(decimalFormat.parse(valueString)));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	public static void updateMaxAndMin(
		String ddmFormFieldValueName, List<DDMFormValues> ddmFormValuesList,
		JSONObject fieldJSONObject) {

		List<BigDecimal> valueBigDecimals = _getValueBigDecimals(
			ddmFormFieldValueName, ddmFormValuesList);

		if (valueBigDecimals.isEmpty()) {
			fieldJSONObject.remove("summary");

			return;
		}

		JSONObject summaryJSONObject = fieldJSONObject.getJSONObject("summary");

		BigDecimal maxValueBigDecimal = Collections.max(
			valueBigDecimals, Comparator.comparingDouble(Number::doubleValue));
		BigDecimal minValueBigDecimal = Collections.min(
			valueBigDecimals, Comparator.comparingDouble(Number::doubleValue));

		summaryJSONObject.put(
			"max", maxValueBigDecimal.toString()
		).put(
			"maxCount",
			ListUtil.count(
				valueBigDecimals,
				valueBigDecimal ->
					valueBigDecimal.compareTo(maxValueBigDecimal) == 0)
		).put(
			"min", minValueBigDecimal.toString()
		).put(
			"minCount",
			ListUtil.count(
				valueBigDecimals,
				valueBigDecimal ->
					valueBigDecimal.compareTo(minValueBigDecimal) == 0)
		);
	}

	private static List<BigDecimal> _getValueBigDecimals(
		String ddmFormFieldValueName, List<DDMFormValues> ddmFormValuesList) {

		return TransformUtil.transform(
			ddmFormValuesList,
			ddmFormValues -> {
				Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
					ddmFormValues.getDDMFormFieldValuesMap(true);

				List<DDMFormFieldValue> ddmFormFieldValues =
					ddmFormFieldValuesMap.get(ddmFormFieldValueName);

				return getValueBigDecimal(ddmFormFieldValues.get(0));
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NumericDDMFormFieldTypeReportUtil.class);

}
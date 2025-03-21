/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util;

import com.liferay.dynamic.data.mapping.exception.FormInstanceExpiredException;
import com.liferay.dynamic.data.mapping.exception.FormInstanceSubmissionLimitException;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorFieldContextKey;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormInstanceExpirationStatusUtil;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormInstanceSubmissionLimitStatusUtil;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Leonardo Barros
 */
public class AddFormInstanceRecordMVCCommandUtil {

	public static void updateNonevaluableDDMFormFields(
			Map<String, DDMFormField> ddmFormFieldsMap,
			Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
				ddmFormFieldsPropertyChanges,
			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
			DDMFormLayout ddmFormLayout, Set<Integer> disabledPagesIndexes)
		throws Exception {

		Set<String> nonevaluableFieldNames = new HashSet<>();

		for (Map.Entry<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
				entry : ddmFormFieldsPropertyChanges.entrySet()) {

			if (!MapUtil.getBoolean(entry.getValue(), "readOnly") &&
				MapUtil.getBoolean(entry.getValue(), "visible", true)) {

				continue;
			}

			DDMFormEvaluatorFieldContextKey ddmFormEvaluatorFieldContextKey =
				entry.getKey();

			nonevaluableFieldNames.add(
				ddmFormEvaluatorFieldContextKey.getName());
		}

		for (Integer disabledPagesIndex : disabledPagesIndexes) {
			DDMFormLayoutPage ddmFormLayoutPage =
				ddmFormLayout.getDDMFormLayoutPage(disabledPagesIndex);

			for (DDMFormLayoutRow ddmFormLayoutRow :
					ddmFormLayoutPage.getDDMFormLayoutRows()) {

				for (DDMFormLayoutColumn ddmFormLayoutColumn :
						ddmFormLayoutRow.getDDMFormLayoutColumns()) {

					nonevaluableFieldNames.addAll(
						ddmFormLayoutColumn.getDDMFormFieldNames());
				}
			}
		}

		for (String nonevaluableFieldName : nonevaluableFieldNames) {
			DDMFormField ddmFormField = ddmFormFieldsMap.get(
				nonevaluableFieldName);

			if (ddmFormField == null) {
				continue;
			}

			ddmFormField.setDDMFormFieldValidation(null);
			ddmFormField.setRequired(false);

			for (DDMFormFieldValue ddmFormFieldValue :
					ddmFormFieldValuesMap.get(ddmFormField.getName())) {

				Value value = ddmFormFieldValue.getValue();

				if (value == null) {
					continue;
				}

				if (ddmFormField.isLocalizable()) {
					LocalizedValue localizedValue = new LocalizedValue(
						value.getDefaultLocale());

					for (Locale availableLocale : value.getAvailableLocales()) {
						localizedValue.addString(
							availableLocale, StringPool.BLANK);
					}

					ddmFormFieldValue.setValue(localizedValue);
				}
				else {
					ddmFormFieldValue.setValue(
						new UnlocalizedValue(StringPool.BLANK));
				}
			}
		}
	}

	public static void updateReadOnlyDDMFormFields(
			Map<String, DDMFormField> ddmFormFieldsMap,
			Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
				ddmFormFieldsPropertyChanges)
		throws Exception {

		for (Map.Entry<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
				entry : ddmFormFieldsPropertyChanges.entrySet()) {

			if (!MapUtil.getBoolean(entry.getValue(), "readOnly")) {
				continue;
			}

			DDMFormEvaluatorFieldContextKey ddmFormEvaluatorFieldContextKey =
				entry.getKey();

			DDMFormField ddmFormField = ddmFormFieldsMap.get(
				ddmFormEvaluatorFieldContextKey.getName());

			ddmFormField.setProperty("persistReadOnlyValue", true);
		}
	}

	public static void validateExpirationStatus(
			DDMFormInstance ddmFormInstance, PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (DDMFormInstanceExpirationStatusUtil.isFormExpired(
				ddmFormInstance, themeDisplay.getTimeZone())) {

			throw new FormInstanceExpiredException(
				"Form instance " + ddmFormInstance.getFormInstanceId() +
					" is expired");
		}
	}

	public static void validateSubmissionLimitStatus(
			DDMFormInstance ddmFormInstance,
			DDMFormInstanceRecordVersionLocalService
				ddmFormInstanceRecordVersionLocalService,
			PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (DDMFormInstanceSubmissionLimitStatusUtil.isSubmissionLimitReached(
				ddmFormInstance, ddmFormInstanceRecordVersionLocalService,
				themeDisplay.getUser())) {

			throw new FormInstanceSubmissionLimitException(
				StringBundler.concat(
					"User ", themeDisplay.getUserId(),
					" has already submitted an entry in form instance ",
					ddmFormInstance.getFormInstanceId()));
		}
	}

}
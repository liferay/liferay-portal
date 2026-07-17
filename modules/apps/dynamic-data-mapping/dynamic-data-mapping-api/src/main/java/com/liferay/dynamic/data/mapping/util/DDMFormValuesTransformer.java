/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.journal.article.dynamic.data.mapping.form.field.type.constants.JournalArticleDDMFormFieldTypeConstants;
import com.liferay.layout.dynamic.data.mapping.form.field.type.constants.LayoutDDMFormFieldTypeConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marcellus Tavares
 */
public class DDMFormValuesTransformer {

	public DDMFormValuesTransformer(DDMFormValues ddmFormValues) {
		_ddmFormValues = ddmFormValues;
	}

	public void addTransformer(
		DDMFormFieldValueTransformer ddmFormFieldValueTransformer) {

		_ddmFormFieldValueTransformersMap.put(
			ddmFormFieldValueTransformer.getFieldType(),
			ddmFormFieldValueTransformer);
	}

	public void transform() throws PortalException {
		DDMForm ddmForm = _ddmFormValues.getDDMForm();

		traverse(
			ddmForm.getDDMFormFields(),
			_ddmFormValues.getDDMFormFieldValuesMap());
	}

	protected void performTransformation(
			List<DDMFormFieldValue> ddmFormFieldValues,
			DDMFormFieldValueTransformer ddmFormFieldValueTransformer)
		throws PortalException {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			ddmFormFieldValueTransformer.transform(ddmFormFieldValue);
		}
	}

	protected void traverse(
			List<DDMFormField> ddmFormFields,
			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap)
		throws PortalException {

		for (DDMFormField ddmFormField : ddmFormFields) {
			List<DDMFormFieldValue> ddmFormFieldValues =
				ddmFormFieldValuesMap.get(ddmFormField.getName());

			if (ddmFormFieldValues == null) {
				continue;
			}

			String type = ddmFormField.getType();

			if (StringUtil.equals(type, DDMFormFieldType.DOCUMENT_LIBRARY)) {
				type = DDMFormFieldTypeConstants.DOCUMENT_LIBRARY;
			}
			else if (StringUtil.equals(
						type, DDMFormFieldType.JOURNAL_ARTICLE)) {

				type = JournalArticleDDMFormFieldTypeConstants.JOURNAL_ARTICLE;
			}
			else if (StringUtil.equals(type, DDMFormFieldType.LINK_TO_PAGE)) {
				type = LayoutDDMFormFieldTypeConstants.LINK_TO_LAYOUT;
			}
			else if (StringUtil.equals(type, DDMFormFieldType.TEXT_HTML)) {
				type = DDMFormFieldTypeConstants.RICH_TEXT;
			}

			DDMFormFieldValueTransformer ddmFormFieldValueTransformer =
				_ddmFormFieldValueTransformersMap.get(type);

			if (ddmFormFieldValueTransformer != null) {
				performTransformation(
					ddmFormFieldValues, ddmFormFieldValueTransformer);
			}

			for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
				traverse(
					ddmFormField.getNestedDDMFormFields(),
					ddmFormFieldValue.getNestedDDMFormFieldValuesMap());
			}
		}
	}

	private final Map<String, DDMFormFieldValueTransformer>
		_ddmFormFieldValueTransformersMap = new HashMap<>();
	private final DDMFormValues _ddmFormValues;

}
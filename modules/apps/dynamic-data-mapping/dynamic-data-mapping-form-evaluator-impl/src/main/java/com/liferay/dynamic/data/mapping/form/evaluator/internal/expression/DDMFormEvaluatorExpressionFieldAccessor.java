/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.evaluator.internal.expression;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFieldAccessor;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyRequest;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyResponse;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorFieldContextKey;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.helper.DDMFormEvaluatorFormValuesHelper;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.field.type.DefaultDDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Rafael Praxedes
 */
public class DDMFormEvaluatorExpressionFieldAccessor
	implements DDMExpressionFieldAccessor {

	public DDMFormEvaluatorExpressionFieldAccessor(
		DDMFormEvaluatorFormValuesHelper ddmFormEvaluatorFormValuesHelper,
		Map<String, DDMFormField> ddmFormFieldsMap,
		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges,
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
		Locale locale) {

		_ddmFormEvaluatorFormValuesHelper = ddmFormEvaluatorFormValuesHelper;
		_ddmFormFieldsMap = ddmFormFieldsMap;
		_ddmFormFieldsPropertyChanges = ddmFormFieldsPropertyChanges;
		_ddmFormFieldTypeServicesRegistry = ddmFormFieldTypeServicesRegistry;
		_locale = locale;
	}

	@Override
	public GetFieldPropertyResponse getFieldProperty(
		GetFieldPropertyRequest getFieldPropertyRequest) {

		Object fieldProperty = null;

		if (Validator.isNull(getFieldPropertyRequest.getInstanceId())) {
			fieldProperty = _getFieldPropertyByFieldName(
				getFieldPropertyRequest.getField(),
				getFieldPropertyRequest.getProperty());
		}
		else {
			fieldProperty = _getFieldPropertyByDDMFormFieldContextKey(
				new DDMFormEvaluatorFieldContextKey(
					getFieldPropertyRequest.getField(),
					getFieldPropertyRequest.getInstanceId()),
				getFieldPropertyRequest.getProperty());
		}

		GetFieldPropertyResponse.Builder builder =
			GetFieldPropertyResponse.Builder.newBuilder(fieldProperty);

		return builder.build();
	}

	public Object getFieldPropertyChanged(
		DDMFormEvaluatorFieldContextKey ddmFormEvaluatorFieldContextKey,
		String property) {

		Map<String, Object> ddmFormFieldProperties =
			_ddmFormFieldsPropertyChanges.get(ddmFormEvaluatorFieldContextKey);

		if ((ddmFormFieldProperties != null) &&
			ddmFormFieldProperties.containsKey(property)) {

			return ddmFormFieldProperties.get(property);
		}

		return null;
	}

	public Object getFieldValue(
		DDMFormEvaluatorFieldContextKey ddmFormEvaluatorFieldContextKey) {

		Object value = getFieldPropertyChanged(
			ddmFormEvaluatorFieldContextKey, "value");

		if (value != null) {
			return value;
		}

		DDMFormFieldValue ddmFormFieldValue =
			_ddmFormEvaluatorFormValuesHelper.getDDMFormFieldValue(
				ddmFormEvaluatorFieldContextKey);

		Value ddmFormFieldValueValue = ddmFormFieldValue.getValue();

		DDMFormFieldValueAccessor<?> ddmFormFieldValueAccessor =
			_getDDMFormFieldValueAccessor(
				ddmFormEvaluatorFieldContextKey.getName());

		Locale locale = _locale;

		if (_locale == null) {
			locale = ddmFormFieldValueValue.getDefaultLocale();
		}

		return ddmFormFieldValueAccessor.getValueForEvaluation(
			ddmFormFieldValue, locale);
	}

	@Override
	public boolean isField(String parameter) {
		return _ddmFormFieldsMap.containsKey(parameter);
	}

	protected Object getFieldProperty(String fieldName, String property) {
		Object value = getFieldPropertyChanged(fieldName, property);

		if (value == null) {
			DDMFormField ddmFormField = _ddmFormFieldsMap.get(fieldName);

			if (ddmFormField != null) {
				value = ddmFormField.getProperty(property);
			}
		}

		return value;
	}

	protected Object getFieldPropertyChanged(
		String fieldName, String property) {

		Set<DDMFormEvaluatorFieldContextKey> ddmFormEvaluatorFieldContextKeys =
			_ddmFormEvaluatorFormValuesHelper.getDDMFormFieldContextKeys(
				fieldName);

		if (SetUtil.isEmpty(ddmFormEvaluatorFieldContextKeys)) {
			return null;
		}

		Iterator<DDMFormEvaluatorFieldContextKey> iterator =
			ddmFormEvaluatorFieldContextKeys.iterator();

		return getFieldPropertyChanged(iterator.next(), property);
	}

	private DDMFormFieldValueAccessor<?> _getDDMFormFieldValueAccessor(
		String fieldName) {

		DDMFormField ddmFormField = _ddmFormFieldsMap.get(fieldName);

		if (ddmFormField == null) {
			return _defaultDDMFormFieldValueAccessor;
		}

		DDMFormFieldValueAccessor<?> ddmFormFieldValueAccessor =
			_ddmFormFieldTypeServicesRegistry.getDDMFormFieldValueAccessor(
				ddmFormField.getType());

		if (ddmFormFieldValueAccessor != null) {
			return ddmFormFieldValueAccessor;
		}

		return _defaultDDMFormFieldValueAccessor;
	}

	private Object _getFieldLocalizedValue(
		DDMFormEvaluatorFieldContextKey ddmFormEvaluatorFieldContextKey) {

		Object localizedValue = getFieldPropertyChanged(
			ddmFormEvaluatorFieldContextKey, "localizedValue");

		if (localizedValue != null) {
			return localizedValue;
		}

		DDMFormFieldValue ddmFormFieldValue =
			_ddmFormEvaluatorFormValuesHelper.getDDMFormFieldValue(
				ddmFormEvaluatorFieldContextKey);

		return ddmFormFieldValue.getValue();
	}

	private Object _getFieldLocalizedValue(String fieldName) {
		Set<DDMFormEvaluatorFieldContextKey> ddmFormEvaluatorFieldContextKeys =
			_ddmFormEvaluatorFormValuesHelper.getDDMFormFieldContextKeys(
				fieldName);

		if (SetUtil.isEmpty(ddmFormEvaluatorFieldContextKeys)) {
			return null;
		}

		Iterator<DDMFormEvaluatorFieldContextKey> iterator =
			ddmFormEvaluatorFieldContextKeys.iterator();

		return _getFieldLocalizedValue(iterator.next());
	}

	private Object _getFieldPropertyByDDMFormFieldContextKey(
		DDMFormEvaluatorFieldContextKey ddmFormEvaluatorFieldContextKey,
		String property) {

		if (property.equals("localizedValue")) {
			return _getFieldLocalizedValue(ddmFormEvaluatorFieldContextKey);
		}
		else if (property.equals("value")) {
			return getFieldValue(ddmFormEvaluatorFieldContextKey);
		}

		return getFieldProperty(
			ddmFormEvaluatorFieldContextKey.getName(), property);
	}

	private Object _getFieldPropertyByFieldName(
		String fieldName, String property) {

		if (property.equals("localizedValue")) {
			return _getFieldLocalizedValue(fieldName);
		}
		else if (property.equals("value")) {
			return _getFieldValues(fieldName);
		}

		return getFieldProperty(fieldName, property);
	}

	private Object _getFieldValues(String fieldName) {
		List<Object> list = TransformUtil.transform(
			_ddmFormEvaluatorFormValuesHelper.getDDMFormFieldContextKeys(
				fieldName),
			ddmFormEvaluatorFieldContextKey -> getFieldValue(
				ddmFormEvaluatorFieldContextKey));

		DDMFormFieldValueAccessor<?> ddmFormFieldValueAccessor =
			_getDDMFormFieldValueAccessor(fieldName);

		Object[] values = list.toArray(
			ddmFormFieldValueAccessor.getArrayGenericType());

		if (ArrayUtil.isNotEmpty(values) && (values.length == 1)) {
			return values[0];
		}

		return values;
	}

	private final DDMFormEvaluatorFormValuesHelper
		_ddmFormEvaluatorFormValuesHelper;
	private final DDMFormFieldTypeServicesRegistry
		_ddmFormFieldTypeServicesRegistry;
	private final Map<String, DDMFormField> _ddmFormFieldsMap;
	private final Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
		_ddmFormFieldsPropertyChanges;
	private final DDMFormFieldValueAccessor<String>
		_defaultDDMFormFieldValueAccessor =
			new DefaultDDMFormFieldValueAccessor();
	private final Locale _locale;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderInvoker;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFieldAccessor;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFieldAccessorAware;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionObserver;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionObserverAware;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyRequest;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyResponse;
import com.liferay.dynamic.data.mapping.expression.UpdateFieldPropertyRequest;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Leonardo Barros
 */
public class CallFunction
	implements DDMExpressionFieldAccessorAware,
			   DDMExpressionFunction.Function3<String, String, String, Boolean>,
			   DDMExpressionObserverAware {

	public static final String NAME = "call";

	public CallFunction(
		DDMDataProviderInvoker ddmDataProviderInvoker,
		JSONFactory jsonFactory) {

		this.ddmDataProviderInvoker = ddmDataProviderInvoker;
		this.jsonFactory = jsonFactory;
	}

	@Override
	public Boolean apply(
		String ddmDataProviderInstanceUUID, String paramsExpression,
		String resultMapExpression) {

		if (_ddmExpressionFieldAccessor == null) {
			return false;
		}

		try {
			DDMDataProviderRequest.Builder builder =
				DDMDataProviderRequest.Builder.newBuilder();

			builder = builder.withCompanyId(
				CompanyThreadLocal.getCompanyId()
			).withDDMDataProviderId(
				ddmDataProviderInstanceUUID
			);

			Map<String, String> parameterMap = _extractParameters(
				paramsExpression);

			for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
				if (Objects.equals(entry.getKey(), "locale")) {
					builder = builder.withLocale(
						LocaleUtil.fromLanguageId(entry.getValue()));

					continue;
				}

				builder = builder.withParameter(
					entry.getKey(), entry.getValue());
			}

			DDMDataProviderRequest ddmDataProviderRequest = builder.build();

			DDMDataProviderResponse ddmDataProviderResponse =
				ddmDataProviderInvoker.invoke(ddmDataProviderRequest);

			Map<String, String> resultMap = _extractResults(
				resultMapExpression);

			setDDMFormFieldValues(ddmDataProviderResponse, resultMap);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return true;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setDDMExpressionFieldAccessor(
		DDMExpressionFieldAccessor ddmExpressionFieldAccessor) {

		_ddmExpressionFieldAccessor = ddmExpressionFieldAccessor;
	}

	@Override
	public void setDDMExpressionObserver(
		DDMExpressionObserver ddmExpressionObserver) {

		_ddmExpressionObserver = ddmExpressionObserver;
	}

	protected String getDDMFormFieldValue(String ddmFormFieldName) {
		GetFieldPropertyRequest.Builder builder =
			GetFieldPropertyRequest.Builder.newBuilder(
				ddmFormFieldName, "value");

		GetFieldPropertyResponse getFieldPropertyResponse =
			_ddmExpressionFieldAccessor.getFieldProperty(builder.build());

		Object value = getFieldPropertyResponse.getValue();

		if (Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			Object[] valueArray = (Object[])value;

			if (ArrayUtil.isNotEmpty(valueArray)) {
				value = ((Object[])value)[0];
			}
		}

		try {
			JSONArray jsonArray = jsonFactory.createJSONArray(
				String.valueOf(value));

			return jsonArray.join(
				StringPool.COMMA_AND_SPACE
			).replaceAll(
				StringPool.QUOTE, StringPool.BLANK
			);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return String.valueOf(value);
		}
	}

	protected void setDDMFormFieldOptions(
		String field, List<KeyValuePair> options) {

		UpdateFieldPropertyRequest.Builder builder =
			UpdateFieldPropertyRequest.Builder.newBuilder(
				field, "options", options);

		_ddmExpressionObserver.updateFieldProperty(builder.build());
	}

	protected void setDDMFormFieldValues(
		DDMDataProviderResponse ddmDataProviderResponse,
		Map<String, String> resultMap) {

		for (Map.Entry<String, String> entry : resultMap.entrySet()) {
			String outputName = entry.getValue();
			String ddmFormFieldName = entry.getKey();

			if (!ddmDataProviderResponse.hasOutput(outputName)) {
				setDDMFormFieldOptions(
					ddmFormFieldName, Collections.emptyList());

				continue;
			}

			List<KeyValuePair> keyValuePairs =
				ddmDataProviderResponse.getOutput(outputName, List.class);

			if (keyValuePairs != null) {
				setDDMFormFieldOptions(ddmFormFieldName, keyValuePairs);
			}
			else {
				Object output = ddmDataProviderResponse.getOutput(
					outputName, String.class);

				if (output == null) {
					output = ddmDataProviderResponse.getOutput(
						outputName, Number.class);

					if (output != null) {
						output = new BigDecimal(output.toString());
					}
				}

				if (Validator.isNotNull(output)) {
					_setDDMFormFieldValue(ddmFormFieldName, output);
				}
			}
		}
	}

	protected DDMDataProviderInvoker ddmDataProviderInvoker;
	protected JSONFactory jsonFactory;

	private void _extractDDMFormFieldValue(
		String expression, Map<String, String> parameters) {

		String[] tokens = StringUtil.split(expression, CharPool.EQUAL);

		String parameterName = tokens[0];

		String parameterValue = StringPool.BLANK;

		if (tokens.length == 2) {
			parameterValue = tokens[1];
		}

		if (_ddmExpressionFieldAccessor.isField(parameterValue)) {
			parameterValue = getDDMFormFieldValue(parameterValue);
		}

		parameters.put(parameterName, parameterValue);
	}

	private Map<String, String> _extractParameters(String expression) {
		if (Validator.isNull(expression)) {
			return Collections.emptyMap();
		}

		Map<String, String> parameters = new HashMap<>();

		String[] innerExpressions = StringUtil.split(
			expression, CharPool.SEMICOLON);

		if (innerExpressions.length == 0) {
			_extractDDMFormFieldValue(expression, parameters);
		}
		else {
			for (String innerExpression : innerExpressions) {
				_extractDDMFormFieldValue(innerExpression, parameters);
			}
		}

		return parameters;
	}

	private Map<String, String> _extractResults(String resultMapExpression) {
		if (Validator.isNull(resultMapExpression)) {
			return Collections.emptyMap();
		}

		Map<String, String> results = new HashMap<>();

		String[] innerExpressions = StringUtil.split(
			resultMapExpression, CharPool.SEMICOLON);

		for (String innerExpression : innerExpressions) {
			String[] tokens = StringUtil.split(innerExpression, CharPool.EQUAL);

			results.put(tokens[0], tokens[1]);
		}

		return results;
	}

	private void _setDDMFormFieldValue(String field, Object value) {
		UpdateFieldPropertyRequest.Builder builder =
			UpdateFieldPropertyRequest.Builder.newBuilder(
				field, "value", value);

		_ddmExpressionObserver.updateFieldProperty(builder.build());
	}

	private static final Log _log = LogFactoryUtil.getLog(CallFunction.class);

	private DDMExpressionFieldAccessor _ddmExpressionFieldAccessor;
	private DDMExpressionObserver _ddmExpressionObserver;

}
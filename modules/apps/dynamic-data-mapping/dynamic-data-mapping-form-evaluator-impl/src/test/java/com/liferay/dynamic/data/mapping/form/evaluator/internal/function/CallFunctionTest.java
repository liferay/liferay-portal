/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyRequest;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyResponse;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorFieldContextKey;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.expression.DDMFormEvaluatorExpressionFieldAccessor;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.expression.DDMFormEvaluatorExpressionObserver;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.helper.DDMFormEvaluatorFormValuesHelper;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Leonardo Barros
 */
public class CallFunctionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_callFunction = new CallFunction(null, _jsonFactory);
	}

	@Test
	public void testGetFieldValueFromJSONArray() {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		jsonArray.put("value_1, value_2");

		_mockDDMExpressionFieldAccessor(jsonArray);

		Assert.assertEquals(
			"value_1, value_2", _callFunction.getDDMFormFieldValue("field0"));

		jsonArray.put("value_3");

		Assert.assertEquals(
			"value_1, value_2, value_3",
			_callFunction.getDDMFormFieldValue("field0"));
	}

	@Test
	public void testGetFieldValueFromString() {
		_mockDDMExpressionFieldAccessor("test");

		Assert.assertEquals(
			"test", _callFunction.getDDMFormFieldValue("field0"));
	}

	@Test
	public void testNotAutoSelectOption() {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("field0");

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"1", "field0", new UnlocalizedValue("a")));

		MockDDMExpressionObserver mockDDMExpressionObserver =
			_mockDDMExpressionObserver(ddmFormValues);

		List<KeyValuePair> keyValuePairs = new ArrayList<>();

		keyValuePairs.add(new KeyValuePair("key_1", "value_1"));
		keyValuePairs.add(new KeyValuePair("key_2", "value_2"));

		_callFunction.setDDMFormFieldOptions("field0", keyValuePairs);

		Assert.assertNull(
			mockDDMExpressionObserver.getFieldPropertyValue(
				"field0", "1", "value"));
	}

	@Test
	public void testSetDDMFormFieldOptionsRepeatableFields() {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("field0");

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"1", "field0", new UnlocalizedValue("a")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"2", "field0", new UnlocalizedValue("b")));

		MockDDMExpressionObserver mockDDMExpressionObserver =
			_mockDDMExpressionObserver(ddmFormValues);

		List<KeyValuePair> keyValuePairs = new ArrayList<>();

		keyValuePairs.add(new KeyValuePair("key_1", "value_1"));
		keyValuePairs.add(new KeyValuePair("key_2", "value_2"));

		_callFunction.setDDMFormFieldOptions("field0", keyValuePairs);

		Assert.assertEquals(
			keyValuePairs,
			mockDDMExpressionObserver.getFieldPropertyValue(
				"field0", "1", "options"));
		Assert.assertEquals(
			keyValuePairs,
			mockDDMExpressionObserver.getFieldPropertyValue(
				"field0", "2", "options"));
	}

	@Test
	public void testSetDDMFormFieldValuesWithNumberOutput() {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("fieldName0");

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"1", "fieldName0", new UnlocalizedValue("10")));

		MockDDMExpressionObserver mockDDMExpressionObserver =
			_mockDDMExpressionObserver(ddmFormValues);

		DDMDataProviderResponse.Builder builder =
			DDMDataProviderResponse.Builder.newBuilder();

		builder.withOutput("numberOutputId", 10);

		_callFunction.setDDMFormFieldValues(
			builder.build(),
			HashMapBuilder.put(
				"fieldName0", "numberOutputId"
			).build());

		Assert.assertEquals(
			new BigDecimal(10),
			mockDDMExpressionObserver.getFieldPropertyValue(
				"fieldName0", "1", "value"));
	}

	public static class MockDDMExpressionObserver
		extends DDMFormEvaluatorExpressionObserver {

		public MockDDMExpressionObserver(DDMFormValues ddmFormValues) {
			super(
				new DDMFormEvaluatorFormValuesHelper(ddmFormValues),
				_createFieldsPropertiesMap());
		}

		public Object getFieldPropertyValue(
			String fieldName, String instanceId, String property) {

			Map<String, Object> fieldProperties =
				_fieldsPropertiesMap.getOrDefault(
					new DDMFormEvaluatorFieldContextKey(fieldName, instanceId),
					Collections.emptyMap());

			return fieldProperties.get(property);
		}

		private static Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			_createFieldsPropertiesMap() {

			_fieldsPropertiesMap = new HashMap<>();

			return _fieldsPropertiesMap;
		}

		private static Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			_fieldsPropertiesMap;

	}

	private void _mockDDMExpressionFieldAccessor(Object... values) {
		DDMFormEvaluatorExpressionFieldAccessor
			ddmFormEvaluatorExpressionFieldAccessor = Mockito.mock(
				DDMFormEvaluatorExpressionFieldAccessor.class);

		Mockito.when(
			ddmFormEvaluatorExpressionFieldAccessor.getFieldProperty(
				Mockito.any(GetFieldPropertyRequest.class))
		).then(
			(Answer<GetFieldPropertyResponse>)invocation -> {
				GetFieldPropertyResponse.Builder builder =
					GetFieldPropertyResponse.Builder.newBuilder(values);

				return builder.build();
			}
		);

		_callFunction.setDDMExpressionFieldAccessor(
			ddmFormEvaluatorExpressionFieldAccessor);
	}

	private MockDDMExpressionObserver _mockDDMExpressionObserver(
		DDMFormValues ddmFormValues) {

		MockDDMExpressionObserver mockDDMExpressionObserver =
			new MockDDMExpressionObserver(ddmFormValues);

		_callFunction.setDDMExpressionObserver(mockDDMExpressionObserver);

		return mockDDMExpressionObserver;
	}

	private CallFunction _callFunction;
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

}
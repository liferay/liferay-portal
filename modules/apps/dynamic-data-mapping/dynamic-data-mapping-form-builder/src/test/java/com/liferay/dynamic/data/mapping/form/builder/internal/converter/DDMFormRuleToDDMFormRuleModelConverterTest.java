/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.converter;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionRegistry;
import com.liferay.dynamic.data.mapping.expression.internal.DDMExpressionFactoryImpl;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.spi.converter.model.SPIDDMFormRule;
import com.liferay.dynamic.data.mapping.spi.converter.serializer.SPIDDMFormRuleSerializerContext;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Marcellus Tavares
 */
public class DDMFormRuleToDDMFormRuleModelConverterTest
	extends BaseDDMConverterTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpDDMExpressionFactory();
		_setUpDDMFormRuleConverter();
		_setUpDDMFormRuleDeserializer();
	}

	@Test
	public void testAndOrCondition1() throws Exception {
		_assertConversionToModel(
			"ddm-form-rules-and-or-condition.json",
			"ddm-form-rules-model-and-or-condition.json");
	}

	@Test
	public void testAndOrCondition2() throws Exception {
		_assertConversionToConvertModel(
			"ddm-form-rules-model-and-or-condition.json",
			"ddm-form-rules-and-or-condition.json");
	}

	@Test
	public void testAutoFillActions1() throws Exception {
		JSONArray expectedDDMFormRulesJSONArray = jsonFactory.createJSONArray(
			read("ddm-form-rules-model-auto-fill-actions.json"));

		List<DDMFormRule> actualDDMFormRules = convert(
			"ddm-form-rules-auto-fill-actions.json");

		JSONArray actualDDMFormRulesJSONArray = jsonFactory.createJSONArray(
			serialize(actualDDMFormRules));

		Assert.assertEquals(
			expectedDDMFormRulesJSONArray.length(),
			actualDDMFormRulesJSONArray.length());

		JSONObject expectedAutoFillDDMRuleJSONObject =
			expectedDDMFormRulesJSONArray.getJSONObject(0);

		JSONObject actualAutoFillDDMRuleJSONObject =
			actualDDMFormRulesJSONArray.getJSONObject(0);

		Assert.assertEquals(
			expectedAutoFillDDMRuleJSONObject.get("condition"),
			actualAutoFillDDMRuleJSONObject.get("condition"));

		JSONArray expectedActionDDMRuleJSONArray =
			expectedAutoFillDDMRuleJSONObject.getJSONArray("actions");

		JSONArray actualActionDDMRuleJSONArray =
			actualAutoFillDDMRuleJSONObject.getJSONArray("actions");

		Assert.assertEquals(
			expectedActionDDMRuleJSONArray.length(),
			actualActionDDMRuleJSONArray.length());

		String expectedCallFunction = expectedActionDDMRuleJSONArray.getString(
			0);
		String actualCallFunction = actualActionDDMRuleJSONArray.getString(0);

		List<String> expectedCallFunctionParameters =
			_extractCallFunctionParameters(expectedCallFunction);

		List<String> actualCallFunctionParameters =
			_extractCallFunctionParameters(actualCallFunction);

		String expectedDDMDataProviderInstanceUUID =
			expectedCallFunctionParameters.get(0);

		String actualDDMDataProviderInstanceUUID =
			actualCallFunctionParameters.get(0);

		Assert.assertEquals(
			expectedDDMDataProviderInstanceUUID,
			actualDDMDataProviderInstanceUUID);

		Assert.assertEquals(
			SetUtil.fromArray(
				StringUtil.split(
					expectedCallFunctionParameters.get(1), CharPool.SEMICOLON)),
			SetUtil.fromArray(
				StringUtil.split(
					actualCallFunctionParameters.get(1), CharPool.SEMICOLON)));
		Assert.assertEquals(
			SetUtil.fromArray(
				StringUtil.split(
					expectedCallFunctionParameters.get(2), CharPool.SEMICOLON)),
			SetUtil.fromArray(
				StringUtil.split(
					actualCallFunctionParameters.get(2), CharPool.SEMICOLON)));
	}

	@Test
	public void testAutoFillActions2() throws Exception {
		_assertConversionToConvertModel(
			"ddm-form-rules-model-auto-fill-actions.json",
			"ddm-form-rules-auto-fill-actions.json");
	}

	@Test
	public void testBelongsToCondition1() throws Exception {
		_assertConversionToModel(
			"ddm-form-rules-belongs-to-condition.json",
			"ddm-form-rules-model-belongs-to-condition.json");
	}

	@Test
	public void testBelongsToCondition2() throws Exception {
		_assertConversionToConvertModel(
			"ddm-form-rules-model-belongs-to-condition.json",
			"ddm-form-rules-belongs-to-condition-without-user-operand.json");
	}

	@Test
	public void testBooleanActions1() throws Exception {
		_assertConversionToModel(
			"ddm-form-rules-boolean-actions.json",
			"ddm-form-rules-model-boolean-actions.json");
	}

	@Test
	public void testBooleanActions2() throws Exception {
		_assertConversionToConvertModel(
			"ddm-form-rules-model-boolean-actions.json",
			"ddm-form-rules-boolean-actions.json");
	}

	@Test
	public void testCalculateAction1() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField0 = new DDMFormField(
			"field0", FieldConstants.INTEGER);
		DDMFormField ddmFormField1 = new DDMFormField(
			"field1", FieldConstants.INTEGER);
		DDMFormField ddmFormField2 = new DDMFormField(
			"field2", FieldConstants.INTEGER);

		ddmForm.setDDMFormFields(
			Arrays.asList(ddmFormField0, ddmFormField1, ddmFormField2));

		Mockito.when(
			_spiDDMFormRuleSerializerContext.getAttribute("form")
		).thenReturn(
			ddmForm
		);

		_assertConversionToModel(
			"ddm-form-rules-calculate-action.json",
			"ddm-form-rules-model-calculate-action.json");
	}

	@Test
	public void testCalculateAction2() throws Exception {
		_assertConversionToConvertModel(
			"ddm-form-rules-model-calculate-action.json",
			"ddm-form-rules-calculate-action.json");
	}

	@Test
	public void testComparisonOperatorsCondition1() throws Exception {
		_assertConversionToModel(
			"ddm-form-rules-comparison-operators-condition.json",
			"ddm-form-rules-model-comparison-operators-condition.json");
	}

	@Test
	public void testComparisonOperatorsCondition2() throws Exception {
		_assertConversionToConvertModel(
			"ddm-form-rules-model-comparison-operators-condition.json",
			"ddm-form-rules-comparison-operators-condition.json");
	}

	@Test
	public void testCustomCondition1() throws Exception {
		_assertConversionToModel(
			"ddm-form-rules-custom-condition.json",
			"ddm-form-rules-model-custom-condition.json");
	}

	@Test
	public void testCustomCondition2() throws Exception {
		_assertConversionToConvertModel(
			"ddm-form-rules-model-custom-condition.json",
			"ddm-form-rules-custom-condition.json");
	}

	@Test
	public void testEqualsToCondition() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addDDMFormField(
			_createDDMFormField("CheckboxMultiple", "checkbox_multiple"));
		ddmForm.addDDMFormField(_createDDMFormField("Select", "select"));
		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"Text1", false, false, false));
		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"Text2", false, false, false));

		Mockito.when(
			_spiDDMFormRuleSerializerContext.getAttribute("form")
		).thenReturn(
			ddmForm
		);

		_assertConversionToConvertModel(
			"ddm-form-rules-model-equals-to-condition.json",
			"ddm-form-rules-equals-to-condition.json");
		_assertConversionToModel(
			"ddm-form-rules-equals-to-condition.json",
			"ddm-form-rules-model-equals-to-condition.json");
	}

	@Test
	public void testIsEmptyCondition1() throws Exception {
		_assertConversionToModel(
			"ddm-form-rules-is-empty-condition.json",
			"ddm-form-rules-model-is-empty-condition.json");
	}

	@Test
	public void testIsEmptyCondition2() throws Exception {
		_assertConversionToConvertModel(
			"ddm-form-rules-model-is-empty-condition.json",
			"ddm-form-rules-is-empty-condition.json");
	}

	@Test
	public void testIsNotEmptyCondition1() throws Exception {
		_assertConversionToModel(
			"ddm-form-rules-is-not-empty-condition.json",
			"ddm-form-rules-model-is-not-empty-condition.json");
	}

	@Test
	public void testIsNotEmptyCondition2() throws Exception {
		_assertConversionToConvertModel(
			"ddm-form-rules-model-is-not-empty-condition.json",
			"ddm-form-rules-is-not-empty-condition.json");
	}

	@Test
	public void testJumpToPageActions1() throws Exception {
		_assertConversionToConvertModel(
			"ddm-form-rules-model-jump-to-page-actions.json",
			"ddm-form-rules-jump-to-page-actions.json");
	}

	@Test
	public void testJumpToPageActions2() throws Exception {
		_assertConversionToModel(
			"ddm-form-rules-jump-to-page-actions.json",
			"ddm-form-rules-model-jump-to-page-actions.json");
	}

	@Test
	public void testNullCondition() {
		List<DDMFormRule> ddmFormRules = new ArrayList<>();

		ddmFormRules.add(
			new DDMFormRule(
				ListUtil.fromArray("setVisible('Text1', true)"), " null "));
		ddmFormRules.add(
			new DDMFormRule(
				ListUtil.fromArray("setVisible('Text2', true)"),
				"equals(getValue('Text1'), 'Bob')"));

		List<DDMFormRule> newDDMFormRules = _ddmFormRuleConverterImpl.convert(
			_ddmFormRuleConverterImpl.convert(ddmFormRules),
			_spiDDMFormRuleSerializerContext);

		Assert.assertEquals(
			newDDMFormRules.toString(), 1, newDDMFormRules.size());
		Assert.assertEquals(ddmFormRules.get(1), newDDMFormRules.get(0));
	}

	protected List<DDMFormRule> convert(String fileName) throws Exception {
		String serializedDDMFormRules = read(fileName);

		return _ddmFormRuleConverterImpl.convert(
			_ddmFormRuleDeserializerImpl.deserialize(serializedDDMFormRules),
			_spiDDMFormRuleSerializerContext);
	}

	private void _assertConversionToConvertModel(
			String fromFileName, String toFileName)
		throws Exception {

		String serializedDDMFormRules = read(fromFileName);

		DDMFormRule[] ddmFormRules = deserialize(
			serializedDDMFormRules, DDMFormRule[].class);

		List<SPIDDMFormRule> spiDDMFormRules =
			_ddmFormRuleConverterImpl.convert(ListUtil.fromArray(ddmFormRules));

		JSONAssert.assertEquals(
			read(toFileName), serialize(spiDDMFormRules), false);
	}

	private void _assertConversionToModel(
			String fromFileName, String toFileName)
		throws Exception {

		List<DDMFormRule> ddmFormRules = convert(fromFileName);

		JSONAssert.assertEquals(
			read(toFileName), serialize(ddmFormRules), false);
	}

	private DDMFormField _createDDMFormField(String name, String type) {
		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			name, RandomTestUtil.randomString(), type, "string", false, false,
			false);

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.addOptionLabel(
			"option1", LocaleUtil.US, "Option 1");
		ddmFormFieldOptions.addOptionLabel(
			"option2", LocaleUtil.US, "Option 2");

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		return ddmFormField;
	}

	private List<String> _extractCallFunctionParameters(String callFunction) {
		Matcher matcher = _callFunctionPattern.matcher(callFunction);

		matcher.find();

		return ListUtil.fromArray(
			matcher.group(1), matcher.group(2), matcher.group(3));
	}

	private void _setUpDDMExpressionFactory() throws Exception {
		DDMExpressionFunctionRegistry ddmExpressionFunctionRegistry =
			Mockito.mock(DDMExpressionFunctionRegistry.class);

		Map<String, DDMExpressionFunctionFactory>
			ddmExpressionFunctionFactories = new HashMap<>();

		List<String> ddmExpressionFunctionNames = Arrays.asList(
			"belongsTo", "calculate", "call", "contains", "custom", "equals",
			"getOptionLabel", "getValue", "isEmpty", "jumpPage", "setEnabled",
			"setRequired", "setVisible");

		for (String ddmExpressionFunctionName : ddmExpressionFunctionNames) {
			ddmExpressionFunctionFactories.put(
				ddmExpressionFunctionName,
				new TestDDMExpressionFunctionFactory());
		}

		Mockito.when(
			ddmExpressionFunctionRegistry.getDDMExpressionFunctionFactories(
				Mockito.any())
		).thenReturn(
			ddmExpressionFunctionFactories
		);

		ReflectionTestUtil.setFieldValue(
			_ddmExpressionFactoryImpl, "ddmExpressionFunctionRegistry",
			ddmExpressionFunctionRegistry);
	}

	private void _setUpDDMFormRuleConverter() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_ddmFormRuleConverterImpl, "ddmExpressionFactory",
			_ddmExpressionFactoryImpl);
	}

	private void _setUpDDMFormRuleDeserializer() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_ddmFormRuleDeserializerImpl, "_jsonFactory",
			new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_ddmFormRuleDeserializerImpl, "_spiDDMFormRuleConverter",
			_ddmFormRuleConverterImpl);
	}

	private static final Pattern _callFunctionPattern = Pattern.compile(
		"call\\(\\s*\'([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-" +
			"[0-9a-f]{12})\'\\s*,\\s*\'(.*)\'\\s*,\\s*\'(.*)\'\\s*\\)");

	private final DDMExpressionFactoryImpl _ddmExpressionFactoryImpl =
		new DDMExpressionFactoryImpl();
	private final DDMFormRuleConverterImpl _ddmFormRuleConverterImpl =
		new DDMFormRuleConverterImpl();
	private final DDMFormRuleDeserializerImpl _ddmFormRuleDeserializerImpl =
		new DDMFormRuleDeserializerImpl();
	private final SPIDDMFormRuleSerializerContext
		_spiDDMFormRuleSerializerContext = Mockito.mock(
			SPIDDMFormRuleSerializerContext.class);

	private static class TestDDMExpressionFunctionFactory
		implements DDMExpressionFunctionFactory {

		@Override
		public DDMExpressionFunction create() {
			return null;
		}

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.evaluator.internal.helper;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionRegistry;
import com.liferay.dynamic.data.mapping.expression.internal.DDMExpressionFactoryImpl;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateRequest;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateResponse;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorFieldContextKey;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.AllFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.BelongsToRoleFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.BetweenFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.CalculateFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.ContainsFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.EqualsFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.FutureDatesFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.GetValueFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.JumpPageFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.SetEnabledFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.SetInvalidFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.SetRequiredFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.SetValueFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.SetVisibleFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory.SumFunctionFactory;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.field.type.DefaultDDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.field.type.internal.checkbox.CheckboxDDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.field.type.internal.numeric.NumericDDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.page.change.DDMFormPageChange;
import com.liferay.dynamic.data.mapping.form.page.change.DDMFormPageChangeRegistry;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.time.LocalDate;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Leonardo Barros
 * @author Marcellus Tavares
 */
public class DDMFormEvaluatorHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_setUpJSONFactoryUtil();
		_setUpLanguageUtil();
		_setUpPortalUtil();

		_ddmExpressionFactory = new DDMExpressionFactoryImpl();
	}

	@Test
	public void testAllCondition() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField0 = _createDDMFormField(
			"field0", "text", FieldConstants.STRING);

		DDMFormField ddmFormField1 = _createDDMFormField(
			"field1", "number", FieldConstants.DOUBLE);

		ddmFormField1.setRepeatable(true);

		ddmForm.addDDMFormField(ddmFormField0);
		ddmForm.addDDMFormField(ddmFormField1);
		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList("setEnabled(\"field0\", false)"),
				"all('#value# <= 10', getValue('field1'))"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_0", "field1", new UnlocalizedValue("1")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_1", "field1", new UnlocalizedValue("5")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_2", "field1", new UnlocalizedValue("10")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertTrue((boolean)ddmFormFieldPropertyChanges.get("readOnly"));
	}

	@Test
	public void testBelongsToCondition() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField0 = _createDDMFormField(
			"field0", "text", FieldConstants.STRING);

		ddmForm.addDDMFormField(ddmFormField0);

		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList("setEnabled(\"field0\", false)"),
				"belongsTo([\"Role1\"])"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("")));

		Mockito.when(
			_roleLocalService.fetchRole(Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_role
		);

		Mockito.when(
			_role.getType()
		).thenReturn(
			RoleConstants.TYPE_REGULAR
		);

		Mockito.when(
			_userLocalService.hasRoleUser(
				Mockito.anyLong(), Mockito.eq("Role1"), Mockito.anyLong(),
				Mockito.eq(true))
		).thenReturn(
			true
		);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertTrue((boolean)ddmFormFieldPropertyChanges.get("readOnly"));
	}

	@Test
	public void testDDMFormPageChange() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		ddmForm.addDDMFormField(
			_createRequiredDDMFormField(
				"field0", "numeric", FieldConstants.DOUBLE));
		ddmForm.addDDMFormField(
			_createRequiredDDMFormField(
				"field1", "text", FieldConstants.STRING));

		DDMFormLayout ddmFormLayout = new DDMFormLayout();

		ddmFormLayout.setNextPage(1);
		ddmFormLayout.setPreviousPage(0);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormLayout, ddmFormValues, LocaleUtil.US);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"This field is required.",
			ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertFalse(
			(boolean)ddmFormFieldPropertyChanges.get("showLabel"));
		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));

		ddmFormFieldPropertyChanges = ddmFormFieldsPropertyChanges.get(
			new DDMFormEvaluatorFieldContextKey("field1", "field1_instanceId"));

		Assert.assertEquals(
			"New Value", ddmFormFieldPropertyChanges.get("value"));
		Assert.assertNull(ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertNull(ddmFormFieldPropertyChanges.get("valid"));
		Assert.assertTrue(
			(boolean)ddmFormFieldPropertyChanges.get("repeatable"));
	}

	@Test
	public void testGetSameResponseAfterCalculateDDMFormRuleEvaluations()
		throws Exception {

		DDMForm ddmForm = new DDMForm();

		ddmForm.addDDMFormField(
			_createDDMFormField("field0", "text", FieldConstants.STRING));
		ddmForm.addDDMFormField(
			_createDDMFormField("field1", "numeric", FieldConstants.DOUBLE));

		BigDecimal expectedValue1 = new BigDecimal(1);

		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList(
					String.format(
						"calculate(\"field1\", %s)",
						expectedValue1.toString())),
				"equals(getValue(\"field0\"), \"field0_value\")"));

		BigDecimal expectedValue2 = new BigDecimal(2);

		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList(
					String.format(
						"calculate(\"field1\", %s)",
						expectedValue2.toString())),
				"equals(getValue(\"field0\"), \"field0_value2\")"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0",
				new UnlocalizedValue("field0_value")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		DDMFormEvaluatorFieldContextKey ddmFormEvaluatorFieldContextKey =
			new DDMFormEvaluatorFieldContextKey("field1", "field1_instanceId");

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(ddmFormEvaluatorFieldContextKey);

		Assert.assertEquals(
			expectedValue1, ddmFormFieldPropertyChanges.get("value"));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0",
				new UnlocalizedValue("field0_value2")));

		ddmFormEvaluatorEvaluateResponse = evaluate(ddmForm, ddmFormValues);

		ddmFormFieldsPropertyChanges =
			ddmFormEvaluatorEvaluateResponse.getDDMFormFieldsPropertyChanges();

		ddmFormFieldPropertyChanges = ddmFormFieldsPropertyChanges.get(
			ddmFormEvaluatorFieldContextKey);

		Assert.assertEquals(
			expectedValue2, ddmFormFieldPropertyChanges.get("value"));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0",
				new UnlocalizedValue("field0_value")));

		ddmFormEvaluatorEvaluateResponse = evaluate(ddmForm, ddmFormValues);

		ddmFormFieldsPropertyChanges =
			ddmFormEvaluatorEvaluateResponse.getDDMFormFieldsPropertyChanges();

		ddmFormFieldPropertyChanges = ddmFormFieldsPropertyChanges.get(
			ddmFormEvaluatorFieldContextKey);

		Assert.assertEquals(
			expectedValue1, ddmFormFieldPropertyChanges.get("value"));
	}

	@Test
	public void testInvalidConfirmationDecimalValue() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addDDMFormField(
			_createDDMFormFieldWithConfirmationField(
				"field0", "numeric", FieldConstants.DOUBLE));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			_createDDMFormFieldValueWithConfirmationValue(
				"field0_instanceId", "field0", "1.2", "1,3"));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testInvalidConfirmationValueWithTextField() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormFieldWithConfirmationField(
			"field0", "text", FieldConstants.STRING);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMFormFieldValue ddmFormFieldValue =
			_createDDMFormFieldValueWithConfirmationValue(
				"field0_instanceId", "field0", "field value",
				"different field value");

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testInvalidNumericValueWithInputMask() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "numeric", "integer");

		ddmFormField.setProperty("inputMask", true);
		ddmFormField.setProperty(
			"inputMaskFormat",
			DDMFormValuesTestUtil.createLocalizedValue(
				"(099) 09999-9999", LocaleUtil.US));

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0",
				DDMFormValuesTestUtil.createLocalizedValue(
					"123456789", LocaleUtil.US)));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"Input format is not satisfied.",
			ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testJumpPageAction() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "text", FieldConstants.NUMBER);

		ddmForm.addDDMFormField(ddmFormField);

		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList("jumpPage(1, 3)"), "getValue(\"field0\") >= 1"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("2")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Set<Integer> disabledPagesIndexes =
			ddmFormEvaluatorEvaluateResponse.getDisabledPagesIndexes();

		Assert.assertTrue(
			disabledPagesIndexes.toString(), disabledPagesIndexes.contains(2));
	}

	@Test
	public void testNotAllCondition() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField0 = _createDDMFormField(
			"field0", "text", FieldConstants.STRING);

		DDMFormField ddmFormField1 = _createDDMFormField(
			"field1", "number", FieldConstants.DOUBLE);

		ddmFormField1.setRepeatable(true);

		ddmForm.addDDMFormField(ddmFormField0);
		ddmForm.addDDMFormField(ddmFormField1);
		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList("setVisible(\"field0\", false)"),
				"not(all('between(#value#,2,6)', getValue('field1')))"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_0", "field1", new UnlocalizedValue("1")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_1", "field1", new UnlocalizedValue("5")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("visible"));
	}

	@Test
	public void testNotBelongsToCondition() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField0 = _createDDMFormField(
			"field0", "text", FieldConstants.STRING);

		ddmForm.addDDMFormField(ddmFormField0);

		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList("setVisible(\"field0\", false)"),
				"not(belongsTo([\"Role1\"]))"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("")));

		Mockito.when(
			_userLocalService.hasRoleUser(
				_company.getCompanyId(), "Role1", _user.getUserId(), true)
		).thenReturn(
			false
		);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("visible"));
	}

	@Test
	public void testNotCalledJumpPageAction() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "text", FieldConstants.NUMBER);

		ddmForm.addDDMFormField(ddmFormField);

		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList("jumpPage(1, 3)"), "getValue(\"field0\") > 1"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("1")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Set<Integer> disabledPagesIndexes =
			ddmFormEvaluatorEvaluateResponse.getDisabledPagesIndexes();

		Assert.assertTrue(
			disabledPagesIndexes.toString(), disabledPagesIndexes.isEmpty());
	}

	@Test
	public void testNullCondition() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "text", FieldConstants.STRING);

		ddmForm.addDDMFormField(ddmFormField);

		ddmForm.addDDMFormRule(new DDMFormRule());

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0",
				new UnlocalizedValue("field0 value")));

		evaluate(ddmForm, ddmFormValues);
	}

	@Test
	public void testRequiredFieldWithEmptyValueAndCustomRequiredErrorMessage()
		throws Exception {

		DDMForm ddmForm = _createDDMFormWithField(
			"field0", "field0", "numeric", FieldConstants.DOUBLE, true, false,
			true);

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		DDMFormField ddmFormField = ddmFormFields.get(0);

		LocalizedValue requiredErrorMessage = new LocalizedValue();

		requiredErrorMessage.addString(
			LocaleUtil.US, "Custom required error message.");

		ddmFormField.setRequiredErrorMessage(requiredErrorMessage);

		DDMFormValues ddmFormValues = _createDDMFormFieldValuesWithValue(
			ddmForm, "field0_instanceId", "field0", null);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues, LocaleUtil.US);

		Map<String, Object> ddmFormFieldsPropertyChanges =
			_getDDMFormFieldPropertyChangesByKey(
				ddmFormEvaluatorEvaluateResponse,
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"Custom required error message.",
			ddmFormFieldsPropertyChanges.get("errorMessage"));
	}

	@Test
	public void testRequiredFieldWithEmptyValueAndEmptyCustomRequiredErrorMessage()
		throws Exception {

		DDMForm ddmForm = _createDDMFormWithField(
			"field0", "field0", "numeric", FieldConstants.DOUBLE, true, false,
			true);

		DDMFormValues ddmFormValues = _createDDMFormFieldValuesWithValue(
			ddmForm, "field0_instanceId", "field0", null);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues, LocaleUtil.US);

		Map<String, Object> ddmFormFieldsPropertyChanges =
			_getDDMFormFieldPropertyChangesByKey(
				ddmFormEvaluatorEvaluateResponse,
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"This field is required.",
			ddmFormFieldsPropertyChanges.get("errorMessage"));
	}

	@Test
	public void testRequiredValidationWithCheckboxField() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "checkbox", FieldConstants.BOOLEAN);

		ddmFormField.setRequired(true);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("false")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"This field is required.",
			ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testRequiredValidationWithHiddenField() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addDDMFormField(
			_createDDMFormField("field0", "text", FieldConstants.INTEGER));

		DDMFormField field1DDMFormField = _createDDMFormField(
			"field1", "text", FieldConstants.STRING);

		field1DDMFormField.setRequired(true);
		field1DDMFormField.setVisibilityExpression("field0 > 5");

		ddmForm.addDDMFormField(field1DDMFormField);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("4")));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field1", "field1_instanceId"));

		Assert.assertNull(ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertNull(ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testRequiredValidationWithinRuleAction() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField0 = _createDDMFormField(
			"field0", "text", FieldConstants.NUMBER);

		DDMFormField ddmFormField1 = _createDDMFormField(
			"field1", "text", FieldConstants.STRING);

		ddmForm.addDDMFormField(ddmFormField0);
		ddmForm.addDDMFormField(ddmFormField1);
		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList("setRequired(\"field1\", true)"),
				"getValue(\"field0\") > 10"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("11")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field1", "field1_instanceId"));

		Assert.assertEquals(
			"This field is required.",
			ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testRequiredValidationWithTextField() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "text", FieldConstants.STRING);

		ddmFormField.setRequired(true);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("\n")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"This field is required.",
			ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testRuleConditionWithNestedFunctions() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		ddmForm.addDDMFormField(
			_createDDMFormField("field0", "numeric", FieldConstants.DOUBLE));
		ddmForm.addDDMFormField(
			_createDDMFormField("field1", "numeric", FieldConstants.DOUBLE));
		ddmForm.addDDMFormRule(
			new DDMFormRule(
				"equals(sum(getValue('field0'), 10), 28)",
				"setValue('field1', getValue('field0'))"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("18")));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field1", "field1_instanceId"));

		Assert.assertEquals(
			new BigDecimal(18), ddmFormFieldPropertyChanges.get("value"));
	}

	@Test
	public void testShowHideAndEnableDisableRules() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addDDMFormField(
			_createDDMFormField("field0", "text", FieldConstants.DOUBLE));
		ddmForm.addDDMFormField(
			_createDDMFormField("field1", "text", FieldConstants.DOUBLE));
		ddmForm.addDDMFormField(
			_createDDMFormField("field2", "text", FieldConstants.DOUBLE));
		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList(
					"setVisible(\"field1\", false)",
					"setEnabled(\"field2\", false)"),
				"getValue(\"field0\") >= 30"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("30")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("15")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2", new UnlocalizedValue("10")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 2,
			ddmFormFieldsPropertyChanges.size());

		// Field 0

		Assert.assertNull(
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId")));

		// Field 1

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field1", "field1_instanceId"));

		Assert.assertEquals(
			ddmFormFieldPropertyChanges.toString(), 1,
			ddmFormFieldPropertyChanges.size());

		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("visible"));

		// Field 2

		ddmFormFieldPropertyChanges = ddmFormFieldsPropertyChanges.get(
			new DDMFormEvaluatorFieldContextKey("field2", "field2_instanceId"));

		Assert.assertEquals(
			ddmFormFieldPropertyChanges.toString(), 1,
			ddmFormFieldPropertyChanges.size());

		Assert.assertTrue((boolean)ddmFormFieldPropertyChanges.get("readOnly"));
	}

	@Test
	public void testSumValuesForRepeatableField() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField0 = _createDDMFormField(
			"field0", "numeric", FieldConstants.DOUBLE);

		ddmForm.addDDMFormField(ddmFormField0);

		DDMFormField ddmFormField1 = _createDDMFormField(
			"field1", "numeric", FieldConstants.DOUBLE);

		ddmFormField1.setRepeatable(true);

		ddmForm.addDDMFormField(ddmFormField1);

		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList("setValue('field0', sum(getValue('field1')))"),
				"TRUE"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_0", "field1", new UnlocalizedValue("1")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_1", "field1", new UnlocalizedValue("1")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_2", "field1", new UnlocalizedValue("2")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			new BigDecimal(4), ddmFormFieldPropertyChanges.get("value"));
	}

	@Test
	public void testUpdateAndCalculateRule() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addDDMFormField(
			_createDDMFormField("field0", "numeric", FieldConstants.DOUBLE));
		ddmForm.addDDMFormField(
			_createDDMFormField("field1", "numeric", FieldConstants.DOUBLE));
		ddmForm.addDDMFormField(
			_createDDMFormField("field2", "numeric", FieldConstants.DOUBLE));
		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList(
					"calculate(\"field2\", getValue(\"field0\") * " +
						"getValue(\"field1\"))"),
				"getValue(\"field0\") > 0 && getValue(\"field1\") > 0"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("5")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("2")));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2", new UnlocalizedValue("0")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		// Field 0

		Assert.assertNull(
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId")));

		// Field 1

		Assert.assertNull(
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field1", "field1_instanceId")));

		// Field 2

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field2", "field2_instanceId"));

		Assert.assertEquals(
			ddmFormFieldPropertyChanges.toString(), 1,
			ddmFormFieldPropertyChanges.size());

		Assert.assertEquals(
			ddmFormFieldPropertyChanges.toString(), new BigDecimal(10.0),
			ddmFormFieldPropertyChanges.get("value"));
	}

	@Test
	public void testUpdateAndCalculateRuleWithRequiredFieldsAndUnavailableLocale()
		throws Exception {

		Set<Locale> availableLocales = DDMFormTestUtil.createAvailableLocales(
			LocaleUtil.US);

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			availableLocales, LocaleUtil.US);

		boolean localizable = true;
		boolean repeatable = false;
		boolean required = true;

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"field0", "field0", "numeric", FieldConstants.DOUBLE,
				localizable, repeatable, required));
		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"field1", "field1", "numeric", FieldConstants.DOUBLE,
				localizable, repeatable, required));
		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"field2", "field2", "numeric", FieldConstants.DOUBLE,
				localizable, repeatable, required));

		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList(
					"calculate(\"field2\", getValue(\"field0\") * " +
						"getValue(\"field1\"))"),
				"getValue(\"field0\") > 0 && getValue(\"field1\") > 0"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		LocalizedValue value0 = DDMFormValuesTestUtil.createLocalizedValue(
			"5", LocaleUtil.US);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", value0));

		LocalizedValue value1 = DDMFormValuesTestUtil.createLocalizedValue(
			"2", LocaleUtil.US);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", value1));

		LocalizedValue value2 = DDMFormValuesTestUtil.createLocalizedValue(
			"0", LocaleUtil.US);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2", value2));

		evaluate(ddmForm, ddmFormValues, LocaleUtil.BRAZIL);

		Value actualValue = null;

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormValues.getDDMFormFieldValues()) {

			if (Objects.equals(ddmFormFieldValue.getName(), "field2")) {
				actualValue = ddmFormFieldValue.getValue();

				break;
			}
		}

		Assert.assertEquals("10", actualValue.getString(LocaleUtil.US));
	}

	@Test
	public void testValidationExpression() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormFieldWithValidation();

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("1")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"This field should be zero.",
			ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testValidationExpressionWithDateField() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "date", FieldConstants.DATE);

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			new DDMFormFieldValidationExpression() {
				{
					setValue("futureDates(field0, \"{parameter}\")");
				}
			});
		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue(
				StringPool.BLANK, LocaleUtil.US));
		ddmFormFieldValidation.setParameterLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue(
				"{\"startsFrom\": \"responseDate\"}", LocaleUtil.US));

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		LocalDate todayLocalDate = LocalDate.now();

		LocalDate yesterdayLocalDate = todayLocalDate.minusDays(1);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0",
				DDMFormValuesTestUtil.createLocalizedValue(
					yesterdayLocalDate.toString(), LocaleUtil.US)));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"This field is invalid.",
			ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testValidationExpressionWithEmptyNumericField()
		throws Exception {

		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "numeric", FieldConstants.INTEGER);

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			new DDMFormFieldValidationExpression() {
				{
					setName("lt");
					setValue("field0<{parameter}");
				}
			});
		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue(
				"This field should be less than zero.", LocaleUtil.US));
		ddmFormFieldValidation.setParameterLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue("0", LocaleUtil.US));

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 0,
			ddmFormFieldsPropertyChanges.size());
	}

	@Test
	public void testValidationExpressionWithHideFieldProperty()
		throws Exception {

		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormFieldWithValidation();

		ddmFormField.setProperty("hideField", true);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("1")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 0,
			ddmFormFieldsPropertyChanges.size());
	}

	@Test
	public void testValidationExpressionWithNoErrorMessage() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "numeric", FieldConstants.INTEGER);

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			new DDMFormFieldValidationExpression() {
				{
					setName("gt");
					setValue("field0>{parameter}");
				}
			});
		ddmFormFieldValidation.setParameterLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue("10", LocaleUtil.US));

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("1")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"This field is invalid.",
			ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testValidationExpressionWithNumericField() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "numeric", FieldConstants.INTEGER);

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			new DDMFormFieldValidationExpression() {
				{
					setName("neq");
					setValue("field0!={parameter}");
				}
			});
		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue(
				"This field should not be zero.", LocaleUtil.US));
		ddmFormFieldValidation.setParameterLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue("0", LocaleUtil.US));

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("0")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"This field should not be zero.",
			ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testValidationForRepeatableField() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "text", FieldConstants.STRING);

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			new DDMFormFieldValidationExpression() {
				{
					setName("contains");
					setValue("NOT(contains(field0, \"{parameter}\"))");
				}
			});
		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue(
				"This field should not contain zero.", LocaleUtil.US));
		ddmFormFieldValidation.setParameterLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue("0", LocaleUtil.US));

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_0", "field0", new UnlocalizedValue("0")));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_1", "field0", new UnlocalizedValue("1")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 2,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges1 =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey("field0", "field0_0"));

		Map<String, Object> ddmFormFieldPropertyChanges2 =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey("field0", "field0_1"));

		Assert.assertEquals(
			"This field should not contain zero.",
			ddmFormFieldPropertyChanges1.get("errorMessage"));

		Assert.assertNull(ddmFormFieldPropertyChanges2.get("errorMessage"));

		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges1.get("valid"));
		Assert.assertTrue((boolean)ddmFormFieldPropertyChanges2.get("valid"));
	}

	@Test
	public void testValidationRule() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addDDMFormField(
			_createDDMFormField("field0", "numeric", FieldConstants.DOUBLE));
		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Arrays.asList(
					"setInvalid(" +
						"\"field0\", \"The value should be greater than " +
							"10.\")"),
				"getValue(\"field0\") <= 10"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("5")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field0", "field0_instanceId"));

		Assert.assertEquals(
			"The value should be greater than 10.",
			ddmFormFieldPropertyChanges.get("errorMessage"));
		Assert.assertFalse((boolean)ddmFormFieldPropertyChanges.get("valid"));
	}

	@Test
	public void testValidConfirmationDecimalValue() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addDDMFormField(
			_createDDMFormFieldWithConfirmationField(
				"field0", "numeric", FieldConstants.DOUBLE));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			_createDDMFormFieldValueWithConfirmationValue(
				"field0_instanceId", "field0", "1.2", "1,2"));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 0,
			ddmFormFieldsPropertyChanges.size());
	}

	@Test
	public void testValidConfirmationValueWithTextField() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormFieldWithConfirmationField(
			"field0", "text", FieldConstants.STRING);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMFormFieldValue ddmFormFieldValue =
			_createDDMFormFieldValueWithConfirmationValue(
				"field0_instanceId", "field0", "field value", "field value");

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 0,
			ddmFormFieldsPropertyChanges.size());
	}

	@Test
	public void testValidNumericValueWithInputMask() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "numeric", "integer");

		ddmFormField.setProperty("inputMask", true);
		ddmFormField.setProperty(
			"inputMaskFormat",
			DDMFormValuesTestUtil.createLocalizedValue(
				"999.999.999-99", LocaleUtil.US));

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0",
				DDMFormValuesTestUtil.createLocalizedValue(
					"01234567899", LocaleUtil.US)));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 0,
			ddmFormFieldsPropertyChanges.size());
	}

	@Test
	public void testVisibilityExpression() throws Exception {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addDDMFormField(
			_createDDMFormField("field0", "text", FieldConstants.INTEGER));

		DDMFormField field1DDMFormField = _createDDMFormField(
			"field1", "text", FieldConstants.STRING);

		field1DDMFormField.setVisibilityExpression("field0 > 5");

		ddmForm.addDDMFormField(field1DDMFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("6")));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("")));

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			evaluate(ddmForm, ddmFormValues);

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		Map<String, Object> ddmFormFieldPropertyChanges =
			ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					"field1", "field1_instanceId"));

		Assert.assertTrue((boolean)ddmFormFieldPropertyChanges.get("visible"));
	}

	protected DDMFormEvaluatorEvaluateResponse evaluate(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout,
			DDMFormValues ddmFormValues, Locale locale)
		throws Exception {

		DDMFormEvaluatorEvaluateRequest.Builder builder =
			DDMFormEvaluatorEvaluateRequest.Builder.newBuilder(
				ddmForm, ddmFormValues, locale);

		builder.withCompanyId(
			1L
		).withDDMFormLayout(
			ddmFormLayout
		).withGroupId(
			1L
		).withUserId(
			1L
		);

		DDMFormEvaluatorHelper ddmFormEvaluatorHelper =
			new DDMFormEvaluatorHelper(
				_ddmExpressionFactory, builder.build(),
				_mockDDMFormFieldTypeServicesRegistry(),
				_mockDDMFormPageChangeRegistry());

		_mockDDMExpressionFunctionRegistry();

		return ddmFormEvaluatorHelper.evaluate();
	}

	protected DDMFormEvaluatorEvaluateResponse evaluate(
			DDMForm ddmForm, DDMFormValues ddmFormValues)
		throws Exception {

		return evaluate(ddmForm, ddmFormValues, LocaleUtil.US);
	}

	protected DDMFormEvaluatorEvaluateResponse evaluate(
			DDMForm ddmForm, DDMFormValues ddmFormValues, Locale locale)
		throws Exception {

		return evaluate(ddmForm, null, ddmFormValues, locale);
	}

	private static void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private static void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		_language = Mockito.mock(Language.class);

		Mockito.when(
			_language.get(
				Mockito.any(Locale.class),
				Mockito.eq("input-format-is-not-satisfied"))
		).thenReturn(
			"Input format is not satisfied."
		);

		Mockito.when(
			_language.get(
				Mockito.any(Locale.class), Mockito.eq("this-field-is-invalid"))
		).thenReturn(
			"This field is invalid."
		);

		Mockito.when(
			_language.get(
				Mockito.any(Locale.class), Mockito.eq("this-field-is-required"))
		).thenReturn(
			"This field is required."
		);

		languageUtil.setLanguage(_language);
	}

	private static void _setUpPortalUtil() throws Exception {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getUser(_httpServletRequest)
		).thenReturn(
			_user
		);

		Mockito.when(
			portal.getCompany(_httpServletRequest)
		).thenReturn(
			_company
		);

		portalUtil.setPortal(portal);
	}

	private DDMExpressionFunctionFactory _createAllFunction() throws Exception {
		AllFunctionFactory allFunctionFactory = new AllFunctionFactory();

		ReflectionTestUtil.setFieldValue(
			allFunctionFactory, "_ddmExpressionFactory", _ddmExpressionFactory);

		return allFunctionFactory;
	}

	private DDMExpressionFunctionFactory _createBelongsToRoleFunction() {
		BelongsToRoleFunctionFactory belongsToRoleFunctionFactory =
			new BelongsToRoleFunctionFactory();

		ReflectionTestUtil.setFieldValue(
			belongsToRoleFunctionFactory, "_roleLocalService",
			_roleLocalService);
		ReflectionTestUtil.setFieldValue(
			belongsToRoleFunctionFactory, "_userGroupRoleLocalService",
			_userGroupRoleLocalService);
		ReflectionTestUtil.setFieldValue(
			belongsToRoleFunctionFactory, "_userLocalService",
			_userLocalService);

		return belongsToRoleFunctionFactory;
	}

	private Map<String, DDMExpressionFunctionFactory>
			_createDDMExpressionFunctionMap()
		throws Exception {

		return HashMapBuilder.<String, DDMExpressionFunctionFactory>put(
			"all", _createAllFunction()
		).put(
			"belongsTo", _createBelongsToRoleFunction()
		).put(
			"between", new BetweenFunctionFactory()
		).put(
			"calculate", new CalculateFunctionFactory()
		).put(
			"contains", new ContainsFunctionFactory()
		).put(
			"equals", new EqualsFunctionFactory()
		).put(
			"futureDates", new FutureDatesFunctionFactory()
		).put(
			"getValue", new GetValueFunctionFactory()
		).put(
			"jumpPage", new JumpPageFunctionFactory()
		).put(
			"setEnabled", new SetEnabledFunctionFactory()
		).put(
			"setInvalid", new SetInvalidFunctionFactory()
		).put(
			"setRequired", new SetRequiredFunctionFactory()
		).put(
			"setValue", new SetValueFunctionFactory()
		).put(
			"setVisible", new SetVisibleFunctionFactory()
		).put(
			"sum", new SumFunctionFactory()
		).build();
	}

	private DDMFormField _createDDMFormField(
		String name, String type, String dataType) {

		DDMFormField ddmFormField = new DDMFormField(name, type);

		ddmFormField.setDataType(dataType);

		return ddmFormField;
	}

	private Map<String, DDMFormFieldValueAccessor<?>>
		_createDDMFormFieldValueAccessorMap() {

		return HashMapBuilder.<String, DDMFormFieldValueAccessor<?>>put(
			"checkbox", new CheckboxDDMFormFieldValueAccessor()
		).put(
			"numeric", new NumericDDMFormFieldValueAccessor()
		).put(
			"text", new DefaultDDMFormFieldValueAccessor()
		).build();
	}

	private DDMFormValues _createDDMFormFieldValuesWithValue(
		DDMForm ddmForm, String instanceId, String name, Value value) {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				instanceId, name, value));

		return ddmFormValues;
	}

	private DDMFormFieldValue _createDDMFormFieldValueWithConfirmationValue(
		String instanceId, String fieldName, String fieldValue,
		String confirmationValue) {

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				instanceId, fieldName,
				DDMFormValuesTestUtil.createLocalizedValue(
					fieldValue, LocaleUtil.US));

		ddmFormFieldValue.setConfirmationValue(confirmationValue);

		return ddmFormFieldValue;
	}

	private DDMFormField _createDDMFormFieldWithConfirmationField(
		String name, String type, String dataType) {

		DDMFormField ddmFormField = _createDDMFormField(name, type, dataType);

		ddmFormField.setProperty(
			"confirmationErrorMessage",
			DDMFormValuesTestUtil.createLocalizedValue(
				"The information does not match", LocaleUtil.US));
		ddmFormField.setProperty(
			"confirmationLabel",
			DDMFormValuesTestUtil.createLocalizedValue(
				"Confirmation Field", LocaleUtil.US));
		ddmFormField.setProperty("requireConfirmation", true);

		return ddmFormField;
	}

	private DDMFormField _createDDMFormFieldWithValidation() {
		DDMFormField ddmFormField = _createDDMFormField(
			"field0", "text", FieldConstants.INTEGER);

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			new DDMFormFieldValidationExpression() {
				{
					setName("equals");
					setValue("field0=={parameter}");
				}
			});
		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue(
				"This field should be zero.", LocaleUtil.US));
		ddmFormFieldValidation.setParameterLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue("0", LocaleUtil.US));

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);

		return ddmFormField;
	}

	private DDMForm _createDDMFormWithField(
		String fieldName, String fieldLabel, String fieldType,
		String fieldDataType, boolean fieldLocalizable, boolean fieldRepeatable,
		boolean fieldRequired) {

		Set<Locale> availableLocales = DDMFormTestUtil.createAvailableLocales(
			LocaleUtil.US);

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			availableLocales, LocaleUtil.US);

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				fieldName, fieldLabel, fieldType, fieldDataType,
				fieldLocalizable, fieldRepeatable, fieldRequired));

		return ddmForm;
	}

	private DDMFormField _createRequiredDDMFormField(
		String name, String type, String dataType) {

		DDMFormField ddmFormField = _createDDMFormField(name, type, dataType);

		ddmFormField.setRequired(true);

		return ddmFormField;
	}

	private Map<String, Object> _getDDMFormFieldPropertyChangesByKey(
		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse,
		DDMFormEvaluatorFieldContextKey ddmFormEvaluatorFieldContextKey) {

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		return ddmFormFieldsPropertyChanges.get(
			ddmFormEvaluatorFieldContextKey);
	}

	private void _mockDDMExpressionFunctionRegistry() throws Exception {
		DDMExpressionFunctionRegistry ddmExpressionFunctionRegistry =
			Mockito.mock(DDMExpressionFunctionRegistry.class);

		Mockito.when(
			ddmExpressionFunctionRegistry.getDDMExpressionFunctionFactories(
				Mockito.any())
		).thenReturn(
			_createDDMExpressionFunctionMap()
		);

		ReflectionTestUtil.setFieldValue(
			_ddmExpressionFactory, "ddmExpressionFunctionRegistry",
			ddmExpressionFunctionRegistry);
	}

	private DDMFormFieldTypeServicesRegistry
		_mockDDMFormFieldTypeServicesRegistry() {

		Map<String, DDMFormFieldValueAccessor<?>> ddmFormFieldValueAccessorMap =
			_createDDMFormFieldValueAccessorMap();

		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry =
			Mockito.mock(DDMFormFieldTypeServicesRegistry.class);

		for (Map.Entry<String, DDMFormFieldValueAccessor<?>> entry :
				ddmFormFieldValueAccessorMap.entrySet()) {

			Mockito.when(
				ddmFormFieldTypeServicesRegistry.getDDMFormFieldValueAccessor(
					Mockito.eq(entry.getKey()))
			).then(
				(Answer<DDMFormFieldValueAccessor<?>>)
					invocation -> entry.getValue()
			);
		}

		return ddmFormFieldTypeServicesRegistry;
	}

	private DDMFormPageChangeRegistry _mockDDMFormPageChangeRegistry() {
		DDMFormPageChangeRegistry ddmFormPageChangeRegistry = Mockito.mock(
			DDMFormPageChangeRegistry.class);

		Mockito.when(
			ddmFormPageChangeRegistry.getDDMFormPageChangeByDDMFormInstanceId(
				Mockito.anyString())
		).then(
			(Answer<DDMFormPageChange>)invocation -> new DDMTestFormPageChange()
		);

		return ddmFormPageChangeRegistry;
	}

	private static final Company _company = Mockito.mock(Company.class);
	private static DDMExpressionFactory _ddmExpressionFactory;
	private static final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private static Language _language;
	private static final User _user = Mockito.mock(User.class);

	private final Role _role = Mockito.mock(Role.class);
	private final RoleLocalService _roleLocalService = Mockito.mock(
		RoleLocalService.class);
	private final UserGroupRoleLocalService _userGroupRoleLocalService =
		Mockito.mock(UserGroupRoleLocalService.class);
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

	private static class DDMTestFormPageChange implements DDMFormPageChange {

		@Override
		public DDMFormEvaluatorEvaluateResponse evaluate(
			DDMFormEvaluatorEvaluateRequest ddmFormEvaluatorEvaluateRequest) {

			DDMFormEvaluatorEvaluateResponse.Builder
				ddmFormEvaluatorEvaluateResponse =
					DDMFormEvaluatorEvaluateResponse.Builder.newBuilder(
						_getDDMFormFieldsPropertyChanges());

			return ddmFormEvaluatorEvaluateResponse.build();
		}

		private Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			_getDDMFormFieldsPropertyChanges() {

			return HashMapBuilder.
				<DDMFormEvaluatorFieldContextKey, Map<String, Object>>put(
					new DDMFormEvaluatorFieldContextKey(
						"field0", "field0_instanceId"),
					HashMapBuilder.<String, Object>put(
						"showLabel", false
					).build()
				).put(
					new DDMFormEvaluatorFieldContextKey(
						"field1", "field1_instanceId"),
					HashMapBuilder.<String, Object>put(
						"repeatable", true
					).put(
						"value", "New Value"
					).build()
				).build();
		}

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.validator.internal;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionRegistry;
import com.liferay.dynamic.data.mapping.expression.internal.DDMExpressionFactoryImpl;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException.MustNotSetValue;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException.MustSetValidAvailableLocales;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException.MustSetValidDefaultLocale;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException.MustSetValidValue;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException.MustSetValidValuesSize;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException.RequiredValue;
import com.liferay.dynamic.data.mapping.validator.internal.expression.DDMFormFieldValueExpressionParameterAccessor;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Marcellus Tavares
 */
public class DDMFormValuesValidatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpDDMFormValuesValidator();
	}

	@Test
	public void testEvaluateValidationExpression() throws Exception {
		Assert.assertTrue(
			_ddmFormValuesValidatorImpl.evaluateValidationExpression(
				new DDMFormField("Field", DDMFormFieldTypeConstants.DATE),
				_createDDMFormFieldValidation(
					new DDMFormFieldValidationExpression() {
						{
							setValue("dateValidation(Field, \"{parameter}\")");
						}
					},
					DDMFormValuesTestUtil.createLocalizedValue(
						"{\"startsFrom\": \"responseDate\"}", LocaleUtil.US)),
				DDMFormValuesTestUtil.createLocalizedDDMFormFieldValue(
					"Field", null)));
		Assert.assertTrue(
			_ddmFormValuesValidatorImpl.evaluateValidationExpression(
				new DDMFormField("Field", DDMFormFieldTypeConstants.NUMERIC),
				_createDDMFormFieldValidation(
					new DDMFormFieldValidationExpression() {
						{
							setName("eq");
							setValue("Field=={parameter}");
						}
					},
					DDMFormValuesTestUtil.createLocalizedValue(
						"10.0", LocaleUtil.US)),
				DDMFormValuesTestUtil.createLocalizedDDMFormFieldValue(
					"Field", "10")));
		Assert.assertTrue(
			_ddmFormValuesValidatorImpl.evaluateValidationExpression(
				new DDMFormField("Field", DDMFormFieldTypeConstants.NUMERIC),
				_createDDMFormFieldValidation(
					new DDMFormFieldValidationExpression() {
						{
							setName("eq");
							setValue("Field=={parameter}");
						}
					},
					DDMFormValuesTestUtil.createLocalizedValue(
						null, "10.0", LocaleUtil.US)),
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					"Field",
					DDMFormValuesTestUtil.createLocalizedValue(
						null, "10,0", LocaleUtil.BRAZIL))));
		Assert.assertTrue(
			_ddmFormValuesValidatorImpl.evaluateValidationExpression(
				new DDMFormField("Field", DDMFormFieldTypeConstants.NUMERIC),
				_createDDMFormFieldValidation(
					new DDMFormFieldValidationExpression() {
						{
							setName("gt");
							setValue("Field>{parameter}");
						}
					},
					DDMFormValuesTestUtil.createLocalizedValue(
						"999999999", LocaleUtil.US)),
				DDMFormValuesTestUtil.createLocalizedDDMFormFieldValue(
					"Field", "3245870178")));
		Assert.assertTrue(
			_ddmFormValuesValidatorImpl.evaluateValidationExpression(
				new DDMFormField("Field", DDMFormFieldTypeConstants.TEXT),
				_createDDMFormFieldValidation(
					null,
					DDMFormValuesTestUtil.createLocalizedValue(
						StringUtil.randomString(), LocaleUtil.US)),
				DDMFormValuesTestUtil.createLocalizedDDMFormFieldValue(
					"Field", StringUtil.randomString())));
	}

	@Test(expected = MustSetValidValue.class)
	public void testNumericValidationWithWrongValueForDoubleTypeExpression()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("Height", "numeric");

		ddmFormField.setDataType("double");

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			new DDMFormFieldValidationExpression() {
				{
					setName("lteq");
					setValue("Height<={parameter}");
				}
			});
		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue(
				"maximum height allowed 3.5.", LocaleUtil.US));
		ddmFormFieldValidation.setParameterLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue("3.5", LocaleUtil.US));

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"Height", new UnlocalizedValue("4.3")));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = MustSetValidValue.class)
	public void testNumericValidationWithWrongValueForIntegerTypeExpression()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("Age", "numeric");

		ddmFormField.setDataType("integer");

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			new DDMFormFieldValidationExpression() {
				{
					setName("gt");
					setValue("Age>{parameter}");
				}
			});
		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue(
				"Age must be greater than 18.", LocaleUtil.US));
		ddmFormFieldValidation.setParameterLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue("18", LocaleUtil.US));

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"Age", new UnlocalizedValue("14")));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test
	public void testValidationWithAutocompleteText() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"Country", false, false, false);

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.addOptionLabel("Brazil", LocaleUtil.US, "Brazil");
		ddmFormFieldOptions.addOptionLabel("USA", LocaleUtil.US, "USA");
		ddmFormFieldOptions.addOptionLabel("France", LocaleUtil.US, "France");

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"Country", "Spain"));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test
	public void testValidationWithAvailableLocales() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			SetUtil.fromArray(LocaleUtil.SPAIN, LocaleUtil.US), LocaleUtil.US);

		DDMFormTestUtil.addDDMFormFields(
			ddmForm,
			DDMFormTestUtil.createLocalizableTextDDMFormField("textField"));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.setDDMFormFieldValues(
			Collections.singletonList(
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					"textField",
					DDMFormValuesTestUtil.createLocalizedValue(
						"Joe", "João", LocaleUtil.US))));

		AssertUtils.assertFailure(
			MustSetValidAvailableLocales.class,
			"Invalid available locales set for field name textField",
			() -> _ddmFormValuesValidatorImpl.validate(ddmFormValues));

		ddmFormValues.setDDMFormFieldValues(
			Collections.singletonList(
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					"textField",
					DDMFormValuesTestUtil.createLocalizedValue(
						"Joe", LocaleUtil.US))));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test
	public void testValidationWithInvalidFieldName() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("firstName");

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue("lastName", null));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test
	public void testValidationWithInvalidNestedFieldName() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField =
			DDMFormTestUtil.createLocalizableTextDDMFormField("name");

		DDMFormTestUtil.addNestedTextDDMFormFields(ddmFormField, "contact");

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		Value localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(LocaleUtil.US, StringUtil.randomString());

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"name", localizedValue);

		List<DDMFormFieldValue> nestedDDMFormFieldValues =
			ddmFormFieldValue.getNestedDDMFormFieldValues();

		nestedDDMFormFieldValues.add(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"invalid", localizedValue));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = MustSetValidValue.class)
	public void testValidationWithLocalizableField() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField =
			DDMFormTestUtil.createLocalizableTextDDMFormField("name");

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"name", new UnlocalizedValue("Joe")));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = RequiredValue.class)
	public void testValidationWithMissingNestedRequiredField()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("name", "text");

		List<DDMFormField> nestedDDMFormFields =
			ddmFormField.getNestedDDMFormFields();

		nestedDDMFormFields.add(
			DDMFormTestUtil.createTextDDMFormField(
				"contact", "", false, false, true));

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue("name", null);

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = RequiredValue.class)
	public void testValidationWithMissingNestedRequiredFieldValue()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("name", "text");

		List<DDMFormField> nestedDDMFormFields =
			ddmFormField.getNestedDDMFormFields();

		nestedDDMFormFields.add(
			DDMFormTestUtil.createTextDDMFormField(
				"contact", "", false, false, true));

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue("name", null);

		List<DDMFormFieldValue> nestedDDMFormFieldValues =
			ddmFormFieldValue.getNestedDDMFormFieldValues();

		nestedDDMFormFieldValues.add(
			DDMFormValuesTestUtil.createDDMFormFieldValue("contact", null));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = RequiredValue.class)
	public void testValidationWithMissingRequiredField() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField =
			DDMFormTestUtil.createLocalizableTextDDMFormField("name");

		ddmFormField.setRequired(true);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = RequiredValue.class)
	public void testValidationWithMissingRequiredFieldValue() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField =
			DDMFormTestUtil.createLocalizableTextDDMFormField("name");

		ddmFormField.setRequired(true);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue("name", null));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test
	public void testValidationWithNonrequiredFieldAndEmptyDefaultLocaleValue()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(LocaleUtil.US),
			LocaleUtil.US);

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", "Name", true, false, false);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(LocaleUtil.US, StringPool.BLANK);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"name", localizedValue);

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test
	public void testValidationWithNonrequiredFieldValue() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(LocaleUtil.US),
			LocaleUtil.US);

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", "Name", true, false, false);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test
	public void testValidationWithNonrequiredSelect() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("option", "select");

		ddmFormField.setDataType("string");
		ddmFormField.setRequired(false);

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.addOptionLabel("A", LocaleUtil.US, "Option A");
		ddmFormFieldOptions.addOptionLabel("B", LocaleUtil.US, "Option B");

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		ddmFormField.setLocalizable(false);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		String instanceId = StringUtil.randomString();

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				instanceId, "option", new UnlocalizedValue("[\"A\"]")));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test
	public void testValidationWithNonrequiredSelectAndEmptyDefaultLocaleValue()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("option", "select");

		ddmFormField.setDataType("string");
		ddmFormField.setRequired(false);

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.addOptionLabel("A", LocaleUtil.US, "Option A");
		ddmFormFieldOptions.addOptionLabel("B", LocaleUtil.US, "Option B");

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		ddmFormField.setLocalizable(false);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		String instanceId = StringUtil.randomString();

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				instanceId, "option", new UnlocalizedValue("[\"\"]")));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = RequiredValue.class)
	public void testValidationWithRequiredFieldAndEmptyDefaultLocaleValue()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(LocaleUtil.US),
			LocaleUtil.US);

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", "Name", true, false, true);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		String instanceId = StringUtil.randomString();

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(LocaleUtil.US, StringPool.BLANK);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				instanceId, "name", localizedValue);

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = RequiredValue.class)
	public void testValidationWithRequiredFieldAndEmptyTranslatedValue()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(
				LocaleUtil.US, LocaleUtil.BRAZIL),
			LocaleUtil.US);

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", "Name", true, false, true);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm,
			DDMFormTestUtil.createAvailableLocales(
				LocaleUtil.US, LocaleUtil.BRAZIL),
			LocaleUtil.US);

		String instanceId = StringUtil.randomString();

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(LocaleUtil.US, StringPool.BLANK);
		localizedValue.addString(LocaleUtil.BRAZIL, StringPool.BLANK);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				instanceId, "name", localizedValue);

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = RequiredValue.class)
	public void testValidationWithRequiredFieldAndNullValue() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(LocaleUtil.US),
			LocaleUtil.US);

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", "Name", true, false, true);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		String instanceId = StringUtil.randomString();

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				instanceId, "name", localizedValue);

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = RequiredValue.class)
	public void testValidationWithRequiredFieldAndWithNoValue()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(LocaleUtil.US),
			LocaleUtil.US);

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", "Name", true, false, true);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test
	public void testValidationWithRequiredSelect() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("option", "select");

		ddmFormField.setDataType("string");
		ddmFormField.setRequired(true);

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.addOptionLabel("A", LocaleUtil.US, "Option A");
		ddmFormFieldOptions.addOptionLabel("B", LocaleUtil.US, "Option B");

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		ddmFormField.setLocalizable(false);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		String instanceId = StringUtil.randomString();

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				instanceId, "option", new UnlocalizedValue("[\"A\"]")));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = MustNotSetValue.class)
	public void testValidationWithSeparatorField() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createSeparatorDDMFormField(
			"separator", false);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"separator", new UnlocalizedValue("separator value")));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = MustSetValidValue.class)
	public void testValidationWithUnlocalizableField() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", "", false, false, false);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(LocaleUtil.US, "Joe");

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"name", localizedValue));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = MustNotSetValue.class)
	public void testValidationWithValueSetForTransientField() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("fieldset", "fieldset");

		DDMFormTestUtil.addNestedTextDDMFormFields(ddmFormField, "name");

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"fieldset", new UnlocalizedValue("Value"));

		List<DDMFormFieldValue> nestedDDMFormFieldValues =
			ddmFormFieldValue.getNestedDDMFormFieldValues();

		nestedDDMFormFieldValues.add(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"name", new UnlocalizedValue("Joe")));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = MustSetValidDefaultLocale.class)
	public void testValidationWithWrongDefaultLocale() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField =
			DDMFormTestUtil.createLocalizableTextDDMFormField("name");

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.BRAZIL);

		localizedValue.addString(LocaleUtil.US, "Joe");

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"name", localizedValue));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = MustSetValidValue.class)
	public void testValidationWithWrongValueSetDueValidationExpression()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("Age", "text");

		ddmFormField.setDataType("integer");

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			new DDMFormFieldValidationExpression() {
				{
					setName("gt");
					setValue("Age>{parameter}");
				}
			});
		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue(
				"Age must be greater than 18.", LocaleUtil.US));
		ddmFormFieldValidation.setParameterLocalizedValue(
			DDMFormValuesTestUtil.createLocalizedValue("18", LocaleUtil.US));

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"Age", new UnlocalizedValue("5")));

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = MustSetValidValuesSize.class)
	public void testValidationWithWrongValuesForNonrepeatableField()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("name", "text");

		List<DDMFormField> nestedDDMFormFields =
			ddmFormField.getNestedDDMFormFields();

		nestedDDMFormFields.add(
			DDMFormTestUtil.createTextDDMFormField(
				"contact", "", false, false, true));

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"name", new UnlocalizedValue("name value"));

		List<DDMFormFieldValue> nestedDDMFormFieldValues =
			ddmFormFieldValue.getNestedDDMFormFieldValues();

		nestedDDMFormFieldValues.add(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"contact", new UnlocalizedValue("contact value 1")));
		nestedDDMFormFieldValues.add(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"contact", new UnlocalizedValue("contact value 2")));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	@Test(expected = NullPointerException.class)
	public void testValidationWithoutDDMFormReference() throws Exception {
		DDMFormValues ddmFormValues = new DDMFormValues(null);

		_ddmFormValuesValidatorImpl.validate(ddmFormValues);
	}

	private DDMFormFieldValidation _createDDMFormFieldValidation(
		DDMFormFieldValidationExpression ddmFormFieldValidationExpression,
		LocalizedValue localizedValue) {

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			ddmFormFieldValidationExpression);
		ddmFormFieldValidation.setParameterLocalizedValue(localizedValue);

		return ddmFormFieldValidation;
	}

	private void _setUpDDMFormValuesValidator() throws Exception {
		DDMExpressionFactoryImpl ddmExpressionFactoryImpl =
			new DDMExpressionFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			ddmExpressionFactoryImpl, "ddmExpressionFunctionRegistry",
			new DDMExpressionFunctionRegistry() {

				@Override
				public Map<String, DDMExpressionFunction>
					getCustomDDMExpressionFunctions() {

					return Collections.emptyMap();
				}

				@Override
				public Map<String, DDMExpressionFunctionFactory>
					getDDMExpressionFunctionFactories(
						Set<String> functionNames) {

					return HashMapBuilder.
						<String, DDMExpressionFunctionFactory>put(
							"dateValidation",
							new DateValidationFunctionFactory()
						).build();
				}

				@Override
				public Map<String, DDMExpressionFunction>
					getDDMExpressionFunctions(Set<String> functionNames) {

					return Collections.emptyMap();
				}

				@Override
				public void ungetDDMExpressionFunctions(
					Map<String, DDMExpressionFunction>
						ddmExpressionFunctionsMap) {
				}

			});

		ReflectionTestUtil.setFieldValue(
			_ddmFormValuesValidatorImpl, "_ddmExpressionFactory",
			ddmExpressionFactoryImpl);

		ReflectionTestUtil.setFieldValue(
			_ddmFormValuesValidatorImpl, "_ddmFormFieldTypeServicesRegistry",
			ProxyFactory.newDummyInstance(
				DDMFormFieldTypeServicesRegistry.class));
		ReflectionTestUtil.setFieldValue(
			_ddmFormValuesValidatorImpl,
			"_ddmFormFieldValueExpressionParameterAccessor",
			new DDMFormFieldValueExpressionParameterAccessor(
				new JSONFactoryImpl(), null, null));
		ReflectionTestUtil.setFieldValue(
			_ddmFormValuesValidatorImpl, "_serviceTrackerMap",
			ProxyFactory.newDummyInstance(ServiceTrackerMap.class));
	}

	private final DDMFormValuesValidatorImpl _ddmFormValuesValidatorImpl =
		new DDMFormValuesValidatorImpl();

	private static class DateValidationFunction
		implements DDMExpressionFunction.Function2<String, String, Boolean> {

		@Override
		public Boolean apply(String fieldName, String parameter) {
			return StringUtil.equals(parameter, "{startsFrom: responseDate}");
		}

		@Override
		public String getName() {
			return "dateValidation";
		}

	}

	private static class DateValidationFunctionFactory
		implements DDMExpressionFunctionFactory {

		@Override
		public DDMExpressionFunction create() {
			return new DateValidationFunction();
		}

	}

}
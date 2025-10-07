/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.validator;

import com.liferay.fragment.exception.FragmentEntryConfigurationException;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.hamcrest.core.StringContains;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Rubén Pulido
 */
public class FragmentEntryValidatorImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(new LanguageImpl());

		_classLoader = PortalClassLoaderUtil.getClassLoader();

		PortalClassLoaderUtil.setClassLoader(null);
	}

	@AfterClass
	public static void tearDownClass() {
		PortalClassLoaderUtil.setClassLoader(_classLoader);
	}

	@Before
	public void setUp() {
		_fragmentEntryValidatorImpl = new FragmentEntryValidatorImpl();

		ReflectionTestUtil.setFieldValue(
			_fragmentEntryValidatorImpl, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_fragmentEntryValidatorImpl, "_language", new LanguageImpl());
	}

	@Test
	public void testValidateConfigurationInvalidColorPaletteFieldDefaultValueCssClassMissing()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/defaultValue: required key [cssClass] " +
					"not found"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_colorpalette_defaultvalue_" +
					"cssclass_missing.json"));
	}

	@Test
	public void testValidateConfigurationInvalidColorPaletteFieldDefaultValueRgbValueMissing()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/defaultValue: required key [rgbValue] " +
					"not found"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_colorpalette_defaultvalue_" +
					"rgbvalue_missing.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldCheckboxDefaultValueUnsupported()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/defaultValue: unsupported is not a " +
					"valid enum value"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_checkbox_defaultvalue_" +
					"unsupported.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldCheckboxExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0: extraneous key [extra] is not " +
					"permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_checkbox_extra_properties.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldColorPaletteDefaultValueExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/defaultValue: extraneous key [extra] " +
					"is not permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_colorpalette_defaultvalue_extra_" +
					"properties.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldColorPaletteExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0: extraneous key [extra] is not " +
					"permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_colorpalette_extra_properties." +
					"json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldItemSelectorDefaultValueClassNameMissing()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/defaultValue: required key " +
					"[className] not found"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_itemselector_defaultvalue_" +
					"classname_missing.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldItemSelectorDefaultValueClassPKMissing()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/defaultValue: required key [classPK] " +
					"not found"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_itemselector_defaultvalue_" +
					"classpk_missing.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldItemSelectorDefaultValueExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/defaultValue: extraneous key [extra] " +
					"is not permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_itemselector_defaultvalue_extra_" +
					"properties.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldItemSelectorExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0: extraneous key [extra] is not " +
					"permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_itemselector_extra_properties." +
					"json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldItemSelectorTypeOptionsExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/typeOptions: extraneous key [extra] " +
					"is not permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_itemselector_typeoptions_extra_" +
					"properties.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldNameEmpty()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/name: expected minLength: 1, actual: " +
					"0"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject("configuration_invalid_field_name_empty.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldNameMissing()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0: required key [name] not found"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject("configuration_invalid_field_name_missing.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldNameNonalphanumeric()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/name: string [a_b-c.d?e] does not " +
					"match pattern"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_name_non_alphanumeric.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldNameWithSpace()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/name: string [a b] does not match " +
					"pattern"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_name_with_space.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldSelectDataTypeUnsupported()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/dataType: unsupported is not a valid " +
					"enum value"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_select_datatype_unsupported." +
					"json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldSelectDefaultValueMissing()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0: required key [defaultValue] not " +
					"found"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_select_defaultvalue_missing." +
					"json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldSelectExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0: extraneous key [extra] is not " +
					"permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_select_extra_properties.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldSelectTypeOptionsExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/typeOptions: extraneous key [extra] " +
					"is not permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_select_typeoptions_extra_" +
					"properties.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldSelectTypeOptionsMissing()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0: required key [typeOptions] not found"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_select_typeoptions_missing.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldSelectTypeOptionsValidValuesExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/typeOptions/validValues/0: extraneous " +
					"key [extra] is not permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_select_typeoptions_validvalues_" +
					"extra_properties.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldSelectTypeOptionsValidValuesMissing()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/typeOptions: required key " +
					"[validValues] not found"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_select_typeoptions_validvalues_" +
					"missing.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldSelectTypeOptionsValidValuesValueMissing()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/typeOptions/validValues/0: required " +
					"key [value] not found"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_select_typeoptions_validvalues_" +
					"value_missing.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldSetsExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains("extraneous key [extra] is not permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_sets_extra_properties.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldSetsMissing()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains("required key [fieldSets] not found"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject("configuration_invalid_field_sets_missing.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldTextDataTypeUnsupported()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0/dataType: unsupported is not a valid " +
					"enum value"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_text_datatype_unsupported.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldTextDependency()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_field_text_typeoptions_dependency.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldTextDependencyInvalidType()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"Dependency field type should be checkbox, select, or text"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_field_text_typeoptions_dependency_invalid_" +
					"type.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldTextDependencyItself()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains("Dependency field cannot reference itself"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_field_text_typeoptions_dependency_name.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldTextDependencyUnknownField()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"Dependency field cannot depend on field \"field3\" that " +
					"does not exist"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_field_text_typeoptions_dependency_unknown_" +
					"field.json"));
	}

	@Test
	public void testValidateConfigurationInvalidFieldTextExtraProperties()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);
		expectedException.expectMessage(
			new StringContains(
				"/fieldSets/0/fields/0: extraneous key [extra] is not " +
					"permitted"));

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_invalid_field_text_extra_properties.json"));
	}

	@Test
	public void testValidateConfigurationValidComplete() throws Exception {
		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject("configuration_valid_complete.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldCheckboxComplete()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_checkbox_complete.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldCheckboxDefaultValueBooleanFalse()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_checkbox_defaultvalue_boolean_" +
					"false.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldCheckboxDefaultValueBooleanTrue()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_checkbox_defaultvalue_boolean_" +
					"true.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldCheckboxDefaultValueStringFalse()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_checkbox_defaultvalue_string_" +
					"false.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldCheckboxDefaultValueStringTrue()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_checkbox_defaultvalue_string_true." +
					"json"));
	}

	@Test
	public void testValidateConfigurationValidFieldCheckboxRequired()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_checkbox_required.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldColorPaletteComplete()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_colorpalette_complete.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldColorPaletteRequired()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_colorpalette_required.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldItemSelectorComplete()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_itemselector_complete.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldItemSelectorDefaultValueRequired()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_itemselector_defaultvalue_" +
					"required.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldItemSelectorRequired()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_itemselector_required.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldItemSelectorTypeOptionsEnableSelectTemplate()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_itemselector_typeoptions_" +
					"enableselecttemplate.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldItemSelectorTypeOptionsRequired()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_itemselector_typeoptions_required." +
					"json"));
	}

	@Test
	public void testValidateConfigurationValidFieldSelectDoubleRequired()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_select_double_required.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldSelectIntRequired()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_select_int_required.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldSelectStringComplete()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_select_string_complete.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldSelectStringRequired()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject(
				"configuration_valid_field_select_string_required.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldTextComplete()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject("configuration_valid_field_text_complete.json"));
	}

	@Test
	public void testValidateConfigurationValidFieldTextRequired()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject("configuration_valid_field_text_required.json"));
	}

	@Test
	public void testValidateConfigurationValidRequired() throws Exception {
		_fragmentEntryValidatorImpl.validateConfiguration(
			_readJSONObject("configuration_valid_required.json"));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeInvalidEmail()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_email.json"),
			JSONUtil.put("emailField", "test-liferay.com"));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeInvalidLength()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_length.json"),
			JSONUtil.put("textField", StringUtil.randomString(11)));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeInvalidNumberLimit()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_number.json"),
			JSONUtil.put("numberField", 1000));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeInvalidNumberNAN()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_number.json"),
			JSONUtil.put("numberField", StringUtil.randomString()));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeInvalidRegexp()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_regexp.json"),
			JSONUtil.put("regexpField", StringUtil.randomString()));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeInvalidURL()
		throws Exception {

		expectedException.expect(FragmentEntryConfigurationException.class);

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_url.json"),
			JSONUtil.put("urlField", StringUtil.randomString()));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeValidEmail()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_email.json"),
			JSONUtil.put("emailField", "test@liferay.com"));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeValidLength()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_length.json"),
			JSONUtil.put("textField", StringUtil.randomString(9)));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeValidNumber()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_number.json"),
			JSONUtil.put("numberField", 256));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeValidRegexp()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_regexp.json"),
			JSONUtil.put("regexpField", "test-256"));
	}

	@Test
	public void testValidateConfigurationValuesTextFieldTypeValidURL()
		throws Exception {

		_fragmentEntryValidatorImpl.validateConfigurationValues(
			_readJSONObject(
				"configuration_field_text_typeoptions_validation_url.json"),
			JSONUtil.put("urlField", "http://www.liferay.com"));
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private JSONObject _readJSONObject(String fileName) throws Exception {
		return JSONFactoryUtil.createJSONObject(
			new String(
				FileUtil.getBytes(getClass(), "dependencies/" + fileName)));
	}

	private static ClassLoader _classLoader;

	private FragmentEntryValidatorImpl _fragmentEntryValidatorImpl;

}
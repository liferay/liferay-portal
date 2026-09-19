/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.internal.rest;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Leonardo Barros
 */
public class DDMRESTDataProviderSettingsTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_setUpLanguageUtil();
		_setUpPortalUtil();
		_setUpResourceBundleUtil();
	}

	@Test
	public void testCreateForm() {
		DDMForm ddmForm = DDMFormFactory.create(
			DDMRESTDataProviderSettings.class);

		Map<String, DDMFormField> ddmFormFields = ddmForm.getDDMFormFieldsMap(
			false);

		Assert.assertEquals(ddmFormFields.toString(), 12, ddmFormFields.size());

		_assertCacheable(ddmFormFields.get("cacheable"));
		_assertFilterable(ddmFormFields.get("filterable"));
		_assertFilterParameterName(ddmFormFields.get("filterParameterName"));
		_assertInputParameters(ddmFormFields.get("inputParameters"));
		_assertOutputParameters(ddmFormFields.get("outputParameters"));
		_assertPagination(ddmFormFields.get("pagination"));
		_assertPaginationEndParameterName(
			ddmFormFields.get("paginationEndParameterName"));
		_assertPassword(ddmFormFields.get("password"));
		_assertStartPaginationParameterName(
			ddmFormFields.get("paginationStartParameterName"));
		_assertTimeout(ddmFormFields.get("timeout"));
		_assertURL(ddmFormFields.get("url"));
		_assertUsername(ddmFormFields.get("username"));
	}

	private static void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));
	}

	private static void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);
		ResourceBundle resourceBundle = Mockito.mock(ResourceBundle.class);

		Mockito.when(
			portal.getResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			resourceBundle
		);

		portalUtil.setPortal(portal);
	}

	private static void _setUpResourceBundleUtil() {
		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			resourceBundleLoader);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);
	}

	private void _assertCacheable(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertEquals("boolean", ddmFormField.getDataType());
		Assert.assertEquals("true", ddmFormField.getProperty("showAsSwitcher"));
		Assert.assertEquals("checkbox", ddmFormField.getType());
	}

	private void _assertFilterParameterName(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertEquals("string", ddmFormField.getDataType());

		Map<String, Object> properties = ddmFormField.getProperties();

		Assert.assertTrue(properties.containsKey("placeholder"));
		Assert.assertTrue(properties.containsKey("tooltip"));

		Assert.assertEquals("text", ddmFormField.getType());
	}

	private void _assertFilterable(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertEquals("boolean", ddmFormField.getDataType());
		Assert.assertEquals("true", ddmFormField.getProperty("showAsSwitcher"));
		Assert.assertEquals("checkbox", ddmFormField.getType());
	}

	private void _assertInputParameters(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertEquals("", ddmFormField.getDataType());
		Assert.assertEquals("fieldset", ddmFormField.getType());

		Map<String, DDMFormField> nestedDDMFormFieldsMap =
			ddmFormField.getNestedDDMFormFieldsMap();

		Assert.assertEquals(
			nestedDDMFormFieldsMap.toString(), 4,
			nestedDDMFormFieldsMap.size());

		// Label

		DDMFormField inputParameterLabelDDMFormField =
			nestedDDMFormFieldsMap.get("inputParameterLabel");

		Assert.assertNotNull(inputParameterLabelDDMFormField);

		Assert.assertEquals("text", inputParameterLabelDDMFormField.getType());
		Assert.assertEquals(
			"string", inputParameterLabelDDMFormField.getDataType());

		Map<String, Object> inputParameterLabelDDMFormFieldProperties =
			inputParameterLabelDDMFormField.getProperties();

		Assert.assertTrue(
			inputParameterLabelDDMFormFieldProperties.containsKey(
				"placeholder"));

		// Name

		DDMFormField inputParameterNameDDMFormField =
			nestedDDMFormFieldsMap.get("inputParameterName");

		Assert.assertNotNull(inputParameterNameDDMFormField);

		Assert.assertEquals("text", inputParameterNameDDMFormField.getType());
		Assert.assertEquals(
			"string", inputParameterNameDDMFormField.getDataType());

		Map<String, Object> inputParameterNameDDMFormFieldProperties =
			inputParameterNameDDMFormField.getProperties();

		Assert.assertTrue(
			inputParameterNameDDMFormFieldProperties.containsKey(
				"placeholder"));

		// Type

		DDMFormField inputParameterTypeDDMFormField =
			nestedDDMFormFieldsMap.get("inputParameterType");

		Assert.assertNotNull(inputParameterTypeDDMFormField);

		Assert.assertEquals("select", inputParameterTypeDDMFormField.getType());
		Assert.assertEquals(
			"string", inputParameterTypeDDMFormField.getDataType());

		Assert.assertNotNull(
			inputParameterTypeDDMFormField.getDDMFormFieldOptions());

		DDMFormFieldOptions ddmFormFieldOptions =
			inputParameterTypeDDMFormField.getDDMFormFieldOptions();

		Set<String> optionsValues = ddmFormFieldOptions.getOptionsValues();

		Assert.assertTrue(
			optionsValues.toString(), optionsValues.contains("text"));
		Assert.assertTrue(
			optionsValues.toString(), optionsValues.contains("number"));

		// Required

		DDMFormField inputParameterRequiredDDMFormField =
			nestedDDMFormFieldsMap.get("inputParameterRequired");

		Assert.assertNotNull(inputParameterRequiredDDMFormField);

		Assert.assertEquals(
			"checkbox", inputParameterRequiredDDMFormField.getType());
		Assert.assertEquals(
			"boolean", inputParameterRequiredDDMFormField.getDataType());
	}

	private void _assertOutputParameters(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertEquals("", ddmFormField.getDataType());
		Assert.assertEquals("fieldset", ddmFormField.getType());

		Map<String, DDMFormField> nestedDDMFormFieldsMap =
			ddmFormField.getNestedDDMFormFieldsMap();

		Assert.assertEquals(
			nestedDDMFormFieldsMap.toString(), 4,
			nestedDDMFormFieldsMap.size());

		// Name

		DDMFormField outputParameterNameDDMFormField =
			nestedDDMFormFieldsMap.get("outputParameterName");

		Assert.assertNotNull(outputParameterNameDDMFormField);

		Assert.assertEquals("text", outputParameterNameDDMFormField.getType());
		Assert.assertEquals(
			"string", outputParameterNameDDMFormField.getDataType());

		Map<String, Object> outputParameterNameDDMFormFieldProperties =
			outputParameterNameDDMFormField.getProperties();

		Assert.assertTrue(
			outputParameterNameDDMFormFieldProperties.containsKey(
				"placeholder"));

		// Path

		DDMFormField outputParameterPathDDMFormField =
			nestedDDMFormFieldsMap.get("outputParameterPath");

		Assert.assertNotNull(outputParameterPathDDMFormField);

		Assert.assertEquals("text", outputParameterPathDDMFormField.getType());
		Assert.assertEquals(
			"string", outputParameterPathDDMFormField.getDataType());

		Map<String, Object> outputParameterPathDDMFormFieldProperties =
			outputParameterPathDDMFormField.getProperties();

		Assert.assertTrue(
			outputParameterPathDDMFormFieldProperties.containsKey(
				"placeholder"));

		// Type

		DDMFormField outputParameterTypeDDMFormField =
			nestedDDMFormFieldsMap.get("outputParameterType");

		Assert.assertNotNull(outputParameterTypeDDMFormField);

		Assert.assertEquals(
			"select", outputParameterTypeDDMFormField.getType());
		Assert.assertEquals(
			"string", outputParameterTypeDDMFormField.getDataType());

		Assert.assertNotNull(
			outputParameterTypeDDMFormField.getDDMFormFieldOptions());

		DDMFormFieldOptions ddmFormFieldOptions =
			outputParameterTypeDDMFormField.getDDMFormFieldOptions();

		Set<String> optionsValues = ddmFormFieldOptions.getOptionsValues();

		Assert.assertTrue(
			optionsValues.toString(), optionsValues.contains("text"));
		Assert.assertTrue(
			optionsValues.toString(), optionsValues.contains("number"));
	}

	private void _assertPagination(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertEquals("boolean", ddmFormField.getDataType());
		Assert.assertEquals("true", ddmFormField.getProperty("showAsSwitcher"));
		Assert.assertEquals("checkbox", ddmFormField.getType());
	}

	private void _assertPaginationEndParameterName(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertEquals("string", ddmFormField.getDataType());
		Assert.assertEquals("text", ddmFormField.getType());
	}

	private void _assertPassword(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertEquals("string", ddmFormField.getDataType());

		Map<String, Object> properties = ddmFormField.getProperties();

		Assert.assertTrue(properties.containsKey("placeholder"));
		Assert.assertTrue(properties.containsKey("tooltip"));

		Assert.assertEquals("password", ddmFormField.getType());
	}

	private void _assertStartPaginationParameterName(
		DDMFormField ddmFormField) {

		Assert.assertNotNull(ddmFormField);

		Assert.assertEquals("string", ddmFormField.getDataType());
		Assert.assertEquals("text", ddmFormField.getType());
	}

	private void _assertTimeout(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertTrue(ddmFormField.isRequired());

		Assert.assertEquals("integer", ddmFormField.getDataType());

		Map<String, Object> properties = ddmFormField.getProperties();

		Assert.assertTrue(properties.containsKey("placeholder"));

		DDMFormFieldValidation ddmFormFieldValidation =
			ddmFormField.getDDMFormFieldValidation();

		DDMFormFieldValidationExpression ddmFormFieldValidationExpression =
			ddmFormFieldValidation.getDDMFormFieldValidationExpression();

		Assert.assertEquals(
			"(timeout >= 1000) && (timeout <= 30000)",
			ddmFormFieldValidationExpression.getValue());

		Assert.assertEquals("numeric", ddmFormField.getType());
	}

	private void _assertURL(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertTrue(ddmFormField.isRequired());

		Assert.assertEquals("string", ddmFormField.getDataType());

		Map<String, Object> properties = ddmFormField.getProperties();

		Assert.assertTrue(properties.containsKey("placeholder"));

		Assert.assertEquals("text", ddmFormField.getType());
	}

	private void _assertUsername(DDMFormField ddmFormField) {
		Assert.assertNotNull(ddmFormField);

		Assert.assertEquals("string", ddmFormField.getDataType());

		Map<String, Object> properties = ddmFormField.getProperties();

		Assert.assertTrue(properties.containsKey("placeholder"));
		Assert.assertTrue(properties.containsKey("tooltip"));

		Assert.assertEquals("text", ddmFormField.getType());
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.values.factory;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRequestParameterRetriever;
import com.liferay.dynamic.data.mapping.form.values.factory.internal.DDMFormValuesFactoryImpl;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormValuesJSONSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PropsImpl;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Field;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Marcellus Tavares
 */
public class DDMFormValuesFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PropsUtil.setProps(new PropsImpl());
	}

	@Before
	public void setUp() throws Exception {
		setUpDDMFormValuesFactoryServiceTrackerMap();
		setUpDDMFormValuesJSONSerializer();
		setUpJSONFactoryUtil();

		_setUpLanguage();
		_setUpLanguageUtil();

		setUpLocaleThreadLocal();
	}

	@Test
	public void testCreateDefaultWithEmptyRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Name", true, false, false);

		LocalizedValue namePredefinedValue = createLocalizedValue(
			"Joe", LocaleUtil.US);

		nameDDMFormField.setPredefinedValue(namePredefinedValue);

		DDMFormField phoneDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Phone", true, false, false);

		LocalizedValue phonePredefinedValue = createLocalizedValue(
			"123", LocaleUtil.US);

		phoneDDMFormField.setPredefinedValue(phonePredefinedValue);

		nameDDMFormField.addNestedDDMFormField(phoneDDMFormField);

		ddmForm.addDDMFormField(nameDDMFormField);

		DDMFormValues expectedDDMFormValues = createDDMFormValues(
			ddmForm, createAvailableLocales(LocaleUtil.US), LocaleUtil.US);

		DDMFormFieldValue nameDDMFormFieldValue = createDDMFormFieldValue(
			"gatu", "Name", namePredefinedValue);

		nameDDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue("waht", "Phone", phonePredefinedValue));

		expectedDDMFormValues.addDDMFormFieldValue(nameDDMFormFieldValue);

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		List<DDMFormFieldValue> actualDDMFormFieldValues =
			actualDDMFormValues.getDDMFormFieldValues();

		// Name

		DDMFormFieldValue actualNameDDMFormFieldValue =
			actualDDMFormFieldValues.get(0);

		Value actualNameDDMFormFieldValueValue =
			actualNameDDMFormFieldValue.getValue();

		Assert.assertEquals(
			LocaleUtil.US, actualNameDDMFormFieldValueValue.getDefaultLocale());
		Assert.assertEquals(
			"Joe", actualNameDDMFormFieldValueValue.getString(LocaleUtil.US));

		// Phone

		List<DDMFormFieldValue> actualPhoneDDMFormFieldValues =
			actualNameDDMFormFieldValue.getNestedDDMFormFieldValues();

		DDMFormFieldValue actualPhoneDDMFormFieldValue =
			actualPhoneDDMFormFieldValues.get(0);

		Value actualPhoneDDMFormFieldValueValue =
			actualPhoneDDMFormFieldValue.getValue();

		Assert.assertEquals(
			LocaleUtil.US,
			actualPhoneDDMFormFieldValueValue.getDefaultLocale());
		Assert.assertEquals(
			"123", actualPhoneDDMFormFieldValueValue.getString(LocaleUtil.US));
	}

	@Test
	public void testCreateWithDifferentLanguageFromRequest() {
		LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.BRAZIL);

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			new HashSet<>(), LocaleUtil.getSiteDefault());

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"Name", false, false, false));

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Boolean", "Boolean", "checkbox", "boolean", false, false,
				false));

		DDMFormValues expectedDDMFormValues = createDDMFormValues(
			ddmForm, createAvailableLocales(LocaleUtil.US), LocaleUtil.US);

		expectedDDMFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"amay", "Name", new UnlocalizedValue("Joe")));

		expectedDDMFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"wqer", "Boolean", new UnlocalizedValue("true")));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"languageId", LocaleUtil.toLanguageId(LocaleUtil.US));

		mockHttpServletRequest.addParameter("ddm$$Name$amay$0$$pt_BR", "Joe");

		mockHttpServletRequest.addParameter(
			"ddm$$Boolean$wqer$0$$pt_BR", "true");

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		List<DDMFormFieldValue> actualDDMFormFieldValues =
			actualDDMFormValues.getDDMFormFieldValues();

		Assert.assertEquals(
			actualDDMFormFieldValues.toString(), 2,
			actualDDMFormFieldValues.size());

		assertEquals("Joe", actualDDMFormFieldValues.get(0), LocaleUtil.US);
		assertEquals("true", actualDDMFormFieldValues.get(1), LocaleUtil.US);
	}

	@Test
	public void testCreateWithLocalizableFields() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Title", "Content");

		Set<Locale> availableLocales = createAvailableLocales(
			LocaleUtil.BRAZIL, LocaleUtil.US);
		Locale defaultLocale = LocaleUtil.US;

		ddmForm.setAvailableLocales(availableLocales);
		ddmForm.setDefaultLocale(defaultLocale);

		DDMFormValues expectedDDMFormValues = createDDMFormValues(
			ddmForm, availableLocales, defaultLocale);

		expectedDDMFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"wqer", "Title",
				createLocalizedValue("Title", "Titulo", LocaleUtil.US)));
		expectedDDMFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"thsy", "Content",
				createLocalizedValue("Content", "Conteudo", LocaleUtil.US)));

		MockHttpServletRequest mockHttpServletRequest =
			createMockHttpServletRequest(defaultLocale, availableLocales);

		// Title

		mockHttpServletRequest.addParameter(
			"ddm$$Title$wqer$0$$en_US", "Title");
		mockHttpServletRequest.addParameter(
			"ddm$$Title$wqer$0$$pt_BR", "Titulo");

		// Content

		mockHttpServletRequest.addParameter(
			"ddm$$Content$thsy$0$$en_US", "Content");
		mockHttpServletRequest.addParameter(
			"ddm$$Content$thsy$0$$pt_BR", "Conteudo");

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		assertEquals(expectedDDMFormValues, actualDDMFormValues);
	}

	@Test
	public void testCreateWithRepeatableAndLocalizableField() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		Set<Locale> availableLocales = createAvailableLocales(
			LocaleUtil.BRAZIL, LocaleUtil.US);
		Locale defaultLocale = LocaleUtil.US;

		ddmForm.setAvailableLocales(availableLocales);
		ddmForm.setDefaultLocale(defaultLocale);

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"Title", true, true, false);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues expectedDDMFormValues = createDDMFormValues(
			ddmForm, availableLocales, defaultLocale);

		expectedDDMFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"wqer", "Title",
				createLocalizedValue("Title 1", "Titulo 1", defaultLocale)));
		expectedDDMFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"fahu", "Title",
				createLocalizedValue("Title 2", "Titulo 2", defaultLocale)));

		MockHttpServletRequest mockHttpServletRequest =
			createMockHttpServletRequest(defaultLocale, availableLocales);

		// Title

		mockHttpServletRequest.addParameter(
			"ddm$$Title$wqer$0$$en_US", "Title 1");
		mockHttpServletRequest.addParameter(
			"ddm$$Title$wqer$0$$pt_BR", "Titulo 1");
		mockHttpServletRequest.addParameter(
			"ddm$$Title$fahu$1$$en_US", "Title 2");
		mockHttpServletRequest.addParameter(
			"ddm$$Title$fahu$1$$pt_BR", "Titulo 2");

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		assertEquals(expectedDDMFormValues, actualDDMFormValues);
	}

	@Test
	public void testCreateWithRepeatableAndLocalizableNestedField()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		Set<Locale> availableLocales = createAvailableLocales(
			LocaleUtil.BRAZIL, LocaleUtil.US);
		Locale defaultLocale = LocaleUtil.US;

		ddmForm.setAvailableLocales(availableLocales);
		ddmForm.setDefaultLocale(defaultLocale);

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Name", true, true, false);

		DDMFormField phoneDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Phone", true, true, false);

		nameDDMFormField.addNestedDDMFormField(phoneDDMFormField);

		ddmForm.addDDMFormField(nameDDMFormField);

		DDMFormValues expectedDDMFormValues = createDDMFormValues(
			ddmForm, availableLocales, defaultLocale);

		DDMFormFieldValue paulDDMFormFieldValue = createDDMFormFieldValue(
			"wqer", "Name",
			createLocalizedValue("Paul", "Paulo", defaultLocale));

		paulDDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"gatu", "Phone",
				createLocalizedValue("12", "34", defaultLocale)));
		paulDDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"hato", "Phone",
				createLocalizedValue("56", "78", defaultLocale)));

		expectedDDMFormValues.addDDMFormFieldValue(paulDDMFormFieldValue);

		DDMFormFieldValue joeDDMFormFieldValue = createDDMFormFieldValue(
			"fahu", "Name", createLocalizedValue("Joe", "João", defaultLocale));

		joeDDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"jamh", "Phone",
				createLocalizedValue("90", "01", defaultLocale)));

		expectedDDMFormValues.addDDMFormFieldValue(joeDDMFormFieldValue);

		MockHttpServletRequest mockHttpServletRequest =
			createMockHttpServletRequest(defaultLocale, availableLocales);

		// Name

		mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$en_US", "Paul");
		mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$pt_BR", "Paulo");
		mockHttpServletRequest.addParameter("ddm$$Name$fahu$1$$en_US", "Joe");
		mockHttpServletRequest.addParameter("ddm$$Name$fahu$1$$pt_BR", "João");

		// Phone

		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$gatu$0$$en_US", "12");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$gatu$0$$pt_BR", "34");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$hato$1$$en_US", "56");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$hato$1$$pt_BR", "78");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$fahu$1#Phone$jamh$0$$en_US", "90");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$fahu$1#Phone$jamh$0$$pt_BR", "01");

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		assertEquals(expectedDDMFormValues, actualDDMFormValues);
	}

	@Test
	public void testCreateWithRepeatableAndLocalizableNestedFields()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		Set<Locale> availableLocales = createAvailableLocales(
			LocaleUtil.BRAZIL, LocaleUtil.US);
		Locale defaultLocale = LocaleUtil.US;

		ddmForm.setAvailableLocales(availableLocales);
		ddmForm.setDefaultLocale(defaultLocale);

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Name", true, true, false);

		DDMFormField text1DDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Text1", true, true, false);

		nameDDMFormField.addNestedDDMFormField(text1DDMFormField);

		DDMFormField text2DDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Text2", true, true, false);

		nameDDMFormField.addNestedDDMFormField(text2DDMFormField);

		ddmForm.addDDMFormField(nameDDMFormField);

		DDMFormValues expectedDDMFormValues = createDDMFormValues(
			ddmForm, availableLocales, defaultLocale);

		DDMFormFieldValue paulDDMFormFieldValue = createDDMFormFieldValue(
			"wqer", "Name",
			createLocalizedValue("Paul", "Paulo", defaultLocale));

		paulDDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"gatu", "Text1",
				createLocalizedValue(
					"Text1 Paul One", "Text1 Paulo Um", defaultLocale)));
		paulDDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"hayt", "Text1",
				createLocalizedValue(
					"Text1 Paul Two", "Text1 Paulo Dois", defaultLocale)));
		paulDDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"haby", "Text2",
				createLocalizedValue(
					"Text2 Paul One", "Text2 Paulo Um", defaultLocale)));
		paulDDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"makp", "Text2",
				createLocalizedValue(
					"Text2 Paul Two", "Text2 Paulo Dois", defaultLocale)));

		expectedDDMFormValues.addDDMFormFieldValue(paulDDMFormFieldValue);

		DDMFormFieldValue joeDDMFormFieldValue = createDDMFormFieldValue(
			"fahu", "Name", createLocalizedValue("Joe", "João", defaultLocale));

		joeDDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"banm", "Text1",
				createLocalizedValue(
					"Text1 Joe One", "Text1 João Um", defaultLocale)));
		joeDDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"bagj", "Text2",
				createLocalizedValue(
					"Text2 Joe One", "Text2 João Um", defaultLocale)));

		expectedDDMFormValues.addDDMFormFieldValue(joeDDMFormFieldValue);

		MockHttpServletRequest mockHttpServletRequest =
			createMockHttpServletRequest(defaultLocale, availableLocales);

		// Name

		mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$en_US", "Paul");
		mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$pt_BR", "Paulo");
		mockHttpServletRequest.addParameter("ddm$$Name$fahu$1$$en_US", "Joe");
		mockHttpServletRequest.addParameter("ddm$$Name$fahu$1$$pt_BR", "João");

		// Text 1

		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Text1$gatu$0$$en_US", "Text1 Paul One");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Text1$gatu$0$$pt_BR", "Text1 Paulo Um");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Text1$hayt$1$$en_US", "Text1 Paul Two");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Text1$hayt$1$$pt_BR", "Text1 Paulo Dois");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$fahu$1#Text1$banm$0$$en_US", "Text1 Joe One");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$fahu$1#Text1$banm$0$$pt_BR", "Text1 João Um");

		// Text 2

		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Text2$haby$0$$en_US", "Text2 Paul One");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Text2$haby$0$$pt_BR", "Text2 Paulo Um");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Text2$makp$1$$en_US", "Text2 Paul Two");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Text2$makp$1$$pt_BR", "Text2 Paulo Dois");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$fahu$1#Text2$bagj$0$$en_US", "Text2 Joe One");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$fahu$1#Text2$bagj$0$$pt_BR", "Text2 João Um");

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		assertEquals(expectedDDMFormValues, actualDDMFormValues);
	}

	@Test
	public void testCreateWithRepeatableAndUnlocalizableNestedFields()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		Set<Locale> availableLocales = createAvailableLocales(LocaleUtil.US);
		Locale defaultLocale = LocaleUtil.US;

		ddmForm.setAvailableLocales(availableLocales);
		ddmForm.setDefaultLocale(defaultLocale);

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Name", false, true, false);

		DDMFormField phoneDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Phone", false, true, false);

		DDMFormField extDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Ext", false, true, false);

		phoneDDMFormField.addNestedDDMFormField(extDDMFormField);

		nameDDMFormField.addNestedDDMFormField(phoneDDMFormField);

		ddmForm.addDDMFormField(nameDDMFormField);

		DDMFormValues expectedDDMFormValues = createDDMFormValues(ddmForm);

		DDMFormFieldValue paulDDMFormFieldValue = createDDMFormFieldValue(
			"wqer", "Name", new UnlocalizedValue("Paul"));

		DDMFormFieldValue paulPhone1DDMFormFieldValue = createDDMFormFieldValue(
			"gatu", "Phone", new UnlocalizedValue("1"));

		paulPhone1DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"jkau", "Ext", new UnlocalizedValue("1.1")));
		paulPhone1DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"amat", "Ext", new UnlocalizedValue("1.2")));

		paulDDMFormFieldValue.addNestedDDMFormFieldValue(
			paulPhone1DDMFormFieldValue);

		DDMFormFieldValue paulPhone2DDMFormFieldValue = createDDMFormFieldValue(
			"hato", "Phone", new UnlocalizedValue("2"));

		paulPhone2DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"hamp", "Ext", new UnlocalizedValue("2.1")));
		paulPhone2DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"xzal", "Ext", new UnlocalizedValue("2.2")));
		paulPhone2DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"kaly", "Ext", new UnlocalizedValue("2.3")));

		paulDDMFormFieldValue.addNestedDDMFormFieldValue(
			paulPhone2DDMFormFieldValue);

		expectedDDMFormValues.addDDMFormFieldValue(paulDDMFormFieldValue);

		DDMFormFieldValue joeDDMFormFieldValue = createDDMFormFieldValue(
			"fahu", "Name", new UnlocalizedValue("Joe"));

		DDMFormFieldValue joePhone1DDMFormFieldValue = createDDMFormFieldValue(
			"jakl", "Phone", new UnlocalizedValue("3"));

		joePhone1DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"bagt", "Ext", new UnlocalizedValue("3.1")));

		joeDDMFormFieldValue.addNestedDDMFormFieldValue(
			joePhone1DDMFormFieldValue);

		expectedDDMFormValues.addDDMFormFieldValue(joeDDMFormFieldValue);

		MockHttpServletRequest mockHttpServletRequest =
			createMockHttpServletRequest(defaultLocale, availableLocales);

		// Name

		mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$en_US", "Paul");
		mockHttpServletRequest.addParameter("ddm$$Name$fahu$1$$en_US", "Joe");

		// Phone

		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$gatu$0$$en_US", "1");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$hato$1$$en_US", "2");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$fahu$1#Phone$jakl$0$$en_US", "3");

		// Ext

		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$gatu$0#Ext$jkau$0$$en_US", "1.1");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$gatu$0#Ext$amat$1$$en_US", "1.2");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$hato$1#Ext$hamp$0$$en_US", "2.1");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$hato$1#Ext$xzal$1$$en_US", "2.2");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$wqer$0#Phone$hato$1#Ext$kaly$2$$en_US", "2.3");
		mockHttpServletRequest.addParameter(
			"ddm$$Name$fahu$1#Phone$jakl$0#Ext$bagt$0$$en_US", "3.1");

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		assertEquals(expectedDDMFormValues, actualDDMFormValues);
	}

	@Test
	public void testCreateWithRepeatableFieldSetAndNestedCheckbox() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"languageId", LocaleUtil.toLanguageId(LocaleUtil.US));

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField fieldSetDDMFormField = DDMFormTestUtil.createDDMFormField(
			"fieldset", "FieldSet", "fieldset", "", false, true, false);

		fieldSetDDMFormField.addNestedDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"text", "Text", "text", "string", false, false, false));

		DDMFormField checkboxDDMFormField = DDMFormTestUtil.createDDMFormField(
			"checkbox", "Checkbox", "checkbox", "boolean", false, false, false);

		LocalizedValue predefinedValue =
			checkboxDDMFormField.getPredefinedValue();

		predefinedValue.addString(LocaleUtil.US, "false");

		fieldSetDDMFormField.addNestedDDMFormField(checkboxDDMFormField);

		ddmForm.addDDMFormField(fieldSetDDMFormField);

		// Parameters

		mockHttpServletRequest.addParameter(
			"ddm$$fieldset$amay$0#text$mahy$0$$en_US", "Joe");
		mockHttpServletRequest.addParameter(
			"ddm$$fieldset$amay$0#checkbox$wqer$0$$en_US", "true");

		mockHttpServletRequest.addParameter(
			"ddm$$fieldset$mah7$1#text$kamy$0$$en_US", "Bob");

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		List<DDMFormFieldValue> actualDDMFormFieldValues =
			actualDDMFormValues.getDDMFormFieldValues();

		Assert.assertEquals(
			actualDDMFormFieldValues.toString(), 2,
			actualDDMFormFieldValues.size());

		DDMFormFieldValue fieldset1DDMFormFieldValue =
			actualDDMFormFieldValues.get(0);

		List<DDMFormFieldValue> fieldset1NestedDDMFormFieldValues =
			fieldset1DDMFormFieldValue.getNestedDDMFormFieldValues();

		assertEquals(
			"Joe", fieldset1NestedDDMFormFieldValues.get(0), LocaleUtil.US);
		assertEquals(
			"true", fieldset1NestedDDMFormFieldValues.get(1), LocaleUtil.US);

		DDMFormFieldValue fieldset2DDMFormFieldValue =
			actualDDMFormFieldValues.get(1);

		List<DDMFormFieldValue> fieldset2NestedDDMFormFieldValues =
			fieldset2DDMFormFieldValue.getNestedDDMFormFieldValues();

		assertEquals(
			"Bob", fieldset2NestedDDMFormFieldValues.get(0), LocaleUtil.US);
		assertEquals(
			"false", fieldset2NestedDDMFormFieldValues.get(1), LocaleUtil.US);
	}

	@Test
	public void testCreateWithRepeatableTransientParent() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		Set<Locale> availableLocales = createAvailableLocales(
			LocaleUtil.BRAZIL, LocaleUtil.US);
		Locale defaultLocale = LocaleUtil.US;

		ddmForm.setAvailableLocales(availableLocales);
		ddmForm.setDefaultLocale(defaultLocale);

		DDMFormField separatorDDMFormField = DDMFormTestUtil.createDDMFormField(
			"Separator", "Separator", DDMFormFieldType.SEPARATOR,
			StringPool.BLANK, false, true, false);

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Name", true, false, false);

		separatorDDMFormField.addNestedDDMFormField(nameDDMFormField);

		ddmForm.addDDMFormField(separatorDDMFormField);

		DDMFormValues expectedDDMFormValues = createDDMFormValues(
			ddmForm, availableLocales, defaultLocale);

		DDMFormFieldValue separator1DDMFormFieldValue = createDDMFormFieldValue(
			"wqer", "Separator", null);

		separator1DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"gatu", "Name",
				createLocalizedValue("Joe", "João", defaultLocale)));

		expectedDDMFormValues.addDDMFormFieldValue(separator1DDMFormFieldValue);

		DDMFormFieldValue separator2DDMFormFieldValue = createDDMFormFieldValue(
			"haby", "Separator", null);

		separator2DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"hato", "Name",
				createLocalizedValue("Paul", "Paulo", defaultLocale)));

		expectedDDMFormValues.addDDMFormFieldValue(separator2DDMFormFieldValue);

		DDMFormFieldValue separator3DDMFormFieldValue = createDDMFormFieldValue(
			"bajk", "Separator", null);

		separator3DDMFormFieldValue.addNestedDDMFormFieldValue(
			createDDMFormFieldValue(
				"fahu", "Name",
				createLocalizedValue("Claude", "Claudio", defaultLocale)));

		expectedDDMFormValues.addDDMFormFieldValue(separator3DDMFormFieldValue);

		MockHttpServletRequest mockHttpServletRequest =
			createMockHttpServletRequest(defaultLocale, availableLocales);

		// Name

		mockHttpServletRequest.addParameter(
			"ddm$$Separator$wqer$0#Name$gatu$0$$en_US", "Joe");
		mockHttpServletRequest.addParameter(
			"ddm$$Separator$wqer$0#Name$gatu$0$$pt_BR", "João");
		mockHttpServletRequest.addParameter(
			"ddm$$Separator$haby$1#Name$hato$0$$en_US", "Paul");
		mockHttpServletRequest.addParameter(
			"ddm$$Separator$haby$1#Name$hato$0$$pt_BR", "Paulo");
		mockHttpServletRequest.addParameter(
			"ddm$$Separator$bajk$2#Name$fahu$0$$en_US", "Claude");
		mockHttpServletRequest.addParameter(
			"ddm$$Separator$bajk$2#Name$fahu$0$$pt_BR", "Claudio");

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		assertEquals(expectedDDMFormValues, actualDDMFormValues);
	}

	@Test
	public void testCreateWithTextAndUncheckedCheckboxField() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"languageId", LocaleUtil.toLanguageId(LocaleUtil.US));

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"Name", false, false, false));

		DDMFormField checkboxDDMFormField = DDMFormTestUtil.createDDMFormField(
			"Boolean", "Boolean", "checkbox", "boolean", false, false, false);

		LocalizedValue predefinedValue =
			checkboxDDMFormField.getPredefinedValue();

		predefinedValue.addString(LocaleUtil.US, "false");

		ddmForm.addDDMFormField(checkboxDDMFormField);

		DDMFormValues expectedDDMFormValues = createDDMFormValues(
			ddmForm, createAvailableLocales(LocaleUtil.US), LocaleUtil.US);

		expectedDDMFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"amay", "Name", new UnlocalizedValue("Joe")));

		expectedDDMFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"wqer", "Boolean", new UnlocalizedValue("false")));

		// Name

		mockHttpServletRequest.addParameter("ddm$$Name$wqer$0$$en_US", "Joe");

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		List<DDMFormFieldValue> actualDDMFormFieldValues =
			actualDDMFormValues.getDDMFormFieldValues();

		Assert.assertEquals(
			actualDDMFormFieldValues.toString(), 2,
			actualDDMFormFieldValues.size());

		assertEquals("Joe", actualDDMFormFieldValues.get(0), LocaleUtil.US);
		assertEquals("false", actualDDMFormFieldValues.get(1), LocaleUtil.US);
	}

	@Test
	public void testCreateWithTransientField() {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Paragraph", "Paragraph", "paragraph", StringPool.BLANK, false,
				false, false));

		DDMFormValues ddmFormValues = _ddmFormValuesFactory.create(
			new MockHttpServletRequest(), ddmForm);

		List<DDMFormFieldValue> ddmFormFieldValues =
			ddmFormValues.getDDMFormFieldValues();

		Assert.assertEquals(
			ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Assert.assertEquals("Paragraph", ddmFormFieldValue.getName());
	}

	@Test
	public void testCreateWithUncheckedCheckboxAndTextFieldWithSimilarNames() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"languageId", LocaleUtil.toLanguageId(LocaleUtil.US));

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField checkboxDDMFormField = DDMFormTestUtil.createDDMFormField(
			"foo", "Foo", "checkbox", "boolean", false, false, false);

		LocalizedValue predefinedValue =
			checkboxDDMFormField.getPredefinedValue();

		predefinedValue.addString(LocaleUtil.US, "false");

		ddmForm.addDDMFormField(checkboxDDMFormField);

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"fooBar", "Foo Bar", false, false, false));

		DDMFormValues expectedDDMFormValues = createDDMFormValues(
			ddmForm, createAvailableLocales(LocaleUtil.US), LocaleUtil.US);

		expectedDDMFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"amay", "foo", new UnlocalizedValue("false")));

		expectedDDMFormValues.addDDMFormFieldValue(
			createDDMFormFieldValue(
				"wqer", "fooBar", new UnlocalizedValue("Baz")));

		// FooBar

		mockHttpServletRequest.addParameter("ddm$$fooBar$wqer$0$$en_US", "Baz");

		DDMFormValues actualDDMFormValues = _ddmFormValuesFactory.create(
			mockHttpServletRequest, ddmForm);

		List<DDMFormFieldValue> actualDDMFormFieldValues =
			actualDDMFormValues.getDDMFormFieldValues();

		Assert.assertEquals(
			actualDDMFormFieldValues.toString(), 2,
			actualDDMFormFieldValues.size());

		assertEquals("false", actualDDMFormFieldValues.get(0), LocaleUtil.US);
		assertEquals("Baz", actualDDMFormFieldValues.get(1), LocaleUtil.US);
	}

	protected void assertEquals(
			DDMFormValues expectedDDMFormValues,
			DDMFormValues actualDDMFormValues)
		throws Exception {

		String serializedExpectedDDMFormValues = serialize(
			expectedDDMFormValues);

		String serializedActualDDMFormValues = serialize(actualDDMFormValues);

		JSONAssert.assertEquals(
			serializedExpectedDDMFormValues, serializedActualDDMFormValues,
			false);
	}

	protected void assertEquals(
		String expectedValueString, DDMFormFieldValue actualDDMFormFieldValue,
		Locale locale) {

		Value actualValue = actualDDMFormFieldValue.getValue();

		Assert.assertEquals(expectedValueString, actualValue.getString(locale));
	}

	protected Set<Locale> createAvailableLocales(Locale... locales) {
		return DDMFormValuesTestUtil.createAvailableLocales(locales);
	}

	protected DDMFormFieldValue createDDMFormFieldValue(
		String instanceId, String name, Value value) {

		return DDMFormValuesTestUtil.createDDMFormFieldValue(
			instanceId, name, value);
	}

	protected DDMFormValues createDDMFormValues(DDMForm ddmForm) {
		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.addAvailableLocale(LocaleUtil.US);
		ddmFormValues.setDefaultLocale(LocaleUtil.US);

		return ddmFormValues;
	}

	protected DDMFormValues createDDMFormValues(
		DDMForm ddmForm, Set<Locale> availableLocales, Locale defaultLocale) {

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setAvailableLocales(availableLocales);
		ddmFormValues.setDefaultLocale(defaultLocale);

		return ddmFormValues;
	}

	protected LocalizedValue createLocalizedValue(
		String enValue, Locale defaultLocale) {

		return DDMFormValuesTestUtil.createLocalizedValue(
			enValue, defaultLocale);
	}

	protected LocalizedValue createLocalizedValue(
		String enValue, String ptValue, Locale defaultLocale) {

		return DDMFormValuesTestUtil.createLocalizedValue(
			enValue, ptValue, defaultLocale);
	}

	protected MockHttpServletRequest createMockHttpServletRequest(
		Locale defaultLocale, Set<Locale> availableLocales) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"availableLocales", getAvaiablesLocaleArray(availableLocales));

		mockHttpServletRequest.addParameter(
			"languageId", LocaleUtil.toLanguageId(defaultLocale));

		return mockHttpServletRequest;
	}

	protected String[] getAvaiablesLocaleArray(Set<Locale> availableLocales) {
		String[] avaiablesLocaleArray = new String[availableLocales.size()];

		int i = 0;

		for (Locale locale : availableLocales) {
			avaiablesLocaleArray[i] = LocaleUtil.toLanguageId(locale);
			i++;
		}

		return avaiablesLocaleArray;
	}

	protected String serialize(DDMFormValues ddmFormValues) {
		DDMFormValuesSerializerSerializeRequest.Builder builder =
			DDMFormValuesSerializerSerializeRequest.Builder.newBuilder(
				ddmFormValues);

		DDMFormValuesSerializerSerializeResponse
			ddmFormValuesSerializerSerializeResponse =
				_ddmFormValuesJSONSerializer.serialize(builder.build());

		return ddmFormValuesSerializerSerializeResponse.getContent();
	}

	protected void setUpDDMFormValuesFactoryServiceTrackerMap()
		throws Exception {

		Mockito.when(
			_serviceTrackerMap.containsKey(Mockito.anyString())
		).thenReturn(
			false
		);

		Field field = ReflectionUtil.getDeclaredField(
			_ddmFormValuesFactory.getClass(), "_serviceTrackerMap");

		field.set(_ddmFormValuesFactory, _serviceTrackerMap);
	}

	protected void setUpDDMFormValuesJSONSerializer() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_ddmFormValuesJSONSerializer, "_jsonFactory",
			new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_ddmFormValuesJSONSerializer, "_serviceTrackerMap",
			ProxyFactory.newDummyInstance(ServiceTrackerMap.class));
	}

	protected void setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	protected void setUpLocaleThreadLocal() {
		LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.US);
		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.BRAZIL);
	}

	private void _setUpLanguage() {
		Set<Locale> availableLocales = new HashSet<>(
			Arrays.asList(LocaleUtil.BRAZIL, LocaleUtil.US));

		Mockito.when(
			_language.getAvailableLocales(Mockito.anyLong())
		).thenReturn(
			availableLocales
		);

		Mockito.when(
			_language.getLanguageId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			"es_ES"
		);

		Mockito.when(
			_language.getLanguageId(LocaleUtil.BRAZIL)
		).thenReturn(
			"pt_BR"
		);

		_whenLanguageIsAvailableLocale(LocaleUtil.BRAZIL);
		_whenLanguageIsAvailableLocale(LocaleUtil.US);

		ReflectionTestUtil.setFieldValue(
			_ddmFormValuesFactory, "_language", _language);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);
	}

	private void _whenLanguageIsAvailableLocale(Locale locale) {
		Mockito.when(
			_language.isAvailableLocale(Mockito.eq(locale))
		).thenReturn(
			true
		);

		Mockito.when(
			_language.isAvailableLocale(
				Mockito.eq(LocaleUtil.toLanguageId(locale)))
		).thenReturn(
			true
		);
	}

	private final DDMFormValuesFactory _ddmFormValuesFactory =
		new DDMFormValuesFactoryImpl();
	private final DDMFormValuesJSONSerializer _ddmFormValuesJSONSerializer =
		new DDMFormValuesJSONSerializer();
	private final Language _language = Mockito.mock(Language.class);
	private final ServiceTrackerMap
		<String, DDMFormFieldValueRequestParameterRetriever>
			_serviceTrackerMap = Mockito.mock(ServiceTrackerMap.class);

}
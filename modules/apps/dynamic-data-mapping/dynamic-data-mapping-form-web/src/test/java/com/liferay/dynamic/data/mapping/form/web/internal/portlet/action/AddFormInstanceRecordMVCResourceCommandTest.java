/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializer;
import com.liferay.dynamic.data.mapping.form.builder.internal.context.DDMFormContextToDDMFormValues;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PropsImpl;

import jakarta.portlet.ResourceRequest;

import java.io.InputStream;

import java.util.Objects;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Rodrigo Paulino
 */
public class AddFormInstanceRecordMVCResourceCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_setUpDDMFormContextToDDMFormValues();

		_setUpAddFormInstanceRecordMVCResourceCommand();
		_setUpDDMFormInstance();
		_setUpPropsUtil();
		_setUpLanguage();
		_setUpLanguageUtil();
	}

	@Test
	public void testCreateDDMFormValues() throws Exception {
		String serializedDDMFormValues = _read("ddm-form-values.json");

		Mockito.when(
			_resourceRequest.getParameter("serializedDDMFormValues")
		).thenReturn(
			serializedDDMFormValues
		);

		Mockito.when(
			_language.getLanguageId(_resourceRequest)
		).thenReturn(
			"pt_BR"
		);

		Mockito.when(
			_language.isAvailableLocale(LocaleUtil.BRAZIL)
		).thenReturn(
			true
		);

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"TextField1", true, false, false));
		ddmForm.addDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				"TextField2", true, false, false));

		Mockito.when(
			_ddmStructure.getDDMForm()
		).thenReturn(
			ddmForm
		);

		LocalizedValue value1 = new LocalizedValue();

		value1.addString(LocaleUtil.BRAZIL, "Texto 1");

		DDMFormFieldValue ddmFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"eBvF8zup", "TextField1", value1);

		LocalizedValue value2 = new LocalizedValue();

		value2.addString(LocaleUtil.BRAZIL, "Texto 2");

		DDMFormFieldValue ddmFormFieldValue2 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"6VYYLvfJ", "TextField2", value2);

		DDMFormValues ddmFormValues1 =
			DDMFormValuesTestUtil.createDDMFormValues(
				ddmForm, SetUtil.fromArray(LocaleUtil.BRAZIL),
				LocaleUtil.BRAZIL);

		ddmFormValues1.addDDMFormFieldValue(ddmFormFieldValue1);
		ddmFormValues1.addDDMFormFieldValue(ddmFormFieldValue2);

		DDMFormValues ddmFormValues2 =
			_addFormInstanceRecordMVCResourceCommand.createDDMFormValues(
				_ddmFormInstance, _resourceRequest);

		Assert.assertNotEquals(LocaleUtil.getSiteDefault(), LocaleUtil.BRAZIL);
		Assert.assertTrue(Objects.equals(ddmFormValues1, ddmFormValues2));
	}

	private static void _setUpAddFormInstanceRecordMVCResourceCommand() {
		_addFormInstanceRecordMVCResourceCommand =
			new AddFormInstanceRecordMVCResourceCommand();

		ReflectionTestUtil.setFieldValue(
			_addFormInstanceRecordMVCResourceCommand,
			"_ddmFormBuilderContextToDDMFormValues",
			_ddmFormContextToDDMFormValues);
	}

	private static void _setUpDDMFormContextToDDMFormValues() {
		_ddmFormContextToDDMFormValues = new DDMFormContextToDDMFormValues();

		ReflectionTestUtil.setFieldValue(
			_ddmFormContextToDDMFormValues, "jsonFactory",
			new JSONFactoryImpl());
	}

	private static void _setUpDDMFormInstance() throws Exception {
		Mockito.when(
			_ddmFormInstance.getStructure()
		).thenReturn(
			_ddmStructure
		);
	}

	private static void _setUpLanguage() {
		ReflectionTestUtil.setFieldValue(
			_addFormInstanceRecordMVCResourceCommand, "_language", _language);
	}

	private static void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);
	}

	private static void _setUpPropsUtil() {
		PropsUtil.setProps(new PropsImpl());
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	private static AddFormInstanceRecordMVCResourceCommand
		_addFormInstanceRecordMVCResourceCommand;
	private static DDMFormContextDeserializer<DDMFormValues>
		_ddmFormContextToDDMFormValues;
	private static final DDMFormInstance _ddmFormInstance = Mockito.mock(
		DDMFormInstance.class);
	private static final DDMStructure _ddmStructure = Mockito.mock(
		DDMStructure.class);
	private static final Language _language = Mockito.mock(Language.class);

	private final ResourceRequest _resourceRequest = Mockito.mock(
		ResourceRequest.class);

}
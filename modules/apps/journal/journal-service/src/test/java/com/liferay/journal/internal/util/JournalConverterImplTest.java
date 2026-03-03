/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class JournalConverterImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testImageFieldDefaultValueSkipped() throws Exception {
		ReflectionTestUtil.setFieldValue(
			LanguageUtil.class, "_language", new LanguageImpl());

		JournalConverterImpl journalConverterImpl = new JournalConverterImpl();

		String imageFieldName = RandomTestUtil.randomString();

		DDMFormField ddmFormField = new DDMFormField(
			imageFieldName, DDMFormFieldTypeConstants.IMAGE);

		ddmFormField.setLocalizable(true);

		LocalizedValue predefinedValue = new LocalizedValue(LocaleUtil.US);

		predefinedValue.addString(LocaleUtil.US, "{}");
		predefinedValue.addString(LocaleUtil.GERMANY, "{}");

		ddmFormField.setPredefinedValue(predefinedValue);

		DDMStructure ddmStructure = Mockito.mock(DDMStructure.class);

		Mockito.when(
			ddmStructure.getStructureId()
		).thenReturn(
			1L
		);

		Mockito.when(
			ddmStructure.getFieldProperty(imageFieldName, "localizable")
		).thenReturn(
			"true"
		);

		Mockito.when(
			ddmStructure.getDDMFormField(imageFieldName)
		).thenReturn(
			ddmFormField
		);

		Element root = _createRootElement();

		Element dynamicElement = root.addElement("dynamic-element");

		dynamicElement.addAttribute("name", imageFieldName);

		Element germanContent = dynamicElement.addElement("dynamic-content");

		germanContent.addAttribute("language-id", "de_DE");
		germanContent.addCDATA("{}");

		Field field = ReflectionTestUtil.invoke(
			journalConverterImpl, "_getField",
			new Class<?>[] {
				String[].class, DDMStructure.class, String.class, Element.class,
				String.class
			},
			new String[] {"en_US", "de_DE"}, ddmStructure, "en_US",
			dynamicElement, imageFieldName);

		Assert.assertFalse(
			field.getAvailableLocales(
			).contains(
				LocaleUtil.GERMANY
			));

		Assert.assertTrue(
			field.getAvailableLocales(
			).isEmpty());
	}

	@Test
	public void testUpdateContentDynamicElement() {
		_testUpdateContentDynamicElement(StringPool.BLANK, null);

		String value = RandomTestUtil.randomString();

		_testUpdateContentDynamicElement(value, value);

		_testUpdateContentDynamicElementWithCheckBox();
		_testUpdateContentDynamicElementWithOptions();
	}

	private void _assertDynamicContentElement(
		Element dynamicContentElement, List<String> expectedReferences) {

		List<Element> optionReferenceElements = dynamicContentElement.elements(
			"option-reference");

		if (ListUtil.isEmpty(expectedReferences)) {
			Assert.assertTrue(
				"Expected no option references, but found: " +
					optionReferenceElements,
				optionReferenceElements.isEmpty());

			return;
		}

		Assert.assertEquals(
			optionReferenceElements.toString(), expectedReferences.size(),
			optionReferenceElements.size());

		for (int i = 0; i < expectedReferences.size(); i++) {
			Element optionReferenceElement = optionReferenceElements.get(i);

			Assert.assertEquals(
				expectedReferences.get(i), optionReferenceElement.getText());
		}
	}

	private DDMFormField _createDDMFormField(
		String dataType, boolean localizable, String name, String type) {

		DDMFormField ddmFormField = new DDMFormField(name, type);

		ddmFormField.setDataType(dataType);
		ddmFormField.setLocalizable(localizable);

		return ddmFormField;
	}

	private Element _createRootElement() {
		SAXReaderImpl saxReaderImpl = new SAXReaderImpl();

		Document document = saxReaderImpl.createDocument();

		return document.addElement("root");
	}

	private void _testUpdateContentDynamicElement(
		String expectedValue, String value) {

		DDMFormField ddmFormField = _createDDMFormField(
			"string", true, "field1", "text");

		Element rootElement = _createRootElement();

		Field field = new Field(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(), value);

		ReflectionTestUtil.invoke(
			new JournalConverterImpl(), "_updateContentDynamicElement",
			new Class<?>[] {
				int.class, DDMFormField.class, Element.class, Field.class
			},
			0, ddmFormField, rootElement, field);

		Assert.assertEquals(expectedValue, rootElement.getStringValue());
	}

	private void _testUpdateContentDynamicElementWithCheckBox() {
		List<List<String>> optionReferences = new ArrayList<>();
		Element rootElement = _createRootElement();

		_updateContentDynamicElement(optionReferences, true, rootElement);
		_updateContentDynamicElement(optionReferences, false, rootElement);

		List<Element> dynamicContentElements = rootElement.elements(
			"dynamic-content");

		Assert.assertEquals(
			dynamicContentElements.toString(), 2,
			dynamicContentElements.size());

		_assertDynamicContentElement(
			dynamicContentElements.get(0), optionReferences.get(0));
		_assertDynamicContentElement(dynamicContentElements.get(1), null);
	}

	private void _testUpdateContentDynamicElementWithOptions() {
		JournalConverterImpl journalConverterImpl = new JournalConverterImpl();

		ReflectionTestUtil.setFieldValue(
			journalConverterImpl, "_jsonFactory", new JSONFactoryImpl());

		List<String> optionReferences = new ArrayList<>();
		Element rootElement = _createRootElement();

		_updateContentDynamicElement(
			"field_0", "select", journalConverterImpl, true, optionReferences,
			rootElement);
		_updateContentDynamicElement(
			"field_1", "select", journalConverterImpl, false, optionReferences,
			rootElement);
		_updateContentDynamicElement(
			"field_2", "radio", journalConverterImpl, null, optionReferences,
			rootElement);

		List<Element> dynamicContentElements = rootElement.elements(
			"dynamic-content");

		Assert.assertEquals(
			dynamicContentElements.toString(), 3,
			dynamicContentElements.size());

		_assertDynamicContentElement(
			dynamicContentElements.get(0),
			Collections.singletonList(optionReferences.get(0)));
		_assertDynamicContentElement(
			dynamicContentElements.get(1),
			Collections.singletonList(optionReferences.get(1)));
		_assertDynamicContentElement(
			dynamicContentElements.get(2),
			Collections.singletonList(optionReferences.get(2)));
	}

	private void _updateContentDynamicElement(
		List<List<String>> allOptionReferences, boolean multiple,
		Element rootElement) {

		JournalConverterImpl journalConverterImpl = new JournalConverterImpl();

		ReflectionTestUtil.setFieldValue(
			journalConverterImpl, "_jsonFactory", new JSONFactoryImpl());

		DDMFormField ddmFormField = _createDDMFormField(
			"string", true, "checkbox_" + multiple, "checkbox_multiple");

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();
		List<String> optionReferences = new ArrayList<>();
		List<String> optionValues = new ArrayList<>();

		int optionCount = 1;

		if (multiple) {
			optionCount = 2;
		}

		for (int i = 0; i < optionCount; i++) {
			String value = "Option" + RandomTestUtil.randomString();

			ddmFormFieldOptions.addOption(value);
			ddmFormFieldOptions.addOptionLabel(
				value, LocaleUtil.US, RandomTestUtil.randomString());

			String optionReference = RandomTestUtil.randomString();

			ddmFormFieldOptions.addOptionReference(value, optionReference);

			optionReferences.add(optionReference);

			optionValues.add(value);
		}

		allOptionReferences.add(optionReferences);
		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		Serializable fieldValue = optionValues.get(0);

		if (multiple) {
			fieldValue = JSONUtil.putAll(
				optionValues.toArray()
			).toString();
		}

		Field field = new Field(
			RandomTestUtil.randomLong(), ddmFormField.getName(),
			HashMapBuilder.put(
				LocaleUtil.US, ListUtil.fromArray(fieldValue)
			).build(),
			LocaleUtil.US);

		ReflectionTestUtil.invoke(
			journalConverterImpl, "_updateContentDynamicElement",
			new Class<?>[] {
				int.class, DDMFormField.class, Element.class, Field.class
			},
			0, ddmFormField, rootElement, field);
	}

	private void _updateContentDynamicElement(
		String fieldName, String fieldType,
		JournalConverterImpl journalConverterImpl, Boolean multiple,
		List<String> optionReferences, Element rootElement) {

		DDMFormField ddmFormField = _createDDMFormField(
			"string", true, fieldName, fieldType);

		if (fieldType.equals("select") && (multiple != null)) {
			ddmFormField.setMultiple(multiple);
		}

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		String value = RandomTestUtil.randomString();

		ddmFormFieldOptions.addOption(value);
		ddmFormFieldOptions.addOptionLabel(
			value, LocaleUtil.US, RandomTestUtil.randomString());

		String optionReference = RandomTestUtil.randomString();

		ddmFormFieldOptions.addOptionReference(value, optionReference);

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		optionReferences.add(optionReference);

		Serializable fieldValue = value;

		if (fieldType.equals("select")) {
			fieldValue = JSONUtil.put(
				value
			).toString();
		}

		Field field = new Field(
			RandomTestUtil.randomLong(), ddmFormField.getName(),
			HashMapBuilder.put(
				LocaleUtil.US, ListUtil.fromArray(fieldValue)
			).build(),
			LocaleUtil.US);

		ReflectionTestUtil.invoke(
			journalConverterImpl, "_updateContentDynamicElement",
			new Class<?>[] {
				int.class, DDMFormField.class, Element.class, Field.class
			},
			0, ddmFormField, rootElement, field);
	}

}
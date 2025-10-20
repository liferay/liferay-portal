/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jürgen Kappler
 */
public class JournalConverterImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testUpdateContentDynamicElement() {
		_testUpdateContentDynamicElement(StringPool.BLANK, null);

		String value = RandomTestUtil.randomString();

		_testUpdateContentDynamicElement(value, value);

		_testUpdateContentDynamicElementWithOptions();

		try {
			_testUpdateContentDynamicElementWithCheckBox();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private DDMFormField _createDDMFormField(
		String dataType, boolean localizable, String name, String type) {

		DDMFormField ddmFormField = new DDMFormField(name, type);

		ddmFormField.setDataType(dataType);
		ddmFormField.setLocalizable(localizable);

		return ddmFormField;
	}

	private void _testUpdateContentDynamicElement(
		String expectedValue, String value) {

		DDMFormField ddmFormField = _createDDMFormField(
			"string", true, "field1", "text");

		SAXReaderImpl saxReaderImpl = new SAXReaderImpl();

		Document document = saxReaderImpl.createDocument();

		Element rootElement = document.addElement("root");

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
		JournalConverterImpl journalConverterImpl = new JournalConverterImpl();

		ReflectionTestUtil.setFieldValue(
			journalConverterImpl, "_jsonFactory", new JSONFactoryImpl());

		SAXReaderImpl saxReaderImpl = new SAXReaderImpl();

		Document document = saxReaderImpl.createDocument();

		Element rootElement = document.addElement("root");

		boolean[] multipleFlags = {true, false};
		List<List<String>> allOptionReferences = new ArrayList<>();

		for (boolean multiple : multipleFlags) {
			DDMFormField ddmFormField = _createDDMFormField(
				"string", true, "checkbox_" + multiple, "checkbox_multiple");

			DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

			List<String> optionValues = new ArrayList<>();
			List<String> optionReferences = new ArrayList<>();

			for (int j = 0; j < (multiple ? 2 : 1); j++) {
				String value = "Option" + RandomTestUtil.randomString();
				String optionReference = RandomTestUtil.randomString();

				optionValues.add(value);
				optionReferences.add(optionReference);

				ddmFormFieldOptions.addOption(value);
				ddmFormFieldOptions.addOptionLabel(
					value, LocaleUtil.US, RandomTestUtil.randomString());
				ddmFormFieldOptions.addOptionReference(value, optionReference);
			}

			ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);
			allOptionReferences.add(optionReferences);

			Serializable fieldValue;

			if (multiple) {
				fieldValue = JSONUtil.putAll(
					optionValues.toArray()
				).toString();
			}
			else {
				fieldValue = optionValues.get(0);
			}

			ReflectionTestUtil.invoke(
				journalConverterImpl, "_updateContentDynamicElement",
				new Class<?>[] {
					int.class, DDMFormField.class, Element.class, Field.class
				},
				0, ddmFormField, rootElement,
				new Field(
					RandomTestUtil.randomLong(), ddmFormField.getName(),
					HashMapBuilder.<Locale, List<Serializable>>put(
						LocaleUtil.US, ListUtil.fromArray(fieldValue)
					).build(),
					LocaleUtil.US));
		}

		List<Element> dynamicContentElements = rootElement.elements(
			"dynamic-content");

		Assert.assertEquals(
			dynamicContentElements.toString(), 2,
			dynamicContentElements.size());

		for (int i = 0; i < dynamicContentElements.size(); i++) {
			Element dynamicContentElement = dynamicContentElements.get(i);

			List<Element> optionReferenceElements =
				dynamicContentElement.elements("option-reference");

			if (i == 0) {
				Assert.assertEquals(
					optionReferenceElements.toString(), 2,
					optionReferenceElements.size());

				for (int j = 0; j < optionReferenceElements.size(); j++) {
					Assert.assertEquals(
						allOptionReferences.get(
							i
						).get(
							j
						),
						optionReferenceElements.get(
							j
						).getText());
				}
			}
			else {
				Assert.assertTrue(optionReferenceElements.isEmpty());
			}
		}
	}

	private void _testUpdateContentDynamicElementWithOptions() {
		JournalConverterImpl journalConverterImpl = new JournalConverterImpl();

		ReflectionTestUtil.setFieldValue(
			journalConverterImpl, "_jsonFactory", new JSONFactoryImpl());

		SAXReaderImpl saxReaderImpl = new SAXReaderImpl();

		Document document = saxReaderImpl.createDocument();

		Element rootElement = document.addElement("root");

		String[] fieldTypes = {"select", "select", "radio"};
		boolean[] multipleFlags = {true, false};

		List<String> optionReferences = new ArrayList<>();

		for (int i = 0; i < fieldTypes.length; i++) {
			String fieldType = fieldTypes[i];
			String fieldName = "field_" + i;

			DDMFormField ddmFormField = _createDDMFormField(
				"string", true, fieldName, fieldType);

			if (fieldType.equals("select")) {
				ddmFormField.setMultiple(multipleFlags[i]);
			}

			DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

			String value = RandomTestUtil.randomString();

			String optionReference = RandomTestUtil.randomString();

			optionReferences.add(optionReference);

			ddmFormFieldOptions.addOption(value);
			ddmFormFieldOptions.addOptionLabel(
				value, LocaleUtil.US, RandomTestUtil.randomString());

			ddmFormFieldOptions.addOptionReference(value, optionReference);

			ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

			Serializable fieldValue;

			if (fieldType.equals("select")) {
				fieldValue = JSONUtil.put(
					value
				).toString();
			}
			else {
				fieldValue = value;
			}

			ReflectionTestUtil.invoke(
				journalConverterImpl, "_updateContentDynamicElement",
				new Class<?>[] {
					int.class, DDMFormField.class, Element.class, Field.class
				},
				0, ddmFormField, rootElement,
				new Field(
					RandomTestUtil.randomLong(), ddmFormField.getName(),
					HashMapBuilder.<Locale, List<Serializable>>put(
						LocaleUtil.US, ListUtil.fromArray(fieldValue)
					).build(),
					LocaleUtil.US));
		}

		List<Element> dynamicContentElements = rootElement.elements(
			"dynamic-content");

		Assert.assertEquals(
			dynamicContentElements.toString(), 3,
			dynamicContentElements.size());

		for (int i = 0; i < dynamicContentElements.size(); i++) {
			Element dynamicContentElement = dynamicContentElements.get(i);

			List<Element> optionReferenceElements =
				dynamicContentElement.elements("option-reference");

			Assert.assertEquals(
				optionReferenceElements.toString() + fieldTypes[i], 1,
				optionReferenceElements.size());

			Element optionReferenceElement = optionReferenceElements.get(0);

			Assert.assertEquals(
				optionReferences.get(i), optionReferenceElement.getText());
		}
	}

}
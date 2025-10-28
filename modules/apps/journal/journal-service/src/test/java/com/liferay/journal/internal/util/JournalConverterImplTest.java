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

		_testUpdateContentDynamicElementWithCheckBox();
		_testUpdateContentDynamicElementWithOptions();

	}

	private void _assertDynamicContentElement(
		Boolean multiple, Element dynamicContentElement,
		List<String> expectedReferences) {

		List<Element> optionReferenceElements = dynamicContentElement.elements(
			"option-reference");

		if (multiple) {
			Assert.assertTrue(optionReferenceElements.isEmpty());
		}
		else {
			Assert.assertEquals(
				optionReferenceElements.toString(), expectedReferences.size(),
				optionReferenceElements.size());

			for (int i = 0; i < expectedReferences.size(); i++) {
				Assert.assertEquals(
					expectedReferences.get(i),
					optionReferenceElements.get(
						i
					).getText());
			}
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
		Element rootElement = _createRootElement();
		List<List<String>> allOptionReferences = new ArrayList<>();

		_updateContentDynamicElement(true, rootElement, allOptionReferences);
		_updateContentDynamicElement(false, rootElement, allOptionReferences);

		List<Element> dynamicContentElements = rootElement.elements(
			"dynamic-content");

		Assert.assertEquals(
			dynamicContentElements.toString(), 2,
			dynamicContentElements.size());

		_assertDynamicContentElement(
			false, dynamicContentElements.get(0), allOptionReferences.get(0));
		_assertDynamicContentElement(true, dynamicContentElements.get(1), null);
	}

	private void _testUpdateContentDynamicElementWithOptions() {
		JournalConverterImpl journalConverterImpl = new JournalConverterImpl();

		ReflectionTestUtil.setFieldValue(
			journalConverterImpl, "_jsonFactory", new JSONFactoryImpl());

		Element rootElement = _createRootElement();

		List<String> optionReferences = new ArrayList<>();

		_updateContentDynamicElement(
			journalConverterImpl, rootElement, "select", true, "field_0",
			optionReferences);
		_updateContentDynamicElement(
			journalConverterImpl, rootElement, "select", false, "field_1",
			optionReferences);
		_updateContentDynamicElement(
			journalConverterImpl, rootElement, "radio", null, "field_2",
			optionReferences);

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
				optionReferenceElements.toString(), 1,
				optionReferenceElements.size());

			Element optionReferenceElement = optionReferenceElements.get(0);

			Assert.assertEquals(
				optionReferences.get(i), optionReferenceElement.getText());
		}
	}

	private void _updateContentDynamicElement(
		boolean multiple, Element rootElement,
		List<List<String>> allOptionReferences) {

		JournalConverterImpl journalConverterImpl = new JournalConverterImpl();

		ReflectionTestUtil.setFieldValue(
			journalConverterImpl, "_jsonFactory", new JSONFactoryImpl());

		DDMFormField ddmFormField = _createDDMFormField(
			"string", true, "checkbox_" + multiple, "checkbox_multiple");

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();
		List<String> optionValues = new ArrayList<>();
		List<String> optionReferences = new ArrayList<>();

		int optionCount = 1;

		if (multiple) {
			optionCount = 2;
		}

		for (int i = 0; i < optionCount; i++) {
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
		JournalConverterImpl journalConverterImpl, Element rootElement,
		String fieldType, Boolean multiple, String fieldName,
		List<String> optionReferences) {

		DDMFormField ddmFormField = _createDDMFormField(
			"string", true, fieldName, fieldType);

		if (fieldType.equals("select") && (multiple != null)) {
			ddmFormField.setMultiple(multiple);
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
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class JournalConverterImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_originalLanguage = LanguageUtil.getLanguage();
		_originalSAXReader = SAXReaderUtil.getSAXReader();
		_originalUnsecureSAXReader = UnsecureSAXReaderUtil.getSAXReader();

		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.isAvailableLanguageCode(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			language.isAvailableLocale(Mockito.any(Locale.class))
		).thenReturn(
			true
		);

		languageUtil.setLanguage(language);

		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		SAXReaderImpl secureSAXReaderImpl = new SAXReaderImpl();

		secureSAXReaderImpl.setSecure(true);

		saxReaderUtil.setSAXReader(secureSAXReaderImpl);

		UnsecureSAXReaderUtil unsecureSAXReaderUtil =
			new UnsecureSAXReaderUtil();

		unsecureSAXReaderUtil.setSAXReader(new SAXReaderImpl());

		_mockedStatic = Mockito.mockStatic(DDMStructureLocalServiceUtil.class);
	}

	@AfterClass
	public static void tearDownClass() {
		_mockedStatic.close();

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_originalLanguage);

		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		saxReaderUtil.setSAXReader(_originalSAXReader);

		UnsecureSAXReaderUtil unsecureSAXReaderUtil =
			new UnsecureSAXReaderUtil();

		unsecureSAXReaderUtil.setSAXReader(_originalUnsecureSAXReader);
	}

	@Test
	public void testGetDDMFields() throws Exception {
		JournalConverter journalConverter = new JournalConverterImpl();

		DDMStructure ddmStructure = Mockito.mock(DDMStructure.class);

		Mockito.when(
			ddmStructure.getStructureId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			DDMStructureLocalServiceUtil.fetchStructure(Mockito.anyLong())
		).thenReturn(
			ddmStructure
		);

		DDMForm ddmForm = new DDMForm();

		Mockito.when(
			ddmStructure.getDDMForm()
		).thenReturn(
			ddmForm
		);

		int leafCount = RandomTestUtil.randomInt(10, 50);

		StringBundler contentSB = new StringBundler(3 + (leafCount * 9));

		contentSB.append("<?xml version=\"1.0\"?><root available-locales=");
		contentSB.append("\"en_US\" default-locale=\"en_US\">");

		StringBundler expectedSB = new StringBundler((leafCount * 4) - 1);

		for (int i = 0; i < leafCount; i++) {
			String name = "text" + i;
			String instanceId = "instance" + i;

			DDMFormField ddmFormField = _createDDMFormField(
				"string", true, name, "text");

			ddmForm.addDDMFormField(ddmFormField);

			Mockito.when(
				ddmStructure.getDDMFormField(name)
			).thenReturn(
				ddmFormField
			);

			contentSB.append("<dynamic-element instance-id=\"");
			contentSB.append(instanceId);
			contentSB.append("\" name=\"");
			contentSB.append(name);
			contentSB.append("\" type=\"text\"><dynamic-content language-id=");
			contentSB.append("\"en_US\"><![CDATA[");
			contentSB.append(RandomTestUtil.randomString());
			contentSB.append("]]></dynamic-content>");
			contentSB.append("</dynamic-element>");

			if (i > 0) {
				expectedSB.append(StringPool.COMMA);
			}

			expectedSB.append(name);
			expectedSB.append(DDM.INSTANCE_SEPARATOR);
			expectedSB.append(instanceId);
		}

		contentSB.append("</root>");

		Assert.assertEquals(
			expectedSB.toString(),
			_getFieldsDisplayValue(
				journalConverter.getDDMFields(
					ddmStructure, contentSB.toString())));
	}

	@Test
	public void testUpdateContentDynamicElement() {
		_testUpdateContentDynamicElement(StringPool.BLANK, null);

		String value = RandomTestUtil.randomString();

		_testUpdateContentDynamicElement(value, value);

		_testUpdateContentDynamicElementWithCheckBox();
		_testUpdateContentDynamicElementWithOptions();
	}

	@Test
	@TestInfo("LPS-95441")
	public void testUpdateContentDynamicElementWithInvalidJSONSelectValue() {
		JournalConverterImpl journalConverterImpl = new JournalConverterImpl();

		ReflectionTestUtil.setFieldValue(
			journalConverterImpl, "_jsonFactory", new JSONFactoryImpl());

		DDMFormField ddmFormField = _createDDMFormField(
			"string", true, "webContent", "select");

		Element rootElement = _createRootElement();

		Field field = new Field(
			RandomTestUtil.randomLong(), ddmFormField.getName(),
			HashMapBuilder.put(
				LocaleUtil.US, ListUtil.fromArray((Serializable)"TestName")
			).build(),
			LocaleUtil.US);

		ReflectionTestUtil.invoke(
			journalConverterImpl, "_updateContentDynamicElement",
			new Class<?>[] {
				int.class, DDMFormField.class, Element.class, Field.class
			},
			0, ddmFormField, rootElement, field);

		List<Element> dynamicContentElements = rootElement.elements(
			"dynamic-content");

		Assert.assertEquals(
			dynamicContentElements.toString(), 1,
			dynamicContentElements.size());

		Element dynamicContentElement = dynamicContentElements.get(0);

		Assert.assertEquals(
			StringPool.BLANK, dynamicContentElement.getStringValue());

		_assertDynamicContentElement(dynamicContentElement, null);
	}

	private void _assertDynamicContentElement(
		Element dynamicContentElement, List<String> expectedReferences) {

		List<Element> optionReferenceElements = dynamicContentElement.elements(
			"option-reference");

		if (ListUtil.isEmpty(expectedReferences)) {
			Assert.assertTrue(optionReferenceElements.isEmpty());

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

	private String _getFieldsDisplayValue(Fields ddmFields) {
		Field fieldsDisplayField = ddmFields.get(DDM.FIELDS_DISPLAY_NAME);

		List<Serializable> values = fieldsDisplayField.getValues(
			LocaleUtil.getSiteDefault());

		return (String)values.get(0);
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

	private static MockedStatic<DDMStructureLocalServiceUtil> _mockedStatic;
	private static Language _originalLanguage;
	private static SAXReader _originalSAXReader;
	private static SAXReader _originalUnsecureSAXReader;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.xml.SAXReaderImpl;

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

	private void _testUpdateContentDynamicElementWithOptions() {
		DDMFormField ddmFormField = _createDDMFormField(
			"string", true, "field2", "text");

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		String optionLabel1 = RandomTestUtil.randomString();
		String optionReference1 = RandomTestUtil.randomString();
		String optionValue1 = RandomTestUtil.randomString();

		ddmFormFieldOptions.addOption(optionValue1);
		ddmFormFieldOptions.addOptionLabel(
			optionValue1, LocaleUtil.US, optionLabel1);
		ddmFormFieldOptions.addOptionReference(optionValue1, optionReference1);

		String optionLabel2 = RandomTestUtil.randomString();
		String optionValue2 = RandomTestUtil.randomString();
		String optionReference2 = RandomTestUtil.randomString();

		ddmFormFieldOptions.addOption(optionValue2);

		ddmFormFieldOptions.addOptionLabel(
			optionValue2, LocaleUtil.US, optionLabel2);
		ddmFormFieldOptions.addOptionReference(optionValue2, optionReference2);

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		SAXReaderImpl saxReaderImpl = new SAXReaderImpl();

		Document document = saxReaderImpl.createDocument();

		Element rootElement = document.addElement("root");

		Field field = new Field(
			RandomTestUtil.randomLong(), ddmFormField.getName(), optionValue2);

		ReflectionTestUtil.invoke(
			new JournalConverterImpl(), "_updateContentDynamicElement",
			new Class<?>[] {
				int.class, DDMFormField.class, Element.class, Field.class
			},
			0, ddmFormField, rootElement, field);

		String xmlValue = ddmFormFieldOptions.getOptionReference(
			rootElement.getStringValue());

		Assert.assertEquals( optionReference2, xmlValue);
	}

}
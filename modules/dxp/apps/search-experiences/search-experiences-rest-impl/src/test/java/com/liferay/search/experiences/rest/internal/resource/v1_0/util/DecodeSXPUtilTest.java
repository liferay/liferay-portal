/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.internal.resource.v1_0.util;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.search.experiences.rest.dto.v1_0.ElementDefinition;
import com.liferay.search.experiences.rest.dto.v1_0.Field;
import com.liferay.search.experiences.rest.dto.v1_0.FieldSet;
import com.liferay.search.experiences.rest.dto.v1_0.SXPElement;
import com.liferay.search.experiences.rest.dto.v1_0.UiConfiguration;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Selena Aungst
 */
public class DecodeSXPUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDecodeSXPElement() throws Exception {
		_testDecodeSXPElement(
			Arrays.asList(
				_createEntry(_CLASS_NAME_BLOGS_ENTRY),
				_createEntry(_CLASS_NAME_JOURNAL_ARTICLE)));
		_testDecodeSXPElement(
			new Object[] {
				_createEntry(_CLASS_NAME_BLOGS_ENTRY),
				_createEntry(_CLASS_NAME_JOURNAL_ARTICLE)
			});
	}

	private Map<String, String> _createEntry(String className) {
		return HashMapBuilder.put(
			_LABEL, className
		).put(
			_VALUE, className
		).build();
	}

	private void _testDecodeSXPElement(Object defaultValue) throws Exception {
		Field field = new Field();

		field.setDefaultValue(defaultValue);
		field.setLabel("Entry Class Names");
		field.setName("entry_class_names");
		field.setType("multiselect");

		FieldSet fieldSet = new FieldSet();

		fieldSet.setFields(new Field[] {field});

		UiConfiguration uiConfiguration = new UiConfiguration();

		uiConfiguration.setFieldSets(new FieldSet[] {fieldSet});

		ElementDefinition elementDefinition = new ElementDefinition();

		elementDefinition.setUiConfiguration(uiConfiguration);

		SXPElement sxpElement = new SXPElement();

		sxpElement.setElementDefinition(elementDefinition);

		DecodeSXPUtil.decodeSXPElement(sxpElement);

		ElementDefinition decodedElementDefinition =
			sxpElement.getElementDefinition();

		UiConfiguration decodedUiConfiguration =
			decodedElementDefinition.getUiConfiguration();

		FieldSet[] decodedFieldSets = decodedUiConfiguration.getFieldSets();

		Assert.assertEquals(
			Arrays.toString(decodedFieldSets), 1, decodedFieldSets.length);

		Field[] decodedFields = decodedFieldSets[0].getFields();

		Assert.assertEquals(
			Arrays.toString(decodedFields), 1, decodedFields.length);

		Field decodedField = decodedFields[0];

		Assert.assertEquals("entry_class_names", decodedField.getName());
		Assert.assertEquals("multiselect", decodedField.getType());

		Object[] decodedEntries = (Object[])decodedField.getDefaultValue();

		Assert.assertEquals(
			Arrays.toString(decodedEntries), 2, decodedEntries.length);

		Map<?, ?> firstEntry = (Map<?, ?>)decodedEntries[0];

		Assert.assertEquals(_CLASS_NAME_BLOGS_ENTRY, firstEntry.get(_LABEL));
		Assert.assertEquals(_CLASS_NAME_BLOGS_ENTRY, firstEntry.get(_VALUE));

		Map<?, ?> secondEntry = (Map<?, ?>)decodedEntries[1];

		Assert.assertEquals(
			_CLASS_NAME_JOURNAL_ARTICLE, secondEntry.get(_LABEL));
		Assert.assertEquals(
			_CLASS_NAME_JOURNAL_ARTICLE, secondEntry.get(_VALUE));
	}

	private static final String _CLASS_NAME_BLOGS_ENTRY =
		"com.liferay.blogs.model.BlogsEntry";

	private static final String _CLASS_NAME_JOURNAL_ARTICLE =
		"com.liferay.journal.model.JournalArticle";

	private static final String _LABEL = "label";

	private static final String _VALUE = "value";

}
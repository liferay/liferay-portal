/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFieldAttributePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFieldPersistence;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Anderson Luiz
 */
public class DDMFieldLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpLanguageUtil();

		ReflectionTestUtil.setFieldValue(
			_ddmFieldLocalServiceImpl, "_ddmFieldAttributePersistence",
			_ddmFieldAttributePersistence);
		ReflectionTestUtil.setFieldValue(
			_ddmFieldLocalServiceImpl, "ddmFieldPersistence",
			_ddmFieldPersistence);
	}

	@Test
	public void testGetDDMFormValues() {
		Mockito.when(
			_ddmFieldAttributePersistence.findByStorageId(_STORAGE_ID)
		).thenAnswer(
			invocation -> ListUtil.fromArray(
				_getDDMFieldAttribute("availableLanguageIds", "en_US,pt_BR"),
				_getDDMFieldAttribute("defaultLanguageId", "pt_BR"))
		);

		Mockito.when(
			_ddmFieldPersistence.findByStorageId(_STORAGE_ID)
		).thenAnswer(
			invocation -> _getDDMFields()
		);

		DDMFormValues ddmFormValues =
			_ddmFieldLocalServiceImpl.getDDMFormValues(
				new DDMForm(), _STORAGE_ID);

		Assert.assertEquals(
			SetUtil.fromArray(LocaleUtil.US, LocaleUtil.BRAZIL),
			ddmFormValues.getAvailableLocales());
		Assert.assertEquals(
			LocaleUtil.BRAZIL, ddmFormValues.getDefaultLocale());
	}

	@Test
	public void testGetDDMFormValuesWithEmptyDDMFieldAttributes() {
		Mockito.when(
			_ddmFieldAttributePersistence.findByStorageId(_STORAGE_ID)
		).thenReturn(
			Collections.emptyList()
		);

		Mockito.when(
			_ddmFieldPersistence.findByStorageId(_STORAGE_ID)
		).thenAnswer(
			invocation -> _getDDMFields()
		);

		DDMFormValues ddmFormValues =
			_ddmFieldLocalServiceImpl.getDDMFormValues(
				new DDMForm(), _STORAGE_ID);

		Assert.assertTrue(SetUtil.isEmpty(ddmFormValues.getAvailableLocales()));
		Assert.assertNull(ddmFormValues.getDefaultLocale());
	}

	private DDMFieldAttribute _getDDMFieldAttribute(
		String attributeName, String attributeValue) {

		DDMFieldAttribute ddmFieldAttribute = Mockito.mock(
			DDMFieldAttribute.class);

		Mockito.when(
			ddmFieldAttribute.getAttributeName()
		).thenReturn(
			attributeName
		);

		Mockito.when(
			ddmFieldAttribute.getAttributeValue()
		).thenReturn(
			attributeValue
		);

		Mockito.when(
			ddmFieldAttribute.getFieldId()
		).thenReturn(
			_FIELD_ID
		);

		Mockito.when(
			ddmFieldAttribute.getLanguageId()
		).thenReturn(
			StringPool.BLANK
		);

		return ddmFieldAttribute;
	}

	private List<DDMField> _getDDMFields() {
		List<DDMField> ddmFields = new ArrayList<>();

		DDMField ddmField = Mockito.mock(DDMField.class);

		Mockito.when(
			ddmField.getFieldId()
		).thenReturn(
			_FIELD_ID
		);

		Mockito.when(
			ddmField.getFieldName()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			ddmField.getParentFieldId()
		).thenReturn(
			0L
		);

		ddmFields.add(ddmField);

		return ddmFields;
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(new LanguageImpl());
	}

	private static final long _FIELD_ID = RandomTestUtil.randomLong();

	private static final long _STORAGE_ID = RandomTestUtil.randomLong();

	private final DDMFieldAttributePersistence _ddmFieldAttributePersistence =
		Mockito.mock(DDMFieldAttributePersistence.class);
	private final DDMFieldLocalServiceImpl _ddmFieldLocalServiceImpl =
		new DDMFieldLocalServiceImpl();
	private final DDMFieldPersistence _ddmFieldPersistence = Mockito.mock(
		DDMFieldPersistence.class);

}
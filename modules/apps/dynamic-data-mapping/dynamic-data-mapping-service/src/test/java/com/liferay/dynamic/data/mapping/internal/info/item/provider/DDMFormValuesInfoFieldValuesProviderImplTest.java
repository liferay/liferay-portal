/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.info.item.provider;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.info.field.converter.DDMFormFieldInfoFieldConverter;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class DDMFormValuesInfoFieldValuesProviderImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_ddmFormValuesInfoFieldValuesProviderImpl,
			"_ddmFormFieldInfoFieldConverter", _ddmFormFieldInfoFieldConverter);
	}

	@Test
	public void testGetInfoFieldValues() {
		DDMFormField ddmFormField = Mockito.mock(DDMFormField.class);

		Mockito.when(
			_ddmFormFieldInfoFieldConverter.convert(ddmFormField)
		).thenReturn(
			Mockito.mock(InfoField.class)
		);

		Value value = new LocalizedValue(LocaleUtil.US);

		value.addString(LocaleUtil.US, RandomTestUtil.randomString());

		List<InfoFieldValue<InfoLocalizedValue<Object>>> infoFieldValues =
			_ddmFormValuesInfoFieldValuesProviderImpl.getInfoFieldValues(
				_groupedModel,
				_getDDMFormValues(
					_getDDMFormFieldValue(
						ddmFormField, DDMFormFieldTypeConstants.SEPARATOR,
						value)));

		Assert.assertTrue(
			infoFieldValues.toString(), infoFieldValues.isEmpty());
	}

	private DDMFormFieldValue _getDDMFormFieldValue(
		DDMFormField ddmFormField, String type, Value value) {

		DDMFormFieldValue ddmFormFieldValue = Mockito.mock(
			DDMFormFieldValue.class);

		Mockito.when(
			ddmFormFieldValue.getDDMFormField()
		).thenReturn(
			ddmFormField
		);

		Mockito.when(
			ddmFormFieldValue.getType()
		).thenReturn(
			type
		);

		Mockito.when(
			ddmFormFieldValue.getValue()
		).thenReturn(
			value
		);

		return ddmFormFieldValue;
	}

	private DDMFormValues _getDDMFormValues(
		DDMFormFieldValue ddmFormFieldValue) {

		DDMFormValues ddmFormValues = Mockito.mock(DDMFormValues.class);

		Mockito.when(
			ddmFormValues.getDDMFormFieldValuesMap(true)
		).thenReturn(
			HashMapBuilder.put(
				RandomTestUtil.randomString(),
				Collections.singletonList(ddmFormFieldValue)
			).build()
		);

		return ddmFormValues;
	}

	private final DDMFormFieldInfoFieldConverter
		_ddmFormFieldInfoFieldConverter = Mockito.mock(
			DDMFormFieldInfoFieldConverter.class);
	private final DDMFormValuesInfoFieldValuesProviderImpl
		_ddmFormValuesInfoFieldValuesProviderImpl =
			new DDMFormValuesInfoFieldValuesProviderImpl();
	private final GroupedModel _groupedModel = Mockito.mock(GroupedModel.class);

}
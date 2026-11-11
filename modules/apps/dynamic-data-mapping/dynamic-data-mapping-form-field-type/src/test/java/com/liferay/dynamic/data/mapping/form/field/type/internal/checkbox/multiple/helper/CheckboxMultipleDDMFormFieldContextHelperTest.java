/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.checkbox.multiple.helper;

import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.test.util.DDMFormFieldOptionsTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rafael Praxedes
 */
public class CheckboxMultipleDDMFormFieldContextHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetOptions() {
		List<Object> expectedOptions = new ArrayList<>();

		expectedOptions.add(
			DDMFormFieldOptionsTestUtil.createOption(
				"Label 1", "Reference 1", "value 1"));
		expectedOptions.add(
			DDMFormFieldOptionsTestUtil.createOption(
				"Label 2", "Reference 2", "value 2"));
		expectedOptions.add(
			DDMFormFieldOptionsTestUtil.createOption(
				"Label 3", "Reference 3", "value 3"));

		DDMFormFieldOptions ddmFormFieldOptions =
			DDMFormFieldOptionsTestUtil.createDDMFormFieldOptions();

		Assert.assertEquals(
			expectedOptions,
			_getActualOptions(ddmFormFieldOptions, LocaleUtil.US));
	}

	private List<Object> _getActualOptions(
		DDMFormFieldOptions ddmFormFieldOptions, Locale locale) {

		CheckboxMultipleDDMFormFieldContextHelper
			checkboxMultipleDDMFormFieldContextHelper =
				new CheckboxMultipleDDMFormFieldContextHelper(
					ddmFormFieldOptions, locale);

		return checkboxMultipleDDMFormFieldContextHelper.getOptions();
	}

}
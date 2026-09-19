/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Carolina Barbosa
 */
public class DDMFormReportDataUtilTest extends BaseDDMTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setUpJSONFactoryUtil();
		setUpLanguageUtil();
	}

	@Test
	public void testGetFieldValuesJSONArray() throws Exception {
		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			DDMFormTestUtil.createDDMForm("TextField"));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"TextField",
				DDMFormValuesTestUtil.createLocalizedValue(
					"Test 1", LocaleUtil.US)));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"TextField",
				DDMFormValuesTestUtil.createLocalizedValue(
					"Test 2", LocaleUtil.US)));

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"TextField",
				DDMFormValuesTestUtil.createLocalizedValue(
					"Test 3", "Teste 3", LocaleUtil.BRAZIL));

		ddmFormFieldValue.addNestedDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"nestedTextField",
				DDMFormValuesTestUtil.createLocalizedValue(
					"Test 4", "Teste 4", LocaleUtil.BRAZIL)));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		List<DDMFormInstanceRecord> ddmFormInstanceRecords = ListUtil.fromArray(
			_mockDDMFormInstanceRecord(ddmFormValues));

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				"Test 1", "Test 2", "Teste 3"
			).toString(),
			String.valueOf(
				DDMFormReportDataUtil.getFieldValuesJSONArray(
					ddmFormInstanceRecords, "TextField")),
			JSONCompareMode.STRICT_ORDER);
		JSONAssert.assertEquals(
			JSONUtil.put(
				"Teste 4"
			).toString(),
			String.valueOf(
				DDMFormReportDataUtil.getFieldValuesJSONArray(
					ddmFormInstanceRecords, "nestedTextField")),
			JSONCompareMode.STRICT_ORDER);
	}

	@Test
	public void testGetFieldsJSONArray() throws Exception {
		JSONAssert.assertEquals(
			JSONUtil.put(
				JSONUtil.put(
					"columns", JSONFactoryUtil.createJSONObject()
				).put(
					"label", "TextField"
				).put(
					"name", "TextField"
				).put(
					"options", JSONFactoryUtil.createJSONObject()
				).put(
					"rows", JSONFactoryUtil.createJSONObject()
				).put(
					"type", "text"
				)
			).toString(),
			String.valueOf(
				DDMFormReportDataUtil.getFieldsJSONArray(
					_mockDDMFormInstanceReport())),
			JSONCompareMode.STRICT_ORDER);
	}

	@Test
	public void testGetTotalItems() throws Exception {
		Assert.assertEquals(0, DDMFormReportDataUtil.getTotalItems(null));
		Assert.assertEquals(
			10,
			DDMFormReportDataUtil.getTotalItems(_mockDDMFormInstanceReport()));
	}

	private DDMFormInstance _mockDDMFormInstance() throws Exception {
		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		Mockito.when(
			ddmFormInstance.getDDMForm()
		).thenReturn(
			DDMFormTestUtil.createDDMForm("TextField")
		);

		return ddmFormInstance;
	}

	private DDMFormInstanceRecord _mockDDMFormInstanceRecord(
			DDMFormValues ddmFormValues)
		throws Exception {

		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		Mockito.when(
			ddmFormInstanceRecord.getDDMFormValues()
		).thenReturn(
			ddmFormValues
		);

		return ddmFormInstanceRecord;
	}

	private DDMFormInstanceReport _mockDDMFormInstanceReport()
		throws Exception {

		DDMFormInstanceReport ddmFormInstanceReport = Mockito.mock(
			DDMFormInstanceReport.class);

		Mockito.when(
			ddmFormInstanceReport.getData()
		).thenReturn(
			JSONUtil.put(
				"totalItems", 10
			).toString()
		);

		DDMFormInstance ddmFormInstance = _mockDDMFormInstance();

		Mockito.when(
			ddmFormInstanceReport.getFormInstance()
		).thenReturn(
			ddmFormInstance
		);

		return ddmFormInstanceReport;
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.report;

import com.liferay.dynamic.data.mapping.constants.DDMFormInstanceReportConstants;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class NumericDDMFormFieldTypeReportProcessorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_numericDDMFormFieldTypeReportProcessor.
			ddmFormInstanceRecordLocalService =
				_ddmFormInstanceRecordLocalService;

		_numericDDMFormFieldTypeReportProcessor.jsonFactory =
			new JSONFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			DDMFormInstanceRecordVersionLocalServiceUtil.class,
			"_serviceSnapshot",
			new Snapshot<DDMFormInstanceRecordVersionLocalService>(
				DDMFormInstanceRecordVersionLocalServiceUtil.class,
				DDMFormInstanceRecordVersionLocalService.class) {

				@Override
				public DDMFormInstanceRecordVersionLocalService get() {
					return _ddmFormInstanceRecordVersionLocalService;
				}

			});
	}

	@Test
	public void testFormatBigDecimal() {
		Assert.assertEquals(
			"12",
			_numericDDMFormFieldTypeReportProcessor.formatBigDecimal(
				new BigDecimal("12.00000000000000")));
		Assert.assertEquals(
			"12.00000000000001",
			_numericDDMFormFieldTypeReportProcessor.formatBigDecimal(
				new BigDecimal("12.00000000000001")));
		Assert.assertEquals(
			"12.000000001",
			_numericDDMFormFieldTypeReportProcessor.formatBigDecimal(
				new BigDecimal("12.0000000010000")));
	}

	@Test
	public void testProcessDDMFormInstanceReportOnDeleteEvent()
		throws Exception {

		// Between minimum and maximum

		long formInstanceRecordId = 3;

		List<DDMFormInstanceRecord> ddmFormInstanceRecords = new ArrayList<>();

		for (int i = _VALUES_MAX_LENGTH; i > 0; i--) {
			ddmFormInstanceRecords.add(
				_createDDMFormInstanceRecord(String.valueOf(i)));
		}

		Mockito.when(
			_ddmFormInstanceRecordLocalService.searchFormInstanceRecords(
				Mockito.eq(0L), Mockito.eq(new String[] {"field1"}),
				Mockito.eq(WorkflowConstants.STATUS_APPROVED), Mockito.eq(0),
				Mockito.eq(_VALUES_MAX_LENGTH + 1), Mockito.any())
		).thenReturn(
			new BaseModelSearchResult<>(
				ddmFormInstanceRecords, ddmFormInstanceRecords.size())
		);

		DDMFormInstanceRecord ddmFormInstanceRecord =
			_mockDDMFormInstanceRecord(formInstanceRecordId);

		JSONObject processedFieldJSONObject =
			_numericDDMFormFieldTypeReportProcessor.process(
				_mockDDMFormFieldValue(LocaleUtil.US, "field1", "3"),
				JSONUtil.put(
					"summary",
					JSONUtil.put(
						"average", "3"
					).put(
						"max", "5"
					).put(
						"min", "1"
					).put(
						"sum", "15"
					)
				).put(
					"totalEntries", 5
				).put(
					"type", DDMFormFieldTypeConstants.NUMERIC
				).put(
					"values",
					JSONUtil.putAll(
						JSONUtil.put(
							"formInstanceRecordId", 5
						).put(
							"value", "5"
						),
						JSONUtil.put(
							"formInstanceRecordId", 4
						).put(
							"value", "4"
						),
						JSONUtil.put(
							"formInstanceRecordId", 3
						).put(
							"value", "3"
						),
						JSONUtil.put(
							"formInstanceRecordId", 2
						).put(
							"value", "2"
						),
						JSONUtil.put(
							"formInstanceRecordId", 1
						).put(
							"value", "1"
						))
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_DELETE_RECORD_VERSION);

		JSONObject summaryJSONObject = processedFieldJSONObject.getJSONObject(
			"summary");

		Assert.assertEquals("3", summaryJSONObject.getString("average"));
		Assert.assertEquals("5", summaryJSONObject.getString("max"));
		Assert.assertEquals("1", summaryJSONObject.getString("min"));
		Assert.assertEquals("12", summaryJSONObject.getString("sum"));

		Mockito.verify(
			ddmFormInstanceRecord.getFormInstance(), Mockito.never()
		).getFormInstanceRecords();

		// Duplicate maximum

		formInstanceRecordId = RandomTestUtil.randomLong();

		processedFieldJSONObject =
			_numericDDMFormFieldTypeReportProcessor.process(
				_mockDDMFormFieldValue(LocaleUtil.US, "field1", "5"),
				JSONUtil.put(
					"summary",
					JSONUtil.put(
						"max", "5"
					).put(
						"maxCount", 2
					).put(
						"min", "1"
					).put(
						"minCount", 1
					).put(
						"sum", "11"
					)
				).put(
					"totalEntries", RandomTestUtil.randomInt()
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_DELETE_RECORD_VERSION);

		summaryJSONObject = processedFieldJSONObject.getJSONObject("summary");

		Assert.assertEquals("5", summaryJSONObject.getString("max"));
		Assert.assertEquals(1, summaryJSONObject.getInt("maxCount"));
		Assert.assertEquals("1", summaryJSONObject.getString("min"));
		Assert.assertEquals(1, summaryJSONObject.getInt("minCount"));
		Assert.assertEquals("6", summaryJSONObject.getString("sum"));

		Mockito.verify(
			_ddmFormInstanceRecordLocalService, Mockito.never()
		).getFormInstanceRecord(
			formInstanceRecordId
		);

		// Last value

		formInstanceRecordId = RandomTestUtil.randomLong();

		_mockDDMFormInstanceRecord(formInstanceRecordId, "", "");

		processedFieldJSONObject =
			_numericDDMFormFieldTypeReportProcessor.process(
				_mockDDMFormFieldValue(LocaleUtil.US, "field1", "5"),
				JSONUtil.put(
					"summary",
					JSONUtil.put(
						"max", "5"
					).put(
						"maxCount", 1
					).put(
						"min", "5"
					).put(
						"minCount", 1
					)
				).put(
					"totalEntries", RandomTestUtil.randomInt()
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_DELETE_RECORD_VERSION);

		Assert.assertFalse(processedFieldJSONObject.has("summary"));

		// Unique maximum

		formInstanceRecordId = RandomTestUtil.randomLong();

		_mockDDMFormInstanceRecord(formInstanceRecordId, "1", "2", "3", "4");

		processedFieldJSONObject =
			_numericDDMFormFieldTypeReportProcessor.process(
				_mockDDMFormFieldValue(LocaleUtil.US, "field1", "5"),
				JSONUtil.put(
					"summary",
					JSONUtil.put(
						"max", "5"
					).put(
						"maxCount", 1
					).put(
						"min", "1"
					).put(
						"minCount", 1
					).put(
						"sum", "15"
					)
				).put(
					"totalEntries", RandomTestUtil.randomInt()
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_DELETE_RECORD_VERSION);

		summaryJSONObject = processedFieldJSONObject.getJSONObject("summary");

		Assert.assertEquals("4", summaryJSONObject.getString("max"));
		Assert.assertEquals(1, summaryJSONObject.getInt("maxCount"));
		Assert.assertEquals("1", summaryJSONObject.getString("min"));
		Assert.assertEquals(1, summaryJSONObject.getInt("minCount"));
		Assert.assertEquals("10", summaryJSONObject.getString("sum"));
	}

	@Test
	public void testProcessDDMFormInstanceReportWithEmptyData()
		throws Exception {

		DDMFormFieldValue ddmFormFieldValue = _mockDDMFormFieldValue(
			LocaleUtil.US, "field1", "1");

		long formInstanceRecordId = 1;

		_mockDDMFormInstanceRecord(formInstanceRecordId);

		JSONObject processedFieldJSONObject =
			_numericDDMFormFieldTypeReportProcessor.process(
				ddmFormFieldValue,
				JSONUtil.put(
					"type", DDMFormFieldTypeConstants.NUMERIC
				).put(
					"values", JSONFactoryUtil.createJSONObject()
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_ADD_RECORD_VERSION);

		JSONObject summaryJSONObject = processedFieldJSONObject.getJSONObject(
			"summary");

		Assert.assertEquals("1", summaryJSONObject.getString("average"));
		Assert.assertEquals("1", summaryJSONObject.getString("max"));
		Assert.assertEquals("1", summaryJSONObject.getString("min"));
		Assert.assertEquals("1", summaryJSONObject.getString("sum"));
	}

	@Test
	public void testProcessDDMFormInstanceReportWithExistingData()
		throws Exception {

		// Duplicate maximum

		long formInstanceRecordId = 0;

		JSONObject processedFieldJSONObject =
			_numericDDMFormFieldTypeReportProcessor.process(
				_mockDDMFormFieldValue(LocaleUtil.US, "field1", "5"),
				JSONUtil.put(
					"summary",
					JSONUtil.put(
						"max", "5"
					).put(
						"maxCount", 1
					).put(
						"min", "1"
					).put(
						"minCount", 1
					).put(
						"sum", "6"
					)
				).put(
					"totalEntries", RandomTestUtil.randomInt()
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_ADD_RECORD_VERSION);

		JSONObject summaryJSONObject = processedFieldJSONObject.getJSONObject(
			"summary");

		Assert.assertEquals("5", summaryJSONObject.getString("max"));
		Assert.assertEquals(2, summaryJSONObject.getInt("maxCount"));
		Assert.assertEquals("1", summaryJSONObject.getString("min"));
		Assert.assertEquals(1, summaryJSONObject.getInt("minCount"));
		Assert.assertEquals("11", summaryJSONObject.getString("sum"));

		// Larger than maximum

		_mockDDMFormInstanceRecord(formInstanceRecordId);

		processedFieldJSONObject =
			_numericDDMFormFieldTypeReportProcessor.process(
				_mockDDMFormFieldValue(LocaleUtil.US, "field1", "3"),
				JSONUtil.put(
					"summary",
					JSONUtil.put(
						"average", "1"
					).put(
						"max", "1"
					).put(
						"min", "1"
					).put(
						"sum", "1"
					)
				).put(
					"totalEntries", 1
				).put(
					"type", DDMFormFieldTypeConstants.NUMERIC
				).put(
					"values",
					JSONUtil.put(
						JSONUtil.put(
							"formInstanceRecordId", 1
						).put(
							"value", "1"
						))
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_ADD_RECORD_VERSION);

		summaryJSONObject = processedFieldJSONObject.getJSONObject("summary");

		Assert.assertEquals("2", summaryJSONObject.getString("average"));
		Assert.assertEquals("3", summaryJSONObject.getString("max"));
		Assert.assertEquals("1", summaryJSONObject.getString("min"));
		Assert.assertEquals("4", summaryJSONObject.getString("sum"));
	}

	@Test
	public void testProcessDDMFormInstanceReportWithLongFieldValue()
		throws Exception {

		// LPS-118317

		DDMFormFieldValue ddmFormFieldValue = _mockDDMFormFieldValue(
			LocaleUtil.US, "field1",
			"99999999999999999999999999999999999999999");

		long formInstanceRecordId = 0;

		_mockDDMFormInstanceRecord(formInstanceRecordId);

		JSONObject processedFieldJSONObject =
			_numericDDMFormFieldTypeReportProcessor.process(
				ddmFormFieldValue,
				JSONUtil.put(
					"summary",
					JSONUtil.put(
						"average", "1"
					).put(
						"max", "1"
					).put(
						"min", "1"
					).put(
						"sum", "1"
					)
				).put(
					"totalEntries", 1
				).put(
					"type", DDMFormFieldTypeConstants.NUMERIC
				).put(
					"values",
					JSONUtil.put(
						JSONUtil.put(
							"formInstanceRecordId", 1
						).put(
							"value", "1"
						))
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_ADD_RECORD_VERSION);

		JSONObject summaryJSONObject = processedFieldJSONObject.getJSONObject(
			"summary");

		Assert.assertEquals(
			"50000000000000000000000000000000000000000",
			summaryJSONObject.getString("average"));
		Assert.assertEquals(
			"99999999999999999999999999999999999999999",
			summaryJSONObject.getString("max"));
		Assert.assertEquals("1", summaryJSONObject.getString("min"));
		Assert.assertEquals(
			"100000000000000000000000000000000000000000",
			summaryJSONObject.getString("sum"));
	}

	private DDMFormInstanceRecord _createDDMFormInstanceRecord(
			String valueString)
		throws Exception {

		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			Mockito.mock(DDMFormInstanceRecordVersion.class);

		DDMFormFieldValue ddmFormFieldValue = _mockDDMFormFieldValue(
			LocaleUtil.US, "", valueString);

		DDMFormValues ddmFormValues = Mockito.mock(DDMFormValues.class);

		Mockito.when(
			ddmFormValues.getDDMFormFieldValuesMap(true)
		).thenReturn(
			HashMapBuilder.put(
				"field1", Arrays.asList(ddmFormFieldValue)
			).build()
		);

		Mockito.when(
			ddmFormInstanceRecordVersion.getDDMFormValues()
		).thenReturn(
			ddmFormValues
		);

		long formInstanceRecordId = RandomTestUtil.randomLong();

		Mockito.when(
			_ddmFormInstanceRecordVersionLocalService.
				getLatestFormInstanceRecordVersion(
					formInstanceRecordId, WorkflowConstants.STATUS_APPROVED)
		).thenReturn(
			ddmFormInstanceRecordVersion
		);

		Mockito.when(
			ddmFormInstanceRecord.getDDMFormValues()
		).thenReturn(
			ddmFormValues
		);

		Mockito.when(
			ddmFormInstanceRecord.getFormInstanceRecordId()
		).thenReturn(
			formInstanceRecordId
		);

		return ddmFormInstanceRecord;
	}

	private DDMFormFieldValue _mockDDMFormFieldValue(
		Locale defaultLocale, String fieldName, String fieldValue) {

		DDMFormFieldValue ddmFormFieldValue = Mockito.mock(
			DDMFormFieldValue.class);

		Mockito.when(
			ddmFormFieldValue.getName()
		).thenReturn(
			fieldName
		);

		Mockito.when(
			ddmFormFieldValue.getType()
		).thenReturn(
			DDMFormFieldTypeConstants.NUMERIC
		);

		Value value = new LocalizedValue(defaultLocale);

		value.addString(value.getDefaultLocale(), fieldValue);

		Mockito.when(
			ddmFormFieldValue.getValue()
		).thenReturn(
			value
		);

		return ddmFormFieldValue;
	}

	private DDMFormInstanceRecord _mockDDMFormInstanceRecord(
			long formInstanceRecordId, String... valueStrings)
		throws Exception {

		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		List<DDMFormInstanceRecord> ddmFormInstanceRecords =
			TransformUtil.transformToList(
				valueStrings,
				valueString -> _createDDMFormInstanceRecord(valueString));

		Mockito.when(
			ddmFormInstance.getFormInstanceRecords()
		).thenReturn(
			ddmFormInstanceRecords
		);

		Mockito.when(
			ddmFormInstanceRecord.getFormInstance()
		).thenReturn(
			ddmFormInstance
		);

		Mockito.when(
			ddmFormInstanceRecord.getFormInstanceId()
		).thenReturn(
			0L
		);

		Mockito.when(
			_ddmFormInstanceRecordLocalService.getFormInstanceRecord(
				formInstanceRecordId)
		).thenReturn(
			ddmFormInstanceRecord
		);

		return ddmFormInstanceRecord;
	}

	private static final int _VALUES_MAX_LENGTH = 5;

	private final DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService = Mockito.mock(
			DDMFormInstanceRecordLocalService.class);
	private final DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService = Mockito.mock(
			DDMFormInstanceRecordVersionLocalService.class);
	private final NumericDDMFormFieldTypeReportProcessor
		_numericDDMFormFieldTypeReportProcessor =
			new NumericDDMFormFieldTypeReportProcessor();

}
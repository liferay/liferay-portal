/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.report;

import com.liferay.dynamic.data.mapping.constants.DDMFormInstanceReportConstants;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class TextDDMFormFieldTypeReportProcessorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_textDDMFormFieldTypeReportProcessor.ddmFormInstanceRecordLocalService =
			_ddmFormInstanceRecordLocalService;

		_textDDMFormFieldTypeReportProcessor.jsonFactory =
			new JSONFactoryImpl();
	}

	@Test
	public void testProcessDDMFormInstanceReportEditLastFiveRecords()
		throws Exception {

		DDMFormFieldValue ddmFormFieldValue = Mockito.mock(
			DDMFormFieldValue.class);

		Mockito.when(
			ddmFormFieldValue.getName()
		).thenReturn(
			"field1"
		);

		Mockito.when(
			ddmFormFieldValue.getType()
		).thenReturn(
			DDMFormFieldTypeConstants.TEXT
		);

		Value value = new LocalizedValue();

		value.addString(value.getDefaultLocale(), "text 6");
		value.setDefaultLocale(LocaleUtil.US);

		Mockito.when(
			ddmFormFieldValue.getValue()
		).thenReturn(
			value
		);

		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		Mockito.when(
			ddmFormInstanceRecord.getFormInstanceId()
		).thenReturn(
			0L
		);

		long formInstanceRecordId = 3;

		Mockito.when(
			_ddmFormInstanceRecordLocalService.getFormInstanceRecord(
				formInstanceRecordId)
		).thenReturn(
			ddmFormInstanceRecord
		);

		JSONObject processedFieldJSONObject =
			_textDDMFormFieldTypeReportProcessor.process(
				ddmFormFieldValue,
				JSONUtil.put(
					"type", DDMFormFieldTypeConstants.TEXT
				).put(
					"values",
					JSONUtil.putAll(
						JSONUtil.put(
							"formInstanceRecordId", 5
						).put(
							"value", "text 5"
						),
						JSONUtil.put(
							"formInstanceRecordId", 4
						).put(
							"value", "text 4"
						),
						JSONUtil.put(
							"formInstanceRecordId", 3
						).put(
							"value", "text 3"
						),
						JSONUtil.put(
							"formInstanceRecordId", 2
						).put(
							"value", "text 2"
						),
						JSONUtil.put(
							"formInstanceRecordId", 1
						).put(
							"value", "text 1"
						))
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_ADD_RECORD_VERSION);

		Assert.assertEquals(
			DDMFormFieldTypeConstants.TEXT,
			processedFieldJSONObject.getString("type"));

		JSONArray valuesJSONArray = processedFieldJSONObject.getJSONArray(
			"values");

		JSONObject jsonObject = valuesJSONArray.getJSONObject(0);

		Assert.assertEquals("text 6", jsonObject.getString("value"));

		jsonObject = valuesJSONArray.getJSONObject(2);

		Assert.assertEquals("text 4", jsonObject.getString("value"));
	}

	@Test
	public void testProcessDDMFormInstanceReportOnDeleteEvent()
		throws Exception {

		// Value in the report

		DDMFormFieldValue ddmFormFieldValue = Mockito.mock(
			DDMFormFieldValue.class);

		Mockito.when(
			ddmFormFieldValue.getName()
		).thenReturn(
			"field1"
		);

		Value value = new LocalizedValue();

		value.addString(value.getDefaultLocale(), "text 6");
		value.setDefaultLocale(LocaleUtil.US);

		Mockito.when(
			ddmFormFieldValue.getValue()
		).thenReturn(
			value
		);

		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		Mockito.when(
			ddmFormInstance.getFormInstanceId()
		).thenReturn(
			0L
		);

		Mockito.when(
			ddmFormInstanceRecord.getFormInstance()
		).thenReturn(
			ddmFormInstance
		);

		long formInstanceRecordId = 3;

		Mockito.when(
			_ddmFormInstanceRecordLocalService.getFormInstanceRecord(
				formInstanceRecordId)
		).thenReturn(
			ddmFormInstanceRecord
		);

		int maxLength = RandomTestUtil.randomInt(1, _VALUES_MAX_LENGTH);

		List<DDMFormInstanceRecord> ddmFormInstanceRecords = new ArrayList<>();

		for (int i = maxLength; i > 0; i--) {
			ddmFormInstanceRecords.add(_createFormInstanceRecord("text " + i));
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

		JSONObject processedFieldJSONObject =
			_textDDMFormFieldTypeReportProcessor.process(
				ddmFormFieldValue,
				JSONUtil.put(
					"type", DDMFormFieldTypeConstants.TEXT
				).put(
					"values",
					JSONUtil.putAll(
						JSONUtil.put(
							"formInstanceRecordId", formInstanceRecordId))
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_DELETE_RECORD_VERSION);

		Assert.assertEquals(
			DDMFormFieldTypeConstants.TEXT,
			processedFieldJSONObject.getString("type"));

		JSONArray valuesJSONArray = processedFieldJSONObject.getJSONArray(
			"values");

		JSONObject jsonObject = valuesJSONArray.getJSONObject(0);

		Assert.assertEquals("text " + maxLength, jsonObject.getString("value"));

		// Value not in the report

		formInstanceRecordId = 2;

		processedFieldJSONObject = _textDDMFormFieldTypeReportProcessor.process(
			ddmFormFieldValue,
			JSONUtil.put(
				"totalEntries", 2
			).put(
				"values",
				JSONUtil.putAll(JSONUtil.put("formInstanceRecordId", 1))
			),
			formInstanceRecordId,
			DDMFormInstanceReportConstants.EVENT_DELETE_RECORD_VERSION);

		Assert.assertEquals(1, processedFieldJSONObject.getInt("totalEntries"));

		Mockito.verify(
			_ddmFormInstanceRecordLocalService, Mockito.never()
		).getFormInstanceRecord(
			formInstanceRecordId
		);
	}

	@Test
	public void testProcessDDMFormInstanceReportWithEmptyData()
		throws Exception {

		DDMFormFieldValue ddmFormFieldValue = Mockito.mock(
			DDMFormFieldValue.class);

		Mockito.when(
			ddmFormFieldValue.getName()
		).thenReturn(
			"field1"
		);

		Mockito.when(
			ddmFormFieldValue.getType()
		).thenReturn(
			DDMFormFieldTypeConstants.TEXT
		);

		Value value = new LocalizedValue();

		value.addString(value.getDefaultLocale(), "text");
		value.setDefaultLocale(LocaleUtil.US);

		Mockito.when(
			ddmFormFieldValue.getValue()
		).thenReturn(
			value
		);

		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		Mockito.when(
			ddmFormInstanceRecord.getFormInstanceId()
		).thenReturn(
			0L
		);

		long formInstanceRecordId = 1;

		Mockito.when(
			_ddmFormInstanceRecordLocalService.getFormInstanceRecord(
				formInstanceRecordId)
		).thenReturn(
			ddmFormInstanceRecord
		);

		JSONObject processedFieldJSONObject =
			_textDDMFormFieldTypeReportProcessor.process(
				ddmFormFieldValue,
				JSONUtil.put(
					"type", DDMFormFieldTypeConstants.TEXT
				).put(
					"values", JSONFactoryUtil.createJSONObject()
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_ADD_RECORD_VERSION);

		Assert.assertEquals(
			DDMFormFieldTypeConstants.TEXT,
			processedFieldJSONObject.getString("type"));

		JSONArray valuesJSONArray = processedFieldJSONObject.getJSONArray(
			"values");

		JSONObject jsonObject = valuesJSONArray.getJSONObject(0);

		Assert.assertEquals("text", jsonObject.getString("value"));
	}

	@Test
	public void testProcessDDMFormInstanceReportWithExistingData()
		throws Exception {

		DDMFormFieldValue ddmFormFieldValue = Mockito.mock(
			DDMFormFieldValue.class);

		Mockito.when(
			ddmFormFieldValue.getName()
		).thenReturn(
			"field1"
		);

		Mockito.when(
			ddmFormFieldValue.getType()
		).thenReturn(
			DDMFormFieldTypeConstants.TEXT
		);

		Value value = new LocalizedValue();

		value.addString(value.getDefaultLocale(), "text 2");
		value.setDefaultLocale(LocaleUtil.US);

		Mockito.when(
			ddmFormFieldValue.getValue()
		).thenReturn(
			value
		);

		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		Mockito.when(
			ddmFormInstanceRecord.getFormInstanceId()
		).thenReturn(
			0L
		);

		long formInstanceRecordId = 0;

		Mockito.when(
			_ddmFormInstanceRecordLocalService.getFormInstanceRecord(
				formInstanceRecordId)
		).thenReturn(
			ddmFormInstanceRecord
		);

		JSONObject processedFieldJSONObject =
			_textDDMFormFieldTypeReportProcessor.process(
				ddmFormFieldValue,
				JSONUtil.put(
					"type", DDMFormFieldTypeConstants.TEXT
				).put(
					"values",
					JSONUtil.put(
						JSONUtil.put(
							"formInstanceRecordId", 1
						).put(
							"value", "text 1"
						))
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_ADD_RECORD_VERSION);

		Assert.assertEquals(
			DDMFormFieldTypeConstants.TEXT,
			processedFieldJSONObject.getString("type"));

		JSONArray valuesJSONArray = processedFieldJSONObject.getJSONArray(
			"values");

		JSONObject jsonObject = valuesJSONArray.getJSONObject(0);

		Assert.assertEquals("text 2", jsonObject.getString("value"));

		jsonObject = valuesJSONArray.getJSONObject(1);

		Assert.assertEquals("text 1", jsonObject.getString("value"));
	}

	@Test
	public void testProcessDDMFormInstanceReportWithFiveExistingEntries()
		throws Exception {

		DDMFormFieldValue ddmFormFieldValue = Mockito.mock(
			DDMFormFieldValue.class);

		Mockito.when(
			ddmFormFieldValue.getName()
		).thenReturn(
			"field1"
		);

		Mockito.when(
			ddmFormFieldValue.getType()
		).thenReturn(
			DDMFormFieldTypeConstants.TEXT
		);

		Value value = new LocalizedValue();

		value.addString(value.getDefaultLocale(), "text 6");
		value.setDefaultLocale(LocaleUtil.US);

		Mockito.when(
			ddmFormFieldValue.getValue()
		).thenReturn(
			value
		);

		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		Mockito.when(
			ddmFormInstanceRecord.getFormInstanceId()
		).thenReturn(
			0L
		);

		long formInstanceRecordId = 6;

		Mockito.when(
			_ddmFormInstanceRecordLocalService.getFormInstanceRecord(
				formInstanceRecordId)
		).thenReturn(
			ddmFormInstanceRecord
		);

		JSONObject processedFieldJSONObject =
			_textDDMFormFieldTypeReportProcessor.process(
				ddmFormFieldValue,
				JSONUtil.put(
					"type", DDMFormFieldTypeConstants.TEXT
				).put(
					"values",
					JSONUtil.putAll(
						JSONUtil.put(
							"formInstanceRecordId", 5
						).put(
							"value", "text 5"
						),
						JSONUtil.put(
							"formInstanceRecordId", 4
						).put(
							"value", "text 4"
						),
						JSONUtil.put(
							"formInstanceRecordId", 3
						).put(
							"value", "text 3"
						),
						JSONUtil.put(
							"formInstanceRecordId", 2
						).put(
							"value", "text 2"
						),
						JSONUtil.put(
							"formInstanceRecordId", 1
						).put(
							"value", "text 1"
						))
				),
				formInstanceRecordId,
				DDMFormInstanceReportConstants.EVENT_ADD_RECORD_VERSION);

		Assert.assertEquals(
			DDMFormFieldTypeConstants.TEXT,
			processedFieldJSONObject.getString("type"));

		JSONArray valuesJSONArray = processedFieldJSONObject.getJSONArray(
			"values");

		JSONObject jsonObject = valuesJSONArray.getJSONObject(0);

		Assert.assertEquals("text 6", jsonObject.getString("value"));

		jsonObject = valuesJSONArray.getJSONObject(4);

		Assert.assertEquals("text 2", jsonObject.getString("value"));
	}

	private DDMFormInstanceRecord _createFormInstanceRecord(String valueString)
		throws Exception {

		DDMFormFieldValue ddmFormFieldValue = Mockito.mock(
			DDMFormFieldValue.class);

		Value value = new LocalizedValue();

		value.addString(value.getDefaultLocale(), valueString);
		value.setDefaultLocale(LocaleUtil.US);

		Mockito.when(
			ddmFormFieldValue.getValue()
		).thenReturn(
			value
		);

		DDMFormValues ddmFormValues = Mockito.mock(DDMFormValues.class);

		Mockito.when(
			ddmFormValues.getDDMFormFieldValuesMap(true)
		).thenReturn(
			HashMapBuilder.put(
				"field1", Arrays.asList(ddmFormFieldValue)
			).build()
		);

		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		Mockito.when(
			ddmFormInstanceRecord.getDDMFormValues()
		).thenReturn(
			ddmFormValues
		);

		return ddmFormInstanceRecord;
	}

	private static final int _VALUES_MAX_LENGTH = 5;

	private final DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService = Mockito.mock(
			DDMFormInstanceRecordLocalService.class);
	private final TextDDMFormFieldTypeReportProcessor
		_textDDMFormFieldTypeReportProcessor =
			new TextDDMFormFieldTypeReportProcessor();

}
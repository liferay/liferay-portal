/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class DownloadFileEntryMVCResourceCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpDDMFormInstanceRecordService();
		_setUpDLAppLocalService();
		_setUpJSONFactory();
		_setUpMimeTypesUtil();
		_setUpPortletResponseUtil();
	}

	@After
	public void tearDown() throws Exception {
		_mimeTypesUtilMockedStatic.close();
		_portletResponseUtilMockedStatic.close();
	}

	@Test
	public void testServeResource() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest();
		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_downloadFileEntryMVCResourceCommand.doServeResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		Mockito.verify(
			_ddmFormInstanceRecordService, Mockito.times(1)
		).getFormInstanceRecord(
			_DDM_FORM_INSTANCE_RECORD_ID
		);

		Mockito.verify(
			_dlAppLocalService, Mockito.times(1)
		).getFileEntry(
			_FILE_ENTRY_ID
		);

		_portletResponseUtilMockedStatic.verify(
			() -> PortletResponseUtil.sendFile(
				mockLiferayResourceRequest, mockLiferayResourceResponse,
				_FILE_NAME, _inputStream, _CONTENT_TYPE),
			Mockito.times(1));
	}

	private DDMFormValues _getDDMFormValues() {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			SetUtil.fromArray(LocaleUtil.BRAZIL, LocaleUtil.US),
			LocaleUtil.BRAZIL);

		ddmForm.addDDMFormField(
			new DDMFormField(
				_DDM_FORM_FIELD_NAME,
				DDMFormFieldTypeConstants.DOCUMENT_LIBRARY));

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				_DDM_FORM_FIELD_NAME,
				DDMFormValuesTestUtil.createLocalizedValue(
					"{}",
					JSONUtil.put(
						"fileEntryId", _FILE_ENTRY_ID
					).toString(),
					LocaleUtil.BRAZIL)));

		return ddmFormValues;
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest()
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setParameter(
			"ddmFormFieldName", _DDM_FORM_FIELD_NAME);
		mockLiferayResourceRequest.setParameter(
			"ddmFormInstanceRecordId",
			String.valueOf(_DDM_FORM_INSTANCE_RECORD_ID));
		mockLiferayResourceRequest.setParameter(
			"fileEntryId", String.valueOf(_FILE_ENTRY_ID));

		return mockLiferayResourceRequest;
	}

	private void _setUpDDMFormInstanceRecordService() throws Exception {
		DDMFormInstanceRecord ddmFormInstanceRecord = Mockito.mock(
			DDMFormInstanceRecord.class);

		Mockito.when(
			ddmFormInstanceRecord.getDDMFormValues()
		).thenReturn(
			_getDDMFormValues()
		);

		Mockito.when(
			_ddmFormInstanceRecordService.getFormInstanceRecord(
				_DDM_FORM_INSTANCE_RECORD_ID)
		).thenReturn(
			ddmFormInstanceRecord
		);

		ReflectionTestUtil.setFieldValue(
			_downloadFileEntryMVCResourceCommand,
			"_ddmFormInstanceRecordService", _ddmFormInstanceRecordService);
	}

	private void _setUpDLAppLocalService() throws Exception {
		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			fileEntry.getContentStream()
		).thenReturn(
			_inputStream
		);

		Mockito.when(
			fileEntry.getFileName()
		).thenReturn(
			_FILE_NAME
		);

		Mockito.when(
			_dlAppLocalService.getFileEntry(_FILE_ENTRY_ID)
		).thenReturn(
			fileEntry
		);

		ReflectionTestUtil.setFieldValue(
			_downloadFileEntryMVCResourceCommand, "_dlAppLocalService",
			_dlAppLocalService);
	}

	private void _setUpJSONFactory() {
		ReflectionTestUtil.setFieldValue(
			_downloadFileEntryMVCResourceCommand, "_jsonFactory",
			new JSONFactoryImpl());
	}

	private void _setUpMimeTypesUtil() {
		Mockito.when(
			MimeTypesUtil.getContentType(_FILE_NAME)
		).thenReturn(
			_CONTENT_TYPE
		);
	}

	private void _setUpPortletResponseUtil() {
		_portletResponseUtilMockedStatic.when(
			() -> PortletResponseUtil.sendFile(
				Mockito.any(ResourceRequest.class),
				Mockito.any(ResourceResponse.class), Mockito.anyString(),
				Mockito.any(InputStream.class), Mockito.anyString())
		).then(
			invocationOnMock -> null
		);
	}

	private static final String _CONTENT_TYPE = RandomTestUtil.randomString();

	private static final String _DDM_FORM_FIELD_NAME =
		RandomTestUtil.randomString();

	private static final long _DDM_FORM_INSTANCE_RECORD_ID =
		RandomTestUtil.randomLong();

	private static final long _FILE_ENTRY_ID = RandomTestUtil.randomLong();

	private static final String _FILE_NAME = RandomTestUtil.randomString();

	private final DDMFormInstanceRecordService _ddmFormInstanceRecordService =
		Mockito.mock(DDMFormInstanceRecordService.class);
	private final DLAppLocalService _dlAppLocalService = Mockito.mock(
		DLAppLocalService.class);
	private final DownloadFileEntryMVCResourceCommand
		_downloadFileEntryMVCResourceCommand =
			new DownloadFileEntryMVCResourceCommand();
	private final InputStream _inputStream = Mockito.mock(InputStream.class);
	private final MockedStatic<MimeTypesUtil> _mimeTypesUtilMockedStatic =
		Mockito.mockStatic(MimeTypesUtil.class);
	private final MockedStatic<PortletResponseUtil>
		_portletResponseUtilMockedStatic = Mockito.mockStatic(
			PortletResponseUtil.class);

}
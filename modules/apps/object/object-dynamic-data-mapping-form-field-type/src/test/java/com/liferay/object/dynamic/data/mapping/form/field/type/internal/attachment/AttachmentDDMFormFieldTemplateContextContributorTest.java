/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.attachment;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.test.util.BaseDDMFormFieldTemplateContextContributorTestCase;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Leite
 */
@FeatureFlag("LPD-32050")
public class AttachmentDDMFormFieldTemplateContextContributorTest
	extends BaseDDMFormFieldTemplateContextContributorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_setUpAttachmentManager();
		_setUpDLAppLocalService();
		_setUpDLURLHelper();
		_setUpJSONFactory();
		_setUpLanguage();
		_setUpUploadServletRequestConfigurationProvider();

		_ddmFormField.setDDMForm(getDDMForm());
	}

	@Test
	public void testGetParametersFileEntryProperties() throws Exception {
		String contentURL = RandomTestUtil.randomString();

		_ddmFormField.setProperty("contentURL", contentURL);

		_ddmFormField.setProperty("localizedObjectField", true);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			createDDMFormFieldRenderingContext();

		Long value1 = RandomTestUtil.randomLong();
		Long value2 = RandomTestUtil.randomLong();

		ddmFormFieldRenderingContext.setValue(
			JSONUtil.put(
				"en_US", value1
			).put(
				"pt_BR", value2
			).toString());

		_mockFileEntry(_fileEntry1, value1);
		_mockFileEntry(_fileEntry2, value2);

		Map<String, Object> parameters =
			_attachmentDDMFormFieldTemplateContextContributor.getParameters(
				_ddmFormField, ddmFormFieldRenderingContext);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"en_US",
				JSONUtil.put(
					"contentURL", contentURL
				).put(
					"title", String.valueOf(value1)
				)
			).put(
				"pt_BR",
				JSONUtil.put(
					"contentURL", contentURL
				).put(
					"title", String.valueOf(value2)
				)
			).toString(),
			String.valueOf(parameters.get("fileEntryProperties")), false);
	}

	@Test
	public void testGetParametersLocalizedObjectField() {
		_ddmFormField.setProperty("localizedObjectField", true);

		Assert.assertTrue(
			MapUtil.getBoolean(
				_attachmentDDMFormFieldTemplateContextContributor.getParameters(
					_ddmFormField, createDDMFormFieldRenderingContext()),
				"localizedObjectField"));
	}

	@Test
	public void testGetParametersTip() {
		Mockito.when(
			_attachmentManager.getMaximumFileSize(
				Mockito.anyLong(), Mockito.anyBoolean())
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_ddmFormField.setProperty("contentURL", RandomTestUtil.randomString());
		_ddmFormField.setProperty("localizedObjectField", true);

		_testGetParametersTip(LocaleUtil.BRAZIL);
		_testGetParametersTip(LocaleUtil.ENGLISH);
	}

	private DDMFormFieldRenderingContext _createDDMFormFieldRenderingContext(
		Locale locale) {

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			locale
		);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		ddmFormFieldRenderingContext.setHttpServletRequest(httpServletRequest);

		return ddmFormFieldRenderingContext;
	}

	private void _mockFileEntry(FileEntry fileEntry, Long value)
		throws Exception {

		Mockito.when(
			fileEntry.getFileName()
		).thenReturn(
			String.valueOf(value)
		);

		Mockito.when(
			_dlAppLocalService.getFileEntry(Mockito.eq(value))
		).thenReturn(
			fileEntry
		);
	}

	private void _setUpAttachmentManager() {
		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor,
			"_attachmentManager", _attachmentManager);
	}

	private void _setUpDLAppLocalService() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor,
			"_dlAppLocalService", _dlAppLocalService);
	}

	private void _setUpDLURLHelper() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor, "_dlURLHelper",
			_dlURLHelper);
	}

	private void _setUpJSONFactory() {
		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor, "_jsonFactory",
			new JSONFactoryImpl());
	}

	private void _setUpLanguage() {
		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor, "_language",
			language);
	}

	private void _setUpUploadServletRequestConfigurationProvider() {
		Mockito.when(
			_uploadServletRequestConfigurationProvider.getMaxSize()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor,
			"_uploadServletRequestConfigurationProvider",
			_uploadServletRequestConfigurationProvider);
	}

	private void _testGetParametersTip(Locale locale) {
		String tip = RandomTestUtil.randomString();

		Mockito.when(
			language.format(
				Mockito.eq(locale), Mockito.eq("upload-a-x-no-larger-than-x"),
				Mockito.any(Object[].class))
		).thenReturn(
			tip
		);

		Assert.assertEquals(
			tip,
			MapUtil.getString(
				_attachmentDDMFormFieldTemplateContextContributor.getParameters(
					_ddmFormField, _createDDMFormFieldRenderingContext(locale)),
				"tip"));
	}

	private final AttachmentDDMFormFieldTemplateContextContributor
		_attachmentDDMFormFieldTemplateContextContributor =
			new AttachmentDDMFormFieldTemplateContextContributor();
	private final AttachmentManager _attachmentManager = Mockito.mock(
		AttachmentManager.class);
	private final DDMFormField _ddmFormField = new DDMFormField(
		"field", ObjectDDMFormFieldTypeConstants.ATTACHMENT);
	private final DLAppLocalService _dlAppLocalService = Mockito.mock(
		DLAppLocalService.class);
	private final DLURLHelper _dlURLHelper = Mockito.mock(DLURLHelper.class);
	private final FileEntry _fileEntry1 = Mockito.mock(FileEntry.class);
	private final FileEntry _fileEntry2 = Mockito.mock(FileEntry.class);
	private final UploadServletRequestConfigurationProvider
		_uploadServletRequestConfigurationProvider = Mockito.mock(
			UploadServletRequestConfigurationProvider.class);

}
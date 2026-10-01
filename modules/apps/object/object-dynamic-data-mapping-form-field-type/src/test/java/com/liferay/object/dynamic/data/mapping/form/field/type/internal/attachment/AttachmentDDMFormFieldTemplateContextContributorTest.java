/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.attachment;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.test.util.BaseDDMFormFieldTemplateContextContributorTestCase;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Leite
 */
public class AttachmentDDMFormFieldTemplateContextContributorTest
	extends BaseDDMFormFieldTemplateContextContributorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_objectFieldUtilMockedStatic.close();
		_permissionThreadLocalMockedStatic.close();
		_requestBackedPortletURLFactoryUtilMockedStatic.close();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_setUpAttachmentManager();
		_setUpDLAppLocalService();
		_setUpDLURLHelper();
		_setUpGroupLocalService();
		_setUpItemSelector();
		_setUpJSONFactory();
		_setUpLanguage();
		_setUpObjectEntry();
		_setUpObjectEntryLocalService();
		_setUpObjectEntryService();
		_setUpObjectFieldLocalService();
		_setUpObjectFieldUtilMockedStatic();
		_setUpPermissionThreadLocalMockedStatic();
		_setUpRequestBackedPortletURLFactoryUtil();
		_setUpUploadServletRequestConfigurationProvider();

		_ddmFormField.setDDMForm(getDDMForm());
	}

	@Test
	public void testGetParametersFileEntryProperties() throws Exception {
		_ddmFormField.setProperty("localizedObjectField", true);
		_ddmFormField.setProperty(
			"objectDefinitionExternalReferenceCode",
			_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE);
		_ddmFormField.setProperty("objectEntryId", _OBJECT_ENTRY_ID);

		_testGetParametersFileEntryProperties(_ATTACHMENT_DOWNLOAD_URL);

		String contentURL = RandomTestUtil.randomString();

		_ddmFormField.setProperty("contentURL", contentURL);

		_testGetParametersFileEntryProperties(contentURL);
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

	@Test
	@TestInfo("LPD-82932")
	public void testGetParametersURL() {

		// Company scope

		_ddmFormField.setProperty(
			ObjectFieldSettingConstants.NAME_FILE_SOURCE,
			ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA);

		long groupId = RandomTestUtil.randomLong();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyGroupId()
		).thenReturn(
			groupId
		);

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(httpServletRequest);

		PortletURL portletURL = Mockito.mock(PortletURL.class);

		String expectedURL = RandomTestUtil.randomString();

		Mockito.when(
			portletURL.toString()
		).thenReturn(
			expectedURL
		);

		Mockito.when(
			_itemSelector.getItemSelectorURL(
				Mockito.any(RequestBackedPortletURLFactory.class),
				Mockito.nullable(Group.class), Mockito.eq(groupId),
				Mockito.anyString(),
				Mockito.any(FileItemSelectorCriterion.class))
		).thenReturn(
			portletURL
		);

		Assert.assertEquals(
			expectedURL,
			MapUtil.getString(
				_attachmentDDMFormFieldTemplateContextContributor.getParameters(
					_ddmFormField, ddmFormFieldRenderingContext),
				"url"));

		// Site scope

		Mockito.reset(_itemSelector);

		_ddmFormField.setProperty("groupAware", true);

		groupId = RandomTestUtil.randomLong();

		ddmFormFieldRenderingContext.setProperty("groupId", groupId);

		expectedURL = RandomTestUtil.randomString();

		Mockito.when(
			portletURL.toString()
		).thenReturn(
			expectedURL
		);

		Mockito.when(
			_itemSelector.getItemSelectorURL(
				Mockito.any(RequestBackedPortletURLFactory.class),
				Mockito.nullable(Group.class), Mockito.eq(groupId),
				Mockito.anyString(),
				Mockito.any(FileItemSelectorCriterion.class))
		).thenReturn(
			portletURL
		);

		Assert.assertEquals(
			expectedURL,
			MapUtil.getString(
				_attachmentDDMFormFieldTemplateContextContributor.getParameters(
					_ddmFormField, ddmFormFieldRenderingContext),
				"url"));
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
		Mockito.when(
			_dlAppLocalService.getFileEntry(Mockito.anyLong())
		).thenReturn(
			_fileEntry1
		);

		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor,
			"_dlAppLocalService", _dlAppLocalService);
	}

	private void _setUpDLURLHelper() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor, "_dlURLHelper",
			_dlURLHelper);
	}

	private void _setUpGroupLocalService() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor,
			"_groupLocalService", _groupLocalService);
	}

	private void _setUpItemSelector() {
		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor, "_itemSelector",
			_itemSelector);
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

	private void _setUpObjectEntry() {
		Mockito.when(
			_objectEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);
	}

	private void _setUpObjectEntryLocalService() {
		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			_objectEntry
		);

		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor,
			"_objectEntryLocalService", _objectEntryLocalService);
	}

	private void _setUpObjectEntryService() {
		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor,
			"_objectEntryService", _objectEntryService);
	}

	private void _setUpObjectFieldLocalService() throws Exception {
		Mockito.when(
			_objectFieldLocalService.fetchObjectField(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(ObjectField.class)
		);

		ReflectionTestUtil.setFieldValue(
			_attachmentDDMFormFieldTemplateContextContributor,
			"_objectFieldLocalService", _objectFieldLocalService);
	}

	private void _setUpObjectFieldUtilMockedStatic() throws Exception {
		_objectFieldUtilMockedStatic.when(
			() -> ObjectFieldUtil.getAttachmentDownloadURL(
				Mockito.eq(_dlURLHelper), Mockito.any(FileEntry.class),
				Mockito.eq(_GROUP_ID),
				Mockito.eq(_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE),
				Mockito.eq(_objectEntry), Mockito.eq(_objectEntryService),
				Mockito.any(ObjectField.class), Mockito.eq(_permissionChecker),
				Mockito.any(ThemeDisplay.class))
		).thenReturn(
			_ATTACHMENT_DOWNLOAD_URL
		);
	}

	private void _setUpPermissionThreadLocalMockedStatic() {
		_permissionThreadLocalMockedStatic.when(
			PermissionThreadLocal::getPermissionChecker
		).thenReturn(
			_permissionChecker
		);
	}

	private void _setUpRequestBackedPortletURLFactoryUtil() {
		_requestBackedPortletURLFactoryUtilMockedStatic.when(
			() -> RequestBackedPortletURLFactoryUtil.create(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			_requestBackedPortletURLFactory
		);
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

	private void _testGetParametersFileEntryProperties(String contentURL)
		throws Exception {

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

	private static final String _ATTACHMENT_DOWNLOAD_URL =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final long _OBJECT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<ObjectFieldUtil>
		_objectFieldUtilMockedStatic = Mockito.mockStatic(
			ObjectFieldUtil.class);
	private static final MockedStatic<PermissionThreadLocal>
		_permissionThreadLocalMockedStatic = Mockito.mockStatic(
			PermissionThreadLocal.class);
	private static final MockedStatic<RequestBackedPortletURLFactoryUtil>
		_requestBackedPortletURLFactoryUtilMockedStatic = Mockito.mockStatic(
			RequestBackedPortletURLFactoryUtil.class);

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
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final ItemSelector _itemSelector = Mockito.mock(ItemSelector.class);
	private final ObjectEntry _objectEntry = Mockito.mock(ObjectEntry.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final ObjectEntryService _objectEntryService = Mockito.mock(
		ObjectEntryService.class);
	private final ObjectFieldLocalService _objectFieldLocalService =
		Mockito.mock(ObjectFieldLocalService.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final RequestBackedPortletURLFactory
		_requestBackedPortletURLFactory = Mockito.mock(
			RequestBackedPortletURLFactory.class);
	private final UploadServletRequestConfigurationProvider
		_uploadServletRequestConfigurationProvider = Mockito.mock(
			UploadServletRequestConfigurationProvider.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.document.library;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.constants.DDMFormConstants;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.item.selector.DDMUserPersonalFolderItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.MutableResourceParameters;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Queiroz
 */
public class DocumentLibraryDDMFormFieldTemplateContextContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpDDMFormInstanceLocalService();
		_setUpDLAppLocalService();
		_setUpDLURLHelper();
		_setUpFileEntry();
		_setUpGroupLocalService();
		_setUpItemSelector();
		_setUpJSONFactory();
		_setUpJSONFactoryUtil();
		_setUpParamUtil();
		_setUpPortal();
		_setUpPortletFileRepository();
		_setUpRequestBackedPortletURLFactory();
		_setUpUserLocalServiceUtil();
	}

	@After
	public void tearDown() {
		_requestBackedPortletURLFactoryUtilMockedStatic.close();
		_userLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testDDMFormPortletItemSelector() {
		_mockDDMFormPortletItemSelector();

		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			Boolean.TRUE
		);

		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				themeDisplay);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			_createDDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setPortletNamespace(
			_PORTLET_NAMESPACE_DDM_FORM);

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				new DDMFormField("field", "document_library"),
				ddmFormFieldRenderingContext);

		Assert.assertTrue(parameters.containsKey("itemSelectorURL"));
	}

	@Test
	public void testGetParametersForAllowedGuestUser() {
		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				_mockThemeDisplay());

		DDMFormField ddmFormField = new DDMFormField(
			"field", "document_library");

		ddmFormField.setProperty("allowGuestUsers", true);

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				ddmFormField, _createDDMFormFieldRenderingContext());

		Assert.assertTrue((boolean)parameters.get("allowGuestUsers"));
		Assert.assertEquals(_FORMS_FOLDER_ID, parameters.get("folderId"));

		String guestUploadURL = String.valueOf(
			parameters.get("guestUploadURL"));

		Assert.assertThat(
			guestUploadURL,
			CoreMatchers.containsString(
				"param_jakarta.portlet.action=/dynamic_data_mapping_form" +
					"/upload_file_entry"));
		Assert.assertThat(
			guestUploadURL,
			CoreMatchers.containsString(
				"param_formInstanceId=" + _FORM_INSTANCE_ID));
		Assert.assertThat(
			guestUploadURL,
			CoreMatchers.containsString("param_groupId=" + _GROUP_ID));
		Assert.assertThat(
			guestUploadURL,
			CoreMatchers.containsString("param_folderId=" + _FORMS_FOLDER_ID));
	}

	@Test
	public void testGetParametersForGuestUser() {
		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				_mockThemeDisplay());

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				new DDMFormField("field", "document_library"),
				_createDDMFormFieldRenderingContext());

		Assert.assertFalse(parameters.containsKey("folderId"));
		Assert.assertFalse(parameters.containsKey("guestUploadURL"));
	}

	@Test
	public void testGetParametersForSignedInUser() {
		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			Boolean.TRUE
		);

		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				themeDisplay);

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				new DDMFormField("field", "document_library"),
				_createDDMFormFieldRenderingContext());

		Assert.assertEquals(_PRIVATE_FOLDER_ID, parameters.get("folderId"));
		Assert.assertFalse(parameters.containsKey("guestUploadURL"));
		Assert.assertTrue(parameters.containsKey("itemSelectorURL"));
	}

	@Test
	public void testGetParametersForUserWithoutPermission() {
		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			Boolean.TRUE
		);

		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				false, themeDisplay);

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				new DDMFormField("field", "document_library"),
				_createDDMFormFieldRenderingContext());

		Assert.assertFalse(parameters.containsKey("folderId"));
		Assert.assertFalse(parameters.containsKey("itemSelectorURL"));
		Assert.assertTrue(
			(boolean)parameters.get("showUploadPermissionMessage"));
	}

	@Test
	public void testGetParametersShouldContainFileEntryURL()
		throws PortalException {

		ThemeDisplay themeDisplay = _mockThemeDisplay();

		String downloadURL = RandomTestUtil.randomString();

		Mockito.when(
			_dlURLHelper.getDownloadURL(
				_fileEntry, _fileEntry.getFileVersion(), themeDisplay,
				StringPool.BLANK)
		).thenReturn(
			downloadURL
		);

		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				themeDisplay);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			_createDDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setProperty(
			"ddmFormInstanceRecordId", RandomTestUtil.randomLong());

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				new DDMFormField("field", "document_library"),
				ddmFormFieldRenderingContext);

		Assert.assertEquals(
			String.valueOf(new TestMockLiferayPortletURL()),
			parameters.get("fileEntryURL"));

		ddmFormFieldRenderingContext.setProperty("ddmFormInstanceRecordId", 0L);

		parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				new DDMFormField("field", "document_library"),
				ddmFormFieldRenderingContext);

		Assert.assertEquals(downloadURL, parameters.get("fileEntryURL"));
	}

	@Test
	public void testGetParametersShouldContainMaximumRepetitions() {
		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				_mockThemeDisplay());

		DDMFormField ddmFormField = new DDMFormField(
			"field", "document_library");

		ddmFormField.setProperty("maximumRepetitions", 8);

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				ddmFormField, _createDDMFormFieldRenderingContext());

		Assert.assertEquals(8, parameters.get("maximumRepetitions"));
	}

	@Test
	public void testGetParametersShouldUseExistingGuestUploadURL() {
		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				_mockThemeDisplay());

		DDMFormField ddmFormField = new DDMFormField(
			"field", "document_library");

		ddmFormField.setProperty("allowGuestUsers", true);

		String expectedGuestUploadURL = RandomTestUtil.randomString();

		ddmFormField.setProperty("guestUploadURL", expectedGuestUploadURL);

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				ddmFormField, _createDDMFormFieldRenderingContext());

		Assert.assertEquals(
			expectedGuestUploadURL,
			String.valueOf(parameters.get("guestUploadURL")));
	}

	@Test
	public void testGetParametersShouldUseExistingItemSelectorURL() {
		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			Boolean.TRUE
		);

		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				themeDisplay);

		DDMFormField ddmFormField = new DDMFormField(
			"field", "document_library");

		String expectedItemSelectorURL = RandomTestUtil.randomString();

		ddmFormField.setProperty("itemSelectorURL", expectedItemSelectorURL);

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				ddmFormField, _createDDMFormFieldRenderingContext());

		Assert.assertEquals(
			expectedItemSelectorURL,
			String.valueOf(parameters.get("itemSelectorURL")));
	}

	@Test
	public void testGetParametersShouldUseFileEntryTitle() {
		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				_mockThemeDisplay());

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				new DDMFormField("field", "document_library"),
				_createDDMFormFieldRenderingContext());

		Assert.assertEquals("New Title", parameters.get("fileEntryTitle"));
	}

	@Test
	public void testGetParametersWithNullGroupShouldContainItemSelectorURL() {
		_mockGroupLocalServiceFetchGroup(null);

		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			_scopeGroup
		);

		Mockito.when(
			themeDisplay.isSignedIn()
		).thenReturn(
			Boolean.TRUE
		);

		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = _createSpy(
				themeDisplay);

		Map<String, Object> parameters =
			documentLibraryDDMFormFieldTemplateContextContributor.getParameters(
				new DDMFormField("field", "document_library"),
				_createDDMFormFieldRenderingContext());

		Assert.assertTrue(parameters.containsKey("itemSelectorURL"));
	}

	private DDMFormFieldRenderingContext _createDDMFormFieldRenderingContext() {
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			_createHttpServletRequest());
		ddmFormFieldRenderingContext.setPortletNamespace(_PORTLET_NAMESPACE);
		ddmFormFieldRenderingContext.setProperty("groupId", _GROUP_ID);
		ddmFormFieldRenderingContext.setValue(
			JSONUtil.put(
				"groupId", _GROUP_ID
			).put(
				"title", "File Title"
			).put(
				"uuid", _FILE_ENTRY_UUID
			).toString());

		return ddmFormFieldRenderingContext;
	}

	private HttpServletRequest _createHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"formInstanceId", String.valueOf(_FORM_INSTANCE_ID));

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		themeDisplay.setPermissionChecker(
			Mockito.mock(PermissionChecker.class));

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private DocumentLibraryDDMFormFieldTemplateContextContributor _createSpy(
		boolean containsPermission, ThemeDisplay themeDisplay) {

		DocumentLibraryDDMFormFieldTemplateContextContributor
			documentLibraryDDMFormFieldTemplateContextContributor = Mockito.spy(
				_documentLibraryDDMFormFieldTemplateContextContributor);

		Mockito.doReturn(
			containsPermission
		).when(
			documentLibraryDDMFormFieldTemplateContextContributor
		).containsPermission(
			Mockito.any(DDMFormFieldRenderingContext.class), Mockito.anyString()
		);

		Mockito.doReturn(
			_resourceBundle
		).when(
			documentLibraryDDMFormFieldTemplateContextContributor
		).getResourceBundle(
			Mockito.any(Locale.class)
		);

		Mockito.doReturn(
			themeDisplay
		).when(
			documentLibraryDDMFormFieldTemplateContextContributor
		).getThemeDisplay(
			Mockito.any(HttpServletRequest.class)
		);

		return documentLibraryDDMFormFieldTemplateContextContributor;
	}

	private DocumentLibraryDDMFormFieldTemplateContextContributor _createSpy(
		ThemeDisplay themeDisplay) {

		return _createSpy(true, themeDisplay);
	}

	private void _mockDDMFormPortletItemSelector() {
		Mockito.when(
			_itemSelector.getItemSelectorURL(
				Mockito.any(), Mockito.eq(_group), Mockito.eq(_GROUP_ID),
				Mockito.eq(
					_PORTLET_NAMESPACE_DDM_FORM + "selectDocumentLibrary"),
				Mockito.any(DDMUserPersonalFolderItemSelectorCriterion.class))
		).thenReturn(
			new MockLiferayPortletURL()
		);
	}

	private Folder _mockFolder(long folderId) {
		Folder folder = Mockito.mock(Folder.class);

		Mockito.when(
			folder.getFolderId()
		).thenReturn(
			folderId
		);

		return folder;
	}

	private void _mockGroupLocalServiceFetchGroup(Group group) {
		Mockito.when(
			_groupLocalService.fetchGroup(_GROUP_ID)
		).thenReturn(
			group
		);
	}

	private Repository _mockRepository() {
		Repository repository = Mockito.mock(Repository.class);

		Mockito.when(
			repository.getRepositoryId()
		).thenReturn(
			_REPOSITORY_ID
		);

		return repository;
	}

	private ThemeDisplay _mockThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			themeDisplay.getPathContext()
		).thenReturn(
			"/my/path/context/"
		);

		Mockito.when(
			themeDisplay.getPathThemeImages()
		).thenReturn(
			"/my/theme/images/"
		);

		PortletDisplay portletDisplay = Mockito.mock(PortletDisplay.class);

		Mockito.when(
			portletDisplay.getRootPortletId()
		).thenReturn(
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM
		);

		Mockito.when(
			themeDisplay.getPortletDisplay()
		).thenReturn(
			portletDisplay
		);

		User user = _mockUser();

		Mockito.when(
			themeDisplay.getUser()
		).thenReturn(
			user
		);

		return themeDisplay;
	}

	private User _mockUser() {
		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getScreenName()
		).thenReturn(
			"Test"
		);

		Mockito.when(
			user.getUserId()
		).thenReturn(
			0L
		);

		return user;
	}

	private void _setUpDDMFormInstanceLocalService() throws Exception {
		DDMFormInstanceLocalService ddmFormInstanceLocalService = Mockito.mock(
			DDMFormInstanceLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_documentLibraryDDMFormFieldTemplateContextContributor,
			"_ddmFormInstanceLocalService", ddmFormInstanceLocalService);

		Mockito.when(
			ddmFormInstanceLocalService.getDDMFormInstance(_FORM_INSTANCE_ID)
		).thenReturn(
			null
		);
	}

	private void _setUpDLAppLocalService() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_documentLibraryDDMFormFieldTemplateContextContributor,
			"_dlAppLocalService", _dlAppLocalService);

		Mockito.when(
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				_FILE_ENTRY_UUID, _GROUP_ID)
		).thenReturn(
			_fileEntry
		);

		Folder folder = _mockFolder(_PRIVATE_FOLDER_ID);

		Mockito.when(
			_dlAppLocalService.getFolder(
				_REPOSITORY_ID, _FORMS_FOLDER_ID, "Test")
		).thenReturn(
			folder
		);
	}

	private void _setUpDLURLHelper() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_documentLibraryDDMFormFieldTemplateContextContributor,
			"_dlURLHelper", _dlURLHelper);
	}

	private void _setUpFileEntry() {
		_fileEntry.setUuid(_FILE_ENTRY_UUID);
		_fileEntry.setGroupId(_GROUP_ID);

		Mockito.when(
			_fileEntry.getTitle()
		).thenReturn(
			"New Title"
		);
	}

	private void _setUpGroupLocalService() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_documentLibraryDDMFormFieldTemplateContextContributor,
			"_groupLocalService", _groupLocalService);

		_mockGroupLocalServiceFetchGroup(_group);
	}

	private void _setUpItemSelector() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_documentLibraryDDMFormFieldTemplateContextContributor,
			"_itemSelector", _itemSelector);

		Mockito.when(
			_itemSelector.getItemSelectorURL(
				Mockito.any(),
				Mockito.argThat(
					argument -> {
						if ((argument == _group) || (argument == _scopeGroup)) {
							return true;
						}

						return false;
					}),
				Mockito.eq(_GROUP_ID),
				Mockito.eq(_PORTLET_NAMESPACE + "selectDocumentLibrary"),
				Mockito.any(DDMUserPersonalFolderItemSelectorCriterion.class),
				Mockito.any(FileItemSelectorCriterion.class))
		).thenReturn(
			new MockLiferayPortletURL()
		);
	}

	private void _setUpJSONFactory() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_documentLibraryDDMFormFieldTemplateContextContributor,
			"_jsonFactory", _jsonFactory);
	}

	private void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private void _setUpParamUtil() {
		PropsUtil.setProps(Mockito.mock(Props.class));
	}

	private void _setUpPortal() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_documentLibraryDDMFormFieldTemplateContextContributor, "_portal",
			_portal);

		Mockito.when(
			_portal.getPortletNamespace(
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM)
		).thenReturn(
			_PORTLET_NAMESPACE_DDM_FORM
		);
	}

	private void _setUpPortletFileRepository() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_documentLibraryDDMFormFieldTemplateContextContributor,
			"_portletFileRepository", _portletFileRepository);

		Folder folder = _mockFolder(_FORMS_FOLDER_ID);

		Mockito.when(
			_portletFileRepository.getPortletFolder(
				_REPOSITORY_ID, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				DDMFormConstants.DDM_FORM_UPLOADED_FILES_FOLDER_NAME)
		).thenReturn(
			folder
		);

		Repository repository = _mockRepository();

		Mockito.when(
			_portletFileRepository.fetchPortletRepository(
				_GROUP_ID, DDMFormConstants.SERVICE_NAME)
		).thenReturn(
			repository
		);
	}

	private void _setUpRequestBackedPortletURLFactory() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			Mockito.mock(RequestBackedPortletURLFactory.class);

		Mockito.when(
			RequestBackedPortletURLFactoryUtil.create(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			requestBackedPortletURLFactory
		);

		Mockito.doReturn(
			new TestMockLiferayPortletURL()
		).when(
			requestBackedPortletURLFactory
		).createActionURL(
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM
		);

		Mockito.doReturn(
			new TestMockLiferayPortletURL()
		).when(
			requestBackedPortletURLFactory
		).createResourceURL(
			DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM
		);
	}

	private void _setUpUserLocalServiceUtil() throws Exception {
		User user = _mockUser();

		_userLocalServiceUtilMockedStatic.when(
			() -> UserLocalServiceUtil.getUserByExternalReferenceCode(
				DDMFormConstants.DDM_FORM_DEFAULT_USER_EXTERNAL_REFERENCE_CODE,
				_COMPANY_ID)
		).thenReturn(
			user
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _FILE_ENTRY_UUID =
		RandomTestUtil.randomString();

	private static final long _FORM_INSTANCE_ID = RandomTestUtil.randomLong();

	private static final long _FORMS_FOLDER_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _PORTLET_NAMESPACE =
		RandomTestUtil.randomString();

	private static final String _PORTLET_NAMESPACE_DDM_FORM =
		"_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_";

	private static final long _PRIVATE_FOLDER_ID = RandomTestUtil.randomLong();

	private static final long _REPOSITORY_ID = RandomTestUtil.randomLong();

	private final DLAppLocalService _dlAppLocalService = Mockito.mock(
		DLAppLocalService.class);
	private final DLURLHelper _dlURLHelper = Mockito.mock(DLURLHelper.class);
	private final DocumentLibraryDDMFormFieldTemplateContextContributor
		_documentLibraryDDMFormFieldTemplateContextContributor =
			new DocumentLibraryDDMFormFieldTemplateContextContributor();
	private final FileEntry _fileEntry = Mockito.mock(FileEntry.class);
	private final Group _group = Mockito.mock(Group.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final ItemSelector _itemSelector = Mockito.mock(ItemSelector.class);
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private final Portal _portal = Mockito.mock(Portal.class);
	private final PortletFileRepository _portletFileRepository = Mockito.mock(
		PortletFileRepository.class);
	private final MockedStatic<RequestBackedPortletURLFactoryUtil>
		_requestBackedPortletURLFactoryUtilMockedStatic = Mockito.mockStatic(
			RequestBackedPortletURLFactoryUtil.class);
	private final ResourceBundle _resourceBundle = Mockito.mock(
		ResourceBundle.class);
	private final Group _scopeGroup = Mockito.mock(Group.class);
	private final MockedStatic<UserLocalServiceUtil>
		_userLocalServiceUtilMockedStatic = Mockito.mockStatic(
			UserLocalServiceUtil.class);

	private class TestMockLiferayPortletURL extends MockLiferayPortletURL {

		@Override
		public MutableResourceParameters getResourceParameters() {
			return Mockito.mock(MutableResourceParameters.class);
		}

	}

}
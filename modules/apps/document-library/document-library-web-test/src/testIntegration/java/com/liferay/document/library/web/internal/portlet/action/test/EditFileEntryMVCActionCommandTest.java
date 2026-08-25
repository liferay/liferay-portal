/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upload.test.util.UploadTestUtil;

import jakarta.portlet.PortletException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class EditFileEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testProcessActionAddDynamicWithoutRequiredDDMFormField()
		throws Exception {

		String fileName = RandomTestUtil.randomString() + ".txt";
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		_processAction(
			_getMockLiferayPortletActionRequest(
				_CONTENT_BYTES, fileName,
				_getParameters(
					Constants.ADD_DYNAMIC, folder.getFolderId(),
					folder.getRepositoryId(), new String[0])),
			new MockLiferayPortletActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), folder.getFolderId(), fileName);

		FileVersion fileVersion = actualFileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion.getStatus());
	}

	@Test
	public void testProcessActionAddDynamicWithRequiredDDMFormField()
		throws Exception {

		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());
		DLFileEntryType dlFileEntryType = _addFileEntryTypeWithRequiredField();

		_dlAppLocalService.updateFolder(
			folder.getFolderId(), folder.getParentFolderId(), folder.getName(),
			folder.getDescription(), _getFolderServiceContext(dlFileEntryType));

		String fileName = RandomTestUtil.randomString() + ".txt";

		_processAction(
			_getMockLiferayPortletActionRequest(
				_CONTENT_BYTES, fileName,
				_getParameters(
					Constants.ADD_DYNAMIC, folder.getFolderId(),
					folder.getRepositoryId(), new String[0])),
			new MockLiferayPortletActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), folder.getFolderId(), fileName);

		FileVersion fileVersion = actualFileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, fileVersion.getStatus());
	}

	@Test
	public void testProcessActionAddDynamicWithZeroByteFile() throws Exception {
		String fileName = RandomTestUtil.randomString() + ".txt";
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		_processAction(
			_getMockLiferayPortletActionRequest(
				new byte[0], fileName,
				_getParameters(
					Constants.ADD_DYNAMIC, folder.getFolderId(),
					folder.getRepositoryId(), new String[0])),
			new MockLiferayPortletActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), folder.getFolderId(), fileName);

		FileVersion fileVersion = actualFileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion.getStatus());
	}

	@Test
	public void testProcessActionAddMultipleFileEntries() throws Exception {
		FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(), _TEMP_FOLDER_NAME,
			TempFileEntryUtil.getTempFileName("image.jpg"), _getInputStream(),
			ContentTypes.IMAGE_JPEG);

		_processAction(
			_getMockLiferayPortletActionRequest(
				_getParameters(
					Constants.ADD_MULTIPLE, tempFileEntry.getFolderId(),
					tempFileEntry.getRepositoryId(),
					new String[] {tempFileEntry.getFileName()})),
			new MockLiferayPortletActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), tempFileEntry.getFolderId(), "image.jpg");

		Assert.assertEquals("image", actualFileEntry.getTitle());
	}

	@Test
	public void testProcessActionAddMultipleFileEntriesSeveralFiles()
		throws Exception {

		FileEntry tempFileEntry1 = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(), _TEMP_FOLDER_NAME,
			TempFileEntryUtil.getTempFileName("test.jpg"), _getInputStream(),
			ContentTypes.IMAGE_JPEG);
		FileEntry tempFileEntry2 = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(), _TEMP_FOLDER_NAME,
			TempFileEntryUtil.getTempFileName("test.gif"), _getInputStream(),
			ContentTypes.IMAGE_GIF);

		_processAction(
			_getMockLiferayPortletActionRequest(
				_getParameters(
					Constants.ADD_MULTIPLE, tempFileEntry1.getFolderId(),
					tempFileEntry1.getRepositoryId(),
					new String[] {
						tempFileEntry1.getFileName(),
						tempFileEntry2.getFileName()
					})),
			new MockLiferayPortletActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), tempFileEntry1.getFolderId(), "test.jpg");

		Assert.assertEquals("test", actualFileEntry.getTitle());

		actualFileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), tempFileEntry2.getFolderId(), "test.gif");

		Assert.assertEquals("test (1)", actualFileEntry.getTitle());
	}

	@Test
	public void testProcessActionAddMultipleFileEntriesSeveralFilesSameTitleDifferentExtension()
		throws Exception {

		FileEntry tempFileEntry1 = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(), _TEMP_FOLDER_NAME,
			TempFileEntryUtil.getTempFileName("test.jpg"), _getInputStream(),
			ContentTypes.IMAGE_JPEG);
		FileEntry tempFileEntry2 = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(), _TEMP_FOLDER_NAME,
			TempFileEntryUtil.getTempFileName("test.gif"), _getInputStream(),
			ContentTypes.IMAGE_GIF);

		_processAction(
			_getMockLiferayPortletActionRequest(
				_getParameters(
					Constants.ADD_MULTIPLE, tempFileEntry1.getFolderId(),
					tempFileEntry1.getRepositoryId(),
					new String[] {
						tempFileEntry1.getFileName(),
						tempFileEntry2.getFileName()
					})),
			new MockLiferayPortletActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), tempFileEntry1.getFolderId(), "test.jpg");

		Assert.assertEquals("test", actualFileEntry.getTitle());

		actualFileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), tempFileEntry1.getFolderId(), "test.gif");

		Assert.assertEquals("test (1)", actualFileEntry.getTitle());
	}

	@Test
	public void testProcessActionAddMultipleFileEntriesSeveralFilesWithSameTitleAndExtension()
		throws Exception {

		FileEntry tempFileEntry1 = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(), _TEMP_FOLDER_NAME,
			TempFileEntryUtil.getTempFileName("image.jpg"), _getInputStream(),
			ContentTypes.IMAGE_JPEG);
		FileEntry tempFileEntry2 = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(), _TEMP_FOLDER_NAME,
			TempFileEntryUtil.getTempFileName("image.jpg"), _getInputStream(),
			ContentTypes.IMAGE_JPEG);

		_processAction(
			_getMockLiferayPortletActionRequest(
				_getParameters(
					Constants.ADD_MULTIPLE, tempFileEntry1.getFolderId(),
					tempFileEntry1.getRepositoryId(),
					new String[] {
						tempFileEntry1.getFileName(),
						tempFileEntry2.getFileName()
					})),
			new MockLiferayPortletActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), tempFileEntry1.getFolderId(), "image.jpg");

		Assert.assertEquals("image", actualFileEntry.getTitle());

		actualFileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), tempFileEntry1.getFolderId(), "image (1).jpg");

		Assert.assertEquals("image (1)", actualFileEntry.getTitle());
	}

	@Test
	public void testProcessActionAddMultipleFileEntriesWithInvalidMimetype()
		throws Exception {

		FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(), _TEMP_FOLDER_NAME,
			TempFileEntryUtil.getTempFileName("text.txt"), _getInputStream(),
			ContentTypes.TEXT_PLAIN);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DLFileEntryMimeTypeConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"fileMimeTypes", new String[] {"text/html"}
						).build())) {

			MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
				new MockLiferayPortletActionResponse();

			_processAction(
				_getMockLiferayPortletActionRequest(
					_getParameters(
						Constants.ADD_MULTIPLE, 0, _group.getGroupId(),
						new String[] {tempFileEntry.getFileName()})),
				mockLiferayPortletActionResponse);

			MockHttpServletResponse mockHttpServletResponse =
				(MockHttpServletResponse)
					mockLiferayPortletActionResponse.getHttpServletResponse();

			Assert.assertEquals(
				JSONUtil.put(
					JSONUtil.put(
						"added", false
					).put(
						"errorMessage",
						"Please enter a file with a valid mime type " +
							"(text/html)."
					).put(
						"fileName", tempFileEntry.getFileName()
					).put(
						"originalFileName", tempFileEntry.getFileName()
					)
				).toString(),
				mockHttpServletResponse.getContentAsString());
		}
	}

	@Test
	public void testProcessActionAddMultipleFileEntriesWithValidMimetype()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DLFileEntryMimeTypeConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"fileMimeTypes", new String[] {"text/plain"}
						).build())) {

			FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
				_group.getGroupId(), TestPropsValues.getUserId(),
				_TEMP_FOLDER_NAME,
				TempFileEntryUtil.getTempFileName("text.txt"),
				_getInputStream(), ContentTypes.TEXT_PLAIN);

			_processAction(
				_getMockLiferayPortletActionRequest(
					_getParameters(
						Constants.ADD_MULTIPLE, 0, _group.getGroupId(),
						new String[] {tempFileEntry.getFileName()})),
				new MockLiferayPortletActionResponse());

			FileEntry actualFileEntry =
				_dlAppLocalService.getFileEntryByFileName(
					_group.getGroupId(), 0, "text.txt");

			Assert.assertEquals("text", actualFileEntry.getTitle());
		}
	}

	@Test
	public void testProcessActionCheckIn()
		throws PortalException, PortletException {

		FileEntry initialFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN, null, null,
			null, null, ServiceContextTestUtil.getServiceContext());

		_dlAppService.checkOutFileEntry(
			initialFileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext());

		_editFileEntryMVCActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				HashMapBuilder.putAll(
					_getParameters(
						Constants.CHECKIN, initialFileEntry.getFolderId(),
						initialFileEntry.getRepositoryId(),
						new String[] {initialFileEntry.getFileName()})
				).put(
					"changeLog", new String[] {"New Version"}
				).put(
					"rowIdsFileEntry",
					new String[] {
						String.valueOf(initialFileEntry.getFileEntryId())
					}
				).put(
					"versionIncrease",
					new String[] {String.valueOf(DLVersionNumberIncrease.MAJOR)}
				).build()),
			new MockLiferayPortletActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntry(
			initialFileEntry.getFileEntryId());

		FileVersion fileVersion = actualFileEntry.getFileVersion();

		Assert.assertEquals("New Version", fileVersion.getChangeLog());
	}

	@Test
	public void testProcessActionCheckOut()
		throws PortalException, PortletException {

		FileEntry initialFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN, null, null,
			null, null, ServiceContextTestUtil.getServiceContext());

		_editFileEntryMVCActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				HashMapBuilder.putAll(
					_getParameters(
						Constants.CHECKOUT, initialFileEntry.getFolderId(),
						initialFileEntry.getRepositoryId(),
						new String[] {initialFileEntry.getFileName()})
				).put(
					"rowIdsFileEntry",
					new String[] {
						String.valueOf(initialFileEntry.getFileEntryId())
					}
				).build()),
			new MockLiferayPortletActionResponse());

		FileEntry actualFileEntry = _dlAppLocalService.getFileEntry(
			initialFileEntry.getFileEntryId());

		Assert.assertTrue(actualFileEntry.isCheckedOut());
	}

	private DLFileEntryType _addFileEntryTypeWithRequiredField()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(LocaleUtil.US),
			LocaleUtil.US);

		DDMFormTestUtil.addDDMFormFields(
			ddmForm,
			DDMFormTestUtil.createLocalizedTextDDMFormField(
				RandomTestUtil.randomString(), false, true, LocaleUtil.US));

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), DLFileEntryMetadata.class.getName(), ddmForm);

		return _dlFileEntryTypeService.addFileEntryType(
			null, _group.getGroupId(), ddmStructure.getStructureId(), null,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	private MockMultipartHttpServletRequest
		_createMockMultipartHttpServletRequest() {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setCharacterEncoding(StringPool.UTF8);
		mockMultipartHttpServletRequest.setContentType(
			StringBundler.concat(
				MediaType.MULTIPART_FORM_DATA_VALUE,
				"; boundary=WebKitFormBoundary", StringUtil.randomString()));

		return mockMultipartHttpServletRequest;
	}

	private MockMultipartHttpServletRequest
		_createMockMultipartHttpServletRequest(byte[] bytes, String fileName) {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setCharacterEncoding(StringPool.UTF8);

		String boundary = "WebKitFormBoundary" + StringUtil.randomString();

		mockMultipartHttpServletRequest.setContent(
			_getFileContent(boundary, bytes, fileName));
		mockMultipartHttpServletRequest.setContentType(
			StringBundler.concat(
				MediaType.MULTIPART_FORM_DATA_VALUE, "; boundary=", boundary));

		return mockMultipartHttpServletRequest;
	}

	private byte[] _getFileContent(
		String boundary, byte[] bytes, String fileName) {

		String start = StringBundler.concat(
			StringPool.DOUBLE_DASH, boundary,
			"\r\nContent-Disposition: form-data; name=\"file\"; filename=\"",
			fileName, "\"\r\nContent-Type: text/plain\r\n\r\n");
		String end = StringBundler.concat(
			"\r\n--", boundary, StringPool.DOUBLE_DASH);

		return ArrayUtil.append(start.getBytes(), bytes, end.getBytes());
	}

	private ServiceContext _getFolderServiceContext(
			DLFileEntryType dlFileEntryType)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute(
			"defaultFileEntryTypeId", dlFileEntryType.getPrimaryKey());
		serviceContext.setAttribute(
			"dlFileEntryTypesSearchContainerPrimaryKeys",
			String.valueOf(dlFileEntryType.getPrimaryKey()));
		serviceContext.setAttribute(
			"restrictionType",
			DLFolderConstants.RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW);

		return serviceContext;
	}

	private InputStream _getInputStream() {
		return new ByteArrayInputStream(_CONTENT_BYTES);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			byte[] bytes, String fileName, Map<String, String[]> parameters)
		throws Exception {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			_createMockMultipartHttpServletRequest(bytes, fileName);

		mockMultipartHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "/document_library/edit_file_entry");

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				mockMultipartHttpServletRequest, parameters);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockLiferayPortletActionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		return mockLiferayPortletActionRequest;
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Map<String, String[]> parameters)
		throws PortalException {

		return _getMockLiferayPortletActionRequest(
			_createMockMultipartHttpServletRequest(), parameters);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			MockMultipartHttpServletRequest mockMultipartHttpServletRequest,
			Map<String, String[]> parameters)
		throws PortalException {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest(
				mockMultipartHttpServletRequest);

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(
					DLPortletKeys.DOCUMENT_LIBRARY),
				null));

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(mockMultipartHttpServletRequest));

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			mockLiferayPortletActionRequest.setParameter(
				entry.getKey(), entry.getValue());
		}

		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private Map<String, String[]> _getParameters(
		String cmd, long folderId, long repositoryId,
		String[] tempFileEntryNames) {

		return HashMapBuilder.put(
			Constants.CMD, new String[] {cmd}
		).put(
			"displayDateAmPm", new String[] {"am"}
		).put(
			"displayDateDay", new String[] {"1"}
		).put(
			"displayDateHour", new String[] {"11"}
		).put(
			"displayDateMinute", new String[] {"00"}
		).put(
			"displayDateMonth", new String[] {"1"}
		).put(
			"displayDateYear", new String[] {"1989"}
		).put(
			"folderId", new String[] {String.valueOf(folderId)}
		).put(
			"neverExpire", new String[] {Boolean.TRUE.toString()}
		).put(
			"neverReview", new String[] {Boolean.TRUE.toString()}
		).put(
			"repositoryId", new String[] {String.valueOf(repositoryId)}
		).put(
			"selectedFileName", tempFileEntryNames
		).put(
			"workflowAction",
			new String[] {String.valueOf(WorkflowConstants.ACTION_PUBLISH)}
		).build();
	}

	private ThemeDisplay _getThemeDisplay(
			MockMultipartHttpServletRequest mockMultipartHttpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(mockMultipartHttpServletRequest);

		long groupId = _group.getGroupId();

		themeDisplay.setScopeGroupId(groupId);

		themeDisplay.setServerName("localhost");
		themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
		themeDisplay.setSiteGroupId(groupId);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _processAction(
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest,
			MockLiferayPortletActionResponse mockLiferayPortletActionResponse)
		throws Exception {

		try {
			_setUpUploadPortletRequest(mockLiferayPortletActionRequest);

			_editFileEntryMVCActionCommand.processAction(
				mockLiferayPortletActionRequest,
				mockLiferayPortletActionResponse);
		}
		finally {
			if (_mvcActionCommandPortal != null) {
				ReflectionTestUtil.setFieldValue(
					_editFileEntryMVCActionCommand, "_portal",
					_mvcActionCommandPortal);
			}
		}
	}

	private void _setUpUploadPortletRequest(
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest) {

		_mvcActionCommandPortal = ReflectionTestUtil.getFieldValue(
			_editFileEntryMVCActionCommand, "_portal");

		ReflectionTestUtil.setFieldValue(
			_editFileEntryMVCActionCommand, "_portal",
			ProxyUtil.newProxyInstance(
				EditFileEntryMVCActionCommandTest.class.getClassLoader(),
				new Class<?>[] {Portal.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "getUploadPortletRequest")) {

						LiferayPortletRequest liferayPortletRequest =
							_portal.getLiferayPortletRequest(
								mockLiferayPortletActionRequest);

						return UploadTestUtil.createUploadPortletRequest(
							_portal.getUploadServletRequest(
								liferayPortletRequest.getHttpServletRequest()),
							liferayPortletRequest,
							_portal.getPortletNamespace(
								liferayPortletRequest.getPortletName()));
					}

					return method.invoke(_portal, args);
				}));
	}

	private static final byte[] _CONTENT_BYTES = "test".getBytes();

	private static final String _TEMP_FOLDER_NAME =
		"com.liferay.document.library.web.internal.portlet.action." +
			"EditFileEntryMVCActionCommand";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileEntryTypeService _dlFileEntryTypeService;

	@Inject(filter = "mvc.command.name=/document_library/edit_file_entry")
	private MVCActionCommand _editFileEntryMVCActionCommand;

	@DeleteAfterTestRun
	private Group _group;

	private Portal _mvcActionCommandPortal;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

}
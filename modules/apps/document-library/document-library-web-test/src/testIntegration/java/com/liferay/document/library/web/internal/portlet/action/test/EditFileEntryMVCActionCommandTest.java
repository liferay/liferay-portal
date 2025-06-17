/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
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
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upload.test.util.UploadTestUtil;

import jakarta.portlet.PortletException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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

	private InputStream _getInputStream() {
		return new ByteArrayInputStream("test".getBytes());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Map<String, String[]> parameters)
		throws PortalException {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			_createMockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(
					DLPortletKeys.DOCUMENT_LIBRARY),
				null));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest(
				mockMultipartHttpServletRequest);

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
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setServerName("localhost");
		themeDisplay.setServerPort(8080);
		themeDisplay.setSiteGroupId(_group.getGroupId());
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

	private static final String _TEMP_FOLDER_NAME =
		"com.liferay.document.library.web.internal.portlet.action." +
			"EditFileEntryMVCActionCommand";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

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
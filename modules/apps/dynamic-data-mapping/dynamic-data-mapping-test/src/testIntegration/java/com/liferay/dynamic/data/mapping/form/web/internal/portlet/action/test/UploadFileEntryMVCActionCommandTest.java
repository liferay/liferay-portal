/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMFormConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormUtil;
import com.liferay.petra.memory.DeleteFileFinalizeAction;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upload.test.util.UploadTestUtil;
import com.liferay.upload.UploadHandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class UploadFileEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_ddmFormInstance = DDMFormInstanceTestUtil.addDDMFormInstance(
			_group, TestPropsValues.getUserId());

		_uploadHandler = ReflectionTestUtil.getFieldValue(
			_mvcActionCommand, "_uploadHandler");

		ReflectionTestUtil.setFieldValue(
			_uploadHandler, "_portal",
			ProxyUtil.newProxyInstance(
				UploadFileEntryMVCActionCommandTest.class.getClassLoader(),
				new Class<?>[] {Portal.class},
				(proxy, method, args) -> {
					if (!Objects.equals(
							method.getName(), "getUploadPortletRequest")) {

						return method.invoke(_portal, args);
					}

					return UploadTestUtil.createUploadPortletRequest(
						UploadTestUtil.createUploadServletRequest(
							_getMockHttpServletRequest(),
							HashMapBuilder.put(
								"file", new FileItem[] {_getFileItem()}
							).build(),
							new HashMap<>()),
						null, RandomTestUtil.randomString());
				}));

		User user = DDMFormUtil.getDDMFormDefaultUser(
			TestPropsValues.getCompanyId());

		Repository repository = _portletFileRepository.addPortletRepository(
			_group.getGroupId(), DDMFormConstants.SERVICE_NAME,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Folder folder = _portletFileRepository.addPortletFolder(
			user.getUserId(), repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			DDMFormConstants.DDM_FORM_UPLOADED_FILES_FOLDER_NAME,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_folderId = folder.getFolderId();

		_oldDLFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, user.getUserId(), _group.getGroupId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "file.txt",
			ContentTypes.TEXT_PLAIN, "file.txt", StringUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, -1, new HashMap<>(), null,
			new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY), 0,
			null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() throws Exception {
		ReflectionTestUtil.setFieldValue(_uploadHandler, "_portal", _portal);
	}

	@Test
	public void testProcessAction() throws Exception {
		Assert.assertNotNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				_oldDLFileEntry.getFileEntryId()));

		JSONObject jsonObject = _processAction();

		Assert.assertNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				_oldDLFileEntry.getFileEntryId()));

		JSONObject fileJSONObject = jsonObject.getJSONObject("file");

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
			fileJSONObject.getLong("fileEntryId"));

		Assert.assertNotNull(dlFileEntry);

		User user = _userLocalService.getUserByExternalReferenceCode(
			DDMFormConstants.DDM_FORM_DEFAULT_USER_EXTERNAL_REFERENCE_CODE,
			TestPropsValues.getCompanyId());

		Assert.assertEquals(user.getUserId(), dlFileEntry.getUserId());

		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		Assert.assertEquals(
			Collections.emptyList(),
			_resourcePermissionLocalService.
				getAvailableResourcePermissionActionIds(
					TestPropsValues.getCompanyId(), DLFileEntry.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(dlFileEntry.getFileEntryId()),
					role.getRoleId(),
					_resourceActions.getModelResourceActions(
						DLFileEntry.class.getName())));
	}

	@Test
	public void testProcessActionWithInvalidMimetype() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DLFileEntryMimeTypeConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"fileMimeTypes", new String[] {"image/jpeg"}
						).build())) {

			JSONObject jsonObject = _processAction();

			JSONObject errorJSONObject = jsonObject.getJSONObject("error");

			Assert.assertEquals(
				"Please enter a file with a valid mime type (image/jpeg).",
				errorJSONObject.get("message"));
		}
	}

	private FileItem _getFileItem() throws Exception {
		Path path = Files.createTempFile(null, ".txt");

		Files.write(path, "".getBytes());

		File file = path.toFile();

		FinalizeManager.register(
			file, new DeleteFileFinalizeAction(file.getAbsolutePath()),
			FinalizeManager.PHANTOM_REFERENCE_FACTORY);

		return ProxyUtil.newDelegateProxyInstance(
			FileItem.class.getClassLoader(), FileItem.class,
			new Object() {

				public String getFileName() {
					return file.getName();
				}

				public InputStream getInputStream() {
					return null;
				}

				public long getSize() {
					return 0;
				}

				public File getStoreLocation() {
					return file;
				}

				public boolean isInMemory() {
					return true;
				}

			},
			null);
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"folderId", String.valueOf(_folderId));
		mockHttpServletRequest.addParameter(
			"formInstanceId",
			String.valueOf(_ddmFormInstance.getFormInstanceId()));
		mockHttpServletRequest.addParameter(
			"groupId", String.valueOf(_ddmFormInstance.getGroupId()));
		mockHttpServletRequest.addParameter(
			"oldFileEntryId", String.valueOf(_oldDLFileEntry.getFileEntryId()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		return mockHttpServletRequest;
	}

	private JSONObject _processAction() throws Exception {
		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			new MockLiferayPortletActionRequest(_getMockHttpServletRequest()),
			mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		return _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DDMFormInstance _ddmFormInstance;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	private long _folderId;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(
		filter = "mvc.command.name=/dynamic_data_mapping_form/upload_file_entry"
	)
	private MVCActionCommand _mvcActionCommand;

	private DLFileEntry _oldDLFileEntry;

	@Inject
	private Portal _portal;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private UploadHandler _uploadHandler;

	@Inject
	private UserLocalService _userLocalService;

}
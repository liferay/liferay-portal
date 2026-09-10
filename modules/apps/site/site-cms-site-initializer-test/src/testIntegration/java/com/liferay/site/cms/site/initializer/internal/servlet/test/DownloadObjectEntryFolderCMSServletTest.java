/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class DownloadObjectEntryFolderCMSServletTest
	extends BaseCMSServletTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDownloadBulkAction() throws Exception {
		_testDownloadBulkActionWithBulkActionItems();
		_testDownloadBulkActionWithMultipleAttachmentObjectFields();
		_testDownloadBulkActionWithObjectEntryDownloadPermission();
		_testDownloadBulkActionWithSelectAll();
	}

	@Test
	public void testDownloadFolder() throws Exception {
		_testDownloadFolderEmpty();
		_testDownloadFolderWithObjectEntryDownloadPermission();
		_testDownloadFolderWithoutPermissions();
		_testDownloadFolderWithPermissions();
	}

	private long _addFileEntry() throws Exception {
		DLFolder dlFolder = DLTestUtil.addDLFolder(depotEntry.getGroupId());

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), dlFolder.getGroupId(),
			dlFolder.getRepositoryId(), dlFolder.getFolderId(),
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			TestDataConstants.TEST_BYTE_ARRAY.length, null, null, null,
			ServiceContextTestUtil.getServiceContext(dlFolder.getGroupId()));

		return dlFileEntry.getFileEntryId();
	}

	private ObjectEntry _addObjectEntry(
			long objectDefinitionId, long objectEntryFolderId,
			ServiceContext serviceContext)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(), objectDefinitionId,
			objectEntryFolderId, "en_US",
			HashMapBuilder.<String, Serializable>put(
				"file", String.valueOf(_addFileEntry())
			).put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			serviceContext);
	}

	private void _addObjectEntryDownloadPermissionEntries() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", group.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		_deniedObjectEntryFolder = _addObjectEntryFolder(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		_parentObjectEntryFolder = _addObjectEntryFolder(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		_allowedObjectEntry = _addObjectEntry(
			objectDefinition.getObjectDefinitionId(),
			_parentObjectEntryFolder.getObjectEntryFolderId(), serviceContext);

		_deniedObjectEntry = _addObjectEntry(
			objectDefinition.getObjectDefinitionId(),
			_parentObjectEntryFolder.getObjectEntryFolderId(), serviceContext);

		_user = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleLocalServiceUtil.addUserRoles(
			_user.getUserId(), new long[] {_role.getRoleId()});

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_deniedObjectEntry.getObjectEntryId()),
			_role.getRoleId(), new String[] {ActionKeys.VIEW});

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), "file");

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_allowedObjectEntry.getObjectEntryId()),
			_role.getRoleId(),
			new String[] {
				ActionKeys.VIEW, objectField.getAttachmentDownloadActionKey()
			});
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			long parentObjectEntryFolderId)
		throws Exception {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			StringUtil.randomString(), depotEntry.getGroupId(),
			TestPropsValues.getUserId(), parentObjectEntryFolderId,
			RandomTestUtil.randomString(), null, StringUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private FileEntry _addTempFileEntry(ObjectDefinition objectDefinition)
		throws Exception {

		return TempFileEntryUtil.addTempFileEntry(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".txt"),
			FileUtil.createTempFile(RandomTestUtil.randomBytes()),
			ContentTypes.APPLICATION_TEXT);
	}

	private void _assertObjectEntryDownloadPermissionZipContents(
			MockHttpServletResponse mockHttpServletResponse)
		throws Exception {

		Assert.assertEquals(
			ContentTypes.APPLICATION_ZIP,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(
				new ByteArrayInputStream(
					mockHttpServletResponse.getContentAsByteArray()))) {

			List<String> zipEntryNames = zipReader.getEntries();

			String allowedFileName = _getFileName(_allowedObjectEntry);
			String deniedFileName = _getFileName(_deniedObjectEntry);

			Assert.assertTrue(
				ListUtil.exists(
					zipEntryNames,
					zipEntryName -> zipEntryName.endsWith(allowedFileName)));
			Assert.assertFalse(
				ListUtil.exists(
					zipEntryNames,
					zipEntryName -> zipEntryName.endsWith(deniedFileName)));
		}
	}

	private ObjectField _createAttachmentObjectField(String name) {
		return ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
			ObjectFieldConstants.DB_TYPE_LONG, false, false, null,
			RandomTestUtil.randomString(), name,
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS
				).value(
					"txt"
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_FILE_SOURCE
				).value(
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
				).value(
					"100"
				).build()),
			false);
	}

	private String _getFileName(ObjectEntry objectEntry) throws Exception {
		return _getFileName(objectEntry, "file");
	}

	private String _getFileName(ObjectEntry objectEntry, String fieldName)
		throws Exception {

		Map<String, Serializable> values = objectEntry.getValues();

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			GetterUtil.getLong(values.get(fieldName)));

		return dlFileEntry.getFileName();
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			byte[] content, String method, long objectEntryFolderId, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL,
			"http://localhost:" + PortalUtil.getPortalServerPort(false) + "/");
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(mockHttpServletRequest, user));
		mockHttpServletRequest.setAttribute(WebKeys.USER, user);

		if (content != null) {
			mockHttpServletRequest.setContent(content);
		}

		mockHttpServletRequest.setContextPath("/o");
		mockHttpServletRequest.setMethod(method);

		if (objectEntryFolderId != 0) {
			mockHttpServletRequest.setRequestURI(
				StringBundler.concat(
					"/o/cmd/download-folder/",
					_portal.getClassNameId(ObjectEntryFolder.class), "/",
					objectEntryFolderId));
		}

		mockHttpServletRequest.setServletPath("/cms/download-folder");

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(
			MockHttpServletRequest mockHttpServletRequest, User user)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setRequest(mockHttpServletRequest);

		return themeDisplay;
	}

	private void _testDownloadBulkActionWithBulkActionItems() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", group.getCompanyId());
		ObjectEntryFolder parentObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_FILES", depotEntry.getGroupId(),
					depotEntry.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition.getObjectDefinitionId(),
			parentObjectEntryFolder.getObjectEntryFolderId(), serviceContext);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				JSONUtil.put(
					"bulkActionItems",
					JSONUtil.put(
						JSONUtil.put(
							"classExternalReferenceCode",
							objectEntry.getExternalReferenceCode()
						).put(
							"className", objectEntry.getModelClassName()
						).put(
							"classPK", objectEntry.getObjectEntryId()
						).put(
							"name", objectEntry.getTitleValue()
						))
				).put(
					"selectionScope", JSONUtil.put("selectAll", false)
				).put(
					"type", "DownloadBulkAction"
				).toString(
				).getBytes(),
				HttpMethods.POST, 0, TestPropsValues.getUser());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			ContentTypes.APPLICATION_ZIP,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	private void _testDownloadBulkActionWithMultipleAttachmentObjectFields()
		throws Exception {

		ObjectField firstObjectField = _createAttachmentObjectField(
			"firstAttachment");
		ObjectField secondObjectField = _createAttachmentObjectField(
			"secondAttachment");

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Arrays.asList(firstObjectField, secondObjectField));

		FileEntry firstFileEntry = _addTempFileEntry(_objectDefinition);
		FileEntry secondFileEntry = _addTempFileEntry(_objectDefinition);

		ObjectEntry objectEntry =
			_objectEntryLocalService.addOrUpdateObjectEntry(
				RandomTestUtil.randomString(), 0, TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>put(
					"firstAttachment", firstFileEntry.getFileEntryId()
				).put(
					"secondAttachment", secondFileEntry.getFileEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				JSONUtil.put(
					"bulkActionItems",
					JSONUtil.put(
						JSONUtil.put(
							"classExternalReferenceCode",
							objectEntry.getExternalReferenceCode()
						).put(
							"className", objectEntry.getModelClassName()
						).put(
							"classPK", objectEntry.getObjectEntryId()
						).put(
							"name", objectEntry.getTitleValue()
						))
				).put(
					"selectionScope", JSONUtil.put("selectAll", false)
				).put(
					"type", "DownloadBulkAction"
				).toString(
				).getBytes(),
				HttpMethods.POST, 0, TestPropsValues.getUser());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			ContentTypes.APPLICATION_ZIP,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(
				new ByteArrayInputStream(
					mockHttpServletResponse.getContentAsByteArray()))) {

			List<String> zipEntryNames = zipReader.getEntries();

			Assert.assertEquals(
				zipEntryNames.toString(), 1, zipEntryNames.size());

			String firstFileName = _getFileName(objectEntry, "firstAttachment");
			String secondFileName = _getFileName(
				objectEntry, "secondAttachment");

			Assert.assertTrue(
				zipEntryNames.toString(),
				ListUtil.exists(
					zipEntryNames,
					zipEntryName -> zipEntryName.endsWith(firstFileName)));
			Assert.assertFalse(
				zipEntryNames.toString(),
				ListUtil.exists(
					zipEntryNames,
					zipEntryName -> zipEntryName.endsWith(secondFileName)));
		}
	}

	private void _testDownloadBulkActionWithObjectEntryDownloadPermission()
		throws Exception {

		_addObjectEntryDownloadPermissionEntries();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(
					JSONUtil.put(
						"bulkActionItems",
						JSONUtil.putAll(
							JSONUtil.put(
								"classExternalReferenceCode",
								_allowedObjectEntry.getExternalReferenceCode()
							).put(
								"className",
								_allowedObjectEntry.getModelClassName()
							).put(
								"classPK",
								_allowedObjectEntry.getObjectEntryId()
							).put(
								"name", _allowedObjectEntry.getTitleValue()
							),
							JSONUtil.put(
								"classExternalReferenceCode",
								_deniedObjectEntry.getExternalReferenceCode()
							).put(
								"className",
								_deniedObjectEntry.getModelClassName()
							).put(
								"classPK", _deniedObjectEntry.getObjectEntryId()
							).put(
								"name", _deniedObjectEntry.getTitleValue()
							),
							JSONUtil.put(
								"classExternalReferenceCode",
								_deniedObjectEntryFolder.
									getExternalReferenceCode()
							).put(
								"className",
								_deniedObjectEntryFolder.getModelClassName()
							).put(
								"classPK",
								_deniedObjectEntryFolder.
									getObjectEntryFolderId()
							).put(
								"name", _deniedObjectEntryFolder.getName()
							))
					).put(
						"selectionScope", JSONUtil.put("selectAll", false)
					).put(
						"type", "DownloadBulkAction"
					).toString(
					).getBytes(),
					HttpMethods.POST, 0, _user);

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			_assertObjectEntryDownloadPermissionZipContents(
				mockHttpServletResponse);
		}
	}

	private void _testDownloadBulkActionWithSelectAll() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", group.getCompanyId());
		ObjectEntryFolder parentObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_FILES", depotEntry.getGroupId(),
					depotEntry.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		_addObjectEntry(
			objectDefinition.getObjectDefinitionId(),
			parentObjectEntryFolder.getObjectEntryFolderId(), serviceContext);

		ObjectEntryFolder objectEntryFolder1 = _addObjectEntryFolder(
			parentObjectEntryFolder.getObjectEntryFolderId());

		_addObjectEntry(
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder1.getObjectEntryFolderId(), serviceContext);

		ObjectEntryFolder objectEntryFolder2 = _addObjectEntryFolder(
			parentObjectEntryFolder.getObjectEntryFolderId());

		_addObjectEntry(
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder2.getObjectEntryFolderId(), serviceContext);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				JSONUtil.put(
					"selectionScope", JSONUtil.put("selectAll", true)
				).put(
					"type", "DownloadBulkAction"
				).toString(
				).getBytes(),
				HttpMethods.POST, 0, TestPropsValues.getUser());

		mockHttpServletRequest.setParameter(
			"filter",
			"cmsRoot eq true and cmsSection eq 'files' and status in (0, 2, " +
				"3)");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			ContentTypes.APPLICATION_ZIP,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	private void _testDownloadFolderEmpty() throws Exception {
		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				null, HttpMethods.GET,
				objectEntryFolder.getObjectEntryFolderId(),
				TestPropsValues.getUser());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			ContentTypes.APPLICATION_ZIP,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	private void _testDownloadFolderWithObjectEntryDownloadPermission()
		throws Exception {

		_addObjectEntryDownloadPermissionEntries();

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), ObjectEntryFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_parentObjectEntryFolder.getObjectEntryFolderId()),
			_role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(
					null, HttpMethods.GET,
					_parentObjectEntryFolder.getObjectEntryFolderId(), _user);

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			_assertObjectEntryDownloadPermissionZipContents(
				mockHttpServletResponse);
		}
	}

	private void _testDownloadFolderWithoutPermissions() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", group.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		ObjectEntryFolder parentObjectEntryFolder = _addObjectEntryFolder(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		_addObjectEntry(
			objectDefinition.getObjectDefinitionId(),
			parentObjectEntryFolder.getObjectEntryFolderId(), serviceContext);

		ObjectEntryFolder childObjectEntryFolder = _addObjectEntryFolder(
			parentObjectEntryFolder.getObjectEntryFolderId());

		_addObjectEntry(
			objectDefinition.getObjectDefinitionId(),
			childObjectEntryFolder.getObjectEntryFolderId(), serviceContext);

		User user = UserTestUtil.addUser();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleLocalServiceUtil.addUserRoles(
			user.getUserId(), new long[] {role.getRoleId()});

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), ObjectEntryFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(parentObjectEntryFolder.getObjectEntryFolderId()),
			role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(
					null, HttpMethods.GET,
					parentObjectEntryFolder.getObjectEntryFolderId(), user);

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			Assert.assertEquals(
				ContentTypes.APPLICATION_ZIP,
				mockHttpServletResponse.getContentType());
			Assert.assertEquals(
				HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

			String subfolderPrefix = StringBundler.concat(
				parentObjectEntryFolder.getName(), StringPool.SLASH,
				childObjectEntryFolder.getName(), StringPool.SLASH);

			try (ZipReader zipReader = _zipReaderFactory.getZipReader(
					new ByteArrayInputStream(
						mockHttpServletResponse.getContentAsByteArray()))) {

				List<String> zipEntryNames = zipReader.getEntries();

				Assert.assertFalse(
					ListUtil.exists(
						zipEntryNames,
						zipEntryName -> zipEntryName.startsWith(
							subfolderPrefix)));
			}
		}
	}

	private void _testDownloadFolderWithPermissions() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", group.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		ObjectEntryFolder parentObjectEntryFolder = _addObjectEntryFolder(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		_addObjectEntry(
			objectDefinition.getObjectDefinitionId(),
			parentObjectEntryFolder.getObjectEntryFolderId(), serviceContext);

		ObjectEntryFolder childObjectEntryFolder = _addObjectEntryFolder(
			parentObjectEntryFolder.getObjectEntryFolderId());

		_addObjectEntry(
			objectDefinition.getObjectDefinitionId(),
			childObjectEntryFolder.getObjectEntryFolderId(), serviceContext);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				null, HttpMethods.GET,
				parentObjectEntryFolder.getObjectEntryFolderId(),
				TestPropsValues.getUser());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			ContentTypes.APPLICATION_ZIP,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		String subfolderPrefix = StringBundler.concat(
			parentObjectEntryFolder.getName(), StringPool.SLASH,
			childObjectEntryFolder.getName(), StringPool.SLASH);

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(
				new ByteArrayInputStream(
					mockHttpServletResponse.getContentAsByteArray()))) {

			List<String> zipEntryNames = zipReader.getEntries();

			Assert.assertTrue(
				ListUtil.exists(
					zipEntryNames,
					zipEntryName -> zipEntryName.startsWith(subfolderPrefix)));
		}
	}

	private ObjectEntry _allowedObjectEntry;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ObjectEntry _deniedObjectEntry;
	private ObjectEntryFolder _deniedObjectEntryFolder;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	private ObjectEntryFolder _parentObjectEntryFolder;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.site.cms.site.initializer.internal.servlet.DownloadObjectEntryFolderCMSServlet"
	)
	private Servlet _servlet;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

}
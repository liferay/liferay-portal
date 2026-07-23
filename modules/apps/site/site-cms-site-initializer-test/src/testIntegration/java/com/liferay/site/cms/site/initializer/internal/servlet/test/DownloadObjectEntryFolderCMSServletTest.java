/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2024-09
 */

package com.liferay.site.cms.site.initializer.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
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
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.HashMap;
import java.util.List;

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
		_testDownloadBulkActionWithSelectAll();
	}

	@Test
	public void testDownloadFolder() throws Exception {
		_testDownloadFolderEmpty();
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

	private ObjectEntryFolder _addObjectEntryFolder(
			long parentObjectEntryFolderId)
		throws Exception {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			StringUtil.randomString(), depotEntry.getGroupId(),
			TestPropsValues.getUserId(), parentObjectEntryFolderId,
			RandomTestUtil.randomString(), null, StringUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
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

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.site.cms.site.initializer.internal.servlet.DownloadObjectEntryFolderCMSServlet"
	)
	private Servlet _servlet;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

}
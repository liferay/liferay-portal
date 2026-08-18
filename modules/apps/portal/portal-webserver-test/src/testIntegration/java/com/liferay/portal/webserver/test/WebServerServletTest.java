/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webserver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.ImageConstants;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.repository.liferayrepository.LiferayRepository;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.webserver.WebServerServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
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
public class WebServerServletTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserLocalServiceUtil.getGuestUser(_group.getCompanyId());
	}

	@Test
	public void testGetDefaultImage() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.USER,
			UserLocalServiceUtil.getGuestUser(_group.getCompanyId()));
		mockHttpServletRequest.setParameter("img_id", "0");
		mockHttpServletRequest.setPathInfo("/account_logo");

		Image image = ReflectionTestUtil.invoke(
			_webServerServlet, "getDefaultImage",
			new Class<?>[] {HttpServletRequest.class, long.class},
			mockHttpServletRequest, 0L);

		Assert.assertEquals(ImageToolUtil.getDefaultOrganizationLogo(), image);
	}

	@Test
	public void testGetImage() throws Exception {
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".html", ContentTypes.TEXT_HTML,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.USER, _user);
		mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockHttpServletRequest.setParameter("uuid", fileEntry.getUuid());

		Assert.assertNotNull(
			ReflectionTestUtil.invoke(
				_webServerServlet, "getImage",
				new Class<?>[] {HttpServletRequest.class, boolean.class},
				mockHttpServletRequest, false));

		fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".png", ContentTypes.IMAGE_PNG,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.USER, _user);
		mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockHttpServletRequest.setParameter("uuid", fileEntry.getUuid());

		Assert.assertNotNull(
			ReflectionTestUtil.invoke(
				_webServerServlet, "getImage",
				new Class<?>[] {HttpServletRequest.class, boolean.class},
				mockHttpServletRequest, false));
	}

	@Test
	public void testGetStatus() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.login.web.internal.configuration." +
						"AuthLoginConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"promptEnabled", false
					).build())) {

			MockHttpServletResponse mockHttpServletResponse =
				_serviceRestrictedDocumentRequest();

			Assert.assertEquals(
				HttpServletResponse.SC_NOT_FOUND,
				mockHttpServletResponse.getStatus());
		}

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.login.web.internal.configuration." +
						"AuthLoginConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"promptEnabled", true
					).build())) {

			MockHttpServletResponse mockHttpServletResponse =
				_serviceRestrictedDocumentRequest();

			Assert.assertEquals(
				HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE,
				mockHttpServletResponse.getHeader(HttpHeaders.CACHE_CONTROL));
			Assert.assertEquals(
				HttpServletResponse.SC_MOVED_TEMPORARILY,
				mockHttpServletResponse.getStatus());
		}
	}

	@FeatureFlag("LPD-82960")
	@Test
	public void testMaintenanceModeCompanyAdminCanAccessDocument()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_enableMaintenanceMode(_group);

		MockHttpServletResponse mockHttpServletResponse =
			_serviceDocumentRequest(fileEntry, TestPropsValues.getUser());

		Assert.assertNotEquals(
			HttpServletResponse.SC_SERVICE_UNAVAILABLE,
			mockHttpServletResponse.getStatus());
	}

	@FeatureFlag("LPD-82960")
	@Test
	public void testMaintenanceModeGuestUserGets503ForDocument()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_enableMaintenanceMode(_group);

		MockHttpServletResponse mockHttpServletResponse =
			_serviceDocumentRequest(fileEntry, null);

		Assert.assertEquals(
			HttpServletResponse.SC_SERVICE_UNAVAILABLE,
			mockHttpServletResponse.getStatus());
	}

	@FeatureFlag("LPD-82960")
	@Test
	public void testMaintenanceModeRegularUserGets503ForDocument()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_enableMaintenanceMode(_group);

		_regularUser = UserTestUtil.addUser();

		MockHttpServletResponse mockHttpServletResponse =
			_serviceDocumentRequest(fileEntry, _regularUser);

		Assert.assertEquals(
			HttpServletResponse.SC_SERVICE_UNAVAILABLE,
			mockHttpServletResponse.getStatus());
	}

	@FeatureFlag("LPD-82960")
	@Test
	public void testMaintenanceModeSiteAdminCanAccessDocument()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_enableMaintenanceMode(_group);

		_siteAdminUser = UserTestUtil.addGroupAdminUser(_group);

		MockHttpServletResponse mockHttpServletResponse =
			_serviceDocumentRequest(fileEntry, _siteAdminUser);

		Assert.assertNotEquals(
			HttpServletResponse.SC_SERVICE_UNAVAILABLE,
			mockHttpServletResponse.getStatus());
	}

	@Test
	public void testService() throws Exception {
		_testServiceGroupIdUUID();
		_testServicePortletFileEntry();
	}

	@Test
	public void testWriteImage() throws Exception {
		Image image1 = ImageLocalServiceUtil.createImage(0);

		image1.setType(ImageConstants.TYPE_PNG);
		image1.setTextObj(TestDataConstants.TEST_BYTE_ARRAY);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockHttpServletRequest.setParameter(
			"uuid", RandomTestUtil.randomString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		ReflectionTestUtil.invoke(
			_webServerServlet, "writeImage",
			new Class<?>[] {
				Image.class, HttpServletRequest.class, HttpServletResponse.class
			},
			image1, mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertNull(
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION));

		Image image2 = ImageLocalServiceUtil.createImage(0);

		image2.setType("svg");
		image2.setTextObj(TestDataConstants.TEST_BYTE_ARRAY);

		mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockHttpServletRequest.setParameter(
			"uuid", RandomTestUtil.randomString());

		mockHttpServletResponse = new MockHttpServletResponse();

		ReflectionTestUtil.invoke(
			_webServerServlet, "writeImage",
			new Class<?>[] {
				Image.class, HttpServletRequest.class, HttpServletResponse.class
			},
			image2, mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT,
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION));

		Image image3 = ImageLocalServiceUtil.createImage(0);

		image3.setType("html");
		image3.setTextObj(TestDataConstants.TEST_BYTE_ARRAY);

		mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockHttpServletRequest.setParameter(
			"uuid", RandomTestUtil.randomString());

		mockHttpServletResponse = new MockHttpServletResponse();

		ReflectionTestUtil.invoke(
			_webServerServlet, "writeImage",
			new Class<?>[] {
				Image.class, HttpServletRequest.class, HttpServletResponse.class
			},
			image3, mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT,
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION));
	}

	private FileEntry _addFileEntry() throws Exception {
		Repository repository = _repositoryLocalService.addRepository(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			PortalUtil.getClassNameId(LiferayRepository.class.getName()),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), new UnicodeProperties(), true,
			ServiceContextTestUtil.getServiceContext());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), repository.getRepositoryId(),
			folder.getFolderId(), RandomTestUtil.randomString() + ".txt",
			ContentTypes.TEXT_PLAIN, TestDataConstants.TEST_BYTE_ARRAY, null,
			null, null, serviceContext);
	}

	private void _enableMaintenanceMode(Group group) throws Exception {
		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			GroupConstants.TYPE_SETTINGS_KEY_MAINTENANCE_MODE,
			Boolean.TRUE.toString());

		_groupLocalService.updateGroup(
			group.getGroupId(), group.getParentGroupId(), group.getNameMap(),
			group.getDescriptionMap(), group.getType(),
			typeSettingsUnicodeProperties.toString(),
			group.isManualMembership(), group.getMembershipRestriction(),
			group.getFriendlyURL(), group.isInheritContent(), false,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _removeResourcePermission(
			long fileEntryId, String roleName, String actionId)
		throws Exception {

		Role guestRole = RoleLocalServiceUtil.getRole(
			_group.getCompanyId(), roleName);

		ResourcePermissionLocalServiceUtil.removeResourcePermission(
			_group.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(fileEntryId),
			guestRole.getRoleId(), actionId);
	}

	private MockHttpServletResponse _serviceDocumentRequest(
			FileEntry fileEntry, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (user != null) {
			mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		}

		mockHttpServletRequest.setRequestURI(
			StringBundler.concat(
				"/", fileEntry.getRepositoryId(), "/", fileEntry.getUuid()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webServerServlet.service(
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private MockHttpServletResponse _serviceRestrictedDocumentRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.USER,
			UserLocalServiceUtil.getGuestUser(_group.getCompanyId()));

		Repository repository = _repositoryLocalService.addRepository(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			PortalUtil.getClassNameId(LiferayRepository.class.getName()),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), new UnicodeProperties(), true,
			ServiceContextTestUtil.getServiceContext());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), repository.getRepositoryId(),
			folder.getFolderId(), RandomTestUtil.randomString() + ".txt",
			ContentTypes.TEXT_PLAIN, TestDataConstants.TEST_BYTE_ARRAY, null,
			null, null, serviceContext);

		_removeResourcePermission(
			fileEntry.getFileEntryId(), RoleConstants.GUEST,
			ActionKeys.DOWNLOAD);
		_removeResourcePermission(
			fileEntry.getFileEntryId(), RoleConstants.OWNER,
			ActionKeys.DOWNLOAD);
		_removeResourcePermission(
			fileEntry.getFileEntryId(), RoleConstants.SITE_MEMBER,
			ActionKeys.DOWNLOAD);

		mockHttpServletRequest.setRequestURI(
			StringBundler.concat(
				"/", repository.getRepositoryId(), "/", fileEntry.getUuid()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webServerServlet.service(
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private void _testService(String requestURI) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());
		mockHttpServletRequest.setRequestURI(requestURI);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webServerServlet.service(
			mockHttpServletRequest, mockHttpServletResponse);

		String contentDisposition = mockHttpServletResponse.getHeader(
			HttpHeaders.CONTENT_DISPOSITION);

		Assert.assertTrue(
			contentDisposition.startsWith(
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT));

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	private void _testServiceGroupIdUUID() throws Exception {
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".html", ContentTypes.TEXT_HTML,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_testService(
			StringBundler.concat(
				"/", fileEntry.getGroupId(), "/", fileEntry.getUuid()));
	}

	private void _testServicePortletFileEntry() throws Exception {
		FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			WebServerServletTest.class.getName(), _group.getGroupId(),
			"TEST_PORTLET", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			TestDataConstants.TEST_BYTE_ARRAY,
			RandomTestUtil.randomString() + ".html", ContentTypes.TEXT_HTML,
			false);

		_testService(
			StringBundler.concat(
				"/portlet_file_entry/", _group.getGroupId(), "/test.html/",
				fileEntry.getUuid()));
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private User _regularUser;

	@Inject
	private RepositoryLocalService _repositoryLocalService;

	@DeleteAfterTestRun
	private User _siteAdminUser;

	private User _user;
	private final WebServerServlet _webServerServlet = new WebServerServlet();

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2024-09
 */

package com.liferay.site.cms.site.initializer.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

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
public class DownloadObjectEntryFolderServletTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_depotEntry = _addDepotEntry();
	}

	@Test
	public void testDownloadBulkAction() throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		ObjectEntryFolder objectEntryFolder = _addObjectFolderEntry(
			_depotEntry.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				JSONUtil.put(
					"bulkActionItems",
					JSONUtil.put(
						JSONUtil.put(
							"classExternalReferenceCode",
							objectEntryFolder.getExternalReferenceCode()
						).put(
							"className",
							"com.liferay.object.model.ObjectEntryFolder"
						).put(
							"classPK",
							objectEntryFolder.getObjectEntryFolderId()
						).put(
							"name", objectEntryFolder.getName()
						))
				).put(
					"selectAll", false
				).put(
					"type", "DownloadBulkAction"
				).toString(
				).getBytes(),
				HttpMethods.POST, 0);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			ContentTypes.APPLICATION_ZIP,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		PermissionThreadLocal.setPermissionChecker(originalPermissionChecker);
		PrincipalThreadLocal.setName(originalName);
	}

	@Test
	public void testDownloadFolder() throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		ObjectEntryFolder objectEntryFolder = _addObjectFolderEntry(
			_depotEntry.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				null, HttpMethods.GET,
				objectEntryFolder.getObjectEntryFolderId());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			ContentTypes.APPLICATION_ZIP,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		PermissionThreadLocal.setPermissionChecker(originalPermissionChecker);
		PrincipalThreadLocal.setName(originalName);
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(_group.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
	}

	private ObjectEntryFolder _addObjectFolderEntry(long groupId)
		throws Exception {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			StringUtil.randomString(), groupId, TestPropsValues.getUserId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			RandomTestUtil.randomString(), null, StringUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			byte[] content, String method, long objectEntryFolderId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "http://localhost:8080/");
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));
		mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUserId());

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
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setRequest(mockHttpServletRequest);

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.site.cms.site.initializer.internal.servlet.DownloadObjectEntryFolderServlet"
	)
	private Servlet _servlet;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class ContentManagerActionsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.fetchCompany(
			TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		FileEntry fileEntry = _addFileEntry(_group);

		_layoutClassedModelUsage =
			_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
				_group.getGroupId(),
				_portal.getClassNameId(FileEntry.class.getName()),
				fileEntry.getFileEntryId(), StringPool.BLANK,
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				_layout.getPlid(), new ServiceContext());

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					_layoutClassedModelUsage.getClassName());

		_layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					_layoutClassedModelUsage.getClassName(),
					new ClassPKInfoItemIdentifier(
						_layoutClassedModelUsage.getClassPK())));
	}

	@Test
	public void testGetActionsJSONObjectWithoutPermissions() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
				_company, _group, _layout);

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			JSONObject actionsJSONObject = ReflectionTestUtil.invoke(
				_contentManager, "_getActionsJSONObject",
				new Class<?>[] {
					LayoutClassedModelUsage.class,
					LayoutDisplayPageObjectProvider.class, ThemeDisplay.class,
					HttpServletRequest.class
				},
				_layoutClassedModelUsage, _layoutDisplayPageObjectProvider,
				themeDisplay, mockHttpServletRequest);

			Assert.assertFalse(actionsJSONObject.has("editImage"));
			Assert.assertFalse(actionsJSONObject.has("editURL"));
			Assert.assertFalse(actionsJSONObject.has("permissionsURL"));
			Assert.assertFalse(actionsJSONObject.has("viewUsagesURL"));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testGetActionsJSONObjectWithPermissions() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_layoutClassedModelUsage.getClassPK()),
			role.getRoleId(),
			new String[] {
				ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW
			});

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE,
				new MockLiferayResourceResponse());

			ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
				_company, _group, _layout);

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			JSONObject actionsJSONObject = ReflectionTestUtil.invoke(
				_contentManager, "_getActionsJSONObject",
				new Class<?>[] {
					LayoutClassedModelUsage.class,
					LayoutDisplayPageObjectProvider.class, ThemeDisplay.class,
					HttpServletRequest.class
				},
				_layoutClassedModelUsage, _layoutDisplayPageObjectProvider,
				themeDisplay, mockHttpServletRequest);

			Assert.assertTrue(actionsJSONObject.has("editImage"));
			Assert.assertTrue(actionsJSONObject.has("editURL"));
			Assert.assertTrue(actionsJSONObject.has("permissionsURL"));
			Assert.assertTrue(actionsJSONObject.has("viewUsagesURL"));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private FileEntry _addFileEntry(Group group) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		return _dlAppLocalService.addFileEntry(
			null, serviceContext.getUserId(), folder.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, "liferay".getBytes(), null, null, null,
			serviceContext);
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.content.page.editor.web.internal.manager.ContentManager",
		type = Inject.NoType.class
	)
	private Object _contentManager;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private LayoutClassedModelUsage _layoutClassedModelUsage;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	private LayoutDisplayPageObjectProvider<?> _layoutDisplayPageObjectProvider;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class DeleteLayoutUtilityPageEntryPreviewMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		Repository repository = _portletFileRepository.addPortletRepository(
			_group.getGroupId(), LayoutAdminPortletKeys.GROUP_PAGES,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_fileEntry = _portletFileRepository.addPortletFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(), null, 0,
			LayoutAdminPortletKeys.GROUP_PAGES, repository.getDlFolderId(),
			new byte[0], StringUtil.randomString() + ".png",
			ContentTypes.IMAGE_PNG, false);
	}

	@Test
	@TestInfo("LPD-74557")
	public void testDoProcessAction() throws Exception {
		String name = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fileEntry.getFileEntryId(), false, name,
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, null,
				serviceContext);

		FileEntry fileEntry = _portletFileRepository.addPortletFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			LayoutUtilityPageEntry.class.getName(),
			layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
			LayoutAdminPortletKeys.GROUP_PAGES, 0, new byte[0], "test.png",
			ContentTypes.IMAGE_PNG, false);

		layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
				fileEntry.getFileEntryId(), serviceContext);

		ReflectionTestUtil.invoke(
			_deleteLayoutUtilityPageEntryPreviewMVCActionCommand,
			"doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				layoutUtilityPageEntry, TestPropsValues.getUser()),
			new MockLiferayPortletActionResponse());

		layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.fetchLayoutUtilityPageEntry(
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId());

		Assert.assertEquals(0, layoutUtilityPageEntry.getPreviewFileEntryId());
	}

	@Test
	@TestInfo("LPD-74557")
	public void testDoProcessActionWithoutPermissions() throws Exception {
		String name = RandomTestUtil.randomString();

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_fileEntry.getFileEntryId(), false, name,
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), LayoutUtilityPageEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId()),
			role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> ReflectionTestUtil.invoke(
					_deleteLayoutUtilityPageEntryPreviewMVCActionCommand,
					"doProcessAction",
					new Class<?>[] {ActionRequest.class, ActionResponse.class},
					_getMockLiferayPortletActionRequest(
						layoutUtilityPageEntry, user),
					new MockLiferayPortletActionResponse()));
		}
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			LayoutUtilityPageEntry layoutUtilityPageEntry, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setParameter(
			"layoutUtilityPageEntryId",
			String.valueOf(
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId()));

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_admin/delete_layout_utility_page_entry_preview"
	)
	private MVCActionCommand
		_deleteLayoutUtilityPageEntryPreviewMVCActionCommand;

	private FileEntry _fileEntry;
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
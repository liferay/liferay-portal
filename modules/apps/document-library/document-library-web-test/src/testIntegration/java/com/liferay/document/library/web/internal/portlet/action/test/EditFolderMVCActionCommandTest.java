/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class EditFolderMVCActionCommandTest {

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
	@TestInfo({"LPD-113625", "LPS-128366"})
	public void testProcessAction() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			Constants.CMD, Constants.ADD);
		mockLiferayPortletActionRequest.addParameter(
			"inputPermissionsViewRole", RoleConstants.OWNER);

		String name = RandomTestUtil.randomString();

		mockLiferayPortletActionRequest.addParameter("name", name);

		mockLiferayPortletActionRequest.addParameter(
			"parentFolderId",
			String.valueOf(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));
		mockLiferayPortletActionRequest.addParameter(
			"repositoryId", String.valueOf(_group.getGroupId()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, new MockActionResponse());

		Folder folder = _dlAppLocalService.getFolder(
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			name);

		Role guestRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.GUEST);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				_group.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(folder.getFolderId()), guestRole.getRoleId(),
				ActionKeys.VIEW));

		Role siteMemberRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.SITE_MEMBER);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				_group.getCompanyId(), DLFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(folder.getFolderId()),
				siteMemberRole.getRoleId(), ActionKeys.VIEW));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.portlet.action.EditFolderMVCActionCommand"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}
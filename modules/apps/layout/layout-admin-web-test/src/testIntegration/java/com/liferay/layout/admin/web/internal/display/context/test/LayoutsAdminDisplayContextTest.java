/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class LayoutsAdminDisplayContextTest {

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
	public void testAvailableActions() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		Group group = _groupLocalService.getGroup(
			company.getCompanyId(), GroupConstants.GUEST);

		_layoutLocalService.deleteLayouts(
			group.getGroupId(), false,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(group);

		Layout layout2 = LayoutTestUtil.addTypeContentLayout(group);

		List<String> availableActions = _getAvailableActions(group, layout2);

		Assert.assertTrue(availableActions.contains("changePermissions"));
		Assert.assertFalse(availableActions.contains("convertSelectedPages"));
		Assert.assertTrue(availableActions.contains("deleteSelectedPages"));
		Assert.assertTrue(availableActions.contains("exportTranslation"));

		_layoutLocalService.deleteLayout(layout2);

		availableActions = _getAvailableActions(group, layout1);

		Assert.assertTrue(availableActions.contains("changePermissions"));
		Assert.assertTrue(availableActions.contains("convertSelectedPages"));
		Assert.assertFalse(availableActions.contains("deleteSelectedPages"));
		Assert.assertFalse(availableActions.contains("exportTranslation"));
	}

	@Test
	public void testGetEditOrViewLayoutURLInLiveGroup() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		GroupTestUtil.enableLocalStaging(_group);

		String editOrViewLayoutURL = _getEditOrViewLayoutURL(_group, layout);

		Assert.assertFalse(editOrViewLayoutURL.contains(Constants.EDIT));
	}

	@Test
	public void testGetEditOrViewLayoutURLInStagingGroup() throws Exception {
		GroupTestUtil.enableLocalStaging(_group);

		Group stagingGroup = _group.getStagingGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(stagingGroup);

		String editOrViewLayoutURL = _getEditOrViewLayoutURL(
			stagingGroup, layout);

		Assert.assertTrue(editOrViewLayoutURL.contains(Constants.EDIT));
	}

	@Test
	public void testGetEditOrViewLayoutURLWithContentLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		String editOrViewLayoutURL = _getEditOrViewLayoutURL(_group, layout);

		Assert.assertTrue(editOrViewLayoutURL.contains(Constants.EDIT));
	}

	@Test
	public void testGetEditOrViewLayoutURLWithoutEditPermission()
		throws Exception {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			User user = UserTestUtil.addGroupUser(
				_group, RoleConstants.SITE_MEMBER);

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			String editOrViewLayoutURL = _getEditOrViewLayoutURL(
				_group, layout);

			Assert.assertFalse(editOrViewLayoutURL.contains(Constants.EDIT));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testGetEditOrViewLayoutURLWithPortletLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		String editOrViewLayoutURL = _getEditOrViewLayoutURL(_group, layout);

		Assert.assertFalse(editOrViewLayoutURL.contains(Constants.EDIT));
	}

	private List<String> _getAvailableActions(Group group, Layout layout)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(group);

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"LAYOUT_PAGE_LAYOUT_ADMIN_DISPLAY_CONTEXT"),
			"getAvailableActions", new Class<?>[] {Layout.class}, layout);
	}

	private String _getEditOrViewLayoutURL(Group group, Layout layout)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(group);

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"LAYOUT_PAGE_LAYOUT_ADMIN_DISPLAY_CONTEXT"),
			"getEditOrViewLayoutURL", new Class<?>[] {Layout.class}, layout);
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			Group group)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG, null);
		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, group.getCompanyId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletRenderRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.admin.web.internal.portlet.GroupPagesPortlet"
	)
	private Portlet _portlet;

}
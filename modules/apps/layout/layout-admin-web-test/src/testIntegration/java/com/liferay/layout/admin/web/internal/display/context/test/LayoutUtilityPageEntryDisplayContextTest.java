/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Bárbara Cabrera
 */
@RunWith(Arquillian.class)
public class LayoutUtilityPageEntryDisplayContextTest {

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
	public void testGetSearchContainerWithKeywords() throws Exception {
		_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomLong(), 0, false, "custom 404",
			LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, 0,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
		_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomLong(), 0, false, "custom 500",
			LayoutUtilityPageEntryConstants.TYPE_SC_INTERNAL_SERVER_ERROR, 0,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter("keywords", "custom");

		SearchContainer<LayoutUtilityPageEntry> searchContainer =
			_getSearchContainer(mockLiferayPortletRenderRequest);

		Assert.assertEquals(2, searchContainer.getTotal());

		mockLiferayPortletRenderRequest.setParameter("keywords", "404");

		searchContainer = _getSearchContainer(mockLiferayPortletRenderRequest);

		Assert.assertEquals(1, searchContainer.getTotal());
	}

	@Test
	public void testGetSearchContainerWithoutPermissions() throws Exception {
		LayoutUtilityPageEntry layoutUtilityPageEntry1 =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomLong(), 0, false, "custom 404",
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Role guestRole = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		ResourcePermissionLocalServiceUtil.removeResourcePermission(
			TestPropsValues.getCompanyId(),
			LayoutUtilityPageEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				layoutUtilityPageEntry1.getLayoutUtilityPageEntryId()),
			guestRole.getRoleId(), ActionKeys.VIEW);

		LayoutUtilityPageEntry layoutUtilityPageEntry2 =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomLong(), 0, false, "custom 500",
				LayoutUtilityPageEntryConstants.TYPE_SC_INTERNAL_SERVER_ERROR,
				0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		ResourcePermissionLocalServiceUtil.removeResourcePermission(
			TestPropsValues.getCompanyId(),
			LayoutUtilityPageEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				layoutUtilityPageEntry2.getLayoutUtilityPageEntryId()),
			guestRole.getRoleId(), ActionKeys.VIEW);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			Company company = _companyLocalService.fetchCompany(
				TestPropsValues.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(company.getGuestUser()));

			SearchContainer<LayoutUtilityPageEntry> searchContainer =
				_getSearchContainer(_getMockLiferayPortletRenderRequest());

			Assert.assertEquals(0, searchContainer.getTotal());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, _group.getCompanyId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setScopeGroupId(_group.getGroupId());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter("type", "utility-page");

		return mockLiferayPortletRenderRequest;
	}

	private SearchContainer<LayoutUtilityPageEntry> _getSearchContainer(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.layout.admin.web.internal.display.context." +
					"LayoutUtilityPageEntryDisplayContext"),
			"getLayoutUtilityPageEntrySearchContainer", new Class<?>[0]);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.admin.web.internal.portlet.GroupPagesPortlet"
	)
	private Portlet _portlet;

}
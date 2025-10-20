/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.manager.RecentGroupManager;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Adolfo Pérez
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class RecentGroupManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_addCMSGroup();

		_group = GroupTestUtil.addGroup();

		LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testAddRecentGroup() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		_recentGroupManager.addRecentGroup(
			mockHttpServletRequest,
			_groupLocalService.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS));

		Assert.assertEquals(
			Collections.emptyList(),
			_recentGroupManager.getRecentGroups(mockHttpServletRequest));

		_recentGroupManager.addRecentGroup(mockHttpServletRequest, _group);

		Assert.assertEquals(
			Collections.singletonList(_group),
			_recentGroupManager.getRecentGroups(mockHttpServletRequest));
	}

	private void _addCMSGroup() throws Exception {

		// These tests require the instance to be created with the feature
		// flag LPD-17564 enabled. On CI, feature flags are enabled on
		// demand for each test, but not during instance initialization.
		// Until the feature flag LPD-17564 is removed, we need an explicit CMS
		// group creation.

		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		if (group != null) {
			return;
		}

		Role role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		if (role == null) {
			_roleLocalService.addRole(
				null, TestPropsValues.getUserId(), null, 0,
				RoleConstants.SITE_MEMBER, null, null,
				RoleConstants.TYPE_REGULAR, null, null);
		}

		GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, GroupConstants.CMS);
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockPortletRequest mockPortletRequest = new MockPortletRequest();

		mockPortletRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST, mockPortletRequest);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setSignedIn(true);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockPortletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());

		return mockHttpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private RecentGroupManager _recentGroupManager;

	@Inject
	private RoleLocalService _roleLocalService;

}
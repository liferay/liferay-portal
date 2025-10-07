/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.taglib.servlet.taglib.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbUtil;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class BreadcrumbUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
		_locale = _portal.getSiteDefaultLocale(_group);
	}

	@Test
	public void testGetEmptyLayoutBreadcrumbEntries() throws Exception {
		Layout layout = LayoutTestUtil.addTypeEmptyLayout(_group);

		_setUpThemeDisplay(_group, layout);

		List<BreadcrumbEntry> breadcrumbEntries =
			BreadcrumbUtil.getLayoutBreadcrumbEntries(
				_mockHttpServletRequest, _themeDisplay);

		Assert.assertEquals(
			breadcrumbEntries.toString(), 1, breadcrumbEntries.size());

		BreadcrumbEntry breadcrumbEntry = breadcrumbEntries.get(0);

		Assert.assertFalse(breadcrumbEntry.isBrowsable());

		Assert.assertEquals(
			layout.getName(_locale), breadcrumbEntry.getTitle());
	}

	@Test
	public void testGetGuestGroupBreadcrumbEntry() throws Exception {
		_setUpThemeDisplay(_group, _layout);

		Group guestGroup = _groupLocalService.getGroup(
			_group.getCompanyId(), GroupConstants.GUEST);

		Assert.assertNotNull(
			_layoutService.fetchFirstLayout(
				guestGroup.getGroupId(), false, true));

		_assertBreadcrumbEntry(
			BreadcrumbUtil.getGuestGroupBreadcrumbEntry(_themeDisplay),
			_company.getName(),
			_portal.getLayoutSetFriendlyURL(
				guestGroup.getPublicLayoutSet(), _themeDisplay));
	}

	@Test
	public void testGetLayoutBreadcrumbEntries() throws Exception {
		_setUpThemeDisplay(_group, _layout);

		List<BreadcrumbEntry> breadcrumbEntries =
			BreadcrumbUtil.getLayoutBreadcrumbEntries(
				_mockHttpServletRequest, _themeDisplay);

		Assert.assertEquals(
			breadcrumbEntries.toString(), 1, breadcrumbEntries.size());

		_assertBreadcrumbEntry(
			breadcrumbEntries.get(0), _layout.getName(_locale),
			_portal.getLayoutFullURL(_layout, _themeDisplay));
	}

	@Test
	public void testGetParentGroupBreadcrumbEntries() throws Exception {
		Group childGroup = GroupTestUtil.addGroup(_group.getGroupId());
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			_setUpThemeDisplay(
				childGroup, LayoutTestUtil.addTypePortletLayout(childGroup));

			RoleTestUtil.removeResourcePermission(
				RoleConstants.GUEST, Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_layout.getPlid()), ActionKeys.VIEW);

			List<BreadcrumbEntry> breadcrumbEntries =
				BreadcrumbUtil.getParentGroupBreadcrumbEntries(_themeDisplay);

			Assert.assertEquals(
				breadcrumbEntries.toString(), 1, breadcrumbEntries.size());

			_assertBreadcrumbEntry(
				breadcrumbEntries.get(0), _group.getDescriptiveName(_locale),
				_portal.getLayoutSetFriendlyURL(
					_group.getPublicLayoutSet(), _themeDisplay));

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(_company.getGuestUser());

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_themeDisplay.setPermissionChecker(permissionChecker);

			breadcrumbEntries = BreadcrumbUtil.getParentGroupBreadcrumbEntries(
				_themeDisplay);

			Assert.assertEquals(
				breadcrumbEntries.toString(), 1, breadcrumbEntries.size());

			_assertBreadcrumbEntry(
				breadcrumbEntries.get(0), _group.getDescriptiveName(_locale),
				null);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_groupLocalService.deleteGroup(childGroup);
		}
	}

	@Test
	public void testGetScopeGroupBreadcrumbEntry() throws Exception {
		_setUpThemeDisplay(_group, _layout);

		_assertBreadcrumbEntry(
			BreadcrumbUtil.getScopeGroupBreadcrumbEntry(_themeDisplay),
			_group.getDescriptiveName(_locale),
			_portal.getLayoutSetFriendlyURL(
				_group.getPublicLayoutSet(), _themeDisplay));
	}

	private void _assertBreadcrumbEntry(
		BreadcrumbEntry breadcrumbEntry, String expectedTitle,
		String expectedURL) {

		Assert.assertEquals(
			Validator.isNotNull(expectedURL), breadcrumbEntry.isBrowsable());
		Assert.assertEquals(expectedTitle, breadcrumbEntry.getTitle());
		Assert.assertEquals(expectedURL, breadcrumbEntry.getURL());
	}

	private void _setUpThemeDisplay(Group group, Layout layout)
		throws Exception {

		_mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_company, group, layout);

		_mockHttpServletRequest.setAttribute(
			"ORIGINAL_HTTP_SERVLET_REQUEST", _mockHttpServletRequest);

		_themeDisplay = (ThemeDisplay)_mockHttpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_themeDisplay.setLanguageId(LocaleUtil.toLanguageId(_locale));
		_themeDisplay.setLocale(_locale);

		_themeDisplay.setRequest(_mockHttpServletRequest);
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private LayoutService _layoutService;

	private Locale _locale;
	private MockHttpServletRequest _mockHttpServletRequest;

	@Inject
	private Portal _portal;

	private ThemeDisplay _themeDisplay;

}
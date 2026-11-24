/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.product.navigation.control.menu.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class LayoutActionsProductNavigationControlMenuEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	@TestInfo("LPS-137155")
	public void testGetDropdownItems1() throws Exception {
		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		_productNavigationControlMenuEntry.includeIcon(
			httpServletRequest, new MockHttpServletResponse());

		Object object = httpServletRequest.getAttribute(
			"LAYOUT_ACTIONS_DISPLAY_CONTEXT");

		List<DropdownItem> dropdownItems = _getActionDropdownItems(
			ReflectionTestUtil.invoke(
				object, "getDropdownItems", new Class<?>[0]));

		Assert.assertEquals(dropdownItems.toString(), 5, dropdownItems.size());

		String[] actions = {
			"Configure", "Preview in a New Tab", "Convert to Page Template",
			"Permissions", "Delete"
		};

		for (int i = 0; i < dropdownItems.size(); i++) {
			DropdownItem dropdownItem = dropdownItems.get(i);

			Assert.assertEquals(actions[i], dropdownItem.get("label"));
		}
	}

	@Test
	@TestInfo("LPD-80135")
	public void testGetDropdownItems2() throws Exception {
		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		_productNavigationControlMenuEntry.includeIcon(
			httpServletRequest, new MockHttpServletResponse());

		Object object = httpServletRequest.getAttribute(
			"LAYOUT_ACTIONS_DISPLAY_CONTEXT");

		List<DropdownItem> dropdownItems = _getActionDropdownItems(
			ReflectionTestUtil.invoke(
				object, "getDropdownItems", new Class<?>[0]));

		DropdownItem previewDropdownItem = null;

		for (DropdownItem dropdownItem : dropdownItems) {
			if (StringUtil.equals(
					(String)dropdownItem.get("label"),
					"Preview in a New Tab")) {

				previewDropdownItem = dropdownItem;

				break;
			}
		}

		Assert.assertNotNull(previewDropdownItem);

		String url = (String)previewDropdownItem.get("href");

		Assert.assertTrue(url, url.contains("p_l_id="));
	}

	@Test
	@TestInfo("LPD-117394")
	public void testGetDropdownItemsWithLaunchDraftLayout() throws Exception {
		Layout launchDraftLayout = LayoutTestUtil.addLaunchDraftLayout(
			RandomTestUtil.randomLong(), _layout);

		_user = UserTestUtil.addUser();

		_testGetDropdownItemsWithLaunchDraftLayout(launchDraftLayout, _user);

		_testGetDropdownItemsWithLaunchDraftLayout(
			launchDraftLayout, TestPropsValues.getUser(), "Preview Launch",
			"Delete from Launch");
	}

	private List<DropdownItem> _getActionDropdownItems(
		List<DropdownItem> dropdownItems) {

		List<DropdownItem> allDropdownItems = new ArrayList<>();

		for (DropdownItem dropdownItem : dropdownItems) {
			if (!StringUtil.equals((String)dropdownItem.get("type"), "group")) {
				allDropdownItems.add(dropdownItem);

				continue;
			}

			allDropdownItems.addAll(
				(List<DropdownItem>)dropdownItem.get("items"));
		}

		return allDropdownItems;
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		return _getHttpServletRequest(_layout, TestPropsValues.getUser());
	}

	private HttpServletRequest _getHttpServletRequest(Layout layout, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, _portal.getPortalURL(layout, themeDisplay));
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private void _testGetDropdownItemsWithLaunchDraftLayout(
			Layout layout, User user, String... expectedLabels)
		throws Exception {

		HttpServletRequest httpServletRequest = _getHttpServletRequest(
			layout, user);

		_productNavigationControlMenuEntry.includeIcon(
			httpServletRequest, new MockHttpServletResponse());

		Object object = httpServletRequest.getAttribute(
			"LAYOUT_ACTIONS_DISPLAY_CONTEXT");

		List<DropdownItem> dropdownItems = _getActionDropdownItems(
			ReflectionTestUtil.invoke(
				object, "getDropdownItems", new Class<?>[0]));

		for (int i = 0; i < expectedLabels.length; i++) {
			DropdownItem dropdownItem = dropdownItems.get(i);

			Assert.assertEquals(
				dropdownItems.toString(), expectedLabels[i],
				dropdownItem.get("label"));
		}

		Assert.assertEquals(
			dropdownItems.toString(), expectedLabels.length,
			dropdownItems.size());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "component.name=com.liferay.layout.admin.web.internal.product.navigation.control.menu.LayoutActionsProductNavigationControlMenuEntry"
	)
	private ProductNavigationControlMenuEntry
		_productNavigationControlMenuEntry;

	@DeleteAfterTestRun
	private User _user;

}
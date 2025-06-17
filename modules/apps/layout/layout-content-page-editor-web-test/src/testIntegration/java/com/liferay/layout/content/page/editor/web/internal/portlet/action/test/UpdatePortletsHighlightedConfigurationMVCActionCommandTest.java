/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.content.page.editor.web.internal.portlet.constants.LayoutContentPageEditorWebPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.WebAppPool;

import jakarta.portlet.ActionRequest;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class UpdatePortletsHighlightedConfigurationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testUpdatePortletsHighlightedConfiguration() throws Exception {
		try {
			User user = UserTestUtil.addGroupAdminUser(_group);

			ServiceContextThreadLocal.pushServiceContext(
				ServiceContextTestUtil.getServiceContext(
					_group, user.getUserId()));

			String[] defaultHighlightedPortletIds =
				_getDefaultHighlightedPortletIds();

			_assertUpdateHighlightedPortlet(
				ArrayUtil.append(
					defaultHighlightedPortletIds,
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET),
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
				true, user);

			_assertUpdateHighlightedPortlet(
				defaultHighlightedPortletIds,
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
				false, user);

			_assertUpdateHighlightedPortlet(
				ArrayUtil.subset(
					defaultHighlightedPortletIds, 1,
					defaultHighlightedPortletIds.length),
				defaultHighlightedPortletIds[0], false, user);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testUpdatePortletsHighlightedConfigurationAssertPreferencesByUser()
		throws Exception {

		try {
			User user1 = UserTestUtil.addGroupAdminUser(_group);

			ServiceContextThreadLocal.pushServiceContext(
				ServiceContextTestUtil.getServiceContext(
					_group, user1.getUserId()));

			String[] defaultHighlightedPortletIds =
				_getDefaultHighlightedPortletIds();

			_assertUpdateHighlightedPortlet(
				ArrayUtil.subset(
					defaultHighlightedPortletIds, 1,
					defaultHighlightedPortletIds.length),
				defaultHighlightedPortletIds[0], false, user1);

			User user2 = UserTestUtil.addGroupAdminUser(_group);

			ServiceContextThreadLocal.pushServiceContext(
				ServiceContextTestUtil.getServiceContext(
					_group, user2.getUserId()));

			_assertUpdateHighlightedPortlet(
				ArrayUtil.append(
					defaultHighlightedPortletIds,
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET),
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
				true, user2);

			_assertUpdateHighlightedPortlet(
				defaultHighlightedPortletIds,
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
				false, user2);

			ServiceContextThreadLocal.pushServiceContext(
				ServiceContextTestUtil.getServiceContext(
					_group, user1.getUserId()));

			_assertUpdateHighlightedPortlet(
				ArrayUtil.append(
					ArrayUtil.subset(
						defaultHighlightedPortletIds, 1,
						defaultHighlightedPortletIds.length),
					LayoutContentPageEditorWebPortletKeys.
						LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET),
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
				true, user1);

			_assertUpdateHighlightedPortlet(
				ArrayUtil.subset(
					defaultHighlightedPortletIds, 1,
					defaultHighlightedPortletIds.length),
				LayoutContentPageEditorWebPortletKeys.
					LAYOUT_CONTENT_PAGE_EDITOR_WEB_TEST_PORTLET,
				false, user1);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private void _assertUpdateHighlightedPortlet(
			String[] expectedHighlightedPortletIds, String portletId,
			boolean highlighted, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(user);

		mockLiferayPortletActionRequest.addParameter("portletId", portletId);
		mockLiferayPortletActionRequest.addParameter(
			"highlighted", String.valueOf(highlighted));

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updatePortletsHighlightedConfiguration",
			new Class<?>[] {ActionRequest.class},
			mockLiferayPortletActionRequest);

		Assert.assertTrue(jsonObject.has("highlightedPortlets"));
		Assert.assertFalse(jsonObject.has("error"));

		JSONArray highlightedPortletsJSONArray = jsonObject.getJSONArray(
			"highlightedPortlets");

		Assert.assertEquals(
			expectedHighlightedPortletIds.length,
			highlightedPortletsJSONArray.length());

		for (int i = 0; i < highlightedPortletsJSONArray.length(); i++) {
			JSONObject portletJSONObject =
				highlightedPortletsJSONArray.getJSONObject(i);

			Assert.assertTrue(
				ArrayUtil.contains(
					expectedHighlightedPortletIds,
					portletJSONObject.getString("portletId")));
		}
	}

	private String[] _getDefaultHighlightedPortletIds() {
		PortletCategory rootPortletCategory = (PortletCategory)WebAppPool.get(
			_company.getCompanyId(), WebKeys.PORTLET_CATEGORY);

		PortletCategory highlightedPortletCategory =
			rootPortletCategory.getCategory("category.highlighted");

		Set<String> defaultHighlightedPortletIds =
			highlightedPortletCategory.getPortletIds();

		return defaultHighlightedPortletIds.toArray(new String[0]);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockLiferayPortletActionRequest.setAttribute(WebKeys.LAYOUT, _layout);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(user));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay(User user) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setLocale(PortalUtil.getSiteDefaultLocale(_group));

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setRealUser(user);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/update_portlets_highlighted_configuration"
	)
	private MVCActionCommand _mvcActionCommand;

}
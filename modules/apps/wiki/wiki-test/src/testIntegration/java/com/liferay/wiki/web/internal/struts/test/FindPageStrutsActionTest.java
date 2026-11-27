/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.test.util.WikiTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class FindPageStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addPortletToLayout(_layout, WikiPortletKeys.WIKI);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGuestPermissions(false);

		WikiNode wikiNode = WikiTestUtil.addNode(_group.getGroupId());

		_wikiPage = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), wikiNode.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			serviceContext);
	}

	@Test
	@TestInfo("LPD-116227")
	public void testExecute() throws Exception {
		String redirect = _execute(TestPropsValues.getUser());

		Assert.assertTrue(redirect.contains(_wikiPage.getTitle()));
	}

	@Test
	@TestInfo("LPD-116227")
	public void testExecuteWithoutViewPermission() throws Exception {
		Assert.assertEquals(
			_NO_SUCH_ENTRY_REDIRECT,
			_execute(UserLocalServiceUtil.getGuestUser(_group.getCompanyId())));
	}

	private String _execute(User user) throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"noSuchEntryRedirect", _NO_SUCH_ENTRY_REDIRECT);
		mockHttpServletRequest.addParameter(
			"pageResourcePrimKey",
			String.valueOf(_wikiPage.getResourcePrimKey()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setRequestURI("/c/wiki/find_page");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_strutsAction.execute(mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse.getRedirectedUrl();
	}

	private static final String _NO_SUCH_ENTRY_REDIRECT =
		"/" + RandomTestUtil.randomString();

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(
		filter = "component.name=com.liferay.wiki.web.internal.struts.FindPageStrutsAction"
	)
	private StrutsAction _strutsAction;

	private WikiPage _wikiPage;

}
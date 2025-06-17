/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.taglib.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.taglib.servlet.taglib.RepositoryBrowserTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.ManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class RepositoryBrowserTagTest {

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
	public void testViewableByGuest() throws PortalException {
		RepositoryBrowserTag repositoryBrowserTag = new RepositoryBrowserTag();

		repositoryBrowserTag.setRepositoryId(_group.getGroupId());

		_testViewableByGuest("false", repositoryBrowserTag);

		repositoryBrowserTag.setViewableByGuest(true);

		_testViewableByGuest("true", repositoryBrowserTag);
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws PortalException {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String portletName = RandomTestUtil.randomString();

		String attributeName = StringBundler.concat(
			portletName, StringPool.DASH, WebKeys.CURRENT_PORTLET_URL);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest() {

				@Override
				public String getPortletName() {
					return portletName;
				}

			};

		mockLiferayPortletRenderRequest.setAttribute(
			attributeName, new MockLiferayPortletURL());

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws PortalException {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setScopeGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private void _testViewableByGuest(
			String expectedViewableByGuestValue,
			RepositoryBrowserTag repositoryBrowserTag)
		throws PortalException {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		ReflectionTestUtil.setFieldValue(
			repositoryBrowserTag, "_httpServletRequest",
			mockHttpServletRequest);

		ReflectionTestUtil.invoke(
			repositoryBrowserTag, "setAttributes",
			new Class<?>[] {HttpServletRequest.class},
			new Object[] {mockHttpServletRequest});

		Object repositoryBrowserTagDisplayContext =
			mockHttpServletRequest.getAttribute(
				"com.liferay.document.library.taglib.internal.display." +
					"context.RepositoryBrowserTagDisplayContext");

		ManagementToolbarDisplayContext managementToolbarDisplayContext =
			ReflectionTestUtil.invoke(
				repositoryBrowserTagDisplayContext,
				"getManagementToolbarDisplayContext", new Class<?>[0]);

		CreationMenu creationMenu =
			managementToolbarDisplayContext.getCreationMenu();

		DropdownItemList dropdownItemList = (DropdownItemList)creationMenu.get(
			"primaryItems");

		Assert.assertEquals(
			dropdownItemList.toString(), 2, dropdownItemList.size());

		DropdownItem dropdownItem = dropdownItemList.get(1);

		Map<String, String> data = (Map<String, String>)dropdownItem.get(
			"data");

		Assert.assertEquals(
			expectedViewableByGuestValue, data.get("viewableByGuest"));

		Map<String, Object> repositoryBrowserComponentContext =
			ReflectionTestUtil.invoke(
				repositoryBrowserTagDisplayContext,
				"getRepositoryBrowserComponentContext", new Class<?>[0]);

		Assert.assertNotNull(repositoryBrowserComponentContext);

		Assert.assertEquals(
			expectedViewableByGuestValue,
			repositoryBrowserComponentContext.get("viewableByGuest"));
	}

	@DeleteAfterTestRun
	private Group _group;

}
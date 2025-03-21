/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.test.util.PrefsPropsTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.InstancePool;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class DefaultLandingPageActionTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_lifecycleAction = (LifecycleAction)InstancePool.get(
			"com.liferay.portal.events.DefaultLandingPageAction");

		_user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());
	}

	@Test
	public void testGetDefaultLandingPagePath() throws Exception {
		Assert.assertEquals("/%E5%AE%B6", _getDefaultLandingPagePath("/家"));
		Assert.assertEquals("/home", _getDefaultLandingPagePath("/home"));
		Assert.assertEquals(
			StringBundler.concat(
				"/web/", _user.getScreenName(), "/", _user.getUserId()),
			_getDefaultLandingPagePath(
				"/web/${liferay:screenName}/${liferay:userId}"));
	}

	private String _getDefaultLandingPagePath(String defaultLandingPagePath)
		throws Exception {

		try (SafeCloseable safeCloseable =
				PrefsPropsTestUtil.swapWithSafeCloseable(
					TestPropsValues.getCompanyId(),
					PropsKeys.DEFAULT_LANDING_PAGE_PATH,
					defaultLandingPagePath)) {

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			MockHttpSession mockHttpSession = new MockHttpSession();

			mockHttpSession.setAttribute(WebKeys.USER, _user);

			mockHttpServletRequest.setSession(mockHttpSession);

			_lifecycleAction.processLifecycleEvent(
				new LifecycleEvent(
					mockHttpServletRequest, new MockHttpServletResponse()));

			HttpSession httpSession = mockHttpServletRequest.getSession();

			LastPath lastPath = (LastPath)httpSession.getAttribute(
				WebKeys.LAST_PATH);

			return lastPath.getPath();
		}
	}

	private static LifecycleAction _lifecycleAction;
	private static User _user;

}
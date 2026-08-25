/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class LayoutServiceContextHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo({"LPD-79722", "LPD-99386"})
	public void testGetServiceContextAutoCloseable() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			HttpServletRequest httpServletRequest = serviceContext.getRequest();

			Assert.assertNotNull(httpServletRequest.getContextPath());

			Cookie[] cookies = httpServletRequest.getCookies();

			Assert.assertEquals(Arrays.toString(cookies), 0, cookies.length);

			Assert.assertNotNull(httpServletRequest.getLocale());

			List<Locale> locales = Collections.list(
				httpServletRequest.getLocales());

			Assert.assertFalse(locales.isEmpty());

			Assert.assertEquals(
				HttpMethods.GET, httpServletRequest.getMethod());

			Map<String, String[]> parameterMap =
				httpServletRequest.getParameterMap();

			Assert.assertTrue(parameterMap.isEmpty());

			Assert.assertEquals(
				StringPool.SLASH, httpServletRequest.getRequestURI());
			Assert.assertEquals("http", httpServletRequest.getScheme());
			Assert.assertNotNull(httpServletRequest.getServletContext());
			Assert.assertNotNull(httpServletRequest.getSession());
		}
	}

	@Test
	@TestInfo("LPD-102690")
	public void testGetServiceContextAutoCloseableLocale() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		Locale locale = LocaleUtil.GERMANY;

		httpServletRequest.setAttribute(WebKeys.LOCALE, locale);

		serviceContext.setRequest(httpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			GroupTestUtil.addGroup());

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			Assert.assertEquals(
				LocaleUtil.fromLanguageId(layout.getDefaultLanguageId()),
				httpServletRequest.getAttribute(WebKeys.LOCALE));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		Assert.assertEquals(
			locale, httpServletRequest.getAttribute(WebKeys.LOCALE));
	}

	@Test
	@TestInfo("LPD-103697")
	public void testGetServiceContextAutoCloseableThemeDisplay()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		User user = TestPropsValues.getUser();

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout, user)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			Assert.assertNotNull(themeDisplay);

			String cdnBaseURL = themeDisplay.getCDNBaseURL();

			Assert.assertFalse(cdnBaseURL, cdnBaseURL.contains("null"));

			String pathThemeImages = themeDisplay.getPathThemeImages();

			Assert.assertFalse(
				pathThemeImages, pathThemeImages.contains("null"));

			Assert.assertEquals(
				_portal.getPathMain(), themeDisplay.getPathMain());
			Assert.assertTrue(themeDisplay.isSignedIn());
		}
	}

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private Portal _portal;

}
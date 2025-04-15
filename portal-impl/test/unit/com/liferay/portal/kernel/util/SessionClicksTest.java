/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.PortalPreferencesImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Dante Wang
 */
@NewEnv(type = NewEnv.Type.CLASSLOADER)
public class SessionClicksTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testPutMaxAllowedValues() {
		PropsTestUtil.setProps(
			HashMapBuilder.<String, Object>put(
				PropsKeys.SESSION_CLICKS_MAX_ALLOWED_VALUES,
				String.valueOf(_MAX_ALLOWED_VALUES)
			).put(
				PropsKeys.SESSION_CLICKS_MAX_SIZE_TERMS,
				String.valueOf(Integer.MAX_VALUE)
			).build());

		PortalPreferences portalPreferences = new PortalPreferencesImpl();

		PortletPreferencesFactoryUtil portletPreferencesFactoryUtil =
			new PortletPreferencesFactoryUtil();

		portletPreferencesFactoryUtil.setPortletPreferencesFactory(
			(PortletPreferencesFactory)ProxyUtil.newProxyInstance(
				SessionClicksTest.class.getClassLoader(),
				new Class<?>[] {PortletPreferencesFactory.class},
				(proxy, method, args) -> {
					String methodName = method.getName();

					if (methodName.equals("getPortalPreferences") &&
						(args.length == 1) &&
						(args[0] instanceof HttpServletRequest)) {

						return portalPreferences;
					}

					return null;
				}));

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		for (int i = 1; i <= _MAX_ALLOWED_VALUES; i++) {
			SessionClicks.put(
				httpServletRequest, RandomTestUtil.randomString(),
				RandomTestUtil.randomString());
		}

		SessionClicks.put(
			httpServletRequest, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Assert.assertEquals(_MAX_ALLOWED_VALUES, portalPreferences.size());

		HttpSession httpSession = new MockHttpSession();

		for (int i = 1; i <= _MAX_ALLOWED_VALUES; i++) {
			SessionClicks.put(
				httpSession, RandomTestUtil.randomString(),
				RandomTestUtil.randomString());
		}

		SessionClicks.put(
			httpSession, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		List<String> attributeNames = ListUtil.fromEnumeration(
			httpSession.getAttributeNames());

		Assert.assertEquals(
			attributeNames.toString(), _MAX_ALLOWED_VALUES,
			attributeNames.size());
	}

	@Test
	public void testPutMaxSizeTerms() {
		PropsTestUtil.setProps(
			HashMapBuilder.<String, Object>put(
				PropsKeys.SESSION_CLICKS_MAX_ALLOWED_VALUES,
				String.valueOf(Integer.MAX_VALUE)
			).put(
				PropsKeys.SESSION_CLICKS_MAX_SIZE_TERMS,
				String.valueOf(_MAX_SIZE_TERMS)
			).build());

		HttpSession httpSession = new MockHttpSession();

		String key = RandomTestUtil.randomString(_MAX_SIZE_TERMS - 1);

		SessionClicks.put(
			httpSession, key, RandomTestUtil.randomString(_MAX_SIZE_TERMS + 1));

		Assert.assertNull(SessionClicks.get(httpSession, key, null));

		key = RandomTestUtil.randomString(_MAX_SIZE_TERMS + 1);

		SessionClicks.put(
			httpSession, key, RandomTestUtil.randomString(_MAX_SIZE_TERMS - 1));

		Assert.assertNull(SessionClicks.get(httpSession, key, null));
	}

	private static final int _MAX_ALLOWED_VALUES = 10;

	private static final int _MAX_SIZE_TERMS = 10;

}
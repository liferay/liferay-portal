/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.audiences;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Víctor Galán
 */
public class DetectedAudienceEntryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		ReflectionTestUtil.setFieldValue(
			CookiesManagerUtil.class, "_cookiesManagerSnapshot",
			new Snapshot<CookiesManager>(
				CookiesManagerUtil.class, CookiesManager.class) {

				@Override
				public CookiesManager get() {
					return _cookiesManager;
				}

			});
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			CookiesManagerUtil.class, "_cookiesManagerSnapshot",
			_cookiesManagerSnapshot);
	}

	@Test
	public void testGetAudienceEntryERCs() {
		Assert.assertArrayEquals(
			new String[] {"audience1", "audience2"},
			_getAudienceEntryERCs("%5B%22audience1%22%2C%22audience2%22%5D"));
		Assert.assertArrayEquals(
			new String[0], _getAudienceEntryERCs("%5B%5D"));
		Assert.assertNull(_getAudienceEntryERCs("audience1"));
		Assert.assertNull(_getAudienceEntryERCs(null));
	}

	private String[] _getAudienceEntryERCs(String cookieValue) {
		Mockito.when(
			_cookiesManager.getCookieValue(
				"com.liferay.frontend.js.audiences", _httpServletRequest)
		).thenReturn(
			cookieValue
		);

		return DetectedAudienceEntryUtil.getAudienceEntryERCs(
			_httpServletRequest);
	}

	private final CookiesManager _cookiesManager = Mockito.mock(
		CookiesManager.class);
	private final Snapshot<CookiesManager> _cookiesManagerSnapshot =
		ReflectionTestUtil.getFieldValue(
			CookiesManagerUtil.class, "_cookiesManagerSnapshot");
	private final HttpServletRequest _httpServletRequest =
		new MockHttpServletRequest();

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.ServiceLocator;
import com.liferay.portal.template.engine.TemplateContextHelper;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Iván Zaera Avellón
 */
@RunWith(Arquillian.class)
public class TemplateContextHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testExpandoServicesAccessibleOnlyThroughServiceLocator() {
		TemplateContextHelper templateContextHelper =
			new TemplateContextHelper();

		Map<String, Object> helperUtilities =
			templateContextHelper.getHelperUtilities(false);

		Assert.assertFalse(
			helperUtilities.containsKey("expandoColumnLocalService"));
		Assert.assertFalse(
			helperUtilities.containsKey("expandoRowLocalService"));
		Assert.assertFalse(
			helperUtilities.containsKey("expandoTableLocalService"));
		Assert.assertFalse(
			helperUtilities.containsKey("expandoValueLocalService"));

		ServiceLocator serviceLocator = ServiceLocator.getInstance();

		Assert.assertNotNull(
			serviceLocator.findService(
				ExpandoColumnLocalService.class.getName()));
		Assert.assertNotNull(
			serviceLocator.findService(ExpandoRowLocalService.class.getName()));
		Assert.assertNotNull(
			serviceLocator.findService(
				ExpandoTableLocalService.class.getName()));
		Assert.assertNotNull(
			serviceLocator.findService(
				ExpandoValueLocalService.class.getName()));
	}

	@Test
	public void testFollowRedirectDisabled() throws Exception {
		TemplateContextHelper templateContextHelper =
			new TemplateContextHelper();

		Map<String, Object> helperUtilities =
			templateContextHelper.getHelperUtilities(false);

		Http http = (Http)helperUtilities.get("httpUtil");

		String expectedString = RandomTestUtil.randomString();

		byte[] expectedByteArray = expectedString.getBytes();

		InputStream expectedInputStream = new UnsyncByteArrayInputStream(
			expectedByteArray);

		ReflectionTestUtil.setFieldValue(
			http, "_http",
			ProxyUtil.newProxyInstance(
				TemplateContextHelperTest.class.getClassLoader(),
				new Class<?>[] {Http.class},
				(proxy, method, args) -> {
					String methodName = method.getName();

					if (Objects.equals(methodName, "URLtoByteArray") ||
						Objects.equals(methodName, "URLtoInputStream") ||
						Objects.equals(methodName, "URLtoString")) {

						Assert.assertEquals(1, args.length);

						Assert.assertTrue(args[0] instanceof Http.Options);

						Http.Options options = (Http.Options)args[0];

						Assert.assertFalse(options.isFollowRedirects());

						if (Objects.equals(methodName, "URLtoByteArray")) {
							return expectedByteArray;
						}
						else if (Objects.equals(
									methodName, "URLtoInputStream")) {

							return expectedInputStream;
						}

						return expectedString;
					}

					return null;
				}));

		Http.Options options = new Http.Options();

		options.setLocation("http://www.google.com");

		Assert.assertSame(expectedByteArray, http.URLtoByteArray(options));

		Assert.assertSame(
			expectedByteArray, http.URLtoByteArray("http://www.google.com"));
		Assert.assertSame(
			expectedByteArray,
			http.URLtoByteArray("http://www.google.com", false));

		Assert.assertSame(expectedInputStream, http.URLtoInputStream(options));
		Assert.assertSame(
			expectedInputStream,
			http.URLtoInputStream("http://www.google.com"));
		Assert.assertSame(
			expectedInputStream,
			http.URLtoInputStream("http://www.google.com", false));

		Assert.assertSame(expectedString, http.URLtoString(options));
		Assert.assertSame(
			expectedString, http.URLtoString("http://www.google.com"));
		Assert.assertSame(
			expectedString, http.URLtoString("http://www.google.com", false));
		Assert.assertSame(
			expectedString, http.URLtoString(new URL("http://www.google.com")));
	}

	@Test
	public void testGetHelperUtilities() {
		TemplateContextHelper templateContextHelper =
			new TemplateContextHelper();

		Map<String, Object> helperUtilities =
			templateContextHelper.getHelperUtilities(false);

		Assert.assertEquals(
			StringPool.BLANK, helperUtilities.get("nonceAttribute"));

		helperUtilities = templateContextHelper.getHelperUtilities(true);

		Assert.assertEquals(
			StringPool.BLANK, helperUtilities.get("nonceAttribute"));
	}

	@Test
	public void testPrepare() {
		TemplateContextHelper templateContextHelper =
			new TemplateContextHelper();

		Map<String, Object> contextObjects = new HashMap<>();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, new ThemeDisplay());
		mockHttpServletRequest.setAttribute(
			"com.liferay.portal.security.content.security.policy.internal." +
				"ContentSecurityPolicyNonceManager#NONCE",
			"TEST_NONCE");

		templateContextHelper.prepare(contextObjects, mockHttpServletRequest);

		Assert.assertEquals(
			" nonce=\"TEST_NONCE\"", contextObjects.get("nonceAttribute"));
	}

}
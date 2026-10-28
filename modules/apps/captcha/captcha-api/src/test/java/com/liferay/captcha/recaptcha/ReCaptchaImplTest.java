/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.recaptcha;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.provider.CaptchaProvider;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jorge García Jiménez
 */
public class ReCaptchaImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testValidateChallenge() throws Exception {
		ReCaptchaImpl reCaptchaImpl = new ReCaptchaImpl();

		ReflectionTestUtil.setFieldValue(
			reCaptchaImpl, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			reCaptchaImpl, "_secretResolver",
			Mockito.mock(SecretResolver.class));

		CaptchaProvider captchaProvider = Mockito.mock(CaptchaProvider.class);

		Mockito.when(
			captchaProvider.getCaptchaConfiguration()
		).thenReturn(
			Mockito.mock(CaptchaConfiguration.class)
		);

		ReflectionTestUtil.setFieldValue(
			reCaptchaImpl, "captchaProvider", captchaProvider);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(Mockito.mock(Portal.class));

		try (MockedStatic<HttpUtil> httpUtilMockedStatic = Mockito.mockStatic(
				HttpUtil.class)) {

			httpUtilMockedStatic.when(
				() -> HttpUtil.URLtoString(Mockito.any(Http.Options.class))
			).thenAnswer(
				invocationOnMock -> {
					Http.Options options = invocationOnMock.getArgument(0);

					Assert.assertFalse(options.isFollowRedirects());

					return JSONUtil.put(
						"success", true
					).toString();
				}
			);

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setParameter(
				"g-recaptcha-response", RandomTestUtil.randomString());

			Assert.assertTrue(
				reCaptchaImpl.validateChallenge(mockHttpServletRequest));
		}
	}

}
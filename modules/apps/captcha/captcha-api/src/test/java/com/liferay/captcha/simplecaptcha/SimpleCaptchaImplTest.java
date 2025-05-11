/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.simplecaptcha;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.provider.CaptchaProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import nl.captcha.noise.NoiseProducer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Pedro Victor Silvestre
 */
public class SimpleCaptchaImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetNoiseProducer() {
		SimpleCaptchaImpl simpleCaptchaImpl = new SimpleCaptchaImpl();

		ReflectionTestUtil.setFieldValue(
			simpleCaptchaImpl, "captchaProvider",
			_mockCaptchaProvider(
				new String[] {
					"nl.captcha.noise.CurvedLineNoiseProducer",
					"nl.captcha.noise.StraightLineNoiseProducer"
				}));

		NoiseProducer noiseProducer = simpleCaptchaImpl.getNoiseProducer(
			_captchaConfiguration);

		ReflectionTestUtil.setFieldValue(
			simpleCaptchaImpl, "captchaProvider",
			_mockCaptchaProvider(new String[] {StringPool.BLANK}));

		Assert.assertNotEquals(
			noiseProducer,
			simpleCaptchaImpl.getNoiseProducer(_captchaConfiguration));
	}

	private CaptchaProvider _mockCaptchaProvider(String[] expectedClassNames) {
		CaptchaProvider captchaProvider = Mockito.mock(CaptchaProvider.class);

		_captchaConfiguration = Mockito.mock(CaptchaConfiguration.class);

		Mockito.when(
			_captchaConfiguration.simpleCaptchaNoiseProducers()
		).thenReturn(
			expectedClassNames
		);

		Mockito.when(
			captchaProvider.getCaptchaConfiguration()
		).thenReturn(
			_captchaConfiguration
		);

		return captchaProvider;
	}

	private CaptchaConfiguration _captchaConfiguration;

}
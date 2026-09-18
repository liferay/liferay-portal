/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.simplecaptcha;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.provider.CaptchaProvider;
import com.liferay.portal.kernel.captcha.CaptchaTextException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import nl.captcha.noise.NoiseProducer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Pedro Victor Silvestre
 */
public class SimpleCaptchaImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_simpleCaptchaImpl, "captchaProvider", _mockCaptchaProvider());
		ReflectionTestUtil.setFieldValue(_simpleCaptchaImpl, "portal", _portal);
	}

	@Test
	public void testGetNoiseProducer() {
		NoiseProducer noiseProducer = _simpleCaptchaImpl.getNoiseProducer(
			_captchaConfiguration);

		Mockito.when(
			_captchaConfiguration.simpleCaptchaNoiseProducers()
		).thenReturn(
			new String[] {"nl.captcha.noise.StraightLineNoiseProducer"}
		);

		Assert.assertNotEquals(
			noiseProducer,
			_simpleCaptchaImpl.getNoiseProducer(_captchaConfiguration));
	}

	@Test
	public void testServeImage() throws Exception {
		MockHttpSession mockHttpSession = new MockHttpSession(
			new MockServletContext(), RandomTestUtil.randomString());

		String captchaId1 = RandomTestUtil.randomString();

		_serveImage(captchaId1, mockHttpSession);

		String captchaId2 = RandomTestUtil.randomString();

		_serveImage(captchaId2, mockHttpSession);

		Assert.assertNotNull(
			mockHttpSession.getAttribute(captchaId1 + WebKeys.CAPTCHA_TEXT));
		Assert.assertNotNull(
			mockHttpSession.getAttribute(captchaId2 + WebKeys.CAPTCHA_TEXT));
	}

	@Test
	public void testValidateChallenge() throws Exception {
		_testValidateChallenge();
		_testValidateChallengeWithWrongCaptchaText();
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		String captchaId, MockHttpSession mockHttpSession) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("captchaId", captchaId);
		mockHttpServletRequest.setSession(mockHttpSession);

		return mockHttpServletRequest;
	}

	private CaptchaProvider _mockCaptchaProvider() {
		CaptchaProvider captchaProvider = Mockito.mock(CaptchaProvider.class);

		_captchaConfiguration = Mockito.mock(CaptchaConfiguration.class);

		Mockito.when(
			_captchaConfiguration.simpleCaptchaBackgroundProducers()
		).thenReturn(
			new String[] {"nl.captcha.backgrounds.FlatColorBackgroundProducer"}
		);

		Mockito.when(
			_captchaConfiguration.simpleCaptchaGimpyRenderers()
		).thenReturn(
			new String[] {
				"com.liferay.captcha.simplecaptcha.gimpy.BlockGimpyRenderer"
			}
		);

		Mockito.when(
			_captchaConfiguration.simpleCaptchaHeight()
		).thenReturn(
			50
		);

		Mockito.when(
			_captchaConfiguration.simpleCaptchaNoiseProducers()
		).thenReturn(
			new String[] {"nl.captcha.noise.CurvedLineNoiseProducer"}
		);

		Mockito.when(
			_captchaConfiguration.simpleCaptchaTextProducers()
		).thenReturn(
			new String[] {
				"com.liferay.captcha.simplecaptcha.PinNumberTextProducer"
			}
		);

		Mockito.when(
			_captchaConfiguration.simpleCaptchaWidth()
		).thenReturn(
			150
		);

		Mockito.when(
			_captchaConfiguration.simpleCaptchaWordRenderers()
		).thenReturn(
			new String[] {"nl.captcha.text.renderer.DefaultWordRenderer"}
		);

		Mockito.when(
			captchaProvider.getCaptchaConfiguration()
		).thenReturn(
			_captchaConfiguration
		);

		return captchaProvider;
	}

	private void _serveImage(String captchaId, MockHttpSession mockHttpSession)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(captchaId, mockHttpSession);

		Mockito.when(
			_portal.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			mockHttpServletRequest
		);

		_simpleCaptchaImpl.serveImage(
			mockHttpServletRequest, new MockHttpServletResponse());
	}

	private void _testValidateChallenge() throws Exception {
		MockHttpSession mockHttpSession = new MockHttpSession(
			new MockServletContext(), RandomTestUtil.randomString());

		String captchaId1 = RandomTestUtil.randomString();
		String captchaText = RandomTestUtil.randomString();

		mockHttpSession.setAttribute(
			captchaId1 + WebKeys.CAPTCHA_TEXT, captchaText);

		String captchaId2 = RandomTestUtil.randomString();

		mockHttpSession.setAttribute(
			captchaId2 + WebKeys.CAPTCHA_TEXT, RandomTestUtil.randomString());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(captchaId1, mockHttpSession);

		mockHttpServletRequest.setParameter("captchaText", captchaText);

		Mockito.when(
			_portal.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			mockHttpServletRequest
		);

		Assert.assertTrue(
			_simpleCaptchaImpl.validateChallenge(mockHttpServletRequest));

		Assert.assertNull(
			mockHttpSession.getAttribute(captchaId1 + WebKeys.CAPTCHA_TEXT));
		Assert.assertNotNull(
			mockHttpSession.getAttribute(captchaId2 + WebKeys.CAPTCHA_TEXT));
	}

	private void _testValidateChallengeWithWrongCaptchaText() throws Exception {
		MockHttpSession mockHttpSession = new MockHttpSession(
			new MockServletContext(), RandomTestUtil.randomString());

		String captchaId1 = RandomTestUtil.randomString();
		String captchaText = RandomTestUtil.randomString();

		mockHttpSession.setAttribute(
			captchaId1 + WebKeys.CAPTCHA_TEXT, captchaText);

		String captchaId2 = RandomTestUtil.randomString();

		mockHttpSession.setAttribute(
			captchaId2 + WebKeys.CAPTCHA_TEXT, RandomTestUtil.randomString());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(captchaId1, mockHttpSession);

		mockHttpServletRequest.setParameter(
			"captchaText", RandomTestUtil.randomString());

		Mockito.when(
			_portal.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			mockHttpServletRequest
		);

		Assert.assertFalse(
			_simpleCaptchaImpl.validateChallenge(mockHttpServletRequest));

		Assert.assertNull(
			mockHttpSession.getAttribute(captchaId1 + WebKeys.CAPTCHA_TEXT));
		Assert.assertNotNull(
			mockHttpSession.getAttribute(captchaId2 + WebKeys.CAPTCHA_TEXT));

		mockHttpServletRequest.setParameter("captchaText", captchaText);

		Assert.assertThrows(
			CaptchaTextException.class,
			() -> _simpleCaptchaImpl.validateChallenge(mockHttpServletRequest));
	}

	private CaptchaConfiguration _captchaConfiguration;
	private final Portal _portal = Mockito.mock(Portal.class);
	private final SimpleCaptchaImpl _simpleCaptchaImpl =
		new SimpleCaptchaImpl();

}
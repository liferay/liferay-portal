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
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

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
	public void testCheck() throws Exception {
		MockHttpServletRequest mockHttpServletRequest1 =
			new MockHttpServletRequest();

		MockHttpSession mockHttpSession = new MockHttpSession(
			new MockServletContext(), RandomTestUtil.randomString());

		mockHttpServletRequest1.setSession(mockHttpSession);

		Mockito.when(
			_portal.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			mockHttpServletRequest1
		);

		Mockito.when(
			_captchaConfiguration.maxChallenges()
		).thenReturn(
			1
		);

		_simpleCaptchaImpl.enforceCaptcha(mockHttpServletRequest1);

		String captchaId = RandomTestUtil.randomString();

		_serveImage(captchaId, mockHttpSession);

		MockHttpServletRequest mockHttpServletRequest2 =
			_getMockHttpServletRequest(captchaId, mockHttpSession);

		Map<String, String> captchaTexts = _getCaptchaTexts(mockHttpSession);

		mockHttpServletRequest2.setParameter(
			"captchaText", captchaTexts.get(captchaId));

		mockHttpServletRequest2.setRemoteUser(RandomTestUtil.randomString());

		_simpleCaptchaImpl.check(mockHttpServletRequest2);

		Assert.assertThrows(
			CaptchaTextException.class,
			() -> _simpleCaptchaImpl.check(mockHttpServletRequest2));
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
		_testServeImage();
		_testServeImageWithMaxCaptchaTextsExceeded();
	}

	@Test
	public void testValidateChallenge() throws Exception {
		_testValidateChallenge();
		_testValidateChallengeWithWrongCaptchaText();
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> _getCaptchaTexts(
		MockHttpSession mockHttpSession) {

		return (Map<String, String>)mockHttpSession.getAttribute(
			ReflectionTestUtil.getFieldValue(
				SimpleCaptchaImpl.class, "_CAPTCHA_TEXTS"));
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

	private void _testServeImage() throws Exception {
		MockHttpSession mockHttpSession = new MockHttpSession(
			new MockServletContext(), RandomTestUtil.randomString());

		String captchaId1 = RandomTestUtil.randomString();

		_serveImage(captchaId1, mockHttpSession);

		String captchaId2 = RandomTestUtil.randomString();

		_serveImage(captchaId2, mockHttpSession);

		Map<String, String> captchaTexts = _getCaptchaTexts(mockHttpSession);

		Assert.assertTrue(captchaTexts.containsKey(captchaId1));
		Assert.assertTrue(captchaTexts.containsKey(captchaId2));
	}

	private void _testServeImageWithMaxCaptchaTextsExceeded() throws Exception {
		MockHttpSession mockHttpSession = new MockHttpSession(
			new MockServletContext(), RandomTestUtil.randomString());

		String captchaId = RandomTestUtil.randomString();

		_serveImage(captchaId, mockHttpSession);

		int maxCaptchaTexts = ReflectionTestUtil.getFieldValue(
			SimpleCaptchaImpl.class, "_CAPTCHA_MAX_TEXTS");

		for (int i = 0; i < maxCaptchaTexts; i++) {
			_serveImage(RandomTestUtil.randomString(), mockHttpSession);
		}

		Map<String, String> captchaTexts = _getCaptchaTexts(mockHttpSession);

		Assert.assertFalse(captchaTexts.containsKey(captchaId));
		Assert.assertEquals(
			captchaTexts.toString(), maxCaptchaTexts, captchaTexts.size());
	}

	private void _testValidateChallenge() throws Exception {
		MockHttpSession mockHttpSession = new MockHttpSession(
			new MockServletContext(), RandomTestUtil.randomString());

		String captchaId = RandomTestUtil.randomString();

		_serveImage(captchaId, mockHttpSession);

		Map<String, String> captchaTexts = _getCaptchaTexts(mockHttpSession);

		Assert.assertFalse(captchaTexts.containsKey(captchaId));
		Assert.assertTrue(
			_validateChallenge(
				captchaId, captchaTexts.get(captchaId), mockHttpSession));
	}

	private void _testValidateChallengeWithWrongCaptchaText() throws Exception {
		MockHttpSession mockHttpSession = new MockHttpSession(
			new MockServletContext(), RandomTestUtil.randomString());

		String captchaId = RandomTestUtil.randomString();

		_serveImage(captchaId, mockHttpSession);

		Assert.assertFalse(
			_validateChallenge(
				captchaId, RandomTestUtil.randomString(), mockHttpSession));

		Assert.assertThrows(
			CaptchaTextException.class,
			() -> _validateChallenge(
				captchaId, RandomTestUtil.randomString(), mockHttpSession));
	}

	private boolean _validateChallenge(
			String captchaId, String captchaText,
			MockHttpSession mockHttpSession)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(captchaId, mockHttpSession);

		mockHttpServletRequest.setParameter("captchaText", captchaText);

		Mockito.when(
			_portal.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			mockHttpServletRequest
		);

		return _simpleCaptchaImpl.validateChallenge(mockHttpServletRequest);
	}

	private CaptchaConfiguration _captchaConfiguration;
	private final Portal _portal = Mockito.mock(Portal.class);
	private final SimpleCaptchaImpl _simpleCaptchaImpl =
		new SimpleCaptchaImpl();

}
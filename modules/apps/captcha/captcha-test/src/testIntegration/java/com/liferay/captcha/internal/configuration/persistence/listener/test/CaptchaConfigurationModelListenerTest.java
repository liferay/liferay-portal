/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.captcha.recaptcha.ReCaptchaImpl;
import com.liferay.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class CaptchaConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_locale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.ENGLISH);

		_reCaptchaProperties = HashMapDictionaryBuilder.<String, Object>put(
			"captchaEngine", ReCaptchaImpl.class.getName()
		).put(
			"reCaptchaNoScriptURL",
			"https://www.google.com/recaptcha/api/fallback?k="
		).put(
			"reCaptchaPrivateKey", "test"
		).put(
			"reCaptchaPublicKey", "test"
		).put(
			"reCaptchaScriptURL", "https://www.google.com/recaptcha/api.js"
		).put(
			"reCaptchaVerifyURL",
			"https://www.google.com/recaptcha/api/siteverify"
		).build();
		_simpleCaptchaProperties = HashMapDictionaryBuilder.<String, Object>put(
			"captchaEngine", SimpleCaptchaImpl.class.getName()
		).put(
			"simpleCaptchaBackgroundProducers",
			new String[] {"nl.captcha.backgrounds.FlatColorBackgroundProducer"}
		).put(
			"simpleCaptchaGimpyRenderers",
			new String[] {
				"com.liferay.captcha.simplecaptcha.gimpy.BlockGimpyRenderer"
			}
		).put(
			"simpleCaptchaNoiseProducers",
			new String[] {"nl.captcha.noise.CurvedLineNoiseProducer"}
		).put(
			"simpleCaptchaTextProducers",
			new String[] {
				"com.liferay.captcha.simplecaptcha.PinNumberTextProducer"
			}
		).put(
			"simpleCaptchaWordRenderers",
			new String[] {"nl.captcha.text.renderer.DefaultWordRenderer"}
		).build();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setThemeDisplayLocale(_locale);
	}

	@Test
	public void test() throws Exception {
		_test(
			_reCaptchaProperties, "the-recaptcha-no-script-url-is-not-valid",
			"reCaptchaNoScriptURL",
			"https://www.test.com/recaptcha/api/fallback?k=");
		_test(
			_reCaptchaProperties, "the-recaptcha-private-key-is-not-valid",
			"reCaptchaPrivateKey", StringPool.BLANK);
		_test(
			_reCaptchaProperties, "the-recaptcha-public-key-is-not-valid",
			"reCaptchaPublicKey", StringPool.BLANK);
		_test(
			_reCaptchaProperties, "the-recaptcha-script-url-is-not-valid",
			"reCaptchaScriptURL", "https://www.test.com/recaptcha/api.js");
		_test(
			_reCaptchaProperties, "the-recaptcha-verify-url-is-not-valid",
			"reCaptchaVerifyURL",
			"https://www.test.com/recaptcha/api/siteverify");
		_test(
			_simpleCaptchaProperties,
			"the-simplecaptcha-background-producers-configuration-cannot-be-" +
				"empty",
			"simpleCaptchaBackgroundProducers", new String[0]);
		_test(
			_simpleCaptchaProperties,
			"the-simplecaptcha-gimpy-renderers-configuration-cannot-be-empty",
			"simpleCaptchaGimpyRenderers", new String[0]);
		_test(
			_simpleCaptchaProperties,
			"the-simplecaptcha-noise-producers-configuration-cannot-be-empty",
			"simpleCaptchaNoiseProducers", new String[0]);
		_test(
			_simpleCaptchaProperties,
			"the-simplecaptcha-text-producers-configuration-cannot-be-empty",
			"simpleCaptchaTextProducers", new String[0]);
		_test(
			_simpleCaptchaProperties,
			"the-simplecaptcha-word-renderers-configuration-cannot-be-empty",
			"simpleCaptchaWordRenderers", new String[0]);
	}

	private AutoCloseable _swapCaptchaConfiguration(
		Dictionary<String, Object> configuration, String key, Object value) {

		Object originalValue = configuration.put(key, value);

		return () -> configuration.put(key, originalValue);
	}

	private void _test(
			Dictionary<String, Object> configuration,
			String exceptionMessageKey, String key, Object value)
		throws Exception {

		try (AutoCloseable autoCloseable = _swapCaptchaConfiguration(
				configuration, key, value)) {

			_configurationModelListener.onBeforeSave(
				StringPool.BLANK, configuration);

			Assert.fail();
		}
		catch (ConfigurationModelListenerException
					configurationModelListenerException) {

			String message = configurationModelListenerException.getMessage();

			Assert.assertTrue(
				message.contains(
					_language.get(LocaleUtil.US, exceptionMessageKey)));
		}
	}

	@Inject(
		filter = "model.class.name=com.liferay.captcha.configuration.CaptchaConfiguration"
	)
	private ConfigurationModelListener _configurationModelListener;

	@Inject
	private Language _language;

	private Locale _locale;
	private Dictionary<String, Object> _reCaptchaProperties;
	private Dictionary<String, Object> _simpleCaptchaProperties;

}
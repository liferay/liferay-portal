/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.captcha.recaptcha.ReCaptchaImpl;
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
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setThemeDisplayLocale(_locale);
	}

	@Test
	public void test() throws Exception {
		_test(
			"the-recaptcha-no-script-url-is-not-valid", "reCaptchaNoScriptURL",
			"https://www.test.com/recaptcha/api/fallback?k=");
		_test(
			"the-recaptcha-private-key-is-not-valid", "reCaptchaPrivateKey",
			StringPool.BLANK);
		_test(
			"the-recaptcha-public-key-is-not-valid", "reCaptchaPublicKey",
			StringPool.BLANK);
		_test(
			"the-recaptcha-script-url-is-not-valid", "reCaptchaScriptURL",
			"https://www.test.com/recaptcha/api.js");
		_test(
			"the-recaptcha-verify-url-is-not-valid", "reCaptchaVerifyURL",
			"https://www.test.com/recaptcha/api/siteverify");
	}

	private AutoCloseable _swapReCaptchaConfiguration(
		String key, String value) {

		String originalValue = (String)_reCaptchaProperties.put(key, value);

		return () -> _reCaptchaProperties.put(key, originalValue);
	}

	private void _test(String exceptionMessageKey, String key, String value)
		throws Exception {

		try (AutoCloseable autoCloseable = _swapReCaptchaConfiguration(
				key, value)) {

			_configurationModelListener.onBeforeSave(
				StringPool.BLANK, _reCaptchaProperties);

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

}
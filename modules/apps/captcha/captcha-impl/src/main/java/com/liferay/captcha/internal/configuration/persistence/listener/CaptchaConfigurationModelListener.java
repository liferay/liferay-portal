/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.internal.configuration.persistence.listener;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.recaptcha.ReCaptchaImpl;
import com.liferay.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.captcha.CaptchaConfigurationException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "model.class.name=com.liferay.captcha.configuration.CaptchaConfiguration",
	service = ConfigurationModelListener.class
)
public class CaptchaConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		String captchaEngine = (String)properties.get("captchaEngine");

		if (Validator.isNull(captchaEngine)) {
			return;
		}

		try {
			if (captchaEngine.equals(ReCaptchaImpl.class.getName())) {
				_validateReCaptcha(properties);
			}
			else if (captchaEngine.equals(SimpleCaptchaImpl.class.getName())) {
				_validateSimpleCaptcha(properties);
			}
		}
		catch (CaptchaConfigurationException captchaConfigurationException) {
			throw new ConfigurationModelListenerException(
				captchaConfigurationException.getMessage(),
				CaptchaConfiguration.class,
				CaptchaConfigurationModelListener.class, properties);
		}
	}

	private ResourceBundle _getResourceBundle() {
		if (_resourceBundle == null) {
			Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

			return ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());
		}

		return _resourceBundle;
	}

	private void _validateReCaptcha(Dictionary<String, Object> properties)
		throws CaptchaConfigurationException {

		String reCaptchaNoScriptURL = (String)properties.get(
			"reCaptchaNoScriptURL");

		if (!reCaptchaNoScriptURL.startsWith(_RECAPTCHA_URL)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"the-recaptcha-no-script-url-is-not-valid"));
		}

		String reCaptchaPublicKey = (String)properties.get(
			"reCaptchaPublicKey");

		if (Validator.isNull(reCaptchaPublicKey)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"the-recaptcha-public-key-is-not-valid"));
		}

		String reCaptchaPrivateKey = (String)properties.get(
			"reCaptchaPrivateKey");

		if (Validator.isNull(reCaptchaPrivateKey)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"the-recaptcha-private-key-is-not-valid"));
		}

		String reCaptchaScriptURL = (String)properties.get(
			"reCaptchaScriptURL");

		if (!reCaptchaScriptURL.startsWith(_RECAPTCHA_URL)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"the-recaptcha-script-url-is-not-valid"));
		}

		String reCaptchaVerifyURL = (String)properties.get(
			"reCaptchaVerifyURL");

		if (!reCaptchaVerifyURL.startsWith(_RECAPTCHA_URL)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"the-recaptcha-verify-url-is-not-valid"));
		}
	}

	private void _validateSimpleCaptcha(Dictionary<String, Object> properties)
		throws CaptchaConfigurationException {

		String[] simpleCaptchaBackgroundProducers = (String[])properties.get(
			"simpleCaptchaBackgroundProducers");

		if (ArrayUtil.isEmpty(simpleCaptchaBackgroundProducers)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"the-simplecaptcha-background-producers-configuration-" +
						"cannot-be-empty"));
		}

		String[] simpleCaptchaGimpyRenderers = (String[])properties.get(
			"simpleCaptchaGimpyRenderers");

		if (ArrayUtil.isEmpty(simpleCaptchaGimpyRenderers)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"the-simplecaptcha-gimpy-renderers-configuration-cannot-" +
						"be-empty"));
		}

		String[] simpleCaptchaNoiseProducers = (String[])properties.get(
			"simpleCaptchaNoiseProducers");

		if (ArrayUtil.isEmpty(simpleCaptchaNoiseProducers)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"the-simplecaptcha-noise-producers-configuration-cannot-" +
						"be-empty"));
		}

		String[] simpleCaptchaTextProducers = (String[])properties.get(
			"simpleCaptchaTextProducers");

		if (ArrayUtil.isEmpty(simpleCaptchaTextProducers)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"the-simplecaptcha-text-producers-configuration-cannot-" +
						"be-empty"));
		}

		String[] simpleCaptchaWordRenderers = (String[])properties.get(
			"simpleCaptchaWordRenderers");

		if (ArrayUtil.isEmpty(simpleCaptchaWordRenderers)) {
			throw new CaptchaConfigurationException(
				ResourceBundleUtil.getString(
					_getResourceBundle(),
					"the-simplecaptcha-word-renderers-configuration-cannot-" +
						"be-empty"));
		}
	}

	private static final String _RECAPTCHA_URL = "https://www.google.com";

	private ResourceBundle _resourceBundle;

}
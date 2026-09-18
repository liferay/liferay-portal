/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.internal.configuration.settings;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.provider.CaptchaProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.captcha.CaptchaSettings;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = CaptchaSettings.class)
public class CaptchaSettingsImpl implements CaptchaSettings {

	@Override
	public String getCaptchaEngine() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.captchaEngine();
	}

	@Override
	public int getMaxChallenges() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.maxChallenges();
	}

	@Override
	public String getReCaptchaNoScriptURL() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.reCaptchaNoScriptURL();
	}

	@Override
	public String getReCaptchaPrivateKey() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return _secretResolver.resolve(
			CompanyThreadLocal.getCompanyId(),
			captchaConfiguration.reCaptchaPrivateKey());
	}

	@Override
	public String getReCaptchaPublicKey() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.reCaptchaPublicKey();
	}

	@Override
	public String getReCaptchaScriptURL() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.reCaptchaScriptURL();
	}

	@Override
	public String getReCaptchaVerifyURL() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.reCaptchaVerifyURL();
	}

	@Override
	public String[] getSimpleCaptchaBackgroundProducers() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.simpleCaptchaBackgroundProducers();
	}

	@Override
	public String[] getSimpleCaptchaGimpyRenderers() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.simpleCaptchaGimpyRenderers();
	}

	@Override
	public int getSimpleCaptchaHeight() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.simpleCaptchaHeight();
	}

	@Override
	public String[] getSimpleCaptchaNoiseProducers() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.simpleCaptchaNoiseProducers();
	}

	@Override
	public String[] getSimpleCaptchaTextProducers() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.simpleCaptchaTextProducers();
	}

	@Override
	public int getSimpleCaptchaWidth() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.simpleCaptchaWidth();
	}

	@Override
	public String[] getSimpleCaptchaWordRenderers() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.simpleCaptchaWordRenderers();
	}

	@Override
	public boolean isCreateAccountCaptchaEnabled() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.createAccountCaptchaEnabled();
	}

	@Override
	public boolean isMessageBoardsEditCategoryCaptchaEnabled() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.messageBoardsEditCategoryCaptchaEnabled();
	}

	@Override
	public boolean isMessageBoardsEditMessageCaptchaEnabled() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.messageBoardsEditMessageCaptchaEnabled();
	}

	@Override
	public boolean isSendPasswordCaptchaEnabled() {
		CaptchaConfiguration captchaConfiguration =
			_captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.sendPasswordCaptchaEnabled();
	}

	@Override
	public void setCaptchaEngine(String className) throws Exception {
		Configuration configuration = _configurationAdmin.getConfiguration(
			"com.liferay.captcha.configuration.CaptchaConfiguration",
			StringPool.QUESTION);

		Dictionary<String, Object> properties = configuration.getProperties();

		if (properties == null) {
			properties = new Hashtable<>();
		}

		properties.put("captchaEngine", className);

		configuration.update(properties);
	}

	@Reference
	private CaptchaProvider _captchaProvider;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private SecretResolver _secretResolver;

}
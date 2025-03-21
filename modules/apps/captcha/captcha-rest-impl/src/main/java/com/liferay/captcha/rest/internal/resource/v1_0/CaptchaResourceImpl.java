/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.rest.internal.resource.v1_0;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.rest.dto.v1_0.Captcha;
import com.liferay.captcha.rest.resource.v1_0.CaptchaResource;
import com.liferay.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.captcha.CaptchaTextException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.servlet.filters.secure.NonceUtil;

import jakarta.ws.rs.ForbiddenException;

import java.io.ByteArrayOutputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Loc Pham
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/captcha.properties",
	scope = ServiceScope.PROTOTYPE, service = CaptchaResource.class
)
public class CaptchaResourceImpl extends BaseCaptchaResourceImpl {

	@Override
	public Captcha getCaptchaChallenge() throws Exception {
		_checkCaptchaConfiguration();

		com.liferay.portal.kernel.captcha.Captcha kernelCaptcha =
			CaptchaUtil.getCaptcha();

		try (ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream()) {

			String expectedAnswer = kernelCaptcha.serveImage(
				byteArrayOutputStream);

			return new Captcha() {
				{
					setImage(
						() -> {
							String data = Base64.encode(
								byteArrayOutputStream.toByteArray());

							return "data:image/png;base64," + data;
						});
					setToken(
						() -> EncryptorUtil.encrypt(
							contextCompany.getKeyObj(),
							JSONUtil.put(
								"answer", expectedAnswer
							).put(
								"expiryTime",
								System.currentTimeMillis() + (Time.MINUTE * 5)
							).put(
								"nonce",
								NonceUtil.generate(
									contextCompany.getCompanyId(),
									contextHttpServletRequest.getRemoteAddr())
							).toString()));
				}
			};
		}
	}

	@Override
	public void postCaptchaResponse(Captcha captcha) throws Exception {
		_checkCaptchaConfiguration();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			EncryptorUtil.decrypt(
				contextCompany.getKeyObj(), captcha.getToken()));

		if (!jsonObject.has("answer") || !jsonObject.has("expiryTime") ||
			!NonceUtil.verify(jsonObject.getString("nonce"))) {

			throw new IllegalArgumentException("Token: " + captcha.getToken());
		}

		long expiryTime = jsonObject.getLong("expiryTime");

		if (expiryTime < System.currentTimeMillis()) {
			throw new CaptchaTextException("Captcha is expired");
		}

		if (!StringUtil.equalsIgnoreCase(
				jsonObject.getString("answer"), captcha.getAnswer())) {

			throw new CaptchaTextException("Answer is invalid");
		}
	}

	private void _checkCaptchaConfiguration() throws Exception {
		CaptchaConfiguration captchaConfiguration =
			_configurationProvider.getCompanyConfiguration(
				CaptchaConfiguration.class, contextCompany.getCompanyId());

		if (!StringUtil.equalsIgnoreCase(
				captchaConfiguration.captchaEngine(),
				SimpleCaptchaImpl.class.getName())) {

			throw new ForbiddenException(
				"Captcha engine is not configured to use SimpleCaptcha");
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSONFactory _jsonFactory;

}
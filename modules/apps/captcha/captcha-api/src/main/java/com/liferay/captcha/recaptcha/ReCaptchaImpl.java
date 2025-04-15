/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.recaptcha;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.captcha.CaptchaConfigurationException;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tagnaouti Boubker
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @author Daniel Sanz
 */
@Component(
	property = "captcha.engine.impl=com.liferay.captcha.recaptcha.ReCaptchaImpl",
	service = Captcha.class
)
public class ReCaptchaImpl extends SimpleCaptchaImpl {

	@Override
	public String getName() {
		return "reCAPTCHA";
	}

	@Override
	public String getTaglibPath() {
		return _TAGLIB_PATH;
	}

	@Override
	public void serveImage(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void serveImage(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean validateChallenge(HttpServletRequest httpServletRequest)
		throws CaptchaException {

		String reCaptchaResponse = ParamUtil.getString(
			httpServletRequest, "g-recaptcha-response");

		while (Validator.isBlank(reCaptchaResponse) &&
			   (httpServletRequest instanceof HttpServletRequestWrapper)) {

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)httpServletRequest;

			httpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();

			reCaptchaResponse = ParamUtil.getString(
				httpServletRequest, "g-recaptcha-response");
		}

		if (Validator.isBlank(reCaptchaResponse)) {
			_log.error(
				"CAPTCHA text is null. User " +
					httpServletRequest.getRemoteUser() +
						" may be trying to circumvent the CAPTCHA.");

			throw new CaptchaException();
		}

		Http.Options options = new Http.Options();

		CaptchaConfiguration captchaConfiguration =
			captchaProvider.getCaptchaConfiguration();

		options.setLocation(captchaConfiguration.reCaptchaVerifyURL());

		try {
			options.addPart(
				"secret", captchaConfiguration.reCaptchaPrivateKey());
		}
		catch (SystemException systemException) {
			_log.error(systemException);
		}

		options.addPart("remoteip", httpServletRequest.getRemoteAddr());
		options.addPart("response", reCaptchaResponse);
		options.setPost(true);

		String content = null;

		try {
			content = HttpUtil.URLtoString(options);
		}
		catch (IOException ioException) {
			_log.error(ioException);

			throw new CaptchaConfigurationException();
		}

		if (content == null) {
			_log.error("reCAPTCHA did not return a result");

			throw new CaptchaConfigurationException();
		}

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(content);

			String success = jsonObject.getString("success");

			if (StringUtil.equalsIgnoreCase(success, "true")) {
				return true;
			}

			JSONArray jsonArray = jsonObject.getJSONArray("error-codes");

			if ((jsonArray == null) || (jsonArray.length() == 0)) {
				_log.error("reCAPTCHA encountered an error");

				throw new CaptchaConfigurationException();
			}

			StringBundler sb = new StringBundler((jsonArray.length() * 2) - 1);

			for (int i = 0; i < jsonArray.length(); i++) {
				sb.append(jsonArray.getString(i));

				if (i < (jsonArray.length() - 1)) {
					sb.append(StringPool.COMMA_AND_SPACE);
				}
			}

			_log.error("reCAPTCHA encountered an error: " + sb.toString());

			throw new CaptchaConfigurationException();
		}
		catch (JSONException jsonException) {
			_log.error(
				"reCAPTCHA did not return a valid result: " + content,
				jsonException);

			throw new CaptchaConfigurationException();
		}
	}

	@Override
	protected boolean validateChallenge(PortletRequest portletRequest)
		throws CaptchaException {

		return validateChallenge(
			PortalUtil.getHttpServletRequest(portletRequest));
	}

	private static final String _TAGLIB_PATH = "/captcha/recaptcha.jsp";

	private static final Log _log = LogFactoryUtil.getLog(ReCaptchaImpl.class);

	@Reference
	private JSONFactory _jsonFactory;

}
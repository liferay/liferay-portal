/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.internal.function.captcha;

import com.liferay.captcha.internal.configuration.FunctionCaptchaImplConfiguration;
import com.liferay.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.client.extension.type.CustomElementCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.captcha.CaptchaConfigurationException;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 * @author Manuele Castro
 */
@Component(
	configurationPid = "com.liferay.captcha.internal.configuration.FunctionCaptchaImplConfiguration",
	factory = "com.liferay.captcha.internal.function.captcha.FunctionCaptchaImpl",
	service = Captcha.class
)
public class FunctionCaptchaImpl extends SimpleCaptchaImpl {

	@Override
	public String getName() {
		return _functionCaptchaImplConfiguration.captchaName();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write("<script");
		printWriter.write(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.write(" src=\"");

		CustomElementCET customElementCET =
			(CustomElementCET)_cetManager.getCET(
				PortalUtil.getCompanyId(httpServletRequest),
				_functionCaptchaImplConfiguration.
					customElementExternalReferenceCode());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		printWriter.write(
			themeDisplay.getPortalURL() + customElementCET.getURLs());

		printWriter.write("\" type=\"module\"></script><");
		printWriter.write(customElementCET.getHTMLElementName());
		printWriter.write(" liferaywebdavurl=\"");

		try {
			StringBundler sb = new StringBundler(4);

			sb.append(themeDisplay.getPortalURL());
			sb.append("/webdav");

			Group group = _groupLocalService.getGroup(
				themeDisplay.getScopeGroupId());

			sb.append(group.getFriendlyURL());

			sb.append("/document_library");

			printWriter.write(
				StringUtil.replace(sb.toString(), CharPool.QUOTE, "&quote;"));
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		printWriter.write("\"></");
		printWriter.write(customElementCET.getHTMLElementName());
		printWriter.write(StringPool.GREATER_THAN);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_companyId = ConfigurationFactoryUtil.getCompanyId(
			_companyLocalService, properties);
		_functionCaptchaImplConfiguration = ConfigurableUtil.createConfigurable(
			FunctionCaptchaImplConfiguration.class, properties);
	}

	@Override
	protected boolean validateChallenge(HttpServletRequest httpServletRequest)
		throws CaptchaException {

		String captchaResponse = ParamUtil.getString(
			httpServletRequest,
			_functionCaptchaImplConfiguration.captchaResponseParameterName());

		while (Validator.isBlank(captchaResponse) &&
			   (httpServletRequest instanceof
				   HttpServletRequestWrapper httpServletRequestWrapper)) {

			httpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();

			captchaResponse = ParamUtil.getString(
				httpServletRequest,
				_functionCaptchaImplConfiguration.
					captchaResponseParameterName());
		}

		if (Validator.isBlank(captchaResponse)) {
			throw new CaptchaException(
				"CAPTCHA challenge is null. User " +
					httpServletRequest.getRemoteUser() +
						" may be trying to circumvent the CAPTCHA.");
		}

		try {
			User user = _userLocalService.getUserByScreenName(
				_companyId, "default-service-account");

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				new String(
					_portalCatapult.launch(
						_companyId, Http.Method.POST,
						_functionCaptchaImplConfiguration.
							oAuth2ApplicationExternalReferenceCode(),
						JSONUtil.put(
							"remoteip", httpServletRequest.getRemoteAddr()
						).put(
							"response", captchaResponse
						),
						_functionCaptchaImplConfiguration.resourcePath(),
						user.getUserId()
					).get()));

			if (jsonObject == null) {
				throw new CaptchaConfigurationException(
					_functionCaptchaImplConfiguration.captchaName() +
						" did not return a result");
			}

			if (jsonObject.getBoolean("success")) {
				return true;
			}

			JSONArray jsonArray = jsonObject.getJSONArray("error-codes");

			if ((jsonArray == null) || (jsonArray.length() == 0)) {
				throw new CaptchaConfigurationException(
					_functionCaptchaImplConfiguration.captchaName() +
						" encountered an error");
			}

			StringBundler sb = new StringBundler((jsonArray.length() * 2) - 1);

			for (int i = 0; i < jsonArray.length(); i++) {
				sb.append(jsonArray.getString(i));

				if (i >= (jsonArray.length() - 1)) {
					continue;
				}

				sb.append(StringPool.COMMA_AND_SPACE);
			}

			throw new CaptchaConfigurationException(
				_functionCaptchaImplConfiguration.captchaName() +
					" encountered an error: " + sb);
		}
		catch (Exception exception) {
			if (exception instanceof CaptchaException) {
				_log.error(exception);

				throw (CaptchaException)exception;
			}

			_log.error(
				_functionCaptchaImplConfiguration.captchaName() +
					" did not return a valid result",
				exception);

			throw new CaptchaConfigurationException(exception);
		}
	}

	@Override
	protected boolean validateChallenge(PortletRequest portletRequest)
		throws CaptchaException {

		return validateChallenge(
			PortalUtil.getHttpServletRequest(portletRequest));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FunctionCaptchaImpl.class);

	@Reference
	private CETManager _cetManager;

	private long _companyId;

	@Reference
	private CompanyLocalService _companyLocalService;

	private volatile FunctionCaptchaImplConfiguration
		_functionCaptchaImplConfiguration;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PortalCatapult _portalCatapult;

	@Reference
	private UserLocalService _userLocalService;

}
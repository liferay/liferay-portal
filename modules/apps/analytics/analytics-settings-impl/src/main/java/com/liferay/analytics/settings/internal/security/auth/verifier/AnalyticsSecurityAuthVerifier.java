/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.internal.security.auth.verifier;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.security.constants.AnalyticsSecurityConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicy;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.Charset;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(
	configurationPid = "com.liferay.analytics.settings.configuration.AnalyticsConfiguration.scoped",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "auth.verifier.AnalyticsSecurityAuthVerifier.urls.includes=/o/segments-asah/v1.0/experiments/*,/v1.0/experiments/*",
	service = AuthVerifier.class
)
public class AnalyticsSecurityAuthVerifier implements AuthVerifier {

	@Override
	public String getAuthType() {
		Class<?> clazz = getClass();

		return clazz.getSimpleName();
	}

	@Override
	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties properties)
		throws AuthException {

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		HttpServletRequest httpServletRequest =
			accessControlContext.getRequest();

		String signature = httpServletRequest.getHeader(
			"Liferay-Analytics-Cloud-Security-Signature");

		if (signature == null) {
			return authVerifierResult;
		}

		try {
			AnalyticsConfiguration analyticsConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AnalyticsConfiguration.class,
					_portal.getCompanyId(httpServletRequest));

			if (analyticsConfiguration.token() == null) {
				if (_log.isDebugEnabled()) {
					_log.debug("Missing security configuration");
				}

				return authVerifierResult;
			}

			Set<String> hostsAllowed = JSONUtil.toStringSet(
				_jsonFactory.createJSONArray(
					analyticsConfiguration.hostsAllowed()));

			if (!hostsAllowed.isEmpty() &&
				!hostsAllowed.contains(httpServletRequest.getRemoteAddr())) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Access denied for " +
							httpServletRequest.getRemoteAddr());
				}

				return authVerifierResult;
			}

			String timestamp = httpServletRequest.getHeader(
				"Liferay-Analytics-Cloud-Security-Timestamp");

			if ((System.currentTimeMillis() - GetterUtil.getLong(timestamp)) >
					_EXPIRATION) {

				if (_log.isDebugEnabled()) {
					_log.debug("Signature timestamp expired " + timestamp);
				}

				return authVerifierResult;
			}

			if (!_validateSignature(
					httpServletRequest, analyticsConfiguration.publicKey(),
					signature, timestamp)) {

				if (_log.isDebugEnabled()) {
					_log.debug("Invalid signature " + signature);
				}

				return authVerifierResult;
			}

			Map<String, Object> settings = authVerifierResult.getSettings();

			List<String> serviceAccessPolicyNames =
				(List<String>)settings.computeIfAbsent(
					ServiceAccessPolicy.SERVICE_ACCESS_POLICY_NAMES,
					value -> new ArrayList<>());

			serviceAccessPolicyNames.add(
				AnalyticsSecurityConstants.SERVICE_ACCESS_POLICY_NAME);

			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(
				_getAnalyticsAdminUserId(
					_portal.getCompanyId(httpServletRequest)));

			return authVerifierResult;
		}
		catch (Exception exception) {
			throw new AuthException(exception);
		}
	}

	private long _getAnalyticsAdminUserId(long companyId) {
		User user = _userLocalService.fetchUserByScreenName(
			companyId, AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN);

		return user.getUserId();
	}

	private boolean _validateSignature(
			HttpServletRequest httpServletRequest, String publicKey,
			String signatureString, String timestamp)
		throws Exception {

		Signature signature = Signature.getInstance("DSA");

		KeyFactory keyFactory = KeyFactory.getInstance("DSA");

		signature.initVerify(
			keyFactory.generatePublic(
				new X509EncodedKeySpec(Base64.decode(publicKey))));

		Map<String, String> sortedParameters = new TreeMap<>();

		Map<String, String[]> parameters = httpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			sortedParameters.put(entry.getKey(), entry.getValue()[0]);
		}

		sortedParameters.put(
			"Liferay-Analytics-Cloud-Security-Timestamp", timestamp);

		StringBundler sb = new StringBundler((2 * sortedParameters.size()) + 3);

		sb.append(httpServletRequest.getContextPath());
		sb.append(httpServletRequest.getServletPath());
		sb.append(httpServletRequest.getPathInfo());

		for (Map.Entry<String, String> entry : sortedParameters.entrySet()) {
			sb.append(entry.getKey());
			sb.append(entry.getValue());
		}

		String requestContent = sb.toString();

		signature.update(requestContent.getBytes(Charset.defaultCharset()));

		return signature.verify(Base64.decode(signatureString));
	}

	private static final long _EXPIRATION = 10 * 60 * 1000;

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsSecurityAuthVerifier.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}
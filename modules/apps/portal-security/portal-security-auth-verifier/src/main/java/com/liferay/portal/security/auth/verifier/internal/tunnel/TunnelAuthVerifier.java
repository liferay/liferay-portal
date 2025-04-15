/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.verifier.internal.tunnel;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.tunnel.TunnelAuthenticationManagerUtil;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicy;
import com.liferay.portal.kernel.servlet.HttpHeaders;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.component.annotations.Component;

/**
 * @author Zsolt Berentey
 */
@Component(service = AuthVerifier.class)
public class TunnelAuthVerifier implements AuthVerifier {

	@Override
	public String getAuthType() {
		return HttpServletRequest.BASIC_AUTH;
	}

	@Override
	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties properties)
		throws AuthException {

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		try {
			String[] credentials = verify(accessControlContext.getRequest());

			if (credentials != null) {
				authVerifierResult.setPassword(credentials[1]);
				authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
				authVerifierResult.setUserId(Long.valueOf(credentials[0]));

				String serviceAccessPolicyName = (String)properties.get(
					"service.access.policy.name");

				Map<String, Object> settings = authVerifierResult.getSettings();

				List<String> serviceAccessPolicyNames =
					(List<String>)settings.computeIfAbsent(
						ServiceAccessPolicy.SERVICE_ACCESS_POLICY_NAMES,
						value -> new ArrayList<>());

				serviceAccessPolicyNames.add(serviceAccessPolicyName);
			}
		}
		catch (AuthException authException) {
			if (_log.isDebugEnabled()) {
				_log.debug(authException);
			}

			HttpServletResponse httpServletResponse =
				accessControlContext.getResponse();

			try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					httpServletResponse.getOutputStream())) {

				objectOutputStream.writeObject(authException);

				authVerifierResult.setState(
					AuthVerifierResult.State.INVALID_CREDENTIALS);
			}
			catch (IOException ioException) {
				_log.error(ioException);

				throw authException;
			}
		}

		return authVerifierResult;
	}

	protected String[] verify(HttpServletRequest httpServletRequest)
		throws AuthException {

		String authorization = httpServletRequest.getHeader(
			HttpHeaders.AUTHORIZATION);

		if (authorization == null) {
			return null;
		}

		String[] credentials = new String[2];

		credentials[0] = String.valueOf(
			TunnelAuthenticationManagerUtil.getUserId(httpServletRequest));

		credentials[1] = StringPool.BLANK;

		return credentials;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TunnelAuthVerifier.class);

}
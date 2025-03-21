/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.verifier.internal.basic.auth.header;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.http.HttpAuthorizationHeader;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.AutoLoginException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.security.auth.http.HttpAuthManagerUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Properties;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(service = AuthVerifier.class)
public class BasicAuthHeaderAuthVerifier implements AuthVerifier {

	@Override
	public String getAuthType() {
		return HttpServletRequest.BASIC_AUTH;
	}

	@Override
	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties properties)
		throws AuthException {

		try {
			AuthVerifierResult authVerifierResult = new AuthVerifierResult();

			String[] credentials = _autoLogin.login(
				accessControlContext.getRequest(),
				accessControlContext.getResponse());

			if (credentials != null) {
				authVerifierResult.setPassword(credentials[1]);
				authVerifierResult.setPasswordBasedAuthentication(true);
				authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
				authVerifierResult.setUserId(Long.valueOf(credentials[0]));
			}
			else if (_isBasicAuth(accessControlContext, properties)) {
				return _generateChallenge(accessControlContext);
			}

			return authVerifierResult;
		}
		catch (AutoLoginException autoLoginException) {
			if (_log.isDebugEnabled()) {
				_log.debug(autoLoginException);
			}

			return _generateChallenge(accessControlContext);
		}
	}

	private AuthVerifierResult _generateChallenge(
		AccessControlContext accessControlContext) {

		HttpAuthorizationHeader httpAuthorizationHeader =
			new HttpAuthorizationHeader(HttpAuthorizationHeader.SCHEME_BASIC);

		HttpAuthManagerUtil.generateChallenge(
			accessControlContext.getRequest(),
			accessControlContext.getResponse(), httpAuthorizationHeader);

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		authVerifierResult.setState(
			AuthVerifierResult.State.INVALID_CREDENTIALS);

		return authVerifierResult;
	}

	private boolean _isBasicAuth(
		AccessControlContext accessControlContext, Properties properties) {

		boolean basicAuth = MapUtil.getBoolean(
			accessControlContext.getSettings(), "basic_auth");

		if (!basicAuth) {
			basicAuth = GetterUtil.getBoolean(
				properties.getProperty("basic_auth"));
		}

		return basicAuth;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasicAuthHeaderAuthVerifier.class);

	@Reference(target = "(&(private.auto.login=true)(type=basic.auth.header))")
	private AutoLogin _autoLogin;

}
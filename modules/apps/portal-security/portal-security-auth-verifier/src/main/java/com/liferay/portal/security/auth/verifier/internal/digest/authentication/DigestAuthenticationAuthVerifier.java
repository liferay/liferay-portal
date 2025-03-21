/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.verifier.internal.digest.authentication;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.http.HttpAuthorizationHeader;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.security.auth.http.HttpAuthManagerUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Properties;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Component(service = AuthVerifier.class)
@Deprecated
public class DigestAuthenticationAuthVerifier implements AuthVerifier {

	@Override
	public String getAuthType() {
		return HttpServletRequest.DIGEST_AUTH;
	}

	@Override
	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties configuration)
		throws AuthException {

		try {
			AuthVerifierResult authVerifierResult = new AuthVerifierResult();

			HttpServletRequest httpServletRequest =
				accessControlContext.getRequest();

			long userId = HttpAuthManagerUtil.getDigestUserId(
				httpServletRequest);

			if (userId == 0) {
				boolean forcedDigestAuth = MapUtil.getBoolean(
					accessControlContext.getSettings(), "digest_auth");

				if (!forcedDigestAuth) {
					forcedDigestAuth = GetterUtil.getBoolean(
						configuration.getProperty("digest_auth"));
				}

				if (forcedDigestAuth) {
					HttpAuthorizationHeader httpAuthorizationHeader =
						new HttpAuthorizationHeader(
							HttpAuthorizationHeader.SCHEME_DIGEST);

					HttpAuthManagerUtil.generateChallenge(
						httpServletRequest, accessControlContext.getResponse(),
						httpAuthorizationHeader);

					authVerifierResult.setState(
						AuthVerifierResult.State.INVALID_CREDENTIALS);
				}

				return authVerifierResult;
			}

			authVerifierResult.setPasswordBasedAuthentication(true);
			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(userId);

			return authVerifierResult;
		}
		catch (PortalException portalException) {
			throw new AuthException(portalException);
		}
		catch (SystemException systemException) {
			throw new AuthException(systemException);
		}
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.verifier.internal.portal.session;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Properties;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(service = AuthVerifier.class)
public class PortalSessionAuthVerifier implements AuthVerifier {

	public static final String AUTH_TYPE = HttpServletRequest.FORM_AUTH;

	@Override
	public String getAuthType() {
		return AUTH_TYPE;
	}

	@Override
	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties properties)
		throws AuthException {

		try {
			AuthVerifierResult authVerifierResult = new AuthVerifierResult();

			HttpServletRequest httpServletRequest =
				accessControlContext.getRequest();

			User user = _portal.getUser(httpServletRequest);

			if ((user == null) || user.isGuestUser()) {
				return authVerifierResult;
			}

			boolean checkCSRFToken = GetterUtil.getBoolean(
				properties.get("check.csrf.token"), true);

			if (checkCSRFToken) {
				HttpServletRequest originalHttpServletRequest =
					_portal.getOriginalServletRequest(httpServletRequest);

				String requestURI = originalHttpServletRequest.getRequestURI();

				try {
					AuthTokenUtil.checkCSRFToken(
						originalHttpServletRequest, requestURI);
				}
				catch (PrincipalException principalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Unable to verify CSRF token for ", requestURI,
								": ", principalException.getMessage()));
					}

					return authVerifierResult;
				}
			}

			authVerifierResult.setPasswordBasedAuthentication(true);
			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(user.getUserId());

			return authVerifierResult;
		}
		catch (PortalException portalException) {
			throw new AuthException(portalException);
		}
		catch (SystemException systemException) {
			throw new AuthException(systemException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalSessionAuthVerifier.class);

	@Reference
	private Portal _portal;

}
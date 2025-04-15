/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.document.conversion.internal.security.auth.verifier;

import com.liferay.document.library.document.conversion.internal.ImageRequestTokenUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Properties;

/**
 * @author Daniel Sanz
 * @author István András Dézsi
 * @author Tomas Polesovsky
 */
public class ImageRequestAuthVerifier implements AuthVerifier {

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

		try {
			String token = ParamUtil.getString(
				httpServletRequest, "auth_token");

			if (Validator.isBlank(token)) {
				return authVerifierResult;
			}

			long userId = ImageRequestTokenUtil.getUserId(token);

			if (userId != 0) {
				authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
				authVerifierResult.setUserId(userId);
			}
			else {
				authVerifierResult.setState(
					AuthVerifierResult.State.INVALID_CREDENTIALS);
			}

			return authVerifierResult;
		}
		catch (Exception exception) {
			throw new AuthException(exception);
		}
	}

}
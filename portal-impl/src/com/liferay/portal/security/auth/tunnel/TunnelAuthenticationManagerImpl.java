/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.tunnel;

import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.RemoteAuthException;
import com.liferay.portal.kernel.security.auth.http.HttpAuthorizationHeader;
import com.liferay.portal.kernel.security.auth.tunnel.TunnelAuthenticationManager;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.http.HttpAuthManagerUtil;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

import java.net.HttpURLConnection;

import java.security.Key;

import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

/**
 * @author Tomas Polesovsky
 */
public class TunnelAuthenticationManagerImpl
	implements TunnelAuthenticationManager {

	@Override
	public long getUserId(HttpServletRequest httpServletRequest)
		throws AuthException {

		HttpAuthorizationHeader httpAuthorizationHeader =
			HttpAuthManagerUtil.parse(httpServletRequest);

		if (httpAuthorizationHeader == null) {
			return 0;
		}

		String scheme = httpAuthorizationHeader.getScheme();

		if (!StringUtil.equalsIgnoreCase(
				scheme, HttpAuthorizationHeader.SCHEME_BASIC)) {

			AuthException authException = new RemoteAuthException(
				"Invalid scheme " + scheme);

			authException.setType(AuthException.INTERNAL_SERVER_ERROR);

			throw authException;
		}

		String expectedPassword = null;

		String login = httpAuthorizationHeader.getAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_USERNAME);

		try {
			expectedPassword = EncryptorUtil.encrypt(
				getSharedSecretKey(), login);
		}
		catch (EncryptorException encryptorException) {
			AuthException authException = new RemoteAuthException(
				encryptorException);

			authException.setType(AuthException.INTERNAL_SERVER_ERROR);

			throw authException;
		}
		catch (AuthException authException1) {
			AuthException authException2 = new RemoteAuthException(
				authException1);

			authException2.setType(authException1.getType());

			throw authException2;
		}

		String password = httpAuthorizationHeader.getAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_PASSWORD);

		if (!Objects.equals(expectedPassword, password)) {
			AuthException authException = new RemoteAuthException();

			authException.setType(RemoteAuthException.WRONG_SHARED_SECRET);

			throw authException;
		}

		User user = UserLocalServiceUtil.fetchUser(GetterUtil.getLong(login));

		if (user == null) {
			long companyId = PortalInstances.getCompanyId(httpServletRequest);

			user = UserLocalServiceUtil.fetchUserByEmailAddress(
				companyId, login);

			if (user == null) {
				user = UserLocalServiceUtil.fetchUserByScreenName(
					companyId, login);
			}
		}

		if (user == null) {
			AuthException authException = new RemoteAuthException(
				"Unable to find user " + login);

			authException.setType(AuthException.INTERNAL_SERVER_ERROR);

			throw authException;
		}

		return user.getUserId();
	}

	@Override
	public void setCredentials(
			String login, HttpURLConnection httpURLConnection)
		throws Exception {

		if (Validator.isBlank(login)) {
			throw new IllegalArgumentException("Login is null");
		}

		HttpAuthorizationHeader httpAuthorizationHeader =
			new HttpAuthorizationHeader(HttpAuthorizationHeader.SCHEME_BASIC);

		String password = EncryptorUtil.encrypt(getSharedSecretKey(), login);

		httpAuthorizationHeader.setAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_PASSWORD, password);

		httpAuthorizationHeader.setAuthParameter(
			HttpAuthorizationHeader.AUTH_PARAMETER_NAME_USERNAME, login);

		httpURLConnection.setRequestProperty(
			HttpHeaders.AUTHORIZATION, httpAuthorizationHeader.toString());
	}

	protected Key getSharedSecretKey() throws AuthException {
		String sharedSecret = PropsValues.TUNNELING_SERVLET_SHARED_SECRET;

		if (Validator.isNull(sharedSecret)) {
			String message =
				"Please configure " + PropsKeys.TUNNELING_SERVLET_SHARED_SECRET;

			if (_log.isWarnEnabled()) {
				_log.warn(message);
			}

			AuthException authException = new AuthException(message);

			authException.setType(AuthException.NO_SHARED_SECRET);

			throw authException;
		}

		byte[] key = null;

		if (PropsValues.TUNNELING_SERVLET_SHARED_SECRET_HEX) {
			try {
				key = StringUtil.hexStringToBytes(sharedSecret);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				if (_log.isWarnEnabled()) {
					_log.warn(illegalArgumentException);
				}

				AuthException authException = new AuthException();

				authException.setType(AuthException.INVALID_SHARED_SECRET);

				throw authException;
			}
		}
		else {
			key = sharedSecret.getBytes();
		}

		if (key.length < 8) {
			String message =
				PropsKeys.TUNNELING_SERVLET_SHARED_SECRET + " is too short";

			if (_log.isWarnEnabled()) {
				_log.warn(message);
			}

			AuthException authException = new AuthException(message);

			authException.setType(AuthException.INVALID_SHARED_SECRET);

			throw authException;
		}

		if (StringUtil.equalsIgnoreCase(
				PropsValues.TUNNELING_SERVLET_ENCRYPTION_ALGORITHM, "AES") &&
			(key.length != 16) && (key.length != 32)) {

			String message =
				PropsKeys.TUNNELING_SERVLET_SHARED_SECRET +
					" must have 16 or 32 bytes when used with AES";

			if (_log.isWarnEnabled()) {
				_log.warn(message);
			}

			AuthException authException = new AuthException(message);

			authException.setType(AuthException.INVALID_SHARED_SECRET);

			throw authException;
		}

		return new SecretKeySpec(
			key, PropsValues.TUNNELING_SERVLET_ENCRYPTION_ALGORITHM);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TunnelAuthenticationManagerImpl.class);

}
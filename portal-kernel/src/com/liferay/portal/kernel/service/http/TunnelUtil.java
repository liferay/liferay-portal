/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.http;

import com.liferay.petra.io.ProtectedClassLoaderObjectInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.auth.tunnel.TunnelAuthenticationManagerUtil;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * @author Brian Wing Shun Chan
 */
public class TunnelUtil {

	public static Object invoke(
			HttpPrincipal httpPrincipal, MethodHandler methodHandler)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Method handler " + methodHandler);
		}

		HttpURLConnection httpURLConnection = _getConnection(httpPrincipal);

		TunnelAuthenticationManagerUtil.setCredentials(
			httpPrincipal.getLogin(), httpURLConnection);

		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				httpURLConnection.getOutputStream())) {

			objectOutputStream.writeObject(
				new ObjectValuePair<HttpPrincipal, MethodHandler>(
					httpPrincipal, methodHandler));
		}
		catch (SocketTimeoutException socketTimeoutException) {
			_log.error(
				"Tunnel connection time out may be configured with the " +
					"portal property \"tunneling.servlet.timeout\"");

			throw socketTimeoutException;
		}

		Object returnObject = null;

		Thread thread = Thread.currentThread();

		try (ObjectInputStream objectInputStream =
				new ProtectedClassLoaderObjectInputStream(
					httpURLConnection.getInputStream(),
					AggregateClassLoader.getAggregateClassLoader(
						TunnelUtil.class.getClassLoader(),
						thread.getContextClassLoader()))) {

			returnObject = objectInputStream.readObject();
		}
		catch (EOFException eofException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to read object", eofException);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Return object " + returnObject);
		}

		if ((returnObject != null) && (returnObject instanceof Exception)) {
			throw (Exception)returnObject;
		}

		return returnObject;
	}

	private static HttpURLConnection _getConnection(HttpPrincipal httpPrincipal)
		throws Exception {

		if ((httpPrincipal == null) || (httpPrincipal.getUrl() == null)) {
			return null;
		}

		URL url = new URL(httpPrincipal.getUrl() + "/api/liferay/do");

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		int connectTimeout = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.TUNNELING_SERVLET_TIMEOUT));

		if (connectTimeout > 0) {
			httpURLConnection.setConnectTimeout(connectTimeout);
		}

		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(true);

		if (!_VERIFY_SSL_HOSTNAME &&
			(httpURLConnection instanceof HttpsURLConnection)) {

			FIPSModeValidator.validateTLSVerification(_VERIFY_SSL_HOSTNAME);

			HttpsURLConnection httpsURLConnection =
				(HttpsURLConnection)httpURLConnection;

			httpsURLConnection.setHostnameVerifier(
				new HostnameVerifier() {

					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}

				});
		}

		httpURLConnection.setRequestMethod(HttpMethods.POST);
		httpURLConnection.setRequestProperty(
			HttpHeaders.CONTENT_TYPE,
			ContentTypes.APPLICATION_X_JAVA_SERIALIZED_OBJECT);
		httpURLConnection.setUseCaches(false);

		return httpURLConnection;
	}

	private static final boolean _VERIFY_SSL_HOSTNAME = GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.TUNNEL_UTIL_VERIFY_SSL_HOSTNAME));

	private static final Log _log = LogFactoryUtil.getLog(TunnelUtil.class);

}
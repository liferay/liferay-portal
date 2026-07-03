/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.auth.tunnel.TunnelAuthenticationManagerUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProtectedClassLoaderObjectInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * @author Brian Wing Shun Chan
 */
public class TunnelUtil {

	public static Object invoke(
			HttpPrincipal httpPrincipal, MethodHandler methodHandler)
		throws Exception {

		HttpURLConnection httpURLConnection = _getConnection(httpPrincipal);

		TunnelAuthenticationManagerUtil.setCredentials(
			httpPrincipal.getLogin(), httpURLConnection);

		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				httpURLConnection.getOutputStream())) {

			objectOutputStream.writeObject(
				new ObjectValuePair<HttpPrincipal, MethodHandler>(
					httpPrincipal, methodHandler));
		}
		catch (SocketTimeoutException ste) {
			_log.error(
				"Tunnel connection time out may be configured with the " +
					"portal property \"tunneling.servlet.timeout\"");

			throw ste;
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
		catch (EOFException eofe) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to read object", eofe);
			}
		}

		if ((returnObject != null) && returnObject instanceof Exception) {
			throw (Exception)returnObject;
		}

		return returnObject;
	}

	private static HttpURLConnection _getConnection(HttpPrincipal httpPrincipal)
		throws IOException {

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

		httpURLConnection.setRequestMethod(HttpMethods.POST);
		httpURLConnection.setRequestProperty(
			HttpHeaders.CONTENT_TYPE,
			ContentTypes.APPLICATION_X_JAVA_SERIALIZED_OBJECT);
		httpURLConnection.setUseCaches(false);

		return httpURLConnection;
	}

	private static final Log _log = LogFactoryUtil.getLog(TunnelUtil.class);

}
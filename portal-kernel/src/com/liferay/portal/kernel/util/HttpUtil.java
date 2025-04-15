/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

/**
 * @author Brian Wing Shun Chan
 */
public class HttpUtil {

	public static Cookie[] getCookies() {
		Http http = _httpSnapshot.get();

		return http.getCookies();
	}

	public static Http getHttp() {
		return _httpSnapshot.get();
	}

	public static boolean hasProxyConfig() {
		Http http = _httpSnapshot.get();

		return http.hasProxyConfig();
	}

	public static boolean isNonProxyHost(String host) {
		Http http = _httpSnapshot.get();

		return http.isNonProxyHost(host);
	}

	public static boolean isProxyHost(String host) {
		Http http = _httpSnapshot.get();

		return http.isProxyHost(host);
	}

	public static byte[] URLtoByteArray(Http.Options options)
		throws IOException {

		Http http = _httpSnapshot.get();

		return http.URLtoByteArray(options);
	}

	public static byte[] URLtoByteArray(String location) throws IOException {
		Http http = _httpSnapshot.get();

		return http.URLtoByteArray(location);
	}

	public static byte[] URLtoByteArray(String location, boolean post)
		throws IOException {

		Http http = _httpSnapshot.get();

		return http.URLtoByteArray(location, post);
	}

	public static InputStream URLtoInputStream(Http.Options options)
		throws IOException {

		Http http = _httpSnapshot.get();

		return http.URLtoInputStream(options);
	}

	public static InputStream URLtoInputStream(String location)
		throws IOException {

		Http http = _httpSnapshot.get();

		return http.URLtoInputStream(location);
	}

	public static InputStream URLtoInputStream(String location, boolean post)
		throws IOException {

		Http http = _httpSnapshot.get();

		return http.URLtoInputStream(location, post);
	}

	public static String URLtoString(Http.Options options) throws IOException {
		Http http = _httpSnapshot.get();

		return http.URLtoString(options);
	}

	public static String URLtoString(String location) throws IOException {
		Http http = _httpSnapshot.get();

		return http.URLtoString(location);
	}

	public static String URLtoString(String location, boolean post)
		throws IOException {

		Http http = _httpSnapshot.get();

		return http.URLtoString(location, post);
	}

	/**
	 * This method only uses the default Commons HttpClient implementation when
	 * the URL object represents a HTTP resource. The URL object could also
	 * represent a file or some JNDI resource. In that case, the default Java
	 * implementation is used.
	 *
	 * @param  url the URL
	 * @return A string representation of the resource referenced by the URL
	 *         object
	 * @throws IOException if an IO Exception occurred
	 */
	public static String URLtoString(URL url) throws IOException {
		Http http = _httpSnapshot.get();

		return http.URLtoString(url);
	}

	private static final Snapshot<Http> _httpSnapshot = new Snapshot<>(
		HttpUtil.class, Http.class);

}
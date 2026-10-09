/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.benchmarks.http;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author Dante Wang
 * @author Tina Tian
 */
public class HttpUtil {

	public static HttpResponse doGet(String csrfToken, URL url)
		throws Exception {

		return _execute(csrfToken, "GET", url, null);
	}

	public static HttpResponse doPost(
			String content, String csrfToken, String[][] parameters, URL url)
		throws Exception {

		return _execute(
			csrfToken, "POST", url,
			httpURLConnection -> {
				httpURLConnection.setDoOutput(true);

				try (PrintWriter printWriter = new PrintWriter(
						httpURLConnection.getOutputStream())) {

					if (parameters != null) {
						boolean first = true;

						for (String[] parameter : parameters) {
							if (first) {
								first = false;
							}
							else {
								printWriter.print(CharPool.AMPERSAND);
							}

							printWriter.print(parameter[0]);
							printWriter.print(CharPool.EQUAL);
							printWriter.print(
								URLEncoder.encode(
									parameter[1], StringPool.UTF8));
						}
					}

					if (content != null) {
						printWriter.print(content);
					}
				}
			});
	}

	private static HttpResponse _execute(
			String csrfToken, String httpMethod, URL url,
			UnsafeConsumer<HttpURLConnection, Exception> unsafeConsumer)
		throws Exception {

		HttpURLConnection httpURLConnection = null;

		try {
			httpURLConnection = (HttpURLConnection)url.openConnection();

			if (csrfToken != null) {
				httpURLConnection.addRequestProperty("X-Csrf-Token", csrfToken);
			}

			httpURLConnection.setConnectTimeout(0);
			httpURLConnection.setReadTimeout(0);
			httpURLConnection.setRequestMethod(httpMethod);

			if (unsafeConsumer != null) {
				unsafeConsumer.accept(httpURLConnection);
			}

			long startTime = System.currentTimeMillis();

			httpURLConnection.connect();

			ByteBuffer bytes = _read(httpURLConnection.getInputStream());

			return new HttpResponse(
				System.currentTimeMillis() - startTime,
				httpURLConnection.getHeaderFields(),
				httpURLConnection.getResponseCode(),
				httpURLConnection.getResponseMessage(),
				new String(
					bytes.array(), 0, bytes.limit(), StandardCharsets.UTF_8));
		}
		catch (IOException ioException1) {
			if (httpURLConnection == null) {
				throw ioException1;
			}

			try (InputStream inputStream = httpURLConnection.getErrorStream()) {
				if (inputStream != null) {
					while (inputStream.read() != -1);
				}
			}
			catch (IOException ioException2) {
				throw new IOException(ioException2);
			}

			throw ioException1;
		}
		finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	private static ByteBuffer _read(InputStream inputStream) throws Exception {
		byte[] bytes = _bytes.get();

		int left = bytes.length;

		int length = -1;
		int offset = 0;

		while ((length = inputStream.read(bytes, offset, left)) != -1) {
			left -= length;
			offset += length;

			if (left != 0) {
				continue;
			}

			int newLength = bytes.length * 6 / 5;

			byte[] newBytes = new byte[newLength];

			System.arraycopy(bytes, 0, newBytes, 0, bytes.length);

			left = newLength - bytes.length;

			bytes = newBytes;

			_bytes.set(bytes);
		}

		inputStream.close();

		return ByteBuffer.wrap(bytes, 0, offset);
	}

	private static final ThreadLocal<byte[]> _bytes =
		CentralizedThreadLocal.withInitial(() -> new byte[8192]);

	static {
		CookieHandler.setDefault(
			new CookieManager(
				new ThreadLocalCookieStore(), CookiePolicy.ACCEPT_ALL));

		HttpURLConnection.setFollowRedirects(false);
	}

}
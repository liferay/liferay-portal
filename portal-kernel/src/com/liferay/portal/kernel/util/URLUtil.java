/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author Shuyang Zhou
 */
public class URLUtil {

	public static void download(URL url, Path path, String sha1)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info(StringBundler.concat("Downloading ", url, " to ", path));
		}

		try (InputStream inputStream = url.openStream()) {
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
		}

		String digest = null;

		try (InputStream inputStream = Files.newInputStream(path)) {
			digest = DigesterUtil.digestHex(DigesterUtil.SHA_1, inputStream);
		}

		if (!StringUtil.equalsIgnoreCase(sha1, digest)) {
			Files.delete(path);

			throw new Exception(
				StringBundler.concat(
					"Unable to download ", url, " to ", path, " because ", sha1,
					" does not equal ", digest));
		}

		if (_log.isInfoEnabled()) {
			_log.info(StringBundler.concat("Downloaded ", url, " to ", path));
		}
	}

	public static long getLastModifiedTime(URL url) throws IOException {
		URLConnection urlConnection = null;

		try {
			urlConnection = url.openConnection();

			return urlConnection.getLastModified();
		}
		finally {
			if (urlConnection != null) {
				try {
					InputStream inputStream = urlConnection.getInputStream();

					inputStream.close();
				}
				catch (IOException ioException) {
				}
			}
		}
	}

	public static byte[] toByteArray(URL url) throws IOException {
		return StreamUtil.toByteArray(url.openStream());
	}

	public static String toString(URL url) throws IOException {
		return StreamUtil.toString(url.openStream());
	}

	private static final Log _log = LogFactoryUtil.getLog(URLUtil.class);

}
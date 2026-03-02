/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.exportimport.attachment.ExportImportAttachmentManagerUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class URLUtil {

	public static byte[] getByteArray(String urlString) throws Exception {
		URL url = ExportImportAttachmentManagerUtil.getURL(urlString);

		if (Objects.equals(url.getProtocol(), "file")) {
			throw new UnsupportedOperationException(
				StringBundler.concat(
					"Unable to download file from ", urlString,
					" because of unsupported protocol ", url.getProtocol()));
		}

		URLConnection urlConnection = null;

		try {
			urlConnection = url.openConnection();

			if ((urlConnection instanceof
					HttpURLConnection httpURLConnection) &&
				(httpURLConnection.getResponseCode() !=
					HttpURLConnection.HTTP_OK)) {

				throw new IllegalArgumentException(
					"Unable to download file from " + urlString);
			}

			try (InputStream inputStream = urlConnection.getInputStream()) {
				return StreamUtil.toByteArray(inputStream);
			}
		}
		finally {
			if (urlConnection instanceof HttpURLConnection httpURLConnection) {
				httpURLConnection.disconnect();
			}
		}
	}

}
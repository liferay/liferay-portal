/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.exportimport.attachment.ExportImportAttachmentManagerUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;

import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class URLUtil {

	public static byte[] getByteArray(String urlString) throws Exception {
		URL url = ExportImportAttachmentManagerUtil.getURL(urlString);

		String protocol = url.getProtocol();

		if (Objects.equals(protocol, Http.HTTP) ||
			Objects.equals(protocol, Http.HTTPS)) {

			return HttpUtil.URLtoByteArray(url.toString());
		}

		if (Objects.equals(protocol, "lar")) {
			URLConnection urlConnection = url.openConnection();

			try (InputStream inputStream = urlConnection.getInputStream()) {
				return StreamUtil.toByteArray(inputStream);
			}
		}

		throw new UnsupportedOperationException(
			StringBundler.concat(
				"Unable to download file from ", urlString,
				" because of unsupported protocol ", protocol));
	}

}
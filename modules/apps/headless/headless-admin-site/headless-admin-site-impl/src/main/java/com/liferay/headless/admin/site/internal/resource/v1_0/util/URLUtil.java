/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.exportimport.attachment.ExportImportAttachmentManagerUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URL;

import java.nio.charset.StandardCharsets;

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

		String string = HttpUtil.URLtoString(url);

		if (Validator.isNull(string)) {
			return new byte[0];
		}

		return string.getBytes(StandardCharsets.UTF_8);
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

/**
 * @author Iván Zaera Avellón
 */
public class FrontendResourceUtil {

	public static String getCacheKey(URL url) {
		if (HashedFilesUtil.containsHash(url.getFile())) {
			return url.toString();
		}

		try {
			URLConnection urlConnection = url.openConnection();

			long lastModified = urlConnection.getLastModified();

			if (lastModified == 0) {
				return null;
			}

			return url + StringPool.POUND + lastModified;
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to open URL connection for " + url, ioException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendResourceUtil.class);

}
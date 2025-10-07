/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.frontend.hashed.files;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

/**
 * @author Iván Zaera Avellón
 */
public class HashedFilesUtil {

	public static String addHash(String uri, String hash) {
		if (getHash(uri) != null) {
			throw new IllegalArgumentException(
				"URI already contains hash: " + uri);
		}

		int i = uri.lastIndexOf(".");

		if (i == -1) {
			throw new IllegalArgumentException(
				"URI has no file extension: " + uri);
		}

		StringBuilder sb = new StringBuilder();

		sb.append(uri, 0, i);
		sb.append(".(");
		sb.append(hash);
		sb.append(StringPool.CLOSE_PARENTHESIS);
		sb.append(uri.substring(i));

		return sb.toString();
	}

	public static String addNameSuffix(String uri, String suffix) {
		int i = uri.lastIndexOf(".(");

		if (i == -1) {
			throw new IllegalArgumentException(
				"URI does not contain hash: " + uri);
		}

		return uri.substring(0, i) + suffix + uri.substring(i);
	}

	public static boolean containsHash(String uri) {
		if ((uri.lastIndexOf(".(") == -1) || (uri.lastIndexOf(").") == -1)) {
			return false;
		}

		return true;
	}

	public static String getHash(String uri) {
		int i = uri.lastIndexOf(".(");

		if (i == -1) {
			return null;
		}

		int j = uri.lastIndexOf(").");

		if (j == -1) {
			return null;
		}

		return uri.substring(i + 2, j);
	}

	public static String removeHash(String uri) {
		String hash = getHash(uri);

		if (hash == null) {
			throw new IllegalArgumentException(
				"URI does not contain hash: " + uri);
		}

		return StringUtil.replace(uri, ".(" + hash + ").", ".");
	}

}
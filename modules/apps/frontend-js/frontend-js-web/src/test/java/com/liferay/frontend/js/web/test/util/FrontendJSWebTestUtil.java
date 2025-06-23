/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.test.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;

/**
 * @author Iván Zaera Avellón
 */
public class FrontendJSWebTestUtil {

	public static String randomHashedFileHash() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 8; i++) {
			sb.append(
				_RANDOM_HASHED_FILE_HASH_DICTIONARY.charAt(
					RandomTestUtil.randomInt(
						0, _RANDOM_HASHED_FILE_HASH_DICTIONARY.length() - 1)));
		}

		return sb.toString();
	}

	private static final String _RANDOM_HASHED_FILE_HASH_DICTIONARY =
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789$@";

}
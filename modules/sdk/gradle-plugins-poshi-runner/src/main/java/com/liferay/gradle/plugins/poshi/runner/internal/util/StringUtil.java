/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.poshi.runner.internal.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Kenji Heigel
 */
public class StringUtil extends com.liferay.gradle.util.StringUtil {

	public static String read(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[8192];
		int offset = 0;

		while (true) {
			int count = inputStream.read(
				buffer, offset, buffer.length - offset);

			if (count == -1) {
				break;
			}

			offset += count;

			if (offset != buffer.length) {
				continue;
			}

			byte[] newBuffer = new byte[buffer.length << 1];

			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);

			buffer = newBuffer;
		}

		if (offset == 0) {
			return "";
		}

		return new String(buffer, 0, offset, "UTF-8");
	}

}
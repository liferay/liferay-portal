/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.release.util.internal.util;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Drew Brokke
 */
public class StringUtil {

	public static final String COMMA = ",";

	public static final String FORWARD_SLASH = "/";

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

		return new String(buffer, 0, offset, StandardCharsets.UTF_8);
	}

	public static List<String> split(String s) {
		if ((s == null) || s.isEmpty()) {
			return Collections.emptyList();
		}

		s = s.trim();

		if (s.isEmpty()) {
			return Collections.emptyList();
		}

		return Arrays.asList(s.split(COMMA));
	}

}
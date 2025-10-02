/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security;

import java.util.Arrays;

/**
 * @author Tomas Polesovsky
 */
public class ChecksumUtil {

	public static byte[] appendChecksum(byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		if (bytes.length == 0) {
			return new byte[0];
		}

		int checksum = 0;

		for (byte b : bytes) {
			checksum += b & 0xFF;
		}

		byte sumComplement = (byte)(256 - (checksum & 0xFF));

		byte[] result = Arrays.copyOf(bytes, bytes.length + 1);

		result[result.length - 1] = sumComplement;

		return result;
	}

	public static boolean isValid(byte[] bytes) {
		if ((bytes == null) || (bytes.length < 2)) {
			return false;
		}

		int checksum = 0;

		for (byte b : bytes) {
			checksum += b & 0xFF;
		}

		if ((checksum & 0xFF) == 0) {
			return true;
		}

		return false;
	}

	public static byte[] removeChecksum(byte[] bytes) {
		if ((bytes == null) || (bytes.length < 2)) {
			return null;
		}

		return Arrays.copyOf(bytes, bytes.length - 1);
	}

}
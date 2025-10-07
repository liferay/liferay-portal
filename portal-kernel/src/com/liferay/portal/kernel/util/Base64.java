/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.io.ProtectedObjectInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Brian Wing Shun Chan
 */
public class Base64 {

	public static byte[] decode(String base64) {
		return _decode(base64, false);
	}

	public static byte[] decodeFromURL(String base64) {
		return _decode(base64, true);
	}

	public static String encode(byte[] raw) {
		return _encode(raw, 0, raw.length, false);
	}

	public static String encodeToURL(byte[] raw) {
		return _encode(raw, 0, raw.length, true);
	}

	public static String objectToString(Object object) {
		if (object == null) {
			return null;
		}

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream(32000);

		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				unsyncByteArrayOutputStream)) {

			objectOutputStream.writeObject(object);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return _encode(
			unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
			unsyncByteArrayOutputStream.size(), false);
	}

	public static Object stringToObject(String s) {
		return _stringToObject(s, null, false);
	}

	public static Object stringToObject(String s, ClassLoader classLoader) {
		return _stringToObject(s, classLoader, false);
	}

	public static Object stringToObjectSilent(String s) {
		return _stringToObject(s, null, true);
	}

	private static byte[] _decode(String base64, boolean url) {
		if (Validator.isNull(base64)) {
			return new byte[0];
		}

		char padChar = CharPool.EQUAL;

		if (url) {
			padChar = CharPool.STAR;
		}

		int pad = 0;

		for (int i = base64.length() - 1; base64.charAt(i) == padChar; i--) {
			pad++;
		}

		int length = ((base64.length() * 6) / 8) - pad;

		byte[] raw = new byte[length];

		int rawindex = 0;

		for (int i = 0; i < base64.length(); i += 4) {
			int block = _getValue(base64, i, url) << 18;

			block += _getValue(base64, i + 1, url) << 12;
			block += _getValue(base64, i + 2, url) << 6;
			block += _getValue(base64, i + 3, url);

			for (int j = 0; (j < 3) && ((rawindex + j) < raw.length); j++) {
				raw[rawindex + j] = (byte)((block >> (8 * (2 - j))) & 0xff);
			}

			rawindex += 3;
		}

		return raw;
	}

	private static String _encode(
		byte[] raw, int offset, int length, boolean url) {

		int lastIndex = Math.min(raw.length, offset + length);

		StringBundler sb = new StringBundler(
			(((lastIndex - offset) / 3) + 1) * 4);

		for (int i = offset; i < lastIndex; i += 3) {
			sb.append(_encodeBlock(raw, i, lastIndex, url));
		}

		return sb.toString();
	}

	private static char[] _encodeBlock(
		byte[] raw, int offset, int lastIndex, boolean url) {

		int block = 0;

		int slack = lastIndex - offset - 1;

		int end = (slack < 2) ? slack : 2;

		for (int i = 0; i <= end; i++) {
			byte b = raw[offset + i];

			int neuter = (b >= 0) ? (int)b : b + 256;

			block += neuter << (8 * (2 - i));
		}

		char[] base64 = new char[4];

		for (int i = 0; i < 4; i++) {
			int sixbit = (block >>> (6 * (3 - i))) & 0x3f;

			base64[i] = _getChar(sixbit, url);
		}

		if (url) {
			if (slack < 1) {
				base64[2] = CharPool.STAR;
			}

			if (slack < 2) {
				base64[3] = CharPool.STAR;
			}
		}
		else {
			if (slack < 1) {
				base64[2] = CharPool.EQUAL;
			}

			if (slack < 2) {
				base64[3] = CharPool.EQUAL;
			}
		}

		return base64;
	}

	private static char _getChar(int sixbit, boolean url) {
		if ((sixbit >= 0) && (sixbit <= 25)) {
			return (char)(65 + sixbit);
		}

		if ((sixbit >= 26) && (sixbit <= 51)) {
			return (char)(97 + (sixbit - 26));
		}

		if ((sixbit >= 52) && (sixbit <= 61)) {
			return (char)(48 + (sixbit - 52));
		}

		if (sixbit == 62) {
			if (url) {
				return CharPool.MINUS;
			}

			return CharPool.PLUS;
		}

		if (sixbit != 63) {
			return CharPool.QUESTION;
		}

		if (url) {
			return CharPool.UNDERLINE;
		}

		return CharPool.SLASH;
	}

	private static int _getValue(String base64, int index, boolean url) {
		if (index >= base64.length()) {

			// Padding is missing. Pretend that it exists.

			return 0;
		}

		char c = base64.charAt(index);

		if ((c >= CharPool.UPPER_CASE_A) && (c <= CharPool.UPPER_CASE_Z)) {
			return c - 65;
		}

		if ((c >= CharPool.LOWER_CASE_A) && (c <= CharPool.LOWER_CASE_Z)) {
			return (c - 97) + 26;
		}

		if ((c >= CharPool.NUMBER_0) && (c <= CharPool.NUMBER_9)) {
			return (c - 48) + 52;
		}

		if (url) {
			if (c == CharPool.MINUS) {
				return 62;
			}

			if (c == CharPool.UNDERLINE) {
				return 63;
			}

			if (c != CharPool.STAR) {
				throw new IllegalArgumentException(
					String.format("Found illegal character: %c", c));
			}
		}
		else {
			if (c == CharPool.PLUS) {
				return 62;
			}

			if (c == CharPool.SLASH) {
				return 63;
			}

			if (c != CharPool.EQUAL) {
				throw new IllegalArgumentException(
					String.format("Found illegal character: %c", c));
			}
		}

		return 0;
	}

	private static Object _stringToObject(
		String s, ClassLoader classLoader, boolean silent) {

		if (s == null) {
			return null;
		}

		byte[] bytes = _decode(s, false);

		UnsyncByteArrayInputStream unsyncByteArrayInputStream =
			new UnsyncByteArrayInputStream(bytes);

		try {
			ObjectInputStream objectInputStream = null;

			if (classLoader == null) {
				objectInputStream = new ProtectedObjectInputStream(
					unsyncByteArrayInputStream);
			}
			else {
				objectInputStream = new ProtectedClassLoaderObjectInputStream(
					unsyncByteArrayInputStream, classLoader);
			}

			return objectInputStream.readObject();
		}
		catch (Exception exception) {
			if (!silent) {
				_log.error(exception);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(Base64.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.nio.CharsetDecoderUtil;
import com.liferay.petra.nio.CharsetEncoderUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 */
public class URLCodec {

	public static String decodeURL(String encodedURLString) {
		return decodeURL(encodedURLString, StringPool.UTF8);
	}

	public static String decodeURL(
		String encodedURLString, String charsetName) {

		if (encodedURLString == null) {
			return null;
		}

		if (encodedURLString.length() == 0) {
			return StringPool.BLANK;
		}

		StringBuilder sb = null;

		CharsetDecoder charsetDecoder = null;

		for (int i = 0; i < encodedURLString.length(); i++) {
			char c = encodedURLString.charAt(i);

			if (c == CharPool.PERCENT) {
				ByteBuffer byteBuffer = _getEncodedByteBuffer(
					encodedURLString, i);

				if (charsetDecoder == null) {
					charsetDecoder = CharsetDecoderUtil.getCharsetDecoder(
						charsetName);
				}

				CharBuffer charBuffer = null;

				try {
					charBuffer = charsetDecoder.decode(byteBuffer);
				}
				catch (CharacterCodingException characterCodingException) {
					_log.error(characterCodingException);

					return StringPool.BLANK;
				}

				if (sb == null) {
					sb = new StringBuilder(encodedURLString.length());

					if (i > 0) {
						sb.append(encodedURLString, 0, i);
					}
				}

				sb.append(charBuffer);

				i += (byteBuffer.capacity() * 3) - 1;
			}
			else if (c == CharPool.PLUS) {
				if (sb == null) {
					sb = new StringBuilder(encodedURLString.length());

					if (i > 0) {
						sb.append(encodedURLString, 0, i);
					}
				}

				sb.append(CharPool.SPACE);
			}
			else if (sb != null) {
				sb.append(c);
			}
		}

		if (sb == null) {
			return encodedURLString;
		}

		return sb.toString();
	}

	public static String encodeURL(String rawURLString) {
		return encodeURL(rawURLString, StringPool.UTF8, false);
	}

	public static String encodeURL(String rawURLString, boolean escapeSpaces) {
		return encodeURL(rawURLString, StringPool.UTF8, escapeSpaces);
	}

	public static String encodeURL(
		String rawURLString, String charsetName, boolean escapeSpaces) {

		if (rawURLString == null) {
			return null;
		}

		if (rawURLString.isEmpty()) {
			return StringPool.BLANK;
		}

		CharsetEncoder charsetEncoder = null;
		char[] hexes = new char[2];
		int lastReplacementIndex = 0;
		StringBuilder sb = null;

		for (int i = 0; i < rawURLString.length(); i++) {
			char c = rawURLString.charAt(i);

			if ((c < 128) && _VALID_CHARS[c]) {
				continue;
			}

			if (sb == null) {
				sb = new StringBuilder(rawURLString.length() + 64);

				sb.append(rawURLString, 0, i);
			}
			else if (i > lastReplacementIndex) {
				sb.append(rawURLString, lastReplacementIndex, i);
			}

			if (c < 128) {
				char[] encodingReplacement = _ENCODING_REPLACEMENTS[c];

				if (encodingReplacement != null) {
					if (!escapeSpaces && (c == CharPool.SPACE)) {
						sb.append(CharPool.PLUS);
					}
					else {
						sb.append(encodingReplacement);
					}

					lastReplacementIndex = i + 1;

					continue;
				}
			}

			CharBuffer charBuffer = _getRawCharBuffer(
				rawURLString, i, escapeSpaces);

			if (charsetEncoder == null) {
				charsetEncoder = CharsetEncoderUtil.getCharsetEncoder(
					charsetName);
			}

			i += charBuffer.length() - 1;

			lastReplacementIndex = i + 1;

			ByteBuffer byteBuffer = null;

			try {
				byteBuffer = charsetEncoder.encode(charBuffer);
			}
			catch (CharacterCodingException characterCodingException) {
				_log.error(characterCodingException);

				return StringPool.BLANK;
			}

			for (int j = byteBuffer.position(); j < byteBuffer.limit(); j++) {
				sb.append(CharPool.PERCENT);

				sb.append(
					UnicodeFormatter.byteToHex(byteBuffer.get(), hexes, true));
			}
		}

		if (sb == null) {
			return rawURLString;
		}

		if (lastReplacementIndex < rawURLString.length()) {
			sb.append(
				rawURLString, lastReplacementIndex, rawURLString.length());
		}

		return sb.toString();
	}

	private static int _charToHex(char c) {
		if ((c >= CharPool.LOWER_CASE_A) && (c <= CharPool.LOWER_CASE_F)) {
			return c - CharPool.LOWER_CASE_A + 10;
		}

		if ((c >= CharPool.UPPER_CASE_A) && (c <= CharPool.UPPER_CASE_F)) {
			return c - CharPool.UPPER_CASE_A + 10;
		}

		if ((c >= CharPool.NUMBER_0) && (c <= CharPool.NUMBER_9)) {
			return c - CharPool.NUMBER_0;
		}

		throw new IllegalArgumentException(c + " is not a hex char");
	}

	private static ByteBuffer _getEncodedByteBuffer(
		String encodedString, int start) {

		int count = 1;

		for (int i = start + 3; i < encodedString.length(); i += 3) {
			if (encodedString.charAt(i) != CharPool.PERCENT) {
				break;
			}

			count++;
		}

		if (encodedString.length() < (start + (count * 3))) {
			throw new IllegalArgumentException(
				"Invalid URL encoding " + encodedString);
		}

		ByteBuffer byteBuffer = ByteBuffer.allocate(count);

		for (int i = start; i < (start + (count * 3)); i += 3) {
			int high = _charToHex(encodedString.charAt(i + 1));
			int low = _charToHex(encodedString.charAt(i + 2));

			byteBuffer.put((byte)((high << 4) + low));
		}

		byteBuffer.flip();

		return byteBuffer;
	}

	private static CharBuffer _getRawCharBuffer(
		String rawString, int start, boolean escapeSpaces) {

		int count = 0;

		for (int i = start; i < rawString.length(); i++) {
			char rawChar = rawString.charAt(i);

			if (((rawChar < 128) && _VALID_CHARS[rawChar]) ||
				(!escapeSpaces && (rawChar == CharPool.SPACE))) {

				break;
			}

			count++;

			if (!Character.isHighSurrogate(rawChar) ||
				((i + 1) >= rawString.length()) ||
				!Character.isLowSurrogate(rawString.charAt(i + 1))) {

				continue;
			}

			i++;
			count++;
		}

		return CharBuffer.wrap(rawString, start, start + count);
	}

	private static final char[][] _ENCODING_REPLACEMENTS = new char[128][];

	private static final boolean[] _VALID_CHARS = new boolean[128];

	private static final Log _log = LogFactoryUtil.getLog(URLCodec.class);

	static {
		_ENCODING_REPLACEMENTS[CharPool.AMPERSAND] = "%26".toCharArray();
		_ENCODING_REPLACEMENTS[CharPool.COLON] = "%3A".toCharArray();
		_ENCODING_REPLACEMENTS[CharPool.EQUAL] = "%3D".toCharArray();
		_ENCODING_REPLACEMENTS[CharPool.PERCENT] = "%25".toCharArray();
		_ENCODING_REPLACEMENTS[CharPool.PLUS] = "%2B".toCharArray();
		_ENCODING_REPLACEMENTS[CharPool.QUESTION] = "%3F".toCharArray();
		_ENCODING_REPLACEMENTS[CharPool.SLASH] = "%2F".toCharArray();
		_ENCODING_REPLACEMENTS[CharPool.SPACE] = "%20".toCharArray();

		for (int i = 'a'; i <= 'z'; i++) {
			_VALID_CHARS[i] = true;
		}

		for (int i = 'A'; i <= 'Z'; i++) {
			_VALID_CHARS[i] = true;
		}

		for (int i = '0'; i <= '9'; i++) {
			_VALID_CHARS[i] = true;
		}

		_VALID_CHARS['-'] = true;
		_VALID_CHARS['_'] = true;
		_VALID_CHARS['.'] = true;
		_VALID_CHARS['*'] = true;
	}

}
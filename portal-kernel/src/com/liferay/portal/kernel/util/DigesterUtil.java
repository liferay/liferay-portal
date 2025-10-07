/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.nio.ByteBuffer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Connor McKay
 */
public class DigesterUtil {

	public static final String DEFAULT_ALGORITHM = "SHA";

	public static final String ENCODING = StringPool.UTF8;

	public static final String MD5 = "MD5";

	public static final String SHA = "SHA";

	public static final String SHA_1 = "SHA-1";

	public static final String SHA_256 = "SHA-256";

	public static String digest(ByteBuffer byteBuffer) {
		return digest(DEFAULT_ALGORITHM, byteBuffer);
	}

	public static String digest(InputStream inputStream) {
		return digest(DEFAULT_ALGORITHM, inputStream);
	}

	public static String digest(String text) {
		return digest(DEFAULT_ALGORITHM, text);
	}

	public static String digest(String algorithm, ByteBuffer byteBuffer) {
		if (_BASE_64) {
			return digestBase64(algorithm, byteBuffer);
		}

		return digestHex(algorithm, byteBuffer);
	}

	public static String digest(String algorithm, InputStream inputStream) {
		if (_BASE_64) {
			return digestBase64(algorithm, inputStream);
		}

		return digestHex(algorithm, inputStream);
	}

	public static String digest(String algorithm, String... text) {
		if (_BASE_64) {
			return digestBase64(algorithm, text);
		}

		return digestHex(algorithm, text);
	}

	public static String digestBase64(ByteBuffer byteBuffer) {
		return digestBase64(DEFAULT_ALGORITHM, byteBuffer);
	}

	public static String digestBase64(InputStream inputStream) {
		return digestBase64(DEFAULT_ALGORITHM, inputStream);
	}

	public static String digestBase64(String text) {
		return digestBase64(DEFAULT_ALGORITHM, text);
	}

	public static String digestBase64(String algorithm, ByteBuffer byteBuffer) {
		byte[] bytes = digestRaw(algorithm, byteBuffer);

		return Base64.encode(bytes);
	}

	public static String digestBase64(
		String algorithm, InputStream inputStream) {

		byte[] bytes = digestRaw(algorithm, inputStream);

		return Base64.encode(bytes);
	}

	public static String digestBase64(String algorithm, String... text) {
		byte[] bytes = digestRaw(algorithm, text);

		return Base64.encode(bytes);
	}

	public static String digestHex(ByteBuffer byteBuffer) {
		return digestHex(DEFAULT_ALGORITHM, byteBuffer);
	}

	public static String digestHex(InputStream inputStream) {
		return digestHex(DEFAULT_ALGORITHM, inputStream);
	}

	public static String digestHex(String text) {
		return digestHex(DEFAULT_ALGORITHM, text);
	}

	public static String digestHex(String algorithm, ByteBuffer byteBuffer) {
		byte[] bytes = digestRaw(algorithm, byteBuffer);

		return StringUtil.bytesToHexString(bytes);
	}

	public static String digestHex(String algorithm, InputStream inputStream) {
		byte[] bytes = digestRaw(algorithm, inputStream);

		return StringUtil.bytesToHexString(bytes);
	}

	public static String digestHex(String algorithm, String... text) {
		byte[] bytes = digestRaw(algorithm, text);

		return StringUtil.bytesToHexString(bytes);
	}

	public static byte[] digestRaw(ByteBuffer byteBuffer) {
		return digestRaw(DEFAULT_ALGORITHM, byteBuffer);
	}

	public static byte[] digestRaw(String text) {
		return digestRaw(DEFAULT_ALGORITHM, text);
	}

	public static byte[] digestRaw(String algorithm, ByteBuffer byteBuffer) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance(algorithm);

			messageDigest.update(byteBuffer);
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			_log.error(noSuchAlgorithmException);
		}

		return messageDigest.digest();
	}

	public static byte[] digestRaw(String algorithm, InputStream inputStream1) {
		MessageDigest messageDigest = null;

		try (InputStream inputStream2 = inputStream1) {
			messageDigest = MessageDigest.getInstance(algorithm);

			byte[] buffer = new byte[StreamUtil.BUFFER_SIZE];

			int read = 0;

			while ((read = inputStream2.read(buffer)) != -1) {
				if (read > 0) {
					messageDigest.update(buffer, 0, read);
				}
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			_log.error(noSuchAlgorithmException);
		}

		return messageDigest.digest();
	}

	public static byte[] digestRaw(String algorithm, String... text) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance(algorithm);

			StringBundler sb = new StringBundler((text.length * 2) - 1);

			for (String t : text) {
				if (sb.length() > 0) {
					sb.append(StringPool.COLON);
				}

				sb.append(t);
			}

			String s = sb.toString();

			messageDigest.update(s.getBytes(ENCODING));
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			_log.error(noSuchAlgorithmException);
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			_log.error(unsupportedEncodingException);
		}

		return messageDigest.digest();
	}

	private static final boolean _BASE_64 = Objects.equals(
		PropsUtil.get(PropsKeys.PASSWORDS_DIGEST_ENCODING), "base64");

	private static final Log _log = LogFactoryUtil.getLog(DigesterUtil.class);

}
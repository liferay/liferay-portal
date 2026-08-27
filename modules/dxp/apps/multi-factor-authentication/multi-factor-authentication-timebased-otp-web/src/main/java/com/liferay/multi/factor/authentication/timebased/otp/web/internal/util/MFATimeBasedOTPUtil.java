/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.web.internal.util;

import com.liferay.petra.io.BigEndianCodec;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.math.BigInteger;

import java.nio.charset.StandardCharsets;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jodd.util.Base32;

/**
 * @author Arthur Chan
 * @author Marta Medio
 */
public class MFATimeBasedOTPUtil {

	public static final int MFA_TIMEBASED_OTP_COUNTER = 30 * 1000;

	public static final int MFA_TIMEBASED_OTP_DIGITS = 6;

	public static String generateSharedSecret(int algorithmKeySize) {
		int count = (int)Math.ceil((double)algorithmKeySize / 8);

		byte[] buffer = new byte[count * 8];

		for (int i = 0; i < count; i++) {
			BigEndianCodec.putLong(buffer, i * 8, SecureRandomUtil.nextLong());
		}

		byte[] secret = new byte[algorithmKeySize];

		System.arraycopy(buffer, 0, secret, 0, algorithmKeySize);

		return Base32.encode(secret);
	}

	public static boolean verifyTimeBasedOTP(
		long clockSkew, String sharedSecret, String value) {

		long initialTime =
			(System.currentTimeMillis() - clockSkew) /
				MFA_TIMEBASED_OTP_COUNTER;
		long finalTime =
			(System.currentTimeMillis() + clockSkew) /
				MFA_TIMEBASED_OTP_COUNTER;

		for (long i = initialTime; i <= finalTime; i++) {
			String timeBasedOTP = _generateTimeBasedOTP(
				Base32.decode(sharedSecret), _getTimeCountHex(i));

			if (MessageDigest.isEqual(
					value.getBytes(StandardCharsets.UTF_8),
					timeBasedOTP.getBytes(StandardCharsets.UTF_8))) {

				return true;
			}
		}

		return false;
	}

	private static byte[] _generateHMAC(byte[] key, String text) {
		String algorithm = PropsValues.FIPS_ENABLED ? "HmacSHA256" : "HmacSHA1";

		try {
			Mac mac = Mac.getInstance(algorithm);

			mac.init(new SecretKeySpec(key, "RAW"));

			BigInteger bigInteger = new BigInteger("10" + text, 16);

			byte[] byteArray = bigInteger.toByteArray();

			return mac.doFinal(
				Arrays.copyOfRange(byteArray, 1, byteArray.length));
		}
		catch (InvalidKeyException invalidKeyException) {
			throw new IllegalArgumentException(
				"Invalid secret key for algorithm " + algorithm,
				invalidKeyException);
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new IllegalArgumentException(
				"Invalid algorithm " + algorithm, noSuchAlgorithmException);
		}
	}

	private static String _generateTimeBasedOTP(
		byte[] sharedSecret, String timeCountHex) {

		byte[] hash = _generateHMAC(sharedSecret, timeCountHex);

		int offset = hash[hash.length - 1] & 0xf;

		int binary =
			((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) |
			((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

		int otp = binary % (int)Math.pow(10, MFA_TIMEBASED_OTP_DIGITS);

		return String.format(
			StringBundler.concat("%0", MFA_TIMEBASED_OTP_DIGITS, "d"), otp);
	}

	private static String _getTimeCountHex(long time) {
		String hex = StringUtil.toUpperCase(Long.toHexString(time));

		if (hex.length() > 16) {
			return hex;
		}

		return StringUtil.replace(
			String.format("%16s", hex), CharPool.SPACE, CharPool.NUMBER_0);
	}

}
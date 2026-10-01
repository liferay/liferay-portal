/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.util;

import com.liferay.poshi.core.util.CharPool;
import com.liferay.poshi.core.util.PropsUtil;
import com.liferay.poshi.core.util.StringUtil;

import java.math.BigInteger;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jodd.util.Base32;

/**
 * @author Della Wang
 */
public class AuthenticationUtil {

	public static String generateTimeBasedOTP(String secretKey) {
		long time = (System.currentTimeMillis() - 3000) / 30000;

		String timeCountHex = StringUtil.toUpperCase(Long.toHexString(time));

		if (timeCountHex.length() > 16) {
			return timeCountHex;
		}

		timeCountHex = StringUtil.replace(
			String.format("%16s", timeCountHex), CharPool.SPACE,
			CharPool.NUMBER_0);

		String algorithm = "HmacSHA1";

		if (Boolean.parseBoolean(PropsUtil.get("fips.enabled"))) {
			algorithm = "HmacSHA256";
		}

		try {
			Mac mac = Mac.getInstance(algorithm);

			mac.init(new SecretKeySpec(Base32.decode(secretKey), "RAW"));

			BigInteger bigInteger = new BigInteger("10" + timeCountHex, 16);

			byte[] byteArray = bigInteger.toByteArray();

			byte[] hash = mac.doFinal(
				Arrays.copyOfRange(byteArray, 1, byteArray.length));

			int offset = hash[hash.length - 1] & 0xf;

			int binary =
				((hash[offset] & 0x7f) << 24) |
				((hash[offset + 1] & 0xff) << 16) |
				((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

			int timeBasedOTP =
				binary % (int)Math.pow(10, _DIGITS_TIME_BASED_OTP);

			return String.format(
				"%0" + _DIGITS_TIME_BASED_OTP + "d", timeBasedOTP);
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

	private static final int _DIGITS_TIME_BASED_OTP = 6;

}
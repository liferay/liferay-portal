/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.Validator;

import java.nio.charset.StandardCharsets;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;

import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Mariano Álvaro Sáiz
 */
public class AICreatorOpenAIGenerationUtil {

	public static boolean isValidSignature(
			byte[] bytes, Company company, long expirationTime,
			String signature, long userId)
		throws GeneralSecurityException {

		if ((expirationTime < System.currentTimeMillis()) ||
			Validator.isNull(signature)) {

			return false;
		}

		String expectedSignature = sign(bytes, company, expirationTime, userId);

		return MessageDigest.isEqual(
			expectedSignature.getBytes(StandardCharsets.UTF_8),
			signature.getBytes(StandardCharsets.UTF_8));
	}

	public static String sign(
			byte[] bytes, Company company, long expirationTime, long userId)
		throws GeneralSecurityException {

		Key key = company.getKeyObj();

		byte[] keyBytes = key.getEncoded();

		byte[] derivedKeyBytes = null;

		try {
			Mac mac = Mac.getInstance(_ALGORITHM);

			mac.init(new SecretKeySpec(keyBytes, _ALGORITHM));

			derivedKeyBytes = mac.doFinal(
				"ai-creator-openai-generations".getBytes(
					StandardCharsets.UTF_8));

			mac.init(new SecretKeySpec(derivedKeyBytes, _ALGORITHM));

			String prefix = StringBundler.concat(
				company.getCompanyId(), StringPool.COLON, userId,
				StringPool.COLON, expirationTime, StringPool.COLON);

			mac.update(prefix.getBytes(StandardCharsets.UTF_8));

			return Base64.encode(mac.doFinal(bytes));
		}
		finally {
			Arrays.fill(keyBytes, (byte)0);

			if (derivedKeyBytes != null) {
				Arrays.fill(derivedKeyBytes, (byte)0);
			}
		}
	}

	private static final String _ALGORITHM = "HmacSHA256";

}
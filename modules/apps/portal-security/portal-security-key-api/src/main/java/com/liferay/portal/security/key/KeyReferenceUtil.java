/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Christopher Kian
 * @author Pedro Victor Silvestre
 */
public class KeyReferenceUtil {

	public static boolean isKeyReference(String value) {
		if ((value != null) &&
			(value.startsWith(_KEY_REFERENCE_PREFIX_CRYPTO) ||
			 value.startsWith(_KEY_REFERENCE_PREFIX_SECRET))) {

			return true;
		}

		return false;
	}

	public static boolean isParsableKeyReference(String keyReferenceString) {
		if (_parse(keyReferenceString) != null) {
			return true;
		}

		return false;
	}

	public static KeyReference toKeyReference(String keyReferenceString) {
		KeyReference keyReference = _parse(keyReferenceString);

		if (keyReference == null) {
			throw new IllegalArgumentException("Invalid key reference");
		}

		return keyReference;
	}

	public static String toKeyReferenceString(KeyReference keyReference) {
		String prefix = _KEY_REFERENCE_PREFIX_SECRET;

		if (keyReference.getType() == KeyReference.Type.CRYPTO) {
			prefix = _KEY_REFERENCE_PREFIX_CRYPTO;
		}

		return StringBundler.concat(
			prefix, keyReference.getProviderId(), StringPool.COLON,
			keyReference.getIdentifier(), StringPool.CLOSE_CURLY_BRACE);
	}

	private static KeyReference _parse(String keyReferenceString) {
		if (keyReferenceString == null) {
			return null;
		}

		String keyReferencePrefix;
		KeyReference.Type type;

		if (keyReferenceString.startsWith(_KEY_REFERENCE_PREFIX_CRYPTO)) {
			keyReferencePrefix = _KEY_REFERENCE_PREFIX_CRYPTO;
			type = KeyReference.Type.CRYPTO;
		}
		else if (keyReferenceString.startsWith(_KEY_REFERENCE_PREFIX_SECRET)) {
			keyReferencePrefix = _KEY_REFERENCE_PREFIX_SECRET;
			type = KeyReference.Type.SECRET;
		}
		else {
			return null;
		}

		int length = keyReferenceString.length();

		if ((length <= keyReferencePrefix.length()) ||
			(keyReferenceString.charAt(length - 1) !=
				CharPool.CLOSE_CURLY_BRACE)) {

			return null;
		}

		String value = keyReferenceString.substring(
			keyReferencePrefix.length(), length - 1);

		int index = value.indexOf(CharPool.COLON);

		if (index <= 0) {
			return null;
		}

		String providerId = value.substring(0, index);

		if (Validator.isNull(providerId) ||
			(providerId.indexOf(CharPool.CLOSE_CURLY_BRACE) >= 0)) {

			return null;
		}

		String identifier = value.substring(index + 1);

		if (Validator.isNull(identifier)) {
			return null;
		}

		return new KeyReference(identifier, providerId, type);
	}

	private static final String _KEY_REFERENCE_PREFIX_CRYPTO = "${keyRef:";

	private static final String _KEY_REFERENCE_PREFIX_SECRET = "${secretRef:";

}
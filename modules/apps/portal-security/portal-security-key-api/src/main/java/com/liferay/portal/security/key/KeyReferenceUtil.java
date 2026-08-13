/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Christopher Kian
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

	public static KeyReference toKeyReference(String keyReferenceString) {
		String prefix = _KEY_REFERENCE_PREFIX_SECRET;
		KeyReference.Type type = KeyReference.Type.SECRET;

		if (keyReferenceString.startsWith(_KEY_REFERENCE_PREFIX_CRYPTO)) {
			prefix = _KEY_REFERENCE_PREFIX_CRYPTO;
			type = KeyReference.Type.CRYPTO;
		}

		String[] parts = StringUtil.split(
			keyReferenceString.substring(
				prefix.length(), keyReferenceString.length() - 1),
			CharPool.COLON);

		return new KeyReference(parts[1], parts[0], type);
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

	private static final String _KEY_REFERENCE_PREFIX_CRYPTO = "${keyRef:";

	private static final String _KEY_REFERENCE_PREFIX_SECRET = "${secretRef:";

}
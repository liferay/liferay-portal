/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.secret;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;

import java.util.Objects;

/**
 * @author Pedro Victor Silvestre
 */
public class SecretResolverUtil {

	public static String resolve(long companyId, String value) {
		if (!KeyReferenceUtil.isKeyReference(value)) {
			return value;
		}

		SecretResolver secretResolver = _secretResolverSnapshot.get();

		if (secretResolver == null) {
			throw new IllegalStateException("Secret resolver is unavailable");
		}

		return secretResolver.resolve(companyId, value);
	}

	public static String resolve(
		long companyId, String identifier, String value) {

		KeyReference keyReference = KeyReferenceUtil.parseKeyReference(value);

		if ((keyReference == null) ||
			!Objects.equals(identifier, keyReference.getIdentifier())) {

			return value;
		}

		return resolve(companyId, value);
	}

	private static final Snapshot<SecretResolver> _secretResolverSnapshot =
		new Snapshot<>(
			SecretResolverUtil.class, SecretResolver.class, null, true);

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.secret;

import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Pedro Victor Silvestre
 */
public class SecretResolverUtil {

	public static String resolve(long companyId, String value) {
		SecretResolver secretResolver = _secretResolverSnapshot.get();

		if (secretResolver == null) {
			throw new IllegalStateException("Secret resolver is unavailable");
		}

		return secretResolver.resolve(companyId, value);
	}

	private static final Snapshot<SecretResolver> _secretResolverSnapshot =
		new Snapshot<>(
			SecretResolverUtil.class, SecretResolver.class, null, true);

}
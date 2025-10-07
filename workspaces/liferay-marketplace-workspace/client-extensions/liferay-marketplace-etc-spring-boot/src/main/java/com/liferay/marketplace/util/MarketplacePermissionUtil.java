/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.util;

import java.util.Objects;

import org.springframework.security.oauth2.jwt.Jwt;

/**
 * @author Keven Leone
 */
public class MarketplacePermissionUtil {

	public static void checkDefaultServiceAccountPermission(Jwt jwt)
		throws Exception {

		if (!Objects.equals(
				jwt.getClaim("username"), "default-service-account")) {

			throw new Exception("Unauthorized");
		}
	}

}
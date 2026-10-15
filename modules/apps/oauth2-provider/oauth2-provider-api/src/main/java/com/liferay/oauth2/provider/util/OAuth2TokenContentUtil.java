/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.util;

import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author Pedro Victor Silvestre
 */
public class OAuth2TokenContentUtil {

	public static String getTokenContent(String token) {
		if (!PropsValues.FIPS_ENABLED || (token == null)) {
			return token;
		}

		return DigesterUtil.digestHex(DigesterUtil.SHA_256, token);
	}

}
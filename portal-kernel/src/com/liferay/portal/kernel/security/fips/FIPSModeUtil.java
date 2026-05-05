/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Jorge García Jiménez
 */
public class FIPSModeUtil {

	public static boolean isApprovedPasswordAlgorithm(String algorithm) {
		if (Validator.isNull(algorithm)) {
			return false;
		}

		String normalized = StringUtil.toUpperCase(algorithm);

		if (normalized.startsWith("PBKDF2") || normalized.equals("SHA-256") ||
			normalized.equals("SHA-384") || normalized.equals("SHA-512")) {

			return true;
		}

		return false;
	}

	public static boolean isEnabled() {
		return PropsValues.FIPS_ENABLED;
	}

}
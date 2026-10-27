/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.function.Function;

/**
 * @author Nilton Vieira
 */
public class FaroEnumUtil {

	public static <T> T create(Function<String, T> function, String value) {
		try {
			return function.apply(value);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalArgumentException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(FaroEnumUtil.class);

}
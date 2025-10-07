/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;

/**
 * @author Akhash Ramprakash
 */
public class UniqueUtil {

	public static String getCopyValue(
			UnsafeFunction<String, Boolean, PortalException> unsafeFunction,
			String value)
		throws PortalException {

		String copy = LanguageUtil.get(LocaleUtil.getSiteDefault(), "copy");

		String copyValue = StringUtil.appendParentheticalSuffix(value, copy);

		for (int i = 1;; i++) {
			if (unsafeFunction.apply(copyValue)) {
				return copyValue;
			}

			copyValue = StringUtil.appendParentheticalSuffix(
				value, copy + StringPool.SPACE + i);
		}
	}

}
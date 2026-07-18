/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Shuyang Zhou
 */
public class FragmentCompositionKeyUtil {

	public static String getFragmentCompositionKey(
		String fragmentCompositionKey) {

		if (fragmentCompositionKey != null) {
			fragmentCompositionKey = fragmentCompositionKey.trim();

			return StringUtil.toLowerCase(fragmentCompositionKey);
		}

		return StringPool.BLANK;
	}

}
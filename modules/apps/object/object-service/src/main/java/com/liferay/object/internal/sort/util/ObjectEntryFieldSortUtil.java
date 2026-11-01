/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.sort.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Shuyang Zhou
 */
public class ObjectEntryFieldSortUtil {

	public static String getSortFieldName(Sort sort) {
		String fieldName = sort.getFieldName();

		if (!fieldName.contains(StringPool.SLASH)) {
			return fieldName;
		}

		return StringUtil.extractLast(fieldName, StringPool.SLASH);
	}

	public static boolean isParentComplexField(Sort sort) {
		return !StringUtil.equals(getSortFieldName(sort), sort.getFieldPath());
	}

}
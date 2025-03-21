/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.kernel;

import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutUtilityPageEntryTypeUtil {

	public static String getStatusLayoutUtilityPageEntryType(int statusCode) {
		return _externalToInternalValuesMap.get(statusCode);
	}

	private static final Map<Integer, String> _externalToInternalValuesMap =
		HashMapBuilder.put(
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			LayoutUtilityPageEntryConstants.TYPE_SC_INTERNAL_SERVER_ERROR
		).put(
			HttpServletResponse.SC_NOT_FOUND,
			LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND
		).build();

}
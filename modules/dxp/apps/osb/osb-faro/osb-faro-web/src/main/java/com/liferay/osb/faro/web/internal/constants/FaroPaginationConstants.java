/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.constants;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Map;

/**
 * @author Shinn Lok
 */
public class FaroPaginationConstants {

	public static final int CUR_DEFAULT = 1;

	public static final String ORDER_BY_TYPE_ASC = "asc";

	public static final String ORDER_BY_TYPE_DEFAULT = ORDER_BY_TYPE_ASC;

	public static final String ORDER_BY_TYPE_DESC = "desc";

	public static Map<String, Object> getConstants() {
		return _constants;
	}

	private static final Map<String, Object> _constants =
		HashMapBuilder.<String, Object>put(
			"cur", CUR_DEFAULT
		).put(
			"delta", PropsValues.SEARCH_CONTAINER_PAGE_DEFAULT_DELTA
		).put(
			"deltaValues", PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES
		).put(
			"orderAscending", ORDER_BY_TYPE_ASC
		).put(
			"orderDefault", ORDER_BY_TYPE_DEFAULT
		).put(
			"orderDescending", ORDER_BY_TYPE_DESC
		).build();

}
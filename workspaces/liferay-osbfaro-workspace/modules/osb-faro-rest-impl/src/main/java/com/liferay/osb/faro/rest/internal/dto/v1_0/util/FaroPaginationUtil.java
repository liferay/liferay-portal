/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.util;

import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Leslie Wong
 */
public class FaroPaginationUtil {

	public static int getCur(Pagination pagination) {
		if (pagination == null) {
			return _DEFAULT_CUR;
		}

		return pagination.getPage();
	}

	public static int getDelta(Pagination pagination) {
		if (pagination == null) {
			return _DEFAULT_DELTA;
		}

		return pagination.getPageSize();
	}

	public static Map<String, Object> toGraphQLSort(
		Sort defaultSort, Sort[] sorts) {

		Sort sort = defaultSort;

		if (ArrayUtil.isNotEmpty(sorts)) {
			sort = sorts[0];
		}

		return HashMapBuilder.<String, Object>put(
			"column", sort.getFieldName()
		).put(
			"type", sort.isReverse() ? "DESC" : "ASC"
		).build();
	}

	public static List<OrderByField> toOrderByFields(Sort[] sorts) {
		if (ArrayUtil.isEmpty(sorts)) {
			return null;
		}

		List<OrderByField> orderByFields = new ArrayList<>(sorts.length);

		for (Sort sort : sorts) {
			String fieldName = sort.getFieldName();

			if (fieldName == null) {
				continue;
			}

			orderByFields.add(
				new OrderByField(fieldName, sort.isReverse() ? "desc" : "asc"));
		}

		return orderByFields;
	}

	public static String toSortString(Sort[] sorts) {
		if (ArrayUtil.isEmpty(sorts)) {
			return null;
		}

		Sort sort = sorts[0];

		String fieldName = sort.getFieldName();

		if (fieldName == null) {
			return null;
		}

		return StringBundler.concat(
			fieldName, StringPool.COLON, sort.isReverse() ? "desc" : "asc");
	}

	private static final int _DEFAULT_CUR = 1;

	private static final int _DEFAULT_DELTA = 20;

}
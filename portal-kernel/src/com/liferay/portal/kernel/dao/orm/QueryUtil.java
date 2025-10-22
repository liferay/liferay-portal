/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class QueryUtil {

	public static final int ALL_POS = -1;

	public static Iterator<?> iterate(
		Query query, Dialect dialect, int start, int end) {

		return iterate(query, dialect, start, end, true);
	}

	public static Iterator<?> iterate(
		Query query, Dialect dialect, int start, int end,
		boolean unmodifiable) {

		List<?> list = list(query, dialect, start, end);

		return list.iterator();
	}

	public static List<?> list(
		Query query, Dialect dialect, int start, int end) {

		return list(query, dialect, start, end, true);
	}

	public static List<?> list(
		Query query, Dialect dialect, int start, int end,
		boolean unmodifiable) {

		if ((start == ALL_POS) && (end == ALL_POS)) {
			return query.list(unmodifiable);
		}

		if (start < 0) {
			start = 0;
		}

		if (end < start) {
			end = start;
		}

		if (start == end) {
			if (unmodifiable) {
				return Collections.emptyList();
			}

			return new ArrayList<>();
		}

		if (dialect.supportsLimit()) {
			query.setMaxResults(end - start);
			query.setFirstResult(start);

			return query.list(unmodifiable);
		}

		List<Object> list = new ArrayList<>();

		DB db = DBManagerUtil.getDB();

		if (!db.isSupportsScrollableResults()) {
			if (_log.isWarnEnabled()) {
				_log.warn("Database does not support scrollable results");
			}

			return list;
		}

		ScrollableResults sr = query.scroll();

		if (sr.first() && sr.scroll(start)) {
			for (int i = start; i < end; i++) {
				list.add(sr.get());

				if (!sr.next()) {
					break;
				}
			}
		}

		if (unmodifiable) {
			return Collections.unmodifiableList(list);
		}

		return list;
	}

	public static List<?> randomList(
		Query query, Dialect dialect, int total, int num) {

		return randomList(query, dialect, total, num, true);
	}

	public static List<?> randomList(
		Query query, Dialect dialect, int total, int num,
		boolean unmodifiable) {

		if ((total == 0) || (num == 0)) {
			return new ArrayList<>();
		}

		if (num >= total) {
			return list(query, dialect, ALL_POS, ALL_POS, true);
		}

		List<Object> list = new ArrayList<>();

		DB db = DBManagerUtil.getDB();

		if (!db.isSupportsScrollableResults()) {
			if (_log.isWarnEnabled()) {
				_log.warn("Database does not support scrollable results");
			}

			return list;
		}

		ScrollableResults sr = query.scroll();

		int[] scrollIds = RandomUtil.nextInts(total, num);

		for (int scrollId : scrollIds) {
			if (sr.scroll(scrollId)) {
				list.add(sr.get());

				sr.first();
			}
		}

		if (unmodifiable) {
			return Collections.unmodifiableList(list);
		}

		return list;
	}

	private static final Log _log = LogFactoryUtil.getLog(QueryUtil.class);

}
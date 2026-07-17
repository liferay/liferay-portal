/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.dao.search;

import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.ResultRowSplitterEntry;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.display.UADEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Samuel Trong Tran
 */
public class UADHierarchyResultRowSplitter implements ResultRowSplitter {

	public UADHierarchyResultRowSplitter(
		Locale locale, UADDisplay<?>[] uadDisplays) {

		_locale = locale;
		_uadDisplays = uadDisplays;
	}

	@Override
	public List<ResultRowSplitterEntry> split(List<ResultRow> resultRows) {
		List<ResultRowSplitterEntry> resultRowSplitterEntries =
			new ArrayList<>();

		Map<String, List<ResultRow>> classResultRowsMap = new HashMap<>();

		for (UADDisplay<?> uadDisplay : _uadDisplays) {
			classResultRowsMap.put(uadDisplay.getTypeKey(), new ArrayList<>());
		}

		for (ResultRow resultRow : resultRows) {
			UADEntity<?> uadEntity = (UADEntity<?>)resultRow.getObject();

			List<ResultRow> classResultRows = classResultRowsMap.get(
				uadEntity.getTypeKey());

			if (classResultRows != null) {
				classResultRows.add(resultRow);
			}
		}

		for (UADDisplay<?> uadDisplay : _uadDisplays) {
			List<ResultRow> classResultRows = classResultRowsMap.get(
				uadDisplay.getTypeKey());

			if (!classResultRows.isEmpty()) {
				resultRowSplitterEntries.add(
					new ResultRowSplitterEntry(
						uadDisplay.getTypeName(_locale), classResultRows));
			}
		}

		return resultRowSplitterEntries;
	}

	private final Locale _locale;
	private final UADDisplay<?>[] _uadDisplays;

}
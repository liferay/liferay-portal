/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.usage;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class CollectionUsages {

	public void addBooleanQuery(
		BooleanQuery booleanQuery, long[] groupIds, String key) {

		_booleanQueries.put(key, booleanQuery);

		if (ArrayUtil.isEmpty(groupIds)) {
			_booleanQueriesAnyGroup = true;
		}

		Collections.addAll(
			_booleanQueriesGroupIds, ArrayUtil.toArray(groupIds));
	}

	public void addObjectEntryId(long objectEntryId) {
		_objectEntryIds.add(String.valueOf(objectEntryId));
	}

	public void addQuery(Query query) {
		_queries.add(query);
	}

	public Collection<BooleanQuery> getBooleanQueries() {
		return _booleanQueries.values();
	}

	public Set<Long> getBooleanQueriesGroupIds() {
		return _booleanQueriesGroupIds;
	}

	public Set<String> getObjectEntryIds() {
		return _objectEntryIds;
	}

	public List<Query> getQueries() {
		return _queries;
	}

	public boolean hasBooleanQuery(String key) {
		return _booleanQueries.containsKey(key);
	}

	public boolean isBooleanQueriesAnyGroup() {
		return _booleanQueriesAnyGroup;
	}

	private final Map<String, BooleanQuery> _booleanQueries = new HashMap<>();
	private boolean _booleanQueriesAnyGroup;
	private final Set<Long> _booleanQueriesGroupIds = new HashSet<>();
	private final Set<String> _objectEntryIds = new HashSet<>();
	private final List<Query> _queries = new ArrayList<>();

}
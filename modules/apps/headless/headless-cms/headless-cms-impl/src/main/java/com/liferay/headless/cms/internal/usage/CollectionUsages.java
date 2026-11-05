/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.usage;

import com.liferay.portal.search.query.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class CollectionUsages {

	public void addObjectEntryId(long objectEntryId) {
		_objectEntryIds.add(String.valueOf(objectEntryId));
	}

	public void addQuery(Query query) {
		_queries.add(query);
	}

	public Set<String> getObjectEntryIds() {
		return _objectEntryIds;
	}

	public List<Query> getQueries() {
		return _queries;
	}

	private final Set<String> _objectEntryIds = new HashSet<>();
	private final List<Query> _queries = new ArrayList<>();

}
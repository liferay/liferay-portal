/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Petteri Karttunen
 */
@JacksonXmlRootElement(localName = "page")
public class SearchPage<T> extends Page<T> {

	public static <T> SearchPage<T> of(
		Map<String, Map<String, String>> actions, Object aggregations,
		Object searchFacets, Collection<T> items, Pagination pagination,
		long totalCount) {

		return new SearchPage<>(
			actions, aggregations, searchFacets, items, pagination, totalCount);
	}

	@JsonProperty("aggregations")
	public Object getAggregations() {
		return _aggregations;
	}

	@JsonProperty("searchFacets")
	public Object getSearchFacets() {
		return _searchFacets;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler("{\"actions\": ");

		sb.append(_toString((Map)getActions()));
		sb.append(", \"aggregations\": ");
		sb.append(_toString((Map)_aggregations));
		sb.append(", \"searchFacets\": ");
		sb.append(_toString((Map)_searchFacets));
		sb.append(", \"items\": [");

		Collection<T> items = getItems();

		Iterator<T> iterator = items.iterator();

		while (iterator.hasNext()) {
			sb.append(iterator.next());

			if (!iterator.hasNext()) {
				continue;
			}

			sb.append(", ");
		}

		sb.append("], \"page\": ");
		sb.append(getPage());
		sb.append(", \"pageSize\": ");
		sb.append(getPageSize());
		sb.append(", \"totalCount\": ");
		sb.append(getTotalCount());
		sb.append("}");

		return sb.toString();
	}

	private SearchPage(
		Map<String, Map<String, String>> actions, Object aggregations,
		Object searchFacets, Collection<T> items, Pagination pagination,
		long totalCount) {

		super(actions, null, items, pagination, totalCount);

		_aggregations = aggregations;
		_searchFacets = searchFacets;
	}

	private String _toString(Map<String, Object> map) {
		if (map == null) {
			return "{}";
		}

		StringBundler sb = new StringBundler("{");

		Set<Map.Entry<String, Object>> entries = map.entrySet();

		Iterator<Map.Entry<String, Object>> iterator = entries.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			if (value instanceof Map) {
				sb.append(_toString((Map)value));
			}
			else {
				sb.append("\"");
				sb.append(value);
				sb.append("\"");
			}

			if (!iterator.hasNext()) {
				continue;
			}

			sb.append(", ");
		}

		sb.append("}");

		return sb.toString();
	}

	private final Object _aggregations;
	private final Object _searchFacets;

}
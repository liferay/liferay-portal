/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.vulcan.aggregation.Facet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Alejandro Hernández
 * @author Ivica Cardic
 * @author Brian Wing Shun Chan
 */
@JacksonXmlRootElement(localName = "page")
public class Page<T> {

	public static <T> Page<T> of(Collection<T> items) {
		return new Page<>(new HashMap<>(), items);
	}

	public static <T> Page<T> of(
		Collection<T> items, Pagination pagination, long totalCount) {

		return new Page<>(
			new HashMap<>(), new ArrayList<>(), items, pagination, totalCount);
	}

	public static <T> Page<T> of(
		Map<String, Map<String, String>> actions, Collection<T> items) {

		return new Page<>(actions, items);
	}

	public static <T> Page<T> of(
		Map<String, Map<String, String>> actions, Collection<T> items,
		Pagination pagination, long totalCount) {

		return new Page<>(
			actions, new ArrayList<>(), items, pagination, totalCount);
	}

	public static <T> Page<T> of(
		Map<String, Map<String, String>> actions, List<Facet> facets,
		Collection<T> items, Pagination pagination, long totalCount) {

		return new Page<>(actions, facets, items, pagination, totalCount);
	}

	public T fetchFirstItem() {
		Iterator<T> iterator = _items.iterator();

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}

	@JsonProperty("actions")
	public Map<String, Map<String, String>> getActions() {
		return _actions;
	}

	@JsonProperty("facets")
	public List<Facet> getFacets() {
		return _facets;
	}

	@JacksonXmlElementWrapper(localName = "items")
	@JacksonXmlProperty(localName = "item")
	public Collection<T> getItems() {
		return new ArrayList<>(_items);
	}

	public long getLastPage() {
		if ((_pageSize == 0) || (_totalCount == 0)) {
			return 1;
		}

		return -Math.floorDiv(-_totalCount, _pageSize);
	}

	@JsonProperty("page")
	public long getPage() {
		return _page;
	}

	@JsonProperty("pageSize")
	public long getPageSize() {
		return _pageSize;
	}

	public long getTotalCount() {
		return _totalCount;
	}

	public boolean hasNext() {
		if (getLastPage() > _page) {
			return true;
		}

		return false;
	}

	public boolean hasPrevious() {
		if (_page > 1) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler("{\"actions\": ");

		sb.append(_toString((Map)_actions));
		sb.append(", \"items\": [");

		Iterator<T> iterator = _items.iterator();

		while (iterator.hasNext()) {
			sb.append(iterator.next());

			if (!iterator.hasNext()) {
				continue;
			}

			sb.append(", ");
		}

		sb.append("], \"page\": ");
		sb.append(_page);
		sb.append(", \"pageSize\": ");
		sb.append(_pageSize);
		sb.append(", \"totalCount\": ");
		sb.append(_totalCount);
		sb.append("}");

		return sb.toString();
	}

	protected Page(
		Map<String, Map<String, String>> actions, List<Facet> facets,
		Collection<T> items, Pagination pagination, long totalCount) {

		_actions = actions;
		_facets = facets;
		_items = items;

		if (pagination == null) {
			_page = 0;
			_pageSize = 0;
		}
		else {
			_page = pagination.getPage();
			_pageSize = pagination.getPageSize();
		}

		_totalCount = totalCount;
	}

	private Page(
		Map<String, Map<String, String>> actions, Collection<T> items) {

		_actions = actions;
		_items = items;

		_page = 1;
		_pageSize = items.size();
		_totalCount = items.size();
	}

	private String _toString(Map<String, Object> map) {
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

	private final Map<String, Map<String, String>> _actions;
	private List<Facet> _facets = new ArrayList<>();
	private final Collection<T> _items;
	private final long _page;
	private final long _pageSize;
	private final long _totalCount;

}
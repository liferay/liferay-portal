/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.pagination;

import com.liferay.headless.admin.user.client.aggregation.Facet;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Page<T> {

	public static <T> Page<T> of(
		String json, Function<String, T> toDTOFunction) {

		PageJSONParser pageJSONParser = new PageJSONParser(toDTOFunction);

		return (Page<T>)pageJSONParser.parseToDTO(json);
	}

	public T fetchFirstItem() {
		Iterator<T> iterator = _items.iterator();

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}

	public Map<String, Map<String, String>> getActions() {
		return _actions;
	}

	public List<Facet> getFacets() {
		return _facets;
	}

	public Collection<T> getItems() {
		return _items;
	}

	public long getLastPage() {
		if (_totalCount == 0) {
			return 1;
		}

		return -Math.floorDiv(-_totalCount, _pageSize);
	}

	public long getPage() {
		return _page;
	}

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

	public void setActions(Map<String, Map<String, String>> actions) {
		_actions = actions;
	}

	public void setFacets(List<Facet> facets) {
		_facets = facets;
	}

	public void setItems(Collection<T> items) {
		_items = items;
	}

	public void setPage(long page) {
		_page = page;
	}

	public void setPageSize(long pageSize) {
		_pageSize = pageSize;
	}

	public void setTotalCount(long totalCount) {
		_totalCount = totalCount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{\"actions\": ");

		sb.append(_toString((Map)_actions));
		sb.append(", \"items\": [");

		Iterator<T> iterator = _items.iterator();

		while (iterator.hasNext()) {
			sb.append(iterator.next());

			if (iterator.hasNext()) {
				sb.append(", ");
			}
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

	public static class PageJSONParser<T> extends BaseJSONParser<Page> {

		public PageJSONParser() {
			_toDTOFunction = null;
		}

		public PageJSONParser(Function<String, T> toDTOFunction) {
			_toDTOFunction = toDTOFunction;
		}

		@Override
		protected Page createDTO() {
			return new Page();
		}

		@Override
		protected Page[] createDTOArray(int size) {
			return new Page[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "facets")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "items")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastPage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "page")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageSize")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				return false;
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

		@Override
		protected void setField(
			Page page, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					page.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "facets")) {
				if (jsonParserFieldValue == null) {
					return;
				}

				List<Facet> facets = new ArrayList<>();

				for (Object object1 : (Object[])jsonParserFieldValue) {
					List<Facet.FacetValue> facetValues = new ArrayList<>();

					Map<String, Object> jsonParserFieldValuesMap =
						this.parseToMap((String)object1);

					for (Object object2 :
							(Object[])jsonParserFieldValuesMap.get(
								"facetValues")) {

						Map<String, Object> facetValueMap = this.parseToMap(
							(String)object2);

						facetValues.add(
							new Facet.FacetValue(
								Integer.valueOf(
									(String)facetValueMap.get(
										"numberOfOccurrences")),
								(String)facetValueMap.get("term")));
					}

					facets.add(
						new Facet(
							(String)jsonParserFieldValuesMap.get(
								"facetCriteria"),
							facetValues));
				}

				page.setFacets(facets);
			}
			else if (Objects.equals(jsonParserFieldName, "items")) {
				if (jsonParserFieldValue != null) {
					List<T> items = new ArrayList<>();

					for (Object object : (Object[])jsonParserFieldValue) {
						items.add(_toDTOFunction.apply((String)object));
					}

					page.setItems(items);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastPage")) {
			}
			else if (Objects.equals(jsonParserFieldName, "page")) {
				if (jsonParserFieldValue != null) {
					page.setPage(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageSize")) {
				if (jsonParserFieldValue != null) {
					page.setPageSize(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				if (jsonParserFieldValue != null) {
					page.setTotalCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

		private final Function<String, T> _toDTOFunction;

	}

	private String _toString(Map<String, Object> map) {
		StringBuilder sb = new StringBuilder("{");

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

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private Map<String, Map<String, String>> _actions;
	private List<Facet> _facets = new ArrayList<>();
	private Collection<T> _items;
	private long _page;
	private long _pageSize;
	private long _totalCount;

}
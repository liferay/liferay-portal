/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
@GraphQLName("QueryResponse")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "QueryResponse")
public class QueryResponse implements Serializable {

	public static QueryResponse toDTO(String json) {
		return ObjectMapperUtil.readValue(QueryResponse.class, json);
	}

	public static QueryResponse unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(QueryResponse.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A multi-valued list of complex objects containing the requested resources."
	)
	@Valid
	public Object getResources() {
		if (_ResourcesSupplier != null) {
			Resources = _ResourcesSupplier.get();

			_ResourcesSupplier = null;
		}

		return Resources;
	}

	public void setResources(Object Resources) {
		this.Resources = Resources;

		_ResourcesSupplier = null;
	}

	@JsonIgnore
	public void setResources(
		UnsafeSupplier<Object, Exception> ResourcesUnsafeSupplier) {

		_ResourcesSupplier = () -> {
			try {
				return ResourcesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A multi-valued list of complex objects containing the requested resources."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object Resources;

	@JsonIgnore
	private Supplier<Object> _ResourcesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of resources returned in a list response page."
	)
	public Integer getItemsPerPage() {
		if (_itemsPerPageSupplier != null) {
			itemsPerPage = _itemsPerPageSupplier.get();

			_itemsPerPageSupplier = null;
		}

		return itemsPerPage;
	}

	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;

		_itemsPerPageSupplier = null;
	}

	@JsonIgnore
	public void setItemsPerPage(
		UnsafeSupplier<Integer, Exception> itemsPerPageUnsafeSupplier) {

		_itemsPerPageSupplier = () -> {
			try {
				return itemsPerPageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The number of resources returned in a list response page."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer itemsPerPage;

	@JsonIgnore
	private Supplier<Integer> _itemsPerPageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The 1-based index of the first result in the current set of list results."
	)
	public Integer getStartIndex() {
		if (_startIndexSupplier != null) {
			startIndex = _startIndexSupplier.get();

			_startIndexSupplier = null;
		}

		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;

		_startIndexSupplier = null;
	}

	@JsonIgnore
	public void setStartIndex(
		UnsafeSupplier<Integer, Exception> startIndexUnsafeSupplier) {

		_startIndexSupplier = () -> {
			try {
				return startIndexUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The 1-based index of the first result in the current set of list results."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer startIndex;

	@JsonIgnore
	private Supplier<Integer> _startIndexSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The total number of results returned by the list or query operation."
	)
	public Integer getTotalResults() {
		if (_totalResultsSupplier != null) {
			totalResults = _totalResultsSupplier.get();

			_totalResultsSupplier = null;
		}

		return totalResults;
	}

	public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;

		_totalResultsSupplier = null;
	}

	@JsonIgnore
	public void setTotalResults(
		UnsafeSupplier<Integer, Exception> totalResultsUnsafeSupplier) {

		_totalResultsSupplier = () -> {
			try {
				return totalResultsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The total number of results returned by the list or query operation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer totalResults;

	@JsonIgnore
	private Supplier<Integer> _totalResultsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof QueryResponse)) {
			return false;
		}

		QueryResponse queryResponse = (QueryResponse)object;

		return Objects.equals(toString(), queryResponse.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object Resources = getResources();

		if (Resources != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"Resources\": ");

			if (Resources instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)Resources));
			}
			else if (Resources instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)Resources));
				sb.append("\"");
			}
			else {
				sb.append(Resources);
			}
		}

		Integer itemsPerPage = getItemsPerPage();

		if (itemsPerPage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemsPerPage\": ");

			sb.append(itemsPerPage);
		}

		Integer startIndex = getStartIndex();

		if (startIndex != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startIndex\": ");

			sb.append(startIndex);
		}

		Integer totalResults = getTotalResults();

		if (totalResults != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalResults\": ");

			sb.append(totalResults);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.scim.rest.dto.v1_0.QueryResponse",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
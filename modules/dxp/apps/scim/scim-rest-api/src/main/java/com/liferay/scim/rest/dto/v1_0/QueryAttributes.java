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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
@GraphQLName("QueryAttributes")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "QueryAttributes")
public class QueryAttributes implements Serializable {

	public static QueryAttributes toDTO(String json) {
		return ObjectMapperUtil.readValue(QueryAttributes.class, json);
	}

	public static QueryAttributes unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(QueryAttributes.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A multi-valued list of strings indicating the names of resource attributes to return in the response, overriding the set of attributes that would be returned by default."
	)
	public String[] getAttributes() {
		if (_attributesSupplier != null) {
			attributes = _attributesSupplier.get();

			_attributesSupplier = null;
		}

		return attributes;
	}

	public void setAttributes(String[] attributes) {
		this.attributes = attributes;

		_attributesSupplier = null;
	}

	@JsonIgnore
	public void setAttributes(
		UnsafeSupplier<String[], Exception> attributesUnsafeSupplier) {

		_attributesSupplier = () -> {
			try {
				return attributesUnsafeSupplier.get();
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
		description = "A multi-valued list of strings indicating the names of resource attributes to return in the response, overriding the set of attributes that would be returned by default."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] attributes;

	@JsonIgnore
	private Supplier<String[]> _attributesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "An integer indicating the desired maximum number of query results per page."
	)
	public Integer getCount() {
		if (_countSupplier != null) {
			count = _countSupplier.get();

			_countSupplier = null;
		}

		return count;
	}

	public void setCount(Integer count) {
		this.count = count;

		_countSupplier = null;
	}

	@JsonIgnore
	public void setCount(
		UnsafeSupplier<Integer, Exception> countUnsafeSupplier) {

		_countSupplier = () -> {
			try {
				return countUnsafeSupplier.get();
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
		description = "An integer indicating the desired maximum number of query results per page."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer count;

	@JsonIgnore
	private Supplier<Integer> _countSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A multi-valued list of strings indicating the names of resource attributes to be removed from the default set of attributes to return."
	)
	public String[] getExcludedAttributes() {
		if (_excludedAttributesSupplier != null) {
			excludedAttributes = _excludedAttributesSupplier.get();

			_excludedAttributesSupplier = null;
		}

		return excludedAttributes;
	}

	public void setExcludedAttributes(String[] excludedAttributes) {
		this.excludedAttributes = excludedAttributes;

		_excludedAttributesSupplier = null;
	}

	@JsonIgnore
	public void setExcludedAttributes(
		UnsafeSupplier<String[], Exception> excludedAttributesUnsafeSupplier) {

		_excludedAttributesSupplier = () -> {
			try {
				return excludedAttributesUnsafeSupplier.get();
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
		description = "A multi-valued list of strings indicating the names of resource attributes to be removed from the default set of attributes to return."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] excludedAttributes;

	@JsonIgnore
	private Supplier<String[]> _excludedAttributesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The filter string used to request a subset of resources."
	)
	public String getFilter() {
		if (_filterSupplier != null) {
			filter = _filterSupplier.get();

			_filterSupplier = null;
		}

		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;

		_filterSupplier = null;
	}

	@JsonIgnore
	public void setFilter(
		UnsafeSupplier<String, Exception> filterUnsafeSupplier) {

		_filterSupplier = () -> {
			try {
				return filterUnsafeSupplier.get();
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
		description = "The filter string used to request a subset of resources."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String filter;

	@JsonIgnore
	private Supplier<String> _filterSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A string indicating the attribute whose value SHALL be used to order the returned responses."
	)
	public String getSortBy() {
		if (_sortBySupplier != null) {
			sortBy = _sortBySupplier.get();

			_sortBySupplier = null;
		}

		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;

		_sortBySupplier = null;
	}

	@JsonIgnore
	public void setSortBy(
		UnsafeSupplier<String, Exception> sortByUnsafeSupplier) {

		_sortBySupplier = () -> {
			try {
				return sortByUnsafeSupplier.get();
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
		description = "A string indicating the attribute whose value SHALL be used to order the returned responses."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String sortBy;

	@JsonIgnore
	private Supplier<String> _sortBySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A string indicating the order in which the \"sortBy\" parameter is applied."
	)
	public String getSortOrder() {
		if (_sortOrderSupplier != null) {
			sortOrder = _sortOrderSupplier.get();

			_sortOrderSupplier = null;
		}

		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;

		_sortOrderSupplier = null;
	}

	@JsonIgnore
	public void setSortOrder(
		UnsafeSupplier<String, Exception> sortOrderUnsafeSupplier) {

		_sortOrderSupplier = () -> {
			try {
				return sortOrderUnsafeSupplier.get();
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
		description = "A string indicating the order in which the \"sortBy\" parameter is applied."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String sortOrder;

	@JsonIgnore
	private Supplier<String> _sortOrderSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "An integer indicating the 1-based index of the first query result."
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
		description = "An integer indicating the 1-based index of the first query result."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer startIndex;

	@JsonIgnore
	private Supplier<Integer> _startIndexSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof QueryAttributes)) {
			return false;
		}

		QueryAttributes queryAttributes = (QueryAttributes)object;

		return Objects.equals(toString(), queryAttributes.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String[] attributes = getAttributes();

		if (attributes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			sb.append("[");

			for (int i = 0; i < attributes.length; i++) {
				sb.append("\"");

				sb.append(_escape(attributes[i]));

				sb.append("\"");

				if ((i + 1) < attributes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Integer count = getCount();

		if (count != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"count\": ");

			sb.append(count);
		}

		String[] excludedAttributes = getExcludedAttributes();

		if (excludedAttributes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"excludedAttributes\": ");

			sb.append("[");

			for (int i = 0; i < excludedAttributes.length; i++) {
				sb.append("\"");

				sb.append(_escape(excludedAttributes[i]));

				sb.append("\"");

				if ((i + 1) < excludedAttributes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String filter = getFilter();

		if (filter != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"filter\": ");

			sb.append("\"");

			sb.append(_escape(filter));

			sb.append("\"");
		}

		String sortBy = getSortBy();

		if (sortBy != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sortBy\": ");

			sb.append("\"");

			sb.append(_escape(sortBy));

			sb.append("\"");
		}

		String sortOrder = getSortOrder();

		if (sortOrder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sortOrder\": ");

			sb.append("\"");

			sb.append(_escape(sortOrder));

			sb.append("\"");
		}

		Integer startIndex = getStartIndex();

		if (startIndex != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startIndex\": ");

			sb.append(startIndex);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.scim.rest.dto.v1_0.QueryAttributes",
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
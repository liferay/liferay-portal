/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("ObjectViewFilterColumn")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectViewFilterColumn")
public class ObjectViewFilterColumn implements Serializable {

	public static ObjectViewFilterColumn toDTO(String json) {
		return ObjectMapperUtil.readValue(ObjectViewFilterColumn.class, json);
	}

	public static ObjectViewFilterColumn unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ObjectViewFilterColumn.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("filterType")
	@Valid
	public FilterType getFilterType() {
		if (_filterTypeSupplier != null) {
			filterType = _filterTypeSupplier.get();

			_filterTypeSupplier = null;
		}

		return filterType;
	}

	@JsonIgnore
	public String getFilterTypeAsString() {
		FilterType filterType = getFilterType();

		if (filterType == null) {
			return null;
		}

		return filterType.toString();
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;

		_filterTypeSupplier = null;
	}

	@JsonIgnore
	public void setFilterType(
		UnsafeSupplier<FilterType, Exception> filterTypeUnsafeSupplier) {

		_filterTypeSupplier = () -> {
			try {
				return filterTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FilterType filterType;

	@JsonIgnore
	private Supplier<FilterType> _filterTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getJson() {
		if (_jsonSupplier != null) {
			json = _jsonSupplier.get();

			_jsonSupplier = null;
		}

		return json;
	}

	public void setJson(String json) {
		this.json = json;

		_jsonSupplier = null;
	}

	@JsonIgnore
	public void setJson(UnsafeSupplier<String, Exception> jsonUnsafeSupplier) {
		_jsonSupplier = () -> {
			try {
				return jsonUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String json;

	@JsonIgnore
	private Supplier<String> _jsonSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectFieldName() {
		if (_objectFieldNameSupplier != null) {
			objectFieldName = _objectFieldNameSupplier.get();

			_objectFieldNameSupplier = null;
		}

		return objectFieldName;
	}

	public void setObjectFieldName(String objectFieldName) {
		this.objectFieldName = objectFieldName;

		_objectFieldNameSupplier = null;
	}

	@JsonIgnore
	public void setObjectFieldName(
		UnsafeSupplier<String, Exception> objectFieldNameUnsafeSupplier) {

		_objectFieldNameSupplier = () -> {
			try {
				return objectFieldNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String objectFieldName;

	@JsonIgnore
	private Supplier<String> _objectFieldNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getValueSummary() {
		if (_valueSummarySupplier != null) {
			valueSummary = _valueSummarySupplier.get();

			_valueSummarySupplier = null;
		}

		return valueSummary;
	}

	public void setValueSummary(String valueSummary) {
		this.valueSummary = valueSummary;

		_valueSummarySupplier = null;
	}

	@JsonIgnore
	public void setValueSummary(
		UnsafeSupplier<String, Exception> valueSummaryUnsafeSupplier) {

		_valueSummarySupplier = () -> {
			try {
				return valueSummaryUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String valueSummary;

	@JsonIgnore
	private Supplier<String> _valueSummarySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectViewFilterColumn)) {
			return false;
		}

		ObjectViewFilterColumn objectViewFilterColumn =
			(ObjectViewFilterColumn)object;

		return Objects.equals(toString(), objectViewFilterColumn.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		FilterType filterType = getFilterType();

		if (filterType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"filterType\": ");

			sb.append("\"");

			sb.append(filterType);

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String json = getJson();

		if (json != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"json\": ");

			sb.append("\"");

			sb.append(_escape(json));

			sb.append("\"");
		}

		String objectFieldName = getObjectFieldName();

		if (objectFieldName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFieldName\": ");

			sb.append("\"");

			sb.append(_escape(objectFieldName));

			sb.append("\"");
		}

		String valueSummary = getValueSummary();

		if (valueSummary != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"valueSummary\": ");

			sb.append("\"");

			sb.append(_escape(valueSummary));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.admin.rest.dto.v1_0.ObjectViewFilterColumn",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("FilterType")
	public static enum FilterType {

		EXCLUDES("excludes"), INCLUDES("includes");

		@JsonCreator
		public static FilterType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (FilterType filterType : values()) {
				if (Objects.equals(filterType.getValue(), value)) {
					return filterType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private FilterType(String value) {
			_value = value;
		}

		private final String _value;

	}

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
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("HighlightField")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "HighlightField")
public class HighlightField implements Serializable {

	public static HighlightField toDTO(String json) {
		return ObjectMapperUtil.readValue(HighlightField.class, json);
	}

	public static HighlightField unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(HighlightField.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getFragment_offset() {
		if (_fragment_offsetSupplier != null) {
			fragment_offset = _fragment_offsetSupplier.get();

			_fragment_offsetSupplier = null;
		}

		return fragment_offset;
	}

	public void setFragment_offset(Integer fragment_offset) {
		this.fragment_offset = fragment_offset;

		_fragment_offsetSupplier = null;
	}

	@JsonIgnore
	public void setFragment_offset(
		UnsafeSupplier<Integer, Exception> fragment_offsetUnsafeSupplier) {

		_fragment_offsetSupplier = () -> {
			try {
				return fragment_offsetUnsafeSupplier.get();
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
	protected Integer fragment_offset;

	@JsonIgnore
	private Supplier<Integer> _fragment_offsetSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getFragment_size() {
		if (_fragment_sizeSupplier != null) {
			fragment_size = _fragment_sizeSupplier.get();

			_fragment_sizeSupplier = null;
		}

		return fragment_size;
	}

	public void setFragment_size(Integer fragment_size) {
		this.fragment_size = fragment_size;

		_fragment_sizeSupplier = null;
	}

	@JsonIgnore
	public void setFragment_size(
		UnsafeSupplier<Integer, Exception> fragment_sizeUnsafeSupplier) {

		_fragment_sizeSupplier = () -> {
			try {
				return fragment_sizeUnsafeSupplier.get();
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
	protected Integer fragment_size;

	@JsonIgnore
	private Supplier<Integer> _fragment_sizeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getNumber_of_fragments() {
		if (_number_of_fragmentsSupplier != null) {
			number_of_fragments = _number_of_fragmentsSupplier.get();

			_number_of_fragmentsSupplier = null;
		}

		return number_of_fragments;
	}

	public void setNumber_of_fragments(Integer number_of_fragments) {
		this.number_of_fragments = number_of_fragments;

		_number_of_fragmentsSupplier = null;
	}

	@JsonIgnore
	public void setNumber_of_fragments(
		UnsafeSupplier<Integer, Exception> number_of_fragmentsUnsafeSupplier) {

		_number_of_fragmentsSupplier = () -> {
			try {
				return number_of_fragmentsUnsafeSupplier.get();
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
	protected Integer number_of_fragments;

	@JsonIgnore
	private Supplier<Integer> _number_of_fragmentsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof HighlightField)) {
			return false;
		}

		HighlightField highlightField = (HighlightField)object;

		return Objects.equals(toString(), highlightField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Integer fragment_offset = getFragment_offset();

		if (fragment_offset != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragment_offset\": ");

			sb.append(fragment_offset);
		}

		Integer fragment_size = getFragment_size();

		if (fragment_size != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragment_size\": ");

			sb.append(fragment_size);
		}

		Integer number_of_fragments = getNumber_of_fragments();

		if (number_of_fragments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"number_of_fragments\": ");

			sb.append(number_of_fragments);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.HighlightField",
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
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
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

import jakarta.validation.Valid;

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
@GraphQLName("Collapse")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Collapse")
public class Collapse implements Serializable {

	public static Collapse toDTO(String json) {
		return ObjectMapperUtil.readValue(Collapse.class, json);
	}

	public static Collapse unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Collapse.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getField() {
		if (_fieldSupplier != null) {
			field = _fieldSupplier.get();

			_fieldSupplier = null;
		}

		return field;
	}

	public void setField(String field) {
		this.field = field;

		_fieldSupplier = null;
	}

	@JsonIgnore
	public void setField(
		UnsafeSupplier<String, Exception> fieldUnsafeSupplier) {

		_fieldSupplier = () -> {
			try {
				return fieldUnsafeSupplier.get();
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
	protected String field;

	@JsonIgnore
	private Supplier<String> _fieldSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public InnerHit[] getInnerHits() {
		if (_innerHitsSupplier != null) {
			innerHits = _innerHitsSupplier.get();

			_innerHitsSupplier = null;
		}

		return innerHits;
	}

	public void setInnerHits(InnerHit[] innerHits) {
		this.innerHits = innerHits;

		_innerHitsSupplier = null;
	}

	@JsonIgnore
	public void setInnerHits(
		UnsafeSupplier<InnerHit[], Exception> innerHitsUnsafeSupplier) {

		_innerHitsSupplier = () -> {
			try {
				return innerHitsUnsafeSupplier.get();
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
	protected InnerHit[] innerHits;

	@JsonIgnore
	private Supplier<InnerHit[]> _innerHitsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getMaxConcurrentGroupRequests() {
		if (_maxConcurrentGroupRequestsSupplier != null) {
			maxConcurrentGroupRequests =
				_maxConcurrentGroupRequestsSupplier.get();

			_maxConcurrentGroupRequestsSupplier = null;
		}

		return maxConcurrentGroupRequests;
	}

	public void setMaxConcurrentGroupRequests(
		Integer maxConcurrentGroupRequests) {

		this.maxConcurrentGroupRequests = maxConcurrentGroupRequests;

		_maxConcurrentGroupRequestsSupplier = null;
	}

	@JsonIgnore
	public void setMaxConcurrentGroupRequests(
		UnsafeSupplier<Integer, Exception>
			maxConcurrentGroupRequestsUnsafeSupplier) {

		_maxConcurrentGroupRequestsSupplier = () -> {
			try {
				return maxConcurrentGroupRequestsUnsafeSupplier.get();
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
	protected Integer maxConcurrentGroupRequests;

	@JsonIgnore
	private Supplier<Integer> _maxConcurrentGroupRequestsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Collapse)) {
			return false;
		}

		Collapse collapse = (Collapse)object;

		return Objects.equals(toString(), collapse.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String field = getField();

		if (field != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"field\": ");

			sb.append("\"");

			sb.append(_escape(field));

			sb.append("\"");
		}

		InnerHit[] innerHits = getInnerHits();

		if (innerHits != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"innerHits\": ");

			sb.append("[");

			for (int i = 0; i < innerHits.length; i++) {
				sb.append(String.valueOf(innerHits[i]));

				if ((i + 1) < innerHits.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Integer maxConcurrentGroupRequests = getMaxConcurrentGroupRequests();

		if (maxConcurrentGroupRequests != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxConcurrentGroupRequests\": ");

			sb.append(maxConcurrentGroupRequests);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.Collapse",
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
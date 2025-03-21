/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
@GraphQLName("Bulk")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Bulk")
public class Bulk implements Serializable {

	public static Bulk toDTO(String json) {
		return ObjectMapperUtil.readValue(Bulk.class, json);
	}

	public static Bulk unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Bulk.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getMaxOperations() {
		if (_maxOperationsSupplier != null) {
			maxOperations = _maxOperationsSupplier.get();

			_maxOperationsSupplier = null;
		}

		return maxOperations;
	}

	public void setMaxOperations(Integer maxOperations) {
		this.maxOperations = maxOperations;

		_maxOperationsSupplier = null;
	}

	@JsonIgnore
	public void setMaxOperations(
		UnsafeSupplier<Integer, Exception> maxOperationsUnsafeSupplier) {

		_maxOperationsSupplier = () -> {
			try {
				return maxOperationsUnsafeSupplier.get();
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
	protected Integer maxOperations;

	@JsonIgnore
	private Supplier<Integer> _maxOperationsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getMaxPayloadSize() {
		if (_maxPayloadSizeSupplier != null) {
			maxPayloadSize = _maxPayloadSizeSupplier.get();

			_maxPayloadSizeSupplier = null;
		}

		return maxPayloadSize;
	}

	public void setMaxPayloadSize(Integer maxPayloadSize) {
		this.maxPayloadSize = maxPayloadSize;

		_maxPayloadSizeSupplier = null;
	}

	@JsonIgnore
	public void setMaxPayloadSize(
		UnsafeSupplier<Integer, Exception> maxPayloadSizeUnsafeSupplier) {

		_maxPayloadSizeSupplier = () -> {
			try {
				return maxPayloadSizeUnsafeSupplier.get();
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
	protected Integer maxPayloadSize;

	@JsonIgnore
	private Supplier<Integer> _maxPayloadSizeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSupported() {
		if (_supportedSupplier != null) {
			supported = _supportedSupplier.get();

			_supportedSupplier = null;
		}

		return supported;
	}

	public void setSupported(Boolean supported) {
		this.supported = supported;

		_supportedSupplier = null;
	}

	@JsonIgnore
	public void setSupported(
		UnsafeSupplier<Boolean, Exception> supportedUnsafeSupplier) {

		_supportedSupplier = () -> {
			try {
				return supportedUnsafeSupplier.get();
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
	protected Boolean supported;

	@JsonIgnore
	private Supplier<Boolean> _supportedSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Bulk)) {
			return false;
		}

		Bulk bulk = (Bulk)object;

		return Objects.equals(toString(), bulk.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Integer maxOperations = getMaxOperations();

		if (maxOperations != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxOperations\": ");

			sb.append(maxOperations);
		}

		Integer maxPayloadSize = getMaxPayloadSize();

		if (maxPayloadSize != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxPayloadSize\": ");

			sb.append(maxPayloadSize);
		}

		Boolean supported = getSupported();

		if (supported != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"supported\": ");

			sb.append(supported);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.scim.rest.dto.v1_0.Bulk",
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
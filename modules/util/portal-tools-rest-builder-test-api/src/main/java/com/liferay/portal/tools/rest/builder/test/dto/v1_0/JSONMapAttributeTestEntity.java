/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Test Component to test the generation of getValue method on Entities when one or multiple JSON Maps are present.",
	value = "JSONMapAttributeTestEntity"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "JSONMapAttributeTestEntity")
public class JSONMapAttributeTestEntity implements Serializable {

	public static JSONMapAttributeTestEntity toDTO(String json) {
		return ObjectMapperUtil.readValue(
			JSONMapAttributeTestEntity.class, json);
	}

	public static JSONMapAttributeTestEntity unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			JSONMapAttributeTestEntity.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
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
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
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
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Object> getProperties1() {
		if (properties1 == null) {
			return null;
		}

		properties1.replaceAll(
			(key, value) -> {
				if (!(value instanceof UnsafeSupplier<?, ?>)) {
					return value;
				}

				try {
					UnsafeSupplier<?, ?> unsafeSupplier =
						(UnsafeSupplier<?, ?>)value;

					return unsafeSupplier.get();
				}
				catch (Throwable throwable) {
					throw new RuntimeException(throwable);
				}
			});

		return properties1;
	}

	public void setProperties1(Map<String, Object> properties1) {
		if (properties1 == null) {
			this.properties1 = null;

			return;
		}

		Map<String, Object> properties1Map = new LinkedHashMap<>(properties1);

		properties1Map.replaceAll(
			(key, value) -> {
				if (!(value instanceof UnsafeSupplier<?, ?>)) {
					return value;
				}

				return new CachedUnsafeSupplier((UnsafeSupplier<?, ?>)value);
			});

		this.properties1 = Collections.synchronizedMap(properties1Map);
	}

	@JsonIgnore
	public void setProperties1(
		UnsafeSupplier<Map<String, Object>, Exception>
			properties1UnsafeSupplier) {

		if (properties1UnsafeSupplier == null) {
			setProperties1((Map<String, Object>)null);

			return;
		}

		try {
			setProperties1(properties1UnsafeSupplier.get());
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@GraphQLField
	@JsonAnyGetter
	@JsonAnySetter
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, Object> properties1 = Collections.synchronizedMap(
		new LinkedHashMap<>());

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Object> getProperties2() {
		if (properties2 == null) {
			return null;
		}

		properties2.replaceAll(
			(key, value) -> {
				if (!(value instanceof UnsafeSupplier<?, ?>)) {
					return value;
				}

				try {
					UnsafeSupplier<?, ?> unsafeSupplier =
						(UnsafeSupplier<?, ?>)value;

					return unsafeSupplier.get();
				}
				catch (Throwable throwable) {
					throw new RuntimeException(throwable);
				}
			});

		return properties2;
	}

	public void setProperties2(Map<String, Object> properties2) {
		if (properties2 == null) {
			this.properties2 = null;

			return;
		}

		Map<String, Object> properties2Map = new LinkedHashMap<>(properties2);

		properties2Map.replaceAll(
			(key, value) -> {
				if (!(value instanceof UnsafeSupplier<?, ?>)) {
					return value;
				}

				return new CachedUnsafeSupplier((UnsafeSupplier<?, ?>)value);
			});

		this.properties2 = Collections.synchronizedMap(properties2Map);
	}

	@JsonIgnore
	public void setProperties2(
		UnsafeSupplier<Map<String, Object>, Exception>
			properties2UnsafeSupplier) {

		if (properties2UnsafeSupplier == null) {
			setProperties2((Map<String, Object>)null);

			return;
		}

		try {
			setProperties2(properties2UnsafeSupplier.get());
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@GraphQLField
	@JsonAnyGetter
	@JsonAnySetter
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, Object> properties2 = Collections.synchronizedMap(
		new LinkedHashMap<>());

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof JSONMapAttributeTestEntity)) {
			return false;
		}

		JSONMapAttributeTestEntity jsonMapAttributeTestEntity =
			(JSONMapAttributeTestEntity)object;

		return Objects.equals(
			toString(), jsonMapAttributeTestEntity.toString());
	}

	public Object getPropertyValue(String propertyName) {
		if (Objects.equals(propertyName, "description")) {
			return getDescription();
		}
		else if (Objects.equals(propertyName, "name")) {
			return getName();
		}
		else {
			if (properties1.containsKey(propertyName)) {
				Object value = properties1.get(propertyName);

				if (!(value instanceof UnsafeSupplier<?, ?>)) {
					return value;
				}

				UnsafeSupplier<?, ?> unsafeSupplier =
					(UnsafeSupplier<?, ?>)value;

				try {
					return unsafeSupplier.get();
				}
				catch (Throwable throwable) {
					throw new RuntimeException(throwable);
				}
			}

			if (properties2.containsKey(propertyName)) {
				Object value = properties2.get(propertyName);

				if (!(value instanceof UnsafeSupplier<?, ?>)) {
					return value;
				}

				UnsafeSupplier<?, ?> unsafeSupplier =
					(UnsafeSupplier<?, ?>)value;

				try {
					return unsafeSupplier.get();
				}
				catch (Throwable throwable) {
					throw new RuntimeException(throwable);
				}
			}
		}

		return null;
	}

	private final class CachedUnsafeSupplier<T, E extends Throwable>
		implements UnsafeSupplier<T, E> {

		public CachedUnsafeSupplier(UnsafeSupplier<T, E> unsafeSupplier) {
			_unsafeSupplier = unsafeSupplier;
		}

		public T get() throws E {
			if (_set) {
				return _value;
			}

			synchronized (_unsafeSupplier) {
				_value = _unsafeSupplier.get();

				_set = true;
			}

			return _value;
		}

		private boolean _set;
		private final UnsafeSupplier<T, E> _unsafeSupplier;
		private T _value;

	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Map<String, Object> properties1 = getProperties1();

		if (properties1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"properties1\": ");

			sb.append(_toJSON(properties1));
		}

		Map<String, Object> properties2 = getProperties2();

		if (properties2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"properties2\": ");

			sb.append(_toJSON(properties2));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.tools.rest.builder.test.dto.v1_0.JSONMapAttributeTestEntity",
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
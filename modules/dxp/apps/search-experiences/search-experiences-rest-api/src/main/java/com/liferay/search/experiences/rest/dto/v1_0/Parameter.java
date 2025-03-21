/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("Parameter")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Parameter")
public class Parameter implements Serializable {

	public static Parameter toDTO(String json) {
		return ObjectMapperUtil.readValue(Parameter.class, json);
	}

	public static Parameter unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Parameter.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getDefaultValue() {
		if (_defaultValueSupplier != null) {
			defaultValue = _defaultValueSupplier.get();

			_defaultValueSupplier = null;
		}

		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;

		_defaultValueSupplier = null;
	}

	@JsonIgnore
	public void setDefaultValue(
		UnsafeSupplier<Object, Exception> defaultValueUnsafeSupplier) {

		_defaultValueSupplier = () -> {
			try {
				return defaultValueUnsafeSupplier.get();
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
	protected Object defaultValue;

	@JsonIgnore
	private Supplier<Object> _defaultValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFormat() {
		if (_formatSupplier != null) {
			format = _formatSupplier.get();

			_formatSupplier = null;
		}

		return format;
	}

	public void setFormat(String format) {
		this.format = format;

		_formatSupplier = null;
	}

	@JsonIgnore
	public void setFormat(
		UnsafeSupplier<String, Exception> formatUnsafeSupplier) {

		_formatSupplier = () -> {
			try {
				return formatUnsafeSupplier.get();
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
	protected String format;

	@JsonIgnore
	private Supplier<String> _formatSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getMax() {
		if (_maxSupplier != null) {
			max = _maxSupplier.get();

			_maxSupplier = null;
		}

		return max;
	}

	public void setMax(Object max) {
		this.max = max;

		_maxSupplier = null;
	}

	@JsonIgnore
	public void setMax(UnsafeSupplier<Object, Exception> maxUnsafeSupplier) {
		_maxSupplier = () -> {
			try {
				return maxUnsafeSupplier.get();
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
	protected Object max;

	@JsonIgnore
	private Supplier<Object> _maxSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getMin() {
		if (_minSupplier != null) {
			min = _minSupplier.get();

			_minSupplier = null;
		}

		return min;
	}

	public void setMin(Object min) {
		this.min = min;

		_minSupplier = null;
	}

	@JsonIgnore
	public void setMin(UnsafeSupplier<Object, Exception> minUnsafeSupplier) {
		_minSupplier = () -> {
			try {
				return minUnsafeSupplier.get();
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
	protected Object min;

	@JsonIgnore
	private Supplier<Object> _minSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
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
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Parameter)) {
			return false;
		}

		Parameter parameter = (Parameter)object;

		return Objects.equals(toString(), parameter.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object defaultValue = getDefaultValue();

		if (defaultValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			if (defaultValue instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)defaultValue));
			}
			else if (defaultValue instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)defaultValue));
				sb.append("\"");
			}
			else {
				sb.append(defaultValue);
			}
		}

		String format = getFormat();

		if (format != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"format\": ");

			sb.append("\"");

			sb.append(_escape(format));

			sb.append("\"");
		}

		Object max = getMax();

		if (max != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"max\": ");

			if (max instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)max));
			}
			else if (max instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)max));
				sb.append("\"");
			}
			else {
				sb.append(max);
			}
		}

		Object min = getMin();

		if (min != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"min\": ");

			if (min instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)min));
			}
			else if (min instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)min));
				sb.append("\"");
			}
			else {
				sb.append(min);
			}
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(type);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.Parameter",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		BOOLEAN("Boolean"), DATE("Date"), DOUBLE("Double"), FLOAT("Float"),
		INTEGER("Integer"), INTEGER_ARRAY("IntegerArray"), LONG("Long"),
		LONG_ARRAY("LongArray"), STRING("String"), STRING_ARRAY("StringArray"),
		TIME_RANGE("TimeRange");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
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

		private Type(String value) {
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
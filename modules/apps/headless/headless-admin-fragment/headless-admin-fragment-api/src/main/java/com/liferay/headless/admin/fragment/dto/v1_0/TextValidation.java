/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "An `email`, `text`, or `url` validation of a `text` fragment configuration field's value.",
	value = "TextValidation"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "An `email`, `text`, or `url` validation of a `text` fragment configuration field's value."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TextValidation")
public class TextValidation extends Validation implements Serializable {

	public static TextValidation toDTO(String json) {
		return ObjectMapperUtil.readValue(TextValidation.class, json);
	}

	public static TextValidation unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TextValidation.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The maximum length allowed."
	)
	public Long getMaxLength() {
		if (_maxLengthSupplier != null) {
			maxLength = _maxLengthSupplier.get();

			_maxLengthSupplier = null;
		}

		return maxLength;
	}

	public void setMaxLength(Long maxLength) {
		this.maxLength = maxLength;

		_maxLengthSupplier = null;
	}

	@JsonIgnore
	public void setMaxLength(
		UnsafeSupplier<Long, Exception> maxLengthUnsafeSupplier) {

		_maxLengthSupplier = () -> {
			try {
				return maxLengthUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The maximum length allowed.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long maxLength;

	@JsonIgnore
	private Supplier<Long> _maxLengthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The minimum length allowed."
	)
	public Long getMinLength() {
		if (_minLengthSupplier != null) {
			minLength = _minLengthSupplier.get();

			_minLengthSupplier = null;
		}

		return minLength;
	}

	public void setMinLength(Long minLength) {
		this.minLength = minLength;

		_minLengthSupplier = null;
	}

	@JsonIgnore
	public void setMinLength(
		UnsafeSupplier<Long, Exception> minLengthUnsafeSupplier) {

		_minLengthSupplier = () -> {
			try {
				return minLengthUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The minimum length allowed.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long minLength;

	@JsonIgnore
	private Supplier<Long> _minLengthSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TextValidation)) {
			return false;
		}

		TextValidation textValidation = (TextValidation)object;

		return Objects.equals(toString(), textValidation.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long maxLength = getMaxLength();

		if (maxLength != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxLength\": ");

			sb.append(maxLength);
		}

		Long minLength = getMinLength();

		if (minLength != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minLength\": ");

			sb.append(minLength);
		}

		String errorMessage = getErrorMessage();

		if (errorMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(_escape(errorMessage));

			sb.append("\"");
		}

		Boolean required = getRequired();

		if (required != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(required);
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
		defaultValue = "com.liferay.headless.admin.fragment.dto.v1_0.TextValidation",
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-650775600
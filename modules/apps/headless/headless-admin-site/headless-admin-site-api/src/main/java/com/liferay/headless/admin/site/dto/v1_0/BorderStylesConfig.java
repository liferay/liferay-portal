/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName("BorderStylesConfig")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "BorderStylesConfig")
public class BorderStylesConfig implements Serializable {

	public static BorderStylesConfig toDTO(String json) {
		return ObjectMapperUtil.readValue(BorderStylesConfig.class, json);
	}

	public static BorderStylesConfig unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(BorderStylesConfig.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getBorderColor() {
		if (_borderColorSupplier != null) {
			borderColor = _borderColorSupplier.get();

			_borderColorSupplier = null;
		}

		return borderColor;
	}

	public void setBorderColor(Object borderColor) {
		this.borderColor = borderColor;

		_borderColorSupplier = null;
	}

	@JsonIgnore
	public void setBorderColor(
		UnsafeSupplier<Object, Exception> borderColorUnsafeSupplier) {

		_borderColorSupplier = () -> {
			try {
				return borderColorUnsafeSupplier.get();
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
	protected Object borderColor;

	@JsonIgnore
	private Supplier<Object> _borderColorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getBorderStyle() {
		if (_borderStyleSupplier != null) {
			borderStyle = _borderStyleSupplier.get();

			_borderStyleSupplier = null;
		}

		return borderStyle;
	}

	public void setBorderStyle(Object borderStyle) {
		this.borderStyle = borderStyle;

		_borderStyleSupplier = null;
	}

	@JsonIgnore
	public void setBorderStyle(
		UnsafeSupplier<Object, Exception> borderStyleUnsafeSupplier) {

		_borderStyleSupplier = () -> {
			try {
				return borderStyleUnsafeSupplier.get();
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
	protected Object borderStyle;

	@JsonIgnore
	private Supplier<Object> _borderStyleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getBorderWidth() {
		if (_borderWidthSupplier != null) {
			borderWidth = _borderWidthSupplier.get();

			_borderWidthSupplier = null;
		}

		return borderWidth;
	}

	public void setBorderWidth(Object borderWidth) {
		this.borderWidth = borderWidth;

		_borderWidthSupplier = null;
	}

	@JsonIgnore
	public void setBorderWidth(
		UnsafeSupplier<Object, Exception> borderWidthUnsafeSupplier) {

		_borderWidthSupplier = () -> {
			try {
				return borderWidthUnsafeSupplier.get();
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
	protected Object borderWidth;

	@JsonIgnore
	private Supplier<Object> _borderWidthSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BorderStylesConfig)) {
			return false;
		}

		BorderStylesConfig borderStylesConfig = (BorderStylesConfig)object;

		return Objects.equals(toString(), borderStylesConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object borderColor = getBorderColor();

		if (borderColor != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderColor\": ");

			if (borderColor instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)borderColor));
			}
			else if (borderColor instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)borderColor));
				sb.append("\"");
			}
			else {
				sb.append(borderColor);
			}
		}

		Object borderStyle = getBorderStyle();

		if (borderStyle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderStyle\": ");

			if (borderStyle instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)borderStyle));
			}
			else if (borderStyle instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)borderStyle));
				sb.append("\"");
			}
			else {
				sb.append(borderStyle);
			}
		}

		Object borderWidth = getBorderWidth();

		if (borderWidth != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderWidth\": ");

			if (borderWidth instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)borderWidth));
			}
			else if (borderWidth instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)borderWidth));
				sb.append("\"");
			}
			else {
				sb.append(borderWidth);
			}
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.BorderStylesConfig",
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
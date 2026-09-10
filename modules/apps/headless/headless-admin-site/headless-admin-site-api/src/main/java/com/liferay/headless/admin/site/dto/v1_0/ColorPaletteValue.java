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
	description = "The value of a field of type color palette.",
	value = "ColorPaletteValue"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "The value of a field of type color palette."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ColorPaletteValue")
public class ColorPaletteValue implements Serializable {

	public static ColorPaletteValue toDTO(String json) {
		return ObjectMapperUtil.readValue(ColorPaletteValue.class, json);
	}

	public static ColorPaletteValue unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ColorPaletteValue.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getColor() {
		if (_colorSupplier != null) {
			color = _colorSupplier.get();

			_colorSupplier = null;
		}

		return color;
	}

	public void setColor(String color) {
		this.color = color;

		_colorSupplier = null;
	}

	@JsonIgnore
	public void setColor(
		UnsafeSupplier<String, Exception> colorUnsafeSupplier) {

		_colorSupplier = () -> {
			try {
				return colorUnsafeSupplier.get();
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
	protected String color;

	@JsonIgnore
	private Supplier<String> _colorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCssClass() {
		if (_cssClassSupplier != null) {
			cssClass = _cssClassSupplier.get();

			_cssClassSupplier = null;
		}

		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;

		_cssClassSupplier = null;
	}

	@JsonIgnore
	public void setCssClass(
		UnsafeSupplier<String, Exception> cssClassUnsafeSupplier) {

		_cssClassSupplier = () -> {
			try {
				return cssClassUnsafeSupplier.get();
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
	protected String cssClass;

	@JsonIgnore
	private Supplier<String> _cssClassSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getRgbValue() {
		if (_rgbValueSupplier != null) {
			rgbValue = _rgbValueSupplier.get();

			_rgbValueSupplier = null;
		}

		return rgbValue;
	}

	public void setRgbValue(String rgbValue) {
		this.rgbValue = rgbValue;

		_rgbValueSupplier = null;
	}

	@JsonIgnore
	public void setRgbValue(
		UnsafeSupplier<String, Exception> rgbValueUnsafeSupplier) {

		_rgbValueSupplier = () -> {
			try {
				return rgbValueUnsafeSupplier.get();
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
	protected String rgbValue;

	@JsonIgnore
	private Supplier<String> _rgbValueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ColorPaletteValue)) {
			return false;
		}

		ColorPaletteValue colorPaletteValue = (ColorPaletteValue)object;

		return Objects.equals(toString(), colorPaletteValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String color = getColor();

		if (color != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"color\": ");

			sb.append("\"");

			sb.append(_escape(color));

			sb.append("\"");
		}

		String cssClass = getCssClass();

		if (cssClass != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClass\": ");

			sb.append("\"");

			sb.append(_escape(cssClass));

			sb.append("\"");
		}

		String rgbValue = getRgbValue();

		if (rgbValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rgbValue\": ");

			sb.append("\"");

			sb.append(_escape(rgbValue));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.ColorPaletteValue",
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
// LIFERAY-REST-BUILDER-HASH:-1891660912
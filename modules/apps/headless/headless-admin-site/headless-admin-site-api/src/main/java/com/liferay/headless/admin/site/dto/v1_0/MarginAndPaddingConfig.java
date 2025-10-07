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
@GraphQLName("MarginAndPaddingConfig")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "MarginAndPaddingConfig")
public class MarginAndPaddingConfig implements Serializable {

	public static MarginAndPaddingConfig toDTO(String json) {
		return ObjectMapperUtil.readValue(MarginAndPaddingConfig.class, json);
	}

	public static MarginAndPaddingConfig unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			MarginAndPaddingConfig.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getMargin() {
		if (_marginSupplier != null) {
			margin = _marginSupplier.get();

			_marginSupplier = null;
		}

		return margin;
	}

	public void setMargin(Object margin) {
		this.margin = margin;

		_marginSupplier = null;
	}

	@JsonIgnore
	public void setMargin(
		UnsafeSupplier<Object, Exception> marginUnsafeSupplier) {

		_marginSupplier = () -> {
			try {
				return marginUnsafeSupplier.get();
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
	protected Object margin;

	@JsonIgnore
	private Supplier<Object> _marginSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getPadding() {
		if (_paddingSupplier != null) {
			padding = _paddingSupplier.get();

			_paddingSupplier = null;
		}

		return padding;
	}

	public void setPadding(Object padding) {
		this.padding = padding;

		_paddingSupplier = null;
	}

	@JsonIgnore
	public void setPadding(
		UnsafeSupplier<Object, Exception> paddingUnsafeSupplier) {

		_paddingSupplier = () -> {
			try {
				return paddingUnsafeSupplier.get();
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
	protected Object padding;

	@JsonIgnore
	private Supplier<Object> _paddingSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MarginAndPaddingConfig)) {
			return false;
		}

		MarginAndPaddingConfig marginAndPaddingConfig =
			(MarginAndPaddingConfig)object;

		return Objects.equals(toString(), marginAndPaddingConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object margin = getMargin();

		if (margin != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"margin\": ");

			if (margin instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)margin));
			}
			else if (margin instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)margin));
				sb.append("\"");
			}
			else {
				sb.append(margin);
			}
		}

		Object padding = getPadding();

		if (padding != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"padding\": ");

			if (padding instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)padding));
			}
			else if (padding instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)padding));
				sb.append("\"");
			}
			else {
				sb.append(padding);
			}
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.MarginAndPaddingConfig",
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
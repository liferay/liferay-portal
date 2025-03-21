/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

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

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

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
@GraphQLName(
	description = "A reference of type context, used in collection display fragments and display page templates.",
	value = "ContextReference"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A reference of type context, used in collection display fragments and display page templates.",
	requiredProperties = {"contextSource"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ContextReference")
public class ContextReference implements Serializable {

	public static ContextReference toDTO(String json) {
		return ObjectMapperUtil.readValue(ContextReference.class, json);
	}

	public static ContextReference unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ContextReference.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("contextSource")
	@Valid
	public ContextSource getContextSource() {
		if (_contextSourceSupplier != null) {
			contextSource = _contextSourceSupplier.get();

			_contextSourceSupplier = null;
		}

		return contextSource;
	}

	@JsonIgnore
	public String getContextSourceAsString() {
		ContextSource contextSource = getContextSource();

		if (contextSource == null) {
			return null;
		}

		return contextSource.toString();
	}

	public void setContextSource(ContextSource contextSource) {
		this.contextSource = contextSource;

		_contextSourceSupplier = null;
	}

	@JsonIgnore
	public void setContextSource(
		UnsafeSupplier<ContextSource, Exception> contextSourceUnsafeSupplier) {

		_contextSourceSupplier = () -> {
			try {
				return contextSourceUnsafeSupplier.get();
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
	@NotNull
	protected ContextSource contextSource;

	@JsonIgnore
	private Supplier<ContextSource> _contextSourceSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContextReference)) {
			return false;
		}

		ContextReference contextReference = (ContextReference)object;

		return Objects.equals(toString(), contextReference.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ContextSource contextSource = getContextSource();

		if (contextSource != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contextSource\": ");

			sb.append("\"");

			sb.append(contextSource);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.ContextReference",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("ContextSource")
	public static enum ContextSource {

		COLLECTION_ITEM("CollectionItem"), DISPLAY_PAGE_ITEM("DisplayPageItem");

		@JsonCreator
		public static ContextSource create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ContextSource contextSource : values()) {
				if (Objects.equals(contextSource.getValue(), value)) {
					return contextSource;
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

		private ContextSource(String value) {
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
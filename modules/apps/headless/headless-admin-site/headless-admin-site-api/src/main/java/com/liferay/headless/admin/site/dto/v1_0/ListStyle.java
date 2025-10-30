/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
	description = "The collection display's list style.", value = "ListStyle"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ListStyle")
public class ListStyle
	extends CollectionDisplayListStyle implements Serializable {

	public static ListStyle toDTO(String json) {
		return ObjectMapperUtil.readValue(ListStyle.class, json);
	}

	public static ListStyle unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ListStyle.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ListStyleDefinition getListStyleDefinition() {
		if (_listStyleDefinitionSupplier != null) {
			listStyleDefinition = _listStyleDefinitionSupplier.get();

			_listStyleDefinitionSupplier = null;
		}

		return listStyleDefinition;
	}

	public void setListStyleDefinition(
		ListStyleDefinition listStyleDefinition) {

		this.listStyleDefinition = listStyleDefinition;

		_listStyleDefinitionSupplier = null;
	}

	@JsonIgnore
	public void setListStyleDefinition(
		UnsafeSupplier<ListStyleDefinition, Exception>
			listStyleDefinitionUnsafeSupplier) {

		_listStyleDefinitionSupplier = () -> {
			try {
				return listStyleDefinitionUnsafeSupplier.get();
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
	protected ListStyleDefinition listStyleDefinition;

	@JsonIgnore
	private Supplier<ListStyleDefinition> _listStyleDefinitionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("listStyleType")
	@Valid
	public ListStyleType getListStyleType() {
		if (_listStyleTypeSupplier != null) {
			listStyleType = _listStyleTypeSupplier.get();

			_listStyleTypeSupplier = null;
		}

		return listStyleType;
	}

	@JsonIgnore
	public String getListStyleTypeAsString() {
		ListStyleType listStyleType = getListStyleType();

		if (listStyleType == null) {
			return null;
		}

		return listStyleType.toString();
	}

	public void setListStyleType(ListStyleType listStyleType) {
		this.listStyleType = listStyleType;

		_listStyleTypeSupplier = null;
	}

	@JsonIgnore
	public void setListStyleType(
		UnsafeSupplier<ListStyleType, Exception> listStyleTypeUnsafeSupplier) {

		_listStyleTypeSupplier = () -> {
			try {
				return listStyleTypeUnsafeSupplier.get();
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
	protected ListStyleType listStyleType;

	@JsonIgnore
	private Supplier<ListStyleType> _listStyleTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ListStyle)) {
			return false;
		}

		ListStyle listStyle = (ListStyle)object;

		return Objects.equals(toString(), listStyle.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ListStyleDefinition listStyleDefinition = getListStyleDefinition();

		if (listStyleDefinition != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listStyleDefinition\": ");

			sb.append(String.valueOf(listStyleDefinition));
		}

		ListStyleType listStyleType = getListStyleType();

		if (listStyleType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listStyleType\": ");

			sb.append("\"");
			sb.append(listStyleType);
			sb.append("\"");
		}

		CollectionDisplayListStyleType collectionDisplayListStyleType =
			getCollectionDisplayListStyleType();

		if (collectionDisplayListStyleType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionDisplayListStyleType\": ");

			sb.append("\"");
			sb.append(collectionDisplayListStyleType);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.ListStyle",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("ListStyleType")
	public static enum ListStyleType {

		GRID("Grid"), FLEX_COLUMN("FlexColumn"), FLEX_ROW("FlexRow");

		@JsonCreator
		public static ListStyleType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ListStyleType listStyleType : values()) {
				if (Objects.equals(listStyleType.getValue(), value)) {
					return listStyleType;
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

		private ListStyleType(String value) {
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
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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
	description = "The collection display's list style.",
	value = "CollectionDisplayListStyle"
)
@JsonFilter("Liferay.Vulcan")
@JsonSubTypes(
	{
		@JsonSubTypes.Type(name = "ListStyle", value = ListStyle.class),
		@JsonSubTypes.Type(name = "Template", value = TemplateListStyle.class)
	}
)
@JsonTypeInfo(
	include = JsonTypeInfo.As.PROPERTY,
	property = "collectionDisplayListStyleType", use = JsonTypeInfo.Id.NAME,
	visible = true
)
@XmlRootElement(name = "CollectionDisplayListStyle")
public abstract class CollectionDisplayListStyle implements Serializable {

	public static CollectionDisplayListStyle toDTO(String json) {
		return ObjectMapperUtil.readValue(
			CollectionDisplayListStyle.class, json);
	}

	public static CollectionDisplayListStyle unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			CollectionDisplayListStyle.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The collection display's list style type (ListStyle, Template)."
	)
	@JsonGetter("collectionDisplayListStyleType")
	@Valid
	public CollectionDisplayListStyleType getCollectionDisplayListStyleType() {
		if (_collectionDisplayListStyleTypeSupplier != null) {
			collectionDisplayListStyleType =
				_collectionDisplayListStyleTypeSupplier.get();

			_collectionDisplayListStyleTypeSupplier = null;
		}

		return collectionDisplayListStyleType;
	}

	@JsonIgnore
	public String getCollectionDisplayListStyleTypeAsString() {
		CollectionDisplayListStyleType collectionDisplayListStyleType =
			getCollectionDisplayListStyleType();

		if (collectionDisplayListStyleType == null) {
			return null;
		}

		return collectionDisplayListStyleType.toString();
	}

	public void setCollectionDisplayListStyleType(
		CollectionDisplayListStyleType collectionDisplayListStyleType) {

		this.collectionDisplayListStyleType = collectionDisplayListStyleType;

		_collectionDisplayListStyleTypeSupplier = null;
	}

	@JsonIgnore
	public void setCollectionDisplayListStyleType(
		UnsafeSupplier<CollectionDisplayListStyleType, Exception>
			collectionDisplayListStyleTypeUnsafeSupplier) {

		_collectionDisplayListStyleTypeSupplier = () -> {
			try {
				return collectionDisplayListStyleTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The collection display's list style type (ListStyle, Template)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected CollectionDisplayListStyleType collectionDisplayListStyleType;

	@JsonIgnore
	private Supplier<CollectionDisplayListStyleType>
		_collectionDisplayListStyleTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CollectionDisplayListStyle)) {
			return false;
		}

		CollectionDisplayListStyle collectionDisplayListStyle =
			(CollectionDisplayListStyle)object;

		return Objects.equals(
			toString(), collectionDisplayListStyle.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.CollectionDisplayListStyle",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("CollectionDisplayListStyleType")
	public static enum CollectionDisplayListStyleType {

		LIST_STYLE("ListStyle"), TEMPLATE("Template");

		@JsonCreator
		public static CollectionDisplayListStyleType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (CollectionDisplayListStyleType collectionDisplayListStyleType :
					values()) {

				if (Objects.equals(
						collectionDisplayListStyleType.getValue(), value)) {

					return collectionDisplayListStyleType;
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

		private CollectionDisplayListStyleType(String value) {
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
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
	description = "A collection display viewport.",
	value = "CollectionDisplayViewport"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A collection display viewport.",
	requiredProperties = {"collectionDisplayViewportDefinition", "id"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "CollectionDisplayViewport")
public class CollectionDisplayViewport implements Serializable {

	public static CollectionDisplayViewport toDTO(String json) {
		return ObjectMapperUtil.readValue(
			CollectionDisplayViewport.class, json);
	}

	public static CollectionDisplayViewport unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			CollectionDisplayViewport.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The definition of the collection display viewport."
	)
	@Valid
	public CollectionDisplayViewportDefinition
		getCollectionDisplayViewportDefinition() {

		if (_collectionDisplayViewportDefinitionSupplier != null) {
			collectionDisplayViewportDefinition =
				_collectionDisplayViewportDefinitionSupplier.get();

			_collectionDisplayViewportDefinitionSupplier = null;
		}

		return collectionDisplayViewportDefinition;
	}

	public void setCollectionDisplayViewportDefinition(
		CollectionDisplayViewportDefinition
			collectionDisplayViewportDefinition) {

		this.collectionDisplayViewportDefinition =
			collectionDisplayViewportDefinition;

		_collectionDisplayViewportDefinitionSupplier = null;
	}

	@JsonIgnore
	public void setCollectionDisplayViewportDefinition(
		UnsafeSupplier<CollectionDisplayViewportDefinition, Exception>
			collectionDisplayViewportDefinitionUnsafeSupplier) {

		_collectionDisplayViewportDefinitionSupplier = () -> {
			try {
				return collectionDisplayViewportDefinitionUnsafeSupplier.get();
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
		description = "The definition of the collection display viewport."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected CollectionDisplayViewportDefinition
		collectionDisplayViewportDefinition;

	@JsonIgnore
	private Supplier<CollectionDisplayViewportDefinition>
		_collectionDisplayViewportDefinitionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("id")
	@Valid
	public Id getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	@JsonIgnore
	public String getIdAsString() {
		Id id = getId();

		if (id == null) {
			return null;
		}

		return id.toString();
	}

	public void setId(Id id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Id, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
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
	protected Id id;

	@JsonIgnore
	private Supplier<Id> _idSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CollectionDisplayViewport)) {
			return false;
		}

		CollectionDisplayViewport collectionDisplayViewport =
			(CollectionDisplayViewport)object;

		return Objects.equals(toString(), collectionDisplayViewport.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		CollectionDisplayViewportDefinition
			collectionDisplayViewportDefinition =
				getCollectionDisplayViewportDefinition();

		if (collectionDisplayViewportDefinition != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionDisplayViewportDefinition\": ");

			sb.append(String.valueOf(collectionDisplayViewportDefinition));
		}

		Id id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");
			sb.append(id);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.CollectionDisplayViewport",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Id")
	public static enum Id {

		LANDSCAPE_MOBILE("LandscapeMobile"), PORTRAIT_MOBILE("PortraitMobile"),
		TABLET("Tablet");

		@JsonCreator
		public static Id create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Id id : values()) {
				if (Objects.equals(id.getValue(), value)) {
					return id;
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

		private Id(String value) {
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
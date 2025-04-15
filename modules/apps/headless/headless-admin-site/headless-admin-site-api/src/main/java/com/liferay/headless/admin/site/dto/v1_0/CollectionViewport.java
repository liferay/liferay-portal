/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
	description = "A collection viewport.", value = "CollectionViewport"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A collection viewport.",
	requiredProperties = {"id", "collectionViewportDefinition"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "CollectionViewport")
public class CollectionViewport implements Serializable {

	public static CollectionViewport toDTO(String json) {
		return ObjectMapperUtil.readValue(CollectionViewport.class, json);
	}

	public static CollectionViewport unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(CollectionViewport.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The definition of the collection viewport."
	)
	@Valid
	public CollectionViewportDefinition getCollectionViewportDefinition() {
		if (_collectionViewportDefinitionSupplier != null) {
			collectionViewportDefinition =
				_collectionViewportDefinitionSupplier.get();

			_collectionViewportDefinitionSupplier = null;
		}

		return collectionViewportDefinition;
	}

	public void setCollectionViewportDefinition(
		CollectionViewportDefinition collectionViewportDefinition) {

		this.collectionViewportDefinition = collectionViewportDefinition;

		_collectionViewportDefinitionSupplier = null;
	}

	@JsonIgnore
	public void setCollectionViewportDefinition(
		UnsafeSupplier<CollectionViewportDefinition, Exception>
			collectionViewportDefinitionUnsafeSupplier) {

		_collectionViewportDefinitionSupplier = () -> {
			try {
				return collectionViewportDefinitionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The definition of the collection viewport.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected CollectionViewportDefinition collectionViewportDefinition;

	@JsonIgnore
	private Supplier<CollectionViewportDefinition>
		_collectionViewportDefinitionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The collection viewport's ID."
	)
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
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

	@GraphQLField(description = "The collection viewport's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CollectionViewport)) {
			return false;
		}

		CollectionViewport collectionViewport = (CollectionViewport)object;

		return Objects.equals(toString(), collectionViewport.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		CollectionViewportDefinition collectionViewportDefinition =
			getCollectionViewportDefinition();

		if (collectionViewportDefinition != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionViewportDefinition\": ");

			sb.append(String.valueOf(collectionViewportDefinition));
		}

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.CollectionViewport",
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
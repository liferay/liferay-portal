/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
@GraphQLName("ResourceType")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ResourceType")
public class ResourceType implements Serializable {

	public static ResourceType toDTO(String json) {
		return ObjectMapperUtil.readValue(ResourceType.class, json);
	}

	public static ResourceType unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ResourceType.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource type's human-readable description."
	)
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
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
		description = "The resource type's human-readable description."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource type's HTTP-addressable endpoint relative to the Base URL of the service provider."
	)
	public String getEndpoint() {
		if (_endpointSupplier != null) {
			endpoint = _endpointSupplier.get();

			_endpointSupplier = null;
		}

		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;

		_endpointSupplier = null;
	}

	@JsonIgnore
	public void setEndpoint(
		UnsafeSupplier<String, Exception> endpointUnsafeSupplier) {

		_endpointSupplier = () -> {
			try {
				return endpointUnsafeSupplier.get();
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
		description = "The resource type's HTTP-addressable endpoint relative to the Base URL of the service provider."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String endpoint;

	@JsonIgnore
	private Supplier<String> _endpointSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource type's server unique id."
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

	@GraphQLField(description = "The resource type's server unique id.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Meta getMeta() {
		if (_metaSupplier != null) {
			meta = _metaSupplier.get();

			_metaSupplier = null;
		}

		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;

		_metaSupplier = null;
	}

	@JsonIgnore
	public void setMeta(UnsafeSupplier<Meta, Exception> metaUnsafeSupplier) {
		_metaSupplier = () -> {
			try {
				return metaUnsafeSupplier.get();
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
	protected Meta meta;

	@JsonIgnore
	private Supplier<Meta> _metaSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource type name."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The resource type name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The resource type's primary/base schema URI."
	)
	public String getSchema() {
		if (_schemaSupplier != null) {
			schema = _schemaSupplier.get();

			_schemaSupplier = null;
		}

		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;

		_schemaSupplier = null;
	}

	@JsonIgnore
	public void setSchema(
		UnsafeSupplier<String, Exception> schemaUnsafeSupplier) {

		_schemaSupplier = () -> {
			try {
				return schemaUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The resource type's primary/base schema URI.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String schema;

	@JsonIgnore
	private Supplier<String> _schemaSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of URIs of the resource type's schema extensions"
	)
	@Valid
	public Schema[] getSchemas() {
		if (_schemasSupplier != null) {
			schemas = _schemasSupplier.get();

			_schemasSupplier = null;
		}

		return schemas;
	}

	public void setSchemas(Schema[] schemas) {
		this.schemas = schemas;

		_schemasSupplier = null;
	}

	@JsonIgnore
	public void setSchemas(
		UnsafeSupplier<Schema[], Exception> schemasUnsafeSupplier) {

		_schemasSupplier = () -> {
			try {
				return schemasUnsafeSupplier.get();
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
		description = "A list of URIs of the resource type's schema extensions"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Schema[] schemas;

	@JsonIgnore
	private Supplier<Schema[]> _schemasSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ResourceType)) {
			return false;
		}

		ResourceType resourceType = (ResourceType)object;

		return Objects.equals(toString(), resourceType.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		String endpoint = getEndpoint();

		if (endpoint != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endpoint\": ");

			sb.append("\"");

			sb.append(_escape(endpoint));

			sb.append("\"");
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

		Meta meta = getMeta();

		if (meta != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"meta\": ");

			sb.append(String.valueOf(meta));
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		String schema = getSchema();

		if (schema != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schema\": ");

			sb.append("\"");

			sb.append(_escape(schema));

			sb.append("\"");
		}

		Schema[] schemas = getSchemas();

		if (schemas != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < schemas.length; i++) {
				sb.append(String.valueOf(schemas[i]));

				if ((i + 1) < schemas.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.scim.rest.dto.v1_0.ResourceType",
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
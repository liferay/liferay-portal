/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "An attribute recorded on events of an LDP workspace, with its data type and a sample value. Use `getWorkspaceGroupEventAttributeDefinitionsPage` to list them.",
	value = "EventAttributeDefinition"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "An attribute recorded on events of an LDP workspace, with its data type and a sample value. Use `getWorkspaceGroupEventAttributeDefinitionsPage` to list them."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "EventAttributeDefinition")
public class EventAttributeDefinition implements Serializable {

	public static EventAttributeDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(EventAttributeDefinition.class, json);
	}

	public static EventAttributeDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			EventAttributeDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Logical data type of the attribute's values."
	)
	@JsonGetter("dataType")
	@Valid
	public DataType getDataType() {
		if (_dataTypeSupplier != null) {
			dataType = _dataTypeSupplier.get();

			_dataTypeSupplier = null;
		}

		return dataType;
	}

	@JsonIgnore
	public String getDataTypeAsString() {
		DataType dataType = getDataType();

		if (dataType == null) {
			return null;
		}

		return dataType.toString();
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;

		_dataTypeSupplier = null;
	}

	@JsonIgnore
	public void setDataType(
		UnsafeSupplier<DataType, Exception> dataTypeUnsafeSupplier) {

		_dataTypeSupplier = () -> {
			try {
				return dataTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Logical data type of the attribute's values.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected DataType dataType;

	@JsonIgnore
	private Supplier<DataType> _dataTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Description of the attribute."
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

	@GraphQLField(description = "Description of the attribute.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display name of the attribute."
	)
	public String getDisplayName() {
		if (_displayNameSupplier != null) {
			displayName = _displayNameSupplier.get();

			_displayNameSupplier = null;
		}

		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;

		_displayNameSupplier = null;
	}

	@JsonIgnore
	public void setDisplayName(
		UnsafeSupplier<String, Exception> displayNameUnsafeSupplier) {

		_displayNameSupplier = () -> {
			try {
				return displayNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Display name of the attribute.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String displayName;

	@JsonIgnore
	private Supplier<String> _displayNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Event attribute definition ID."
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

	@GraphQLField(description = "Event attribute definition ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Name of the attribute."
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

	@GraphQLField(description = "Name of the attribute.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Sample value of the attribute. It may come from an event other than the one passed as `eventDefinitionId`."
	)
	public String getSampleValue() {
		if (_sampleValueSupplier != null) {
			sampleValue = _sampleValueSupplier.get();

			_sampleValueSupplier = null;
		}

		return sampleValue;
	}

	public void setSampleValue(String sampleValue) {
		this.sampleValue = sampleValue;

		_sampleValueSupplier = null;
	}

	@JsonIgnore
	public void setSampleValue(
		UnsafeSupplier<String, Exception> sampleValueUnsafeSupplier) {

		_sampleValueSupplier = () -> {
			try {
				return sampleValueUnsafeSupplier.get();
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
		description = "Sample value of the attribute. It may come from an event other than the one passed as `eventDefinitionId`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String sampleValue;

	@JsonIgnore
	private Supplier<String> _sampleValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Attribute scope. GLOBAL attributes are shared by all events; LOCAL attributes belong to specific events."
	)
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
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
		description = "Attribute scope. GLOBAL attributes are shared by all events; LOCAL attributes belong to specific events."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EventAttributeDefinition)) {
			return false;
		}

		EventAttributeDefinition eventAttributeDefinition =
			(EventAttributeDefinition)object;

		return Objects.equals(toString(), eventAttributeDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DataType dataType = getDataType();

		if (dataType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataType\": ");

			sb.append("\"");
			sb.append(dataType);
			sb.append("\"");
		}

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

		String displayName = getDisplayName();

		if (displayName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayName\": ");

			sb.append("\"");

			sb.append(_escape(displayName));

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

		String sampleValue = getSampleValue();

		if (sampleValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sampleValue\": ");

			sb.append("\"");

			sb.append(_escape(sampleValue));

			sb.append("\"");
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
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.EventAttributeDefinition",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("DataType")
	public static enum DataType {

		BOOLEAN("BOOLEAN"), DATE("DATE"), DURATION("DURATION"),
		NUMBER("NUMBER"), STRING("STRING");

		@JsonCreator
		public static DataType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (DataType dataType : values()) {
				if (Objects.equals(dataType.getValue(), value)) {
					return dataType;
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

		private DataType(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Type")
	public static enum Type {

		GLOBAL("GLOBAL"), LOCAL("LOCAL");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
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

		private Type(String value) {
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
// LIFERAY-REST-BUILDER-HASH:-1120020262
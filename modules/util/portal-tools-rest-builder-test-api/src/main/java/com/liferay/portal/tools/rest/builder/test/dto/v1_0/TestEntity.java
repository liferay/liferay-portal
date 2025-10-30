/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.dto.v1_0;

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
import com.liferay.portal.tools.rest.builder.test.constant.v1_0.StringTestEntity;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
@GraphQLName("TestEntity")
@JsonFilter("Liferay.Vulcan")
@JsonSubTypes(
	{
		@JsonSubTypes.Type(
			name = "ChildTestEntity1", value = ChildTestEntity1.class
		),
		@JsonSubTypes.Type(
			name = "ChildTestEntity2", value = ChildTestEntity2.class
		),
		@JsonSubTypes.Type(
			name = "ChildTestEntity3", value = ChildTestEntity3.class
		)
	}
)
@JsonTypeInfo(
	include = JsonTypeInfo.As.PROPERTY, property = "type",
	use = JsonTypeInfo.Id.NAME, visible = true
)
@XmlRootElement(name = "TestEntity")
public abstract class TestEntity implements Serializable {

	public static TestEntity toDTO(String json) {
		return ObjectMapperUtil.readValue(TestEntity.class, json);
	}

	public static TestEntity unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TestEntity.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateCreated() {
		if (_dateCreatedSupplier != null) {
			dateCreated = _dateCreatedSupplier.get();

			_dateCreatedSupplier = null;
		}

		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;

		_dateCreatedSupplier = null;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		_dateCreatedSupplier = () -> {
			try {
				return dateCreatedUnsafeSupplier.get();
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
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
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
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getDocumentId() {
		if (_documentIdSupplier != null) {
			documentId = _documentIdSupplier.get();

			_documentIdSupplier = null;
		}

		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;

		_documentIdSupplier = null;
	}

	@JsonIgnore
	public void setDocumentId(
		UnsafeSupplier<Long, Exception> documentIdUnsafeSupplier) {

		_documentIdSupplier = () -> {
			try {
				return documentIdUnsafeSupplier.get();
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
	protected Long documentId;

	@JsonIgnore
	private Supplier<Long> _documentIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getJsonProperty() {
		if (_jsonPropertySupplier != null) {
			jsonProperty = _jsonPropertySupplier.get();

			_jsonPropertySupplier = null;
		}

		return jsonProperty;
	}

	public void setJsonProperty(String jsonProperty) {
		this.jsonProperty = jsonProperty;

		_jsonPropertySupplier = null;
	}

	@JsonIgnore
	public void setJsonProperty(
		UnsafeSupplier<String, Exception> jsonPropertyUnsafeSupplier) {

		_jsonPropertySupplier = () -> {
			try {
				return jsonPropertyUnsafeSupplier.get();
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
	@XmlElement(name = "xmlProperty")
	protected String jsonProperty;

	@JsonIgnore
	private Supplier<String> _jsonPropertySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public NestedTestEntity getNestedTestEntity() {
		if (_nestedTestEntitySupplier != null) {
			nestedTestEntity = _nestedTestEntitySupplier.get();

			_nestedTestEntitySupplier = null;
		}

		return nestedTestEntity;
	}

	public void setNestedTestEntity(NestedTestEntity nestedTestEntity) {
		this.nestedTestEntity = nestedTestEntity;

		_nestedTestEntitySupplier = null;
	}

	@JsonIgnore
	public void setNestedTestEntity(
		UnsafeSupplier<NestedTestEntity, Exception>
			nestedTestEntityUnsafeSupplier) {

		_nestedTestEntitySupplier = () -> {
			try {
				return nestedTestEntityUnsafeSupplier.get();
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
	protected NestedTestEntity nestedTestEntity;

	@JsonIgnore
	private Supplier<NestedTestEntity> _nestedTestEntitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSelf() {
		if (_selfSupplier != null) {
			self = _selfSupplier.get();

			_selfSupplier = null;
		}

		return self;
	}

	public void setSelf(String self) {
		this.self = self;

		_selfSupplier = null;
	}

	@JsonIgnore
	public void setSelf(UnsafeSupplier<String, Exception> selfUnsafeSupplier) {
		_selfSupplier = () -> {
			try {
				return selfUnsafeSupplier.get();
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
	protected String self;

	@JsonIgnore
	private Supplier<String> _selfSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public StringTestEntity[] getStringTestEntities() {
		if (_stringTestEntitiesSupplier != null) {
			stringTestEntities = _stringTestEntitiesSupplier.get();

			_stringTestEntitiesSupplier = null;
		}

		return stringTestEntities;
	}

	public void setStringTestEntities(StringTestEntity[] stringTestEntities) {
		this.stringTestEntities = stringTestEntities;

		_stringTestEntitiesSupplier = null;
	}

	@JsonIgnore
	public void setStringTestEntities(
		UnsafeSupplier<StringTestEntity[], Exception>
			stringTestEntitiesUnsafeSupplier) {

		_stringTestEntitiesSupplier = () -> {
			try {
				return stringTestEntitiesUnsafeSupplier.get();
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
	protected StringTestEntity[] stringTestEntities;

	@JsonIgnore
	private Supplier<StringTestEntity[]> _stringTestEntitiesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public StringTestEntity getStringTestEntity() {
		if (_stringTestEntitySupplier != null) {
			stringTestEntity = _stringTestEntitySupplier.get();

			_stringTestEntitySupplier = null;
		}

		return stringTestEntity;
	}

	public void setStringTestEntity(StringTestEntity stringTestEntity) {
		this.stringTestEntity = stringTestEntity;

		_stringTestEntitySupplier = null;
	}

	@JsonIgnore
	public void setStringTestEntity(
		UnsafeSupplier<StringTestEntity, Exception>
			stringTestEntityUnsafeSupplier) {

		_stringTestEntitySupplier = () -> {
			try {
				return stringTestEntityUnsafeSupplier.get();
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
	protected StringTestEntity stringTestEntity;

	@JsonIgnore
	private Supplier<StringTestEntity> _stringTestEntitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public TestEntity getTestEntities() {
		if (_testEntitiesSupplier != null) {
			testEntities = _testEntitiesSupplier.get();

			_testEntitiesSupplier = null;
		}

		return testEntities;
	}

	public void setTestEntities(TestEntity testEntities) {
		this.testEntities = testEntities;

		_testEntitiesSupplier = null;
	}

	@JsonIgnore
	public void setTestEntities(
		UnsafeSupplier<TestEntity, Exception> testEntitiesUnsafeSupplier) {

		_testEntitiesSupplier = () -> {
			try {
				return testEntitiesUnsafeSupplier.get();
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
	protected TestEntity testEntities;

	@JsonIgnore
	private Supplier<TestEntity> _testEntitiesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TestEntity)) {
			return false;
		}

		TestEntity testEntity = (TestEntity)object;

		return Objects.equals(toString(), testEntity.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

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

		Long documentId = getDocumentId();

		if (documentId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentId\": ");

			sb.append(documentId);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String jsonProperty = getJsonProperty();

		if (jsonProperty != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jsonProperty\": ");

			sb.append("\"");

			sb.append(_escape(jsonProperty));

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

		NestedTestEntity nestedTestEntity = getNestedTestEntity();

		if (nestedTestEntity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedTestEntity\": ");

			sb.append(String.valueOf(nestedTestEntity));
		}

		String self = getSelf();

		if (self != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"self\": ");

			sb.append("\"");

			sb.append(_escape(self));

			sb.append("\"");
		}

		StringTestEntity[] stringTestEntities = getStringTestEntities();

		if (stringTestEntities != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stringTestEntities\": ");

			sb.append("[");

			for (int i = 0; i < stringTestEntities.length; i++) {
				sb.append(stringTestEntities[i]);

				if ((i + 1) < stringTestEntities.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		StringTestEntity stringTestEntity = getStringTestEntity();

		if (stringTestEntity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stringTestEntity\": ");

			sb.append(stringTestEntity);
		}

		TestEntity testEntities = getTestEntities();

		if (testEntities != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"testEntities\": ");

			sb.append(String.valueOf(testEntities));
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
		defaultValue = "com.liferay.portal.tools.rest.builder.test.dto.v1_0.TestEntity",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		CHILD_TEST_ENTITY1("ChildTestEntity1"),
		CHILD_TEST_ENTITY2("ChildTestEntity2"),
		CHILD_TEST_ENTITY3("ChildTestEntity3");

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

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
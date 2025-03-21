/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("ObjectFolderItem")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectFolderItem")
public class ObjectFolderItem implements Serializable {

	public static ObjectFolderItem toDTO(String json) {
		return ObjectMapperUtil.readValue(ObjectFolderItem.class, json);
	}

	public static ObjectFolderItem unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ObjectFolderItem.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getLinkedObjectDefinition() {
		if (_linkedObjectDefinitionSupplier != null) {
			linkedObjectDefinition = _linkedObjectDefinitionSupplier.get();

			_linkedObjectDefinitionSupplier = null;
		}

		return linkedObjectDefinition;
	}

	public void setLinkedObjectDefinition(Boolean linkedObjectDefinition) {
		this.linkedObjectDefinition = linkedObjectDefinition;

		_linkedObjectDefinitionSupplier = null;
	}

	@JsonIgnore
	public void setLinkedObjectDefinition(
		UnsafeSupplier<Boolean, Exception>
			linkedObjectDefinitionUnsafeSupplier) {

		_linkedObjectDefinitionSupplier = () -> {
			try {
				return linkedObjectDefinitionUnsafeSupplier.get();
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
	protected Boolean linkedObjectDefinition;

	@JsonIgnore
	private Supplier<Boolean> _linkedObjectDefinitionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectDefinition getObjectDefinition() {
		if (_objectDefinitionSupplier != null) {
			objectDefinition = _objectDefinitionSupplier.get();

			_objectDefinitionSupplier = null;
		}

		return objectDefinition;
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition) {
		this.objectDefinition = objectDefinition;

		_objectDefinitionSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinition(
		UnsafeSupplier<ObjectDefinition, Exception>
			objectDefinitionUnsafeSupplier) {

		_objectDefinitionSupplier = () -> {
			try {
				return objectDefinitionUnsafeSupplier.get();
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
	protected ObjectDefinition objectDefinition;

	@JsonIgnore
	private Supplier<ObjectDefinition> _objectDefinitionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectDefinitionExternalReferenceCode() {
		if (_objectDefinitionExternalReferenceCodeSupplier != null) {
			objectDefinitionExternalReferenceCode =
				_objectDefinitionExternalReferenceCodeSupplier.get();

			_objectDefinitionExternalReferenceCodeSupplier = null;
		}

		return objectDefinitionExternalReferenceCode;
	}

	public void setObjectDefinitionExternalReferenceCode(
		String objectDefinitionExternalReferenceCode) {

		this.objectDefinitionExternalReferenceCode =
			objectDefinitionExternalReferenceCode;

		_objectDefinitionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCodeUnsafeSupplier) {

		_objectDefinitionExternalReferenceCodeSupplier = () -> {
			try {
				return objectDefinitionExternalReferenceCodeUnsafeSupplier.
					get();
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
	protected String objectDefinitionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _objectDefinitionExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getPositionX() {
		if (_positionXSupplier != null) {
			positionX = _positionXSupplier.get();

			_positionXSupplier = null;
		}

		return positionX;
	}

	public void setPositionX(Integer positionX) {
		this.positionX = positionX;

		_positionXSupplier = null;
	}

	@JsonIgnore
	public void setPositionX(
		UnsafeSupplier<Integer, Exception> positionXUnsafeSupplier) {

		_positionXSupplier = () -> {
			try {
				return positionXUnsafeSupplier.get();
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
	protected Integer positionX;

	@JsonIgnore
	private Supplier<Integer> _positionXSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getPositionY() {
		if (_positionYSupplier != null) {
			positionY = _positionYSupplier.get();

			_positionYSupplier = null;
		}

		return positionY;
	}

	public void setPositionY(Integer positionY) {
		this.positionY = positionY;

		_positionYSupplier = null;
	}

	@JsonIgnore
	public void setPositionY(
		UnsafeSupplier<Integer, Exception> positionYUnsafeSupplier) {

		_positionYSupplier = () -> {
			try {
				return positionYUnsafeSupplier.get();
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
	protected Integer positionY;

	@JsonIgnore
	private Supplier<Integer> _positionYSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectFolderItem)) {
			return false;
		}

		ObjectFolderItem objectFolderItem = (ObjectFolderItem)object;

		return Objects.equals(toString(), objectFolderItem.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean linkedObjectDefinition = getLinkedObjectDefinition();

		if (linkedObjectDefinition != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"linkedObjectDefinition\": ");

			sb.append(linkedObjectDefinition);
		}

		ObjectDefinition objectDefinition = getObjectDefinition();

		if (objectDefinition != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinition\": ");

			sb.append(String.valueOf(objectDefinition));
		}

		String objectDefinitionExternalReferenceCode =
			getObjectDefinitionExternalReferenceCode();

		if (objectDefinitionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectDefinitionExternalReferenceCode));

			sb.append("\"");
		}

		Integer positionX = getPositionX();

		if (positionX != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"positionX\": ");

			sb.append(positionX);
		}

		Integer positionY = getPositionY();

		if (positionY != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"positionY\": ");

			sb.append(positionY);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.admin.rest.dto.v1_0.ObjectFolderItem",
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
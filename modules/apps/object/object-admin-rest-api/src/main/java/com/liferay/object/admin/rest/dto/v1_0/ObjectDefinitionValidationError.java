/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
@GraphQLName("ObjectDefinitionValidationError")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectDefinitionValidationError")
public class ObjectDefinitionValidationError implements Serializable {

	public static ObjectDefinitionValidationError toDTO(String json) {
		return ObjectMapperUtil.readValue(
			ObjectDefinitionValidationError.class, json);
	}

	public static ObjectDefinitionValidationError unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ObjectDefinitionValidationError.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getErrorMessage() {
		if (_errorMessageSupplier != null) {
			errorMessage = _errorMessageSupplier.get();

			_errorMessageSupplier = null;
		}

		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;

		_errorMessageSupplier = null;
	}

	@JsonIgnore
	public void setErrorMessage(
		UnsafeSupplier<String, Exception> errorMessageUnsafeSupplier) {

		_errorMessageSupplier = () -> {
			try {
				return errorMessageUnsafeSupplier.get();
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
	protected String errorMessage;

	@JsonIgnore
	private Supplier<String> _errorMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExceptionClassName() {
		if (_exceptionClassNameSupplier != null) {
			exceptionClassName = _exceptionClassNameSupplier.get();

			_exceptionClassNameSupplier = null;
		}

		return exceptionClassName;
	}

	public void setExceptionClassName(String exceptionClassName) {
		this.exceptionClassName = exceptionClassName;

		_exceptionClassNameSupplier = null;
	}

	@JsonIgnore
	public void setExceptionClassName(
		UnsafeSupplier<String, Exception> exceptionClassNameUnsafeSupplier) {

		_exceptionClassNameSupplier = () -> {
			try {
				return exceptionClassNameUnsafeSupplier.get();
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
	protected String exceptionClassName;

	@JsonIgnore
	private Supplier<String> _exceptionClassNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectDefinitionName() {
		if (_objectDefinitionNameSupplier != null) {
			objectDefinitionName = _objectDefinitionNameSupplier.get();

			_objectDefinitionNameSupplier = null;
		}

		return objectDefinitionName;
	}

	public void setObjectDefinitionName(String objectDefinitionName) {
		this.objectDefinitionName = objectDefinitionName;

		_objectDefinitionNameSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionName(
		UnsafeSupplier<String, Exception> objectDefinitionNameUnsafeSupplier) {

		_objectDefinitionNameSupplier = () -> {
			try {
				return objectDefinitionNameUnsafeSupplier.get();
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
	protected String objectDefinitionName;

	@JsonIgnore
	private Supplier<String> _objectDefinitionNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectFieldName() {
		if (_objectFieldNameSupplier != null) {
			objectFieldName = _objectFieldNameSupplier.get();

			_objectFieldNameSupplier = null;
		}

		return objectFieldName;
	}

	public void setObjectFieldName(String objectFieldName) {
		this.objectFieldName = objectFieldName;

		_objectFieldNameSupplier = null;
	}

	@JsonIgnore
	public void setObjectFieldName(
		UnsafeSupplier<String, Exception> objectFieldNameUnsafeSupplier) {

		_objectFieldNameSupplier = () -> {
			try {
				return objectFieldNameUnsafeSupplier.get();
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
	protected String objectFieldName;

	@JsonIgnore
	private Supplier<String> _objectFieldNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectFieldValue() {
		if (_objectFieldValueSupplier != null) {
			objectFieldValue = _objectFieldValueSupplier.get();

			_objectFieldValueSupplier = null;
		}

		return objectFieldValue;
	}

	public void setObjectFieldValue(String objectFieldValue) {
		this.objectFieldValue = objectFieldValue;

		_objectFieldValueSupplier = null;
	}

	@JsonIgnore
	public void setObjectFieldValue(
		UnsafeSupplier<String, Exception> objectFieldValueUnsafeSupplier) {

		_objectFieldValueSupplier = () -> {
			try {
				return objectFieldValueUnsafeSupplier.get();
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
	protected String objectFieldValue;

	@JsonIgnore
	private Supplier<String> _objectFieldValueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectDefinitionValidationError)) {
			return false;
		}

		ObjectDefinitionValidationError objectDefinitionValidationError =
			(ObjectDefinitionValidationError)object;

		return Objects.equals(
			toString(), objectDefinitionValidationError.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String errorMessage = getErrorMessage();

		if (errorMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(_escape(errorMessage));

			sb.append("\"");
		}

		String exceptionClassName = getExceptionClassName();

		if (exceptionClassName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exceptionClassName\": ");

			sb.append("\"");

			sb.append(_escape(exceptionClassName));

			sb.append("\"");
		}

		String objectDefinitionName = getObjectDefinitionName();

		if (objectDefinitionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionName\": ");

			sb.append("\"");

			sb.append(_escape(objectDefinitionName));

			sb.append("\"");
		}

		String objectFieldName = getObjectFieldName();

		if (objectFieldName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFieldName\": ");

			sb.append("\"");

			sb.append(_escape(objectFieldName));

			sb.append("\"");
		}

		String objectFieldValue = getObjectFieldValue();

		if (objectFieldValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFieldValue\": ");

			sb.append("\"");

			sb.append(_escape(objectFieldValue));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.admin.rest.dto.v1_0.ObjectDefinitionValidationError",
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
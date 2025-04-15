/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@GraphQLName("AuditFieldChange")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AuditFieldChange")
public class AuditFieldChange implements Serializable {

	public static AuditFieldChange toDTO(String json) {
		return ObjectMapperUtil.readValue(AuditFieldChange.class, json);
	}

	public static AuditFieldChange unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AuditFieldChange.class, json);
	}

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
	public Object getNewValue() {
		if (_newValueSupplier != null) {
			newValue = _newValueSupplier.get();

			_newValueSupplier = null;
		}

		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;

		_newValueSupplier = null;
	}

	@JsonIgnore
	public void setNewValue(
		UnsafeSupplier<Object, Exception> newValueUnsafeSupplier) {

		_newValueSupplier = () -> {
			try {
				return newValueUnsafeSupplier.get();
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
	protected Object newValue;

	@JsonIgnore
	private Supplier<Object> _newValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getOldValue() {
		if (_oldValueSupplier != null) {
			oldValue = _oldValueSupplier.get();

			_oldValueSupplier = null;
		}

		return oldValue;
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;

		_oldValueSupplier = null;
	}

	@JsonIgnore
	public void setOldValue(
		UnsafeSupplier<Object, Exception> oldValueUnsafeSupplier) {

		_oldValueSupplier = () -> {
			try {
				return oldValueUnsafeSupplier.get();
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
	protected Object oldValue;

	@JsonIgnore
	private Supplier<Object> _oldValueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AuditFieldChange)) {
			return false;
		}

		AuditFieldChange auditFieldChange = (AuditFieldChange)object;

		return Objects.equals(toString(), auditFieldChange.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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

		Object newValue = getNewValue();

		if (newValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"newValue\": ");

			if (newValue instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)newValue));
			}
			else if (newValue instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)newValue));
				sb.append("\"");
			}
			else {
				sb.append(newValue);
			}
		}

		Object oldValue = getOldValue();

		if (oldValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oldValue\": ");

			if (oldValue instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)oldValue));
			}
			else if (oldValue instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)oldValue));
				sb.append("\"");
			}
			else {
				sb.append(oldValue);
			}
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.rest.dto.v1_0.AuditFieldChange",
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
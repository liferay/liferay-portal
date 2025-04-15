/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.dto.v1_0;

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
 * @author Matija Petanjek
 * @generated
 */
@Generated("")
@GraphQLName("Mapping")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Mapping")
public class Mapping implements Serializable {

	public static Mapping toDTO(String json) {
		return ObjectMapperUtil.readValue(Mapping.class, json);
	}

	public static Mapping unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Mapping.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalFieldName() {
		if (_externalFieldNameSupplier != null) {
			externalFieldName = _externalFieldNameSupplier.get();

			_externalFieldNameSupplier = null;
		}

		return externalFieldName;
	}

	public void setExternalFieldName(String externalFieldName) {
		this.externalFieldName = externalFieldName;

		_externalFieldNameSupplier = null;
	}

	@JsonIgnore
	public void setExternalFieldName(
		UnsafeSupplier<String, Exception> externalFieldNameUnsafeSupplier) {

		_externalFieldNameSupplier = () -> {
			try {
				return externalFieldNameUnsafeSupplier.get();
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
	protected String externalFieldName;

	@JsonIgnore
	private Supplier<String> _externalFieldNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalFieldType() {
		if (_externalFieldTypeSupplier != null) {
			externalFieldType = _externalFieldTypeSupplier.get();

			_externalFieldTypeSupplier = null;
		}

		return externalFieldType;
	}

	public void setExternalFieldType(String externalFieldType) {
		this.externalFieldType = externalFieldType;

		_externalFieldTypeSupplier = null;
	}

	@JsonIgnore
	public void setExternalFieldType(
		UnsafeSupplier<String, Exception> externalFieldTypeUnsafeSupplier) {

		_externalFieldTypeSupplier = () -> {
			try {
				return externalFieldTypeUnsafeSupplier.get();
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
	protected String externalFieldType;

	@JsonIgnore
	private Supplier<String> _externalFieldTypeSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getInternalFieldName() {
		if (_internalFieldNameSupplier != null) {
			internalFieldName = _internalFieldNameSupplier.get();

			_internalFieldNameSupplier = null;
		}

		return internalFieldName;
	}

	public void setInternalFieldName(String internalFieldName) {
		this.internalFieldName = internalFieldName;

		_internalFieldNameSupplier = null;
	}

	@JsonIgnore
	public void setInternalFieldName(
		UnsafeSupplier<String, Exception> internalFieldNameUnsafeSupplier) {

		_internalFieldNameSupplier = () -> {
			try {
				return internalFieldNameUnsafeSupplier.get();
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
	protected String internalFieldName;

	@JsonIgnore
	private Supplier<String> _internalFieldNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getInternalFieldType() {
		if (_internalFieldTypeSupplier != null) {
			internalFieldType = _internalFieldTypeSupplier.get();

			_internalFieldTypeSupplier = null;
		}

		return internalFieldType;
	}

	public void setInternalFieldType(String internalFieldType) {
		this.internalFieldType = internalFieldType;

		_internalFieldTypeSupplier = null;
	}

	@JsonIgnore
	public void setInternalFieldType(
		UnsafeSupplier<String, Exception> internalFieldTypeUnsafeSupplier) {

		_internalFieldTypeSupplier = () -> {
			try {
				return internalFieldTypeUnsafeSupplier.get();
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
	protected String internalFieldType;

	@JsonIgnore
	private Supplier<String> _internalFieldTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getPlanId() {
		if (_planIdSupplier != null) {
			planId = _planIdSupplier.get();

			_planIdSupplier = null;
		}

		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;

		_planIdSupplier = null;
	}

	@JsonIgnore
	public void setPlanId(
		UnsafeSupplier<Long, Exception> planIdUnsafeSupplier) {

		_planIdSupplier = () -> {
			try {
				return planIdUnsafeSupplier.get();
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
	protected Long planId;

	@JsonIgnore
	private Supplier<Long> _planIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getScript() {
		if (_scriptSupplier != null) {
			script = _scriptSupplier.get();

			_scriptSupplier = null;
		}

		return script;
	}

	public void setScript(String script) {
		this.script = script;

		_scriptSupplier = null;
	}

	@JsonIgnore
	public void setScript(
		UnsafeSupplier<String, Exception> scriptUnsafeSupplier) {

		_scriptSupplier = () -> {
			try {
				return scriptUnsafeSupplier.get();
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
	protected String script;

	@JsonIgnore
	private Supplier<String> _scriptSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Mapping)) {
			return false;
		}

		Mapping mapping = (Mapping)object;

		return Objects.equals(toString(), mapping.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String externalFieldName = getExternalFieldName();

		if (externalFieldName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalFieldName\": ");

			sb.append("\"");

			sb.append(_escape(externalFieldName));

			sb.append("\"");
		}

		String externalFieldType = getExternalFieldType();

		if (externalFieldType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalFieldType\": ");

			sb.append("\"");

			sb.append(_escape(externalFieldType));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String internalFieldName = getInternalFieldName();

		if (internalFieldName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"internalFieldName\": ");

			sb.append("\"");

			sb.append(_escape(internalFieldName));

			sb.append("\"");
		}

		String internalFieldType = getInternalFieldType();

		if (internalFieldType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"internalFieldType\": ");

			sb.append("\"");

			sb.append(_escape(internalFieldType));

			sb.append("\"");
		}

		Long planId = getPlanId();

		if (planId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"planId\": ");

			sb.append(planId);
		}

		String script = getScript();

		if (script != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"script\": ");

			sb.append("\"");

			sb.append(_escape(script));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.batch.planner.rest.dto.v1_0.Mapping",
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
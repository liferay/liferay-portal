/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.dto.v1_0;

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

import jakarta.validation.constraints.NotEmpty;

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
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Something the tool needs identified before it can be invoked, typically a path parameter naming another entity: creating a category under `/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories` needs a vocabulary. `toolName` and `toolSetName`, when present, name the operation that lists that entity, so no further search is needed. `note`, when present, says what the parameter accepts and where to get it.",
	value = "Prerequisite"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Something the tool needs identified before it can be invoked, typically a path parameter naming another entity: creating a category under `/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories` needs a vocabulary. `toolName` and `toolSetName`, when present, name the operation that lists that entity, so no further search is needed. `note`, when present, says what the parameter accepts and where to get it.",
	requiredProperties = {"parameter"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Prerequisite")
public class Prerequisite implements Serializable {

	public static Prerequisite toDTO(String json) {
		return ObjectMapperUtil.readValue(Prerequisite.class, json);
	}

	public static Prerequisite unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Prerequisite.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "What the parameter accepts and where to get it. Present only where that changes what you should do, so read it before invoking the operation named in `toolName`."
	)
	public String getNote() {
		if (_noteSupplier != null) {
			note = _noteSupplier.get();

			_noteSupplier = null;
		}

		return note;
	}

	public void setNote(String note) {
		this.note = note;

		_noteSupplier = null;
	}

	@JsonIgnore
	public void setNote(UnsafeSupplier<String, Exception> noteUnsafeSupplier) {
		_noteSupplier = () -> {
			try {
				return noteUnsafeSupplier.get();
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
		description = "What the parameter accepts and where to get it. Present only where that changes what you should do, so read it before invoking the operation named in `toolName`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String note;

	@JsonIgnore
	private Supplier<String> _noteSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The argument that has to be resolved before the tool can be invoked, named exactly as the tool accepts it, for example `taxonomyVocabularyId`."
	)
	public String getParameter() {
		if (_parameterSupplier != null) {
			parameter = _parameterSupplier.get();

			_parameterSupplier = null;
		}

		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;

		_parameterSupplier = null;
	}

	@JsonIgnore
	public void setParameter(
		UnsafeSupplier<String, Exception> parameterUnsafeSupplier) {

		_parameterSupplier = () -> {
			try {
				return parameterUnsafeSupplier.get();
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
		description = "The argument that has to be resolved before the tool can be invoked, named exactly as the tool accepts it, for example `taxonomyVocabularyId`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String parameter;

	@JsonIgnore
	private Supplier<String> _parameterSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The operation that lists the entity this parameter identifies. Pass it verbatim as `toolName` to `postToolSetToolSetNameToolInvoke`, alongside `toolSetName`. Absent when no single listing operation resolves the parameter."
	)
	public String getToolName() {
		if (_toolNameSupplier != null) {
			toolName = _toolNameSupplier.get();

			_toolNameSupplier = null;
		}

		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;

		_toolNameSupplier = null;
	}

	@JsonIgnore
	public void setToolName(
		UnsafeSupplier<String, Exception> toolNameUnsafeSupplier) {

		_toolNameSupplier = () -> {
			try {
				return toolNameUnsafeSupplier.get();
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
		description = "The operation that lists the entity this parameter identifies. Pass it verbatim as `toolName` to `postToolSetToolSetNameToolInvoke`, alongside `toolSetName`. Absent when no single listing operation resolves the parameter."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String toolName;

	@JsonIgnore
	private Supplier<String> _toolNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Tool set that exposes the operation named in `toolName`."
	)
	public String getToolSetName() {
		if (_toolSetNameSupplier != null) {
			toolSetName = _toolSetNameSupplier.get();

			_toolSetNameSupplier = null;
		}

		return toolSetName;
	}

	public void setToolSetName(String toolSetName) {
		this.toolSetName = toolSetName;

		_toolSetNameSupplier = null;
	}

	@JsonIgnore
	public void setToolSetName(
		UnsafeSupplier<String, Exception> toolSetNameUnsafeSupplier) {

		_toolSetNameSupplier = () -> {
			try {
				return toolSetNameUnsafeSupplier.get();
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
		description = "Tool set that exposes the operation named in `toolName`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String toolSetName;

	@JsonIgnore
	private Supplier<String> _toolSetNameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Prerequisite)) {
			return false;
		}

		Prerequisite prerequisite = (Prerequisite)object;

		return Objects.equals(toString(), prerequisite.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String note = getNote();

		if (note != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"note\": ");

			sb.append("\"");

			sb.append(_escape(note));

			sb.append("\"");
		}

		String parameter = getParameter();

		if (parameter != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameter\": ");

			sb.append("\"");

			sb.append(_escape(parameter));

			sb.append("\"");
		}

		String toolName = getToolName();

		if (toolName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"toolName\": ");

			sb.append("\"");

			sb.append(_escape(toolName));

			sb.append("\"");
		}

		String toolSetName = getToolSetName();

		if (toolSetName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"toolSetName\": ");

			sb.append("\"");

			sb.append(_escape(toolSetName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.mcp.server.rest.dto.v1_0.Prerequisite",
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
// LIFERAY-REST-BUILDER-HASH:-304611955
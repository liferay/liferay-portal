/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.headless.delivery.dto.v1_0.Comment;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("SystemProperties")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SystemProperties")
public class SystemProperties implements Serializable {

	public static SystemProperties toDTO(String json) {
		return ObjectMapperUtil.readValue(SystemProperties.class, json);
	}

	public static SystemProperties unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SystemProperties.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public CollaboratorBrief getCollaboratorBrief() {
		if (_collaboratorBriefSupplier != null) {
			collaboratorBrief = _collaboratorBriefSupplier.get();

			_collaboratorBriefSupplier = null;
		}

		return collaboratorBrief;
	}

	public void setCollaboratorBrief(CollaboratorBrief collaboratorBrief) {
		this.collaboratorBrief = collaboratorBrief;

		_collaboratorBriefSupplier = null;
	}

	@JsonIgnore
	public void setCollaboratorBrief(
		UnsafeSupplier<CollaboratorBrief, Exception>
			collaboratorBriefUnsafeSupplier) {

		_collaboratorBriefSupplier = () -> {
			try {
				return collaboratorBriefUnsafeSupplier.get();
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
	protected CollaboratorBrief collaboratorBrief;

	@JsonIgnore
	private Supplier<CollaboratorBrief> _collaboratorBriefSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional field with the comments associated with this object entry, can be embedded with nestedFields"
	)
	@Valid
	public Comment[] getComments() {
		if (_commentsSupplier != null) {
			comments = _commentsSupplier.get();

			_commentsSupplier = null;
		}

		return comments;
	}

	public void setComments(Comment[] comments) {
		this.comments = comments;

		_commentsSupplier = null;
	}

	@JsonIgnore
	public void setComments(
		UnsafeSupplier<Comment[], Exception> commentsUnsafeSupplier) {

		_commentsSupplier = () -> {
			try {
				return commentsUnsafeSupplier.get();
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
		description = "Optional field with the comments associated with this object entry, can be embedded with nestedFields"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Comment[] comments;

	@JsonIgnore
	private Supplier<Comment[]> _commentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectDefinitionBrief getObjectDefinitionBrief() {
		if (_objectDefinitionBriefSupplier != null) {
			objectDefinitionBrief = _objectDefinitionBriefSupplier.get();

			_objectDefinitionBriefSupplier = null;
		}

		return objectDefinitionBrief;
	}

	public void setObjectDefinitionBrief(
		ObjectDefinitionBrief objectDefinitionBrief) {

		this.objectDefinitionBrief = objectDefinitionBrief;

		_objectDefinitionBriefSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionBrief(
		UnsafeSupplier<ObjectDefinitionBrief, Exception>
			objectDefinitionBriefUnsafeSupplier) {

		_objectDefinitionBriefSupplier = () -> {
			try {
				return objectDefinitionBriefUnsafeSupplier.get();
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
	protected ObjectDefinitionBrief objectDefinitionBrief;

	@JsonIgnore
	private Supplier<ObjectDefinitionBrief> _objectDefinitionBriefSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public com.liferay.portal.vulcan.scope.Scope getScope() {
		if (_scopeSupplier != null) {
			scope = _scopeSupplier.get();

			_scopeSupplier = null;
		}

		return scope;
	}

	public void setScope(com.liferay.portal.vulcan.scope.Scope scope) {
		this.scope = scope;

		_scopeSupplier = null;
	}

	@JsonIgnore
	public void setScope(
		UnsafeSupplier<com.liferay.portal.vulcan.scope.Scope, Exception>
			scopeUnsafeSupplier) {

		_scopeSupplier = () -> {
			try {
				return scopeUnsafeSupplier.get();
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
	protected com.liferay.portal.vulcan.scope.Scope scope;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.scope.Scope> _scopeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Version getVersion() {
		if (_versionSupplier != null) {
			version = _versionSupplier.get();

			_versionSupplier = null;
		}

		return version;
	}

	public void setVersion(Version version) {
		this.version = version;

		_versionSupplier = null;
	}

	@JsonIgnore
	public void setVersion(
		UnsafeSupplier<Version, Exception> versionUnsafeSupplier) {

		_versionSupplier = () -> {
			try {
				return versionUnsafeSupplier.get();
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
	protected Version version;

	@JsonIgnore
	private Supplier<Version> _versionSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SystemProperties)) {
			return false;
		}

		SystemProperties systemProperties = (SystemProperties)object;

		return Objects.equals(toString(), systemProperties.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		CollaboratorBrief collaboratorBrief = getCollaboratorBrief();

		if (collaboratorBrief != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collaboratorBrief\": ");

			sb.append(String.valueOf(collaboratorBrief));
		}

		Comment[] comments = getComments();

		if (comments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append("[");

			for (int i = 0; i < comments.length; i++) {
				sb.append(comments[i]);

				if ((i + 1) < comments.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ObjectDefinitionBrief objectDefinitionBrief =
			getObjectDefinitionBrief();

		if (objectDefinitionBrief != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionBrief\": ");

			sb.append(String.valueOf(objectDefinitionBrief));
		}

		com.liferay.portal.vulcan.scope.Scope scope = getScope();

		if (scope != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(scope);
		}

		Version version = getVersion();

		if (version != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append(String.valueOf(version));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.rest.dto.v1_0.SystemProperties",
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
// LIFERAY-REST-BUILDER-HASH:-528641588
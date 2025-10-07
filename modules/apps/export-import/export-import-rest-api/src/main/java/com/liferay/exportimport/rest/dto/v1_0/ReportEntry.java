/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.headless.delivery.dto.v1_0.Creator;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName("ReportEntry")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ReportEntry")
public class ReportEntry implements Serializable {

	public static ReportEntry toDTO(String json) {
		return ObjectMapperUtil.readValue(ReportEntry.class, json);
	}

	public static ReportEntry unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ReportEntry.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The reported item's external reference code."
	)
	public String getClassExternalReferenceCode() {
		if (_classExternalReferenceCodeSupplier != null) {
			classExternalReferenceCode =
				_classExternalReferenceCodeSupplier.get();

			_classExternalReferenceCodeSupplier = null;
		}

		return classExternalReferenceCode;
	}

	public void setClassExternalReferenceCode(
		String classExternalReferenceCode) {

		this.classExternalReferenceCode = classExternalReferenceCode;

		_classExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setClassExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			classExternalReferenceCodeUnsafeSupplier) {

		_classExternalReferenceCodeSupplier = () -> {
			try {
				return classExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The reported item's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String classExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _classExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The reported item's class name id."
	)
	public Long getClassNameId() {
		if (_classNameIdSupplier != null) {
			classNameId = _classNameIdSupplier.get();

			_classNameIdSupplier = null;
		}

		return classNameId;
	}

	public void setClassNameId(Long classNameId) {
		this.classNameId = classNameId;

		_classNameIdSupplier = null;
	}

	@JsonIgnore
	public void setClassNameId(
		UnsafeSupplier<Long, Exception> classNameIdUnsafeSupplier) {

		_classNameIdSupplier = () -> {
			try {
				return classNameIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The reported item's class name id.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long classNameId;

	@JsonIgnore
	private Supplier<Long> _classNameIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The reported item's class primary key."
	)
	public Long getClassPK() {
		if (_classPKSupplier != null) {
			classPK = _classPKSupplier.get();

			_classPKSupplier = null;
		}

		return classPK;
	}

	public void setClassPK(Long classPK) {
		this.classPK = classPK;

		_classPKSupplier = null;
	}

	@JsonIgnore
	public void setClassPK(
		UnsafeSupplier<Long, Exception> classPKUnsafeSupplier) {

		_classPKSupplier = () -> {
			try {
				return classPKUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The reported item's class primary key.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long classPK;

	@JsonIgnore
	private Supplier<Long> _classPKSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The report entry's configuration ID."
	)
	public Long getConfigurationId() {
		if (_configurationIdSupplier != null) {
			configurationId = _configurationIdSupplier.get();

			_configurationIdSupplier = null;
		}

		return configurationId;
	}

	public void setConfigurationId(Long configurationId) {
		this.configurationId = configurationId;

		_configurationIdSupplier = null;
	}

	@JsonIgnore
	public void setConfigurationId(
		UnsafeSupplier<Long, Exception> configurationIdUnsafeSupplier) {

		_configurationIdSupplier = () -> {
			try {
				return configurationIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The report entry's configuration ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long configurationId;

	@JsonIgnore
	private Supplier<Long> _configurationIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Creator getCreator() {
		if (_creatorSupplier != null) {
			creator = _creatorSupplier.get();

			_creatorSupplier = null;
		}

		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;

		_creatorSupplier = null;
	}

	@JsonIgnore
	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		_creatorSupplier = () -> {
			try {
				return creatorUnsafeSupplier.get();
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
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The report entry's creation date."
	)
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

	@GraphQLField(description = "The report entry's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The report entry's latest modification date."
	)
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

	@GraphQLField(description = "The report entry's latest modification date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The error message."
	)
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

	@GraphQLField(description = "The error message.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String errorMessage;

	@JsonIgnore
	private Supplier<String> _errorMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional field with the full stack trace of the error, can be embedded with nestedFields."
	)
	public String getErrorStacktrace() {
		if (_errorStacktraceSupplier != null) {
			errorStacktrace = _errorStacktraceSupplier.get();

			_errorStacktraceSupplier = null;
		}

		return errorStacktrace;
	}

	public void setErrorStacktrace(String errorStacktrace) {
		this.errorStacktrace = errorStacktrace;

		_errorStacktraceSupplier = null;
	}

	@JsonIgnore
	public void setErrorStacktrace(
		UnsafeSupplier<String, Exception> errorStacktraceUnsafeSupplier) {

		_errorStacktraceSupplier = () -> {
			try {
				return errorStacktraceUnsafeSupplier.get();
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
		description = "Optional field with the full stack trace of the error, can be embedded with nestedFields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String errorStacktrace;

	@JsonIgnore
	private Supplier<String> _errorStacktraceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The report entry's ID."
	)
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

	@GraphQLField(description = "The report entry's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The reported item's model name."
	)
	public String getModelName() {
		if (_modelNameSupplier != null) {
			modelName = _modelNameSupplier.get();

			_modelNameSupplier = null;
		}

		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;

		_modelNameSupplier = null;
	}

	@JsonIgnore
	public void setModelName(
		UnsafeSupplier<String, Exception> modelNameUnsafeSupplier) {

		_modelNameSupplier = () -> {
			try {
				return modelNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The reported item's model name.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String modelName;

	@JsonIgnore
	private Supplier<String> _modelNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The report entry's origin."
	)
	@Valid
	public Origin getOrigin() {
		if (_originSupplier != null) {
			origin = _originSupplier.get();

			_originSupplier = null;
		}

		return origin;
	}

	public void setOrigin(Origin origin) {
		this.origin = origin;

		_originSupplier = null;
	}

	@JsonIgnore
	public void setOrigin(
		UnsafeSupplier<Origin, Exception> originUnsafeSupplier) {

		_originSupplier = () -> {
			try {
				return originUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The report entry's origin.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Origin origin;

	@JsonIgnore
	private Supplier<Origin> _originSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The reported item's scope."
	)
	@Valid
	public Scope getScope() {
		if (_scopeSupplier != null) {
			scope = _scopeSupplier.get();

			_scopeSupplier = null;
		}

		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;

		_scopeSupplier = null;
	}

	@JsonIgnore
	public void setScope(UnsafeSupplier<Scope, Exception> scopeUnsafeSupplier) {
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

	@GraphQLField(description = "The reported item's scope.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Scope scope;

	@JsonIgnore
	private Supplier<Scope> _scopeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The report entry's 's status."
	)
	@Valid
	public Status getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(Status status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The report entry's 's status.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status status;

	@JsonIgnore
	private Supplier<Status> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The report entry's type."
	)
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
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

	@GraphQLField(description = "The report entry's type.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ReportEntry)) {
			return false;
		}

		ReportEntry reportEntry = (ReportEntry)object;

		return Objects.equals(toString(), reportEntry.toString());
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

		String classExternalReferenceCode = getClassExternalReferenceCode();

		if (classExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(classExternalReferenceCode));

			sb.append("\"");
		}

		Long classNameId = getClassNameId();

		if (classNameId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classNameId\": ");

			sb.append(classNameId);
		}

		Long classPK = getClassPK();

		if (classPK != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPK\": ");

			sb.append(classPK);
		}

		Long configurationId = getConfigurationId();

		if (configurationId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configurationId\": ");

			sb.append(configurationId);
		}

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(creator);
		}

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

		String errorStacktrace = getErrorStacktrace();

		if (errorStacktrace != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorStacktrace\": ");

			sb.append("\"");

			sb.append(_escape(errorStacktrace));

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

		String modelName = getModelName();

		if (modelName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modelName\": ");

			sb.append("\"");

			sb.append(_escape(modelName));

			sb.append("\"");
		}

		Origin origin = getOrigin();

		if (origin != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"origin\": ");

			sb.append(String.valueOf(origin));
		}

		Scope scope = getScope();

		if (scope != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(String.valueOf(scope));
		}

		Status status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(status));
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(String.valueOf(type));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.ReportEntry",
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
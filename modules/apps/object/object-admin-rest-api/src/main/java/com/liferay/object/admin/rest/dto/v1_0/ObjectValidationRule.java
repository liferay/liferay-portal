/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("ObjectValidationRule")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectValidationRule")
public class ObjectValidationRule implements Serializable {

	public static ObjectValidationRule toDTO(String json) {
		return ObjectMapperUtil.readValue(ObjectValidationRule.class, json);
	}

	public static ObjectValidationRule unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ObjectValidationRule.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getActive() {
		if (_activeSupplier != null) {
			active = _activeSupplier.get();

			_activeSupplier = null;
		}

		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;

		_activeSupplier = null;
	}

	@JsonIgnore
	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		_activeSupplier = () -> {
			try {
				return activeUnsafeSupplier.get();
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
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getEngine() {
		if (_engineSupplier != null) {
			engine = _engineSupplier.get();

			_engineSupplier = null;
		}

		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;

		_engineSupplier = null;
	}

	@JsonIgnore
	public void setEngine(
		UnsafeSupplier<String, Exception> engineUnsafeSupplier) {

		_engineSupplier = () -> {
			try {
				return engineUnsafeSupplier.get();
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
	protected String engine;

	@JsonIgnore
	private Supplier<String> _engineSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getEngineLabel() {
		if (_engineLabelSupplier != null) {
			engineLabel = _engineLabelSupplier.get();

			_engineLabelSupplier = null;
		}

		return engineLabel;
	}

	public void setEngineLabel(String engineLabel) {
		this.engineLabel = engineLabel;

		_engineLabelSupplier = null;
	}

	@JsonIgnore
	public void setEngineLabel(
		UnsafeSupplier<String, Exception> engineLabelUnsafeSupplier) {

		_engineLabelSupplier = () -> {
			try {
				return engineLabelUnsafeSupplier.get();
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
	protected String engineLabel;

	@JsonIgnore
	private Supplier<String> _engineLabelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getErrorLabel() {
		if (_errorLabelSupplier != null) {
			errorLabel = _errorLabelSupplier.get();

			_errorLabelSupplier = null;
		}

		return errorLabel;
	}

	public void setErrorLabel(Map<String, String> errorLabel) {
		this.errorLabel = errorLabel;

		_errorLabelSupplier = null;
	}

	@JsonIgnore
	public void setErrorLabel(
		UnsafeSupplier<Map<String, String>, Exception>
			errorLabelUnsafeSupplier) {

		_errorLabelSupplier = () -> {
			try {
				return errorLabelUnsafeSupplier.get();
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
	protected Map<String, String> errorLabel;

	@JsonIgnore
	private Supplier<Map<String, String>> _errorLabelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

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
	@Valid
	public Map<String, String> getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

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
	protected Map<String, String> name;

	@JsonIgnore
	private Supplier<Map<String, String>> _nameSupplier;

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
	public Long getObjectDefinitionId() {
		if (_objectDefinitionIdSupplier != null) {
			objectDefinitionId = _objectDefinitionIdSupplier.get();

			_objectDefinitionIdSupplier = null;
		}

		return objectDefinitionId;
	}

	public void setObjectDefinitionId(Long objectDefinitionId) {
		this.objectDefinitionId = objectDefinitionId;

		_objectDefinitionIdSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionId(
		UnsafeSupplier<Long, Exception> objectDefinitionIdUnsafeSupplier) {

		_objectDefinitionIdSupplier = () -> {
			try {
				return objectDefinitionIdUnsafeSupplier.get();
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
	protected Long objectDefinitionId;

	@JsonIgnore
	private Supplier<Long> _objectDefinitionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectValidationRuleSetting[] getObjectValidationRuleSettings() {
		if (_objectValidationRuleSettingsSupplier != null) {
			objectValidationRuleSettings =
				_objectValidationRuleSettingsSupplier.get();

			_objectValidationRuleSettingsSupplier = null;
		}

		return objectValidationRuleSettings;
	}

	public void setObjectValidationRuleSettings(
		ObjectValidationRuleSetting[] objectValidationRuleSettings) {

		this.objectValidationRuleSettings = objectValidationRuleSettings;

		_objectValidationRuleSettingsSupplier = null;
	}

	@JsonIgnore
	public void setObjectValidationRuleSettings(
		UnsafeSupplier<ObjectValidationRuleSetting[], Exception>
			objectValidationRuleSettingsUnsafeSupplier) {

		_objectValidationRuleSettingsSupplier = () -> {
			try {
				return objectValidationRuleSettingsUnsafeSupplier.get();
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
	protected ObjectValidationRuleSetting[] objectValidationRuleSettings;

	@JsonIgnore
	private Supplier<ObjectValidationRuleSetting[]>
		_objectValidationRuleSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("outputType")
	@Valid
	public OutputType getOutputType() {
		if (_outputTypeSupplier != null) {
			outputType = _outputTypeSupplier.get();

			_outputTypeSupplier = null;
		}

		return outputType;
	}

	@JsonIgnore
	public String getOutputTypeAsString() {
		OutputType outputType = getOutputType();

		if (outputType == null) {
			return null;
		}

		return outputType.toString();
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;

		_outputTypeSupplier = null;
	}

	@JsonIgnore
	public void setOutputType(
		UnsafeSupplier<OutputType, Exception> outputTypeUnsafeSupplier) {

		_outputTypeSupplier = () -> {
			try {
				return outputTypeUnsafeSupplier.get();
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
	protected OutputType outputType;

	@JsonIgnore
	private Supplier<OutputType> _outputTypeSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSystem() {
		if (_systemSupplier != null) {
			system = _systemSupplier.get();

			_systemSupplier = null;
		}

		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;

		_systemSupplier = null;
	}

	@JsonIgnore
	public void setSystem(
		UnsafeSupplier<Boolean, Exception> systemUnsafeSupplier) {

		_systemSupplier = () -> {
			try {
				return systemUnsafeSupplier.get();
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
	protected Boolean system;

	@JsonIgnore
	private Supplier<Boolean> _systemSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectValidationRule)) {
			return false;
		}

		ObjectValidationRule objectValidationRule =
			(ObjectValidationRule)object;

		return Objects.equals(toString(), objectValidationRule.toString());
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

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
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

		String engine = getEngine();

		if (engine != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"engine\": ");

			sb.append("\"");

			sb.append(_escape(engine));

			sb.append("\"");
		}

		String engineLabel = getEngineLabel();

		if (engineLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"engineLabel\": ");

			sb.append("\"");

			sb.append(_escape(engineLabel));

			sb.append("\"");
		}

		Map<String, String> errorLabel = getErrorLabel();

		if (errorLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorLabel\": ");

			sb.append(_toJSON(errorLabel));
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

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

		Map<String, String> name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(name));
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

		Long objectDefinitionId = getObjectDefinitionId();

		if (objectDefinitionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionId\": ");

			sb.append(objectDefinitionId);
		}

		ObjectValidationRuleSetting[] objectValidationRuleSettings =
			getObjectValidationRuleSettings();

		if (objectValidationRuleSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectValidationRuleSettings\": ");

			sb.append("[");

			for (int i = 0; i < objectValidationRuleSettings.length; i++) {
				sb.append(String.valueOf(objectValidationRuleSettings[i]));

				if ((i + 1) < objectValidationRuleSettings.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		OutputType outputType = getOutputType();

		if (outputType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"outputType\": ");

			sb.append("\"");

			sb.append(outputType);

			sb.append("\"");
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

		Boolean system = getSystem();

		if (system != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(system);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("OutputType")
	public static enum OutputType {

		FULL_VALIDATION("fullValidation"),
		PARTIAL_VALIDATION("partialValidation");

		@JsonCreator
		public static OutputType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (OutputType outputType : values()) {
				if (Objects.equals(outputType.getValue(), value)) {
					return outputType;
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

		private OutputType(String value) {
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
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.dto.v1_0;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
@GraphQLName("AgentInstance")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AgentInstance")
public class AgentInstance implements Serializable {

	public static AgentInstance toDTO(String json) {
		return ObjectMapperUtil.readValue(AgentInstance.class, json);
	}

	public static AgentInstance unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AgentInstance.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAgentDefinitionExternalReferenceCode() {
		if (_agentDefinitionExternalReferenceCodeSupplier != null) {
			agentDefinitionExternalReferenceCode =
				_agentDefinitionExternalReferenceCodeSupplier.get();

			_agentDefinitionExternalReferenceCodeSupplier = null;
		}

		return agentDefinitionExternalReferenceCode;
	}

	public void setAgentDefinitionExternalReferenceCode(
		String agentDefinitionExternalReferenceCode) {

		this.agentDefinitionExternalReferenceCode =
			agentDefinitionExternalReferenceCode;

		_agentDefinitionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setAgentDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			agentDefinitionExternalReferenceCodeUnsafeSupplier) {

		_agentDefinitionExternalReferenceCodeSupplier = () -> {
			try {
				return agentDefinitionExternalReferenceCodeUnsafeSupplier.get();
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
	protected String agentDefinitionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _agentDefinitionExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getAsynchronous() {
		if (_asynchronousSupplier != null) {
			asynchronous = _asynchronousSupplier.get();

			_asynchronousSupplier = null;
		}

		return asynchronous;
	}

	public void setAsynchronous(Boolean asynchronous) {
		this.asynchronous = asynchronous;

		_asynchronousSupplier = null;
	}

	@JsonIgnore
	public void setAsynchronous(
		UnsafeSupplier<Boolean, Exception> asynchronousUnsafeSupplier) {

		_asynchronousSupplier = () -> {
			try {
				return asynchronousUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Boolean asynchronous;

	@JsonIgnore
	private Supplier<Boolean> _asynchronousSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, ?> getContext() {
		if (_contextSupplier != null) {
			context = _contextSupplier.get();

			_contextSupplier = null;
		}

		return context;
	}

	public void setContext(Map<String, ?> context) {
		this.context = context;

		_contextSupplier = null;
	}

	@JsonIgnore
	public void setContext(
		UnsafeSupplier<Map<String, ?>, Exception> contextUnsafeSupplier) {

		_contextSupplier = () -> {
			try {
				return contextUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Map<String, ?> context;

	@JsonIgnore
	private Supplier<Map<String, ?>> _contextSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("instructionDefinitionScope")
	@Valid
	public InstructionDefinitionScope getInstructionDefinitionScope() {
		if (_instructionDefinitionScopeSupplier != null) {
			instructionDefinitionScope =
				_instructionDefinitionScopeSupplier.get();

			_instructionDefinitionScopeSupplier = null;
		}

		return instructionDefinitionScope;
	}

	@JsonIgnore
	public String getInstructionDefinitionScopeAsString() {
		InstructionDefinitionScope instructionDefinitionScope =
			getInstructionDefinitionScope();

		if (instructionDefinitionScope == null) {
			return null;
		}

		return instructionDefinitionScope.toString();
	}

	public void setInstructionDefinitionScope(
		InstructionDefinitionScope instructionDefinitionScope) {

		this.instructionDefinitionScope = instructionDefinitionScope;

		_instructionDefinitionScopeSupplier = null;
	}

	@JsonIgnore
	public void setInstructionDefinitionScope(
		UnsafeSupplier<InstructionDefinitionScope, Exception>
			instructionDefinitionScopeUnsafeSupplier) {

		_instructionDefinitionScopeSupplier = () -> {
			try {
				return instructionDefinitionScopeUnsafeSupplier.get();
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
	protected InstructionDefinitionScope instructionDefinitionScope;

	@JsonIgnore
	private Supplier<InstructionDefinitionScope>
		_instructionDefinitionScopeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getOutput() {
		if (_outputSupplier != null) {
			output = _outputSupplier.get();

			_outputSupplier = null;
		}

		return output;
	}

	public void setOutput(String output) {
		this.output = output;

		_outputSupplier = null;
	}

	@JsonIgnore
	public void setOutput(
		UnsafeSupplier<String, Exception> outputUnsafeSupplier) {

		_outputSupplier = () -> {
			try {
				return outputUnsafeSupplier.get();
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
	protected String output;

	@JsonIgnore
	private Supplier<String> _outputSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSseEventSinkKey() {
		if (_sseEventSinkKeySupplier != null) {
			sseEventSinkKey = _sseEventSinkKeySupplier.get();

			_sseEventSinkKeySupplier = null;
		}

		return sseEventSinkKey;
	}

	public void setSseEventSinkKey(String sseEventSinkKey) {
		this.sseEventSinkKey = sseEventSinkKey;

		_sseEventSinkKeySupplier = null;
	}

	@JsonIgnore
	public void setSseEventSinkKey(
		UnsafeSupplier<String, Exception> sseEventSinkKeyUnsafeSupplier) {

		_sseEventSinkKeySupplier = () -> {
			try {
				return sseEventSinkKeyUnsafeSupplier.get();
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
	protected String sseEventSinkKey;

	@JsonIgnore
	private Supplier<String> _sseEventSinkKeySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AgentInstance)) {
			return false;
		}

		AgentInstance agentInstance = (AgentInstance)object;

		return Objects.equals(toString(), agentInstance.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String agentDefinitionExternalReferenceCode =
			getAgentDefinitionExternalReferenceCode();

		if (agentDefinitionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"agentDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(agentDefinitionExternalReferenceCode));

			sb.append("\"");
		}

		Boolean asynchronous = getAsynchronous();

		if (asynchronous != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"asynchronous\": ");

			sb.append(asynchronous);
		}

		Map<String, ?> context = getContext();

		if (context != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"context\": ");

			sb.append(_toJSON(context));
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

		InstructionDefinitionScope instructionDefinitionScope =
			getInstructionDefinitionScope();

		if (instructionDefinitionScope != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"instructionDefinitionScope\": ");

			sb.append("\"");
			sb.append(instructionDefinitionScope);
			sb.append("\"");
		}

		String output = getOutput();

		if (output != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"output\": ");

			sb.append("\"");

			sb.append(_escape(output));

			sb.append("\"");
		}

		String sseEventSinkKey = getSseEventSinkKey();

		if (sseEventSinkKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sseEventSinkKey\": ");

			sb.append("\"");

			sb.append(_escape(sseEventSinkKey));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.ai.hub.rest.dto.v1_0.AgentInstance",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("InstructionDefinitionScope")
	public static enum InstructionDefinitionScope {

		CLICK_TO_CHAT("clickToChat"), CMS("cms"), EVERYWHERE("everywhere");

		@JsonCreator
		public static InstructionDefinitionScope create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (InstructionDefinitionScope instructionDefinitionScope :
					values()) {

				if (Objects.equals(
						instructionDefinitionScope.getValue(), value)) {

					return instructionDefinitionScope;
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

		private InstructionDefinitionScope(String value) {
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
// LIFERAY-REST-BUILDER-HASH:-1009062540
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("WorkflowInstanceSubmit")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WorkflowInstanceSubmit")
public class WorkflowInstanceSubmit implements Serializable {

	public static WorkflowInstanceSubmit toDTO(String json) {
		return ObjectMapperUtil.readValue(WorkflowInstanceSubmit.class, json);
	}

	public static WorkflowInstanceSubmit unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			WorkflowInstanceSubmit.class, json);
	}

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
	public Long getSiteId() {
		if (_siteIdSupplier != null) {
			siteId = _siteIdSupplier.get();

			_siteIdSupplier = null;
		}

		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;

		_siteIdSupplier = null;
	}

	@JsonIgnore
	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		_siteIdSupplier = () -> {
			try {
				return siteIdUnsafeSupplier.get();
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
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTransitionName() {
		if (_transitionNameSupplier != null) {
			transitionName = _transitionNameSupplier.get();

			_transitionNameSupplier = null;
		}

		return transitionName;
	}

	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;

		_transitionNameSupplier = null;
	}

	@JsonIgnore
	public void setTransitionName(
		UnsafeSupplier<String, Exception> transitionNameUnsafeSupplier) {

		_transitionNameSupplier = () -> {
			try {
				return transitionNameUnsafeSupplier.get();
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
	protected String transitionName;

	@JsonIgnore
	private Supplier<String> _transitionNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getWorkflowDefinitionName() {
		if (_workflowDefinitionNameSupplier != null) {
			workflowDefinitionName = _workflowDefinitionNameSupplier.get();

			_workflowDefinitionNameSupplier = null;
		}

		return workflowDefinitionName;
	}

	public void setWorkflowDefinitionName(String workflowDefinitionName) {
		this.workflowDefinitionName = workflowDefinitionName;

		_workflowDefinitionNameSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowDefinitionName(
		UnsafeSupplier<String, Exception>
			workflowDefinitionNameUnsafeSupplier) {

		_workflowDefinitionNameSupplier = () -> {
			try {
				return workflowDefinitionNameUnsafeSupplier.get();
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
	protected String workflowDefinitionName;

	@JsonIgnore
	private Supplier<String> _workflowDefinitionNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getWorkflowDefinitionVersion() {
		if (_workflowDefinitionVersionSupplier != null) {
			workflowDefinitionVersion =
				_workflowDefinitionVersionSupplier.get();

			_workflowDefinitionVersionSupplier = null;
		}

		return workflowDefinitionVersion;
	}

	public void setWorkflowDefinitionVersion(String workflowDefinitionVersion) {
		this.workflowDefinitionVersion = workflowDefinitionVersion;

		_workflowDefinitionVersionSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowDefinitionVersion(
		UnsafeSupplier<String, Exception>
			workflowDefinitionVersionUnsafeSupplier) {

		_workflowDefinitionVersionSupplier = () -> {
			try {
				return workflowDefinitionVersionUnsafeSupplier.get();
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
	protected String workflowDefinitionVersion;

	@JsonIgnore
	private Supplier<String> _workflowDefinitionVersionSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowInstanceSubmit)) {
			return false;
		}

		WorkflowInstanceSubmit workflowInstanceSubmit =
			(WorkflowInstanceSubmit)object;

		return Objects.equals(toString(), workflowInstanceSubmit.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, ?> context = getContext();

		if (context != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"context\": ");

			sb.append(_toJSON(context));
		}

		Long siteId = getSiteId();

		if (siteId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(siteId);
		}

		String transitionName = getTransitionName();

		if (transitionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transitionName\": ");

			sb.append("\"");

			sb.append(_escape(transitionName));

			sb.append("\"");
		}

		String workflowDefinitionName = getWorkflowDefinitionName();

		if (workflowDefinitionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionName\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinitionName));

			sb.append("\"");
		}

		String workflowDefinitionVersion = getWorkflowDefinitionVersion();

		if (workflowDefinitionVersion != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionVersion\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinitionVersion));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.workflow.dto.v1_0.WorkflowInstanceSubmit",
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
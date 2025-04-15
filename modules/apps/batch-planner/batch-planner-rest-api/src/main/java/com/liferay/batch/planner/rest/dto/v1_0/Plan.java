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

import jakarta.validation.Valid;

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
@GraphQLName("Plan")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Plan")
public class Plan implements Serializable {

	public static Plan toDTO(String json) {
		return ObjectMapperUtil.readValue(Plan.class, json);
	}

	public static Plan unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Plan.class, json);
	}

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
	public Boolean getExport() {
		if (_exportSupplier != null) {
			export = _exportSupplier.get();

			_exportSupplier = null;
		}

		return export;
	}

	public void setExport(Boolean export) {
		this.export = export;

		_exportSupplier = null;
	}

	@JsonIgnore
	public void setExport(
		UnsafeSupplier<Boolean, Exception> exportUnsafeSupplier) {

		_exportSupplier = () -> {
			try {
				return exportUnsafeSupplier.get();
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
	protected Boolean export;

	@JsonIgnore
	private Supplier<Boolean> _exportSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalType() {
		if (_externalTypeSupplier != null) {
			externalType = _externalTypeSupplier.get();

			_externalTypeSupplier = null;
		}

		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;

		_externalTypeSupplier = null;
	}

	@JsonIgnore
	public void setExternalType(
		UnsafeSupplier<String, Exception> externalTypeUnsafeSupplier) {

		_externalTypeSupplier = () -> {
			try {
				return externalTypeUnsafeSupplier.get();
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
	protected String externalType;

	@JsonIgnore
	private Supplier<String> _externalTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalURL() {
		if (_externalURLSupplier != null) {
			externalURL = _externalURLSupplier.get();

			_externalURLSupplier = null;
		}

		return externalURL;
	}

	public void setExternalURL(String externalURL) {
		this.externalURL = externalURL;

		_externalURLSupplier = null;
	}

	@JsonIgnore
	public void setExternalURL(
		UnsafeSupplier<String, Exception> externalURLUnsafeSupplier) {

		_externalURLSupplier = () -> {
			try {
				return externalURLUnsafeSupplier.get();
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
	protected String externalURL;

	@JsonIgnore
	private Supplier<String> _externalURLSupplier;

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
	public String getInternalClassName() {
		if (_internalClassNameSupplier != null) {
			internalClassName = _internalClassNameSupplier.get();

			_internalClassNameSupplier = null;
		}

		return internalClassName;
	}

	public void setInternalClassName(String internalClassName) {
		this.internalClassName = internalClassName;

		_internalClassNameSupplier = null;
	}

	@JsonIgnore
	public void setInternalClassName(
		UnsafeSupplier<String, Exception> internalClassNameUnsafeSupplier) {

		_internalClassNameSupplier = () -> {
			try {
				return internalClassNameUnsafeSupplier.get();
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
	protected String internalClassName;

	@JsonIgnore
	private Supplier<String> _internalClassNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getInternalClassNameKey() {
		if (_internalClassNameKeySupplier != null) {
			internalClassNameKey = _internalClassNameKeySupplier.get();

			_internalClassNameKeySupplier = null;
		}

		return internalClassNameKey;
	}

	public void setInternalClassNameKey(String internalClassNameKey) {
		this.internalClassNameKey = internalClassNameKey;

		_internalClassNameKeySupplier = null;
	}

	@JsonIgnore
	public void setInternalClassNameKey(
		UnsafeSupplier<String, Exception> internalClassNameKeyUnsafeSupplier) {

		_internalClassNameKeySupplier = () -> {
			try {
				return internalClassNameKeyUnsafeSupplier.get();
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
	protected String internalClassNameKey;

	@JsonIgnore
	private Supplier<String> _internalClassNameKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Mapping[] getMappings() {
		if (_mappingsSupplier != null) {
			mappings = _mappingsSupplier.get();

			_mappingsSupplier = null;
		}

		return mappings;
	}

	public void setMappings(Mapping[] mappings) {
		this.mappings = mappings;

		_mappingsSupplier = null;
	}

	@JsonIgnore
	public void setMappings(
		UnsafeSupplier<Mapping[], Exception> mappingsUnsafeSupplier) {

		_mappingsSupplier = () -> {
			try {
				return mappingsUnsafeSupplier.get();
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
	protected Mapping[] mappings;

	@JsonIgnore
	private Supplier<Mapping[]> _mappingsSupplier;

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
	public Policy[] getPolicies() {
		if (_policiesSupplier != null) {
			policies = _policiesSupplier.get();

			_policiesSupplier = null;
		}

		return policies;
	}

	public void setPolicies(Policy[] policies) {
		this.policies = policies;

		_policiesSupplier = null;
	}

	@JsonIgnore
	public void setPolicies(
		UnsafeSupplier<Policy[], Exception> policiesUnsafeSupplier) {

		_policiesSupplier = () -> {
			try {
				return policiesUnsafeSupplier.get();
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
	protected Policy[] policies;

	@JsonIgnore
	private Supplier<Policy[]> _policiesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getSize() {
		if (_sizeSupplier != null) {
			size = _sizeSupplier.get();

			_sizeSupplier = null;
		}

		return size;
	}

	public void setSize(Integer size) {
		this.size = size;

		_sizeSupplier = null;
	}

	@JsonIgnore
	public void setSize(UnsafeSupplier<Integer, Exception> sizeUnsafeSupplier) {
		_sizeSupplier = () -> {
			try {
				return sizeUnsafeSupplier.get();
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
	protected Integer size;

	@JsonIgnore
	private Supplier<Integer> _sizeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Integer, Exception> statusUnsafeSupplier) {

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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer status;

	@JsonIgnore
	private Supplier<Integer> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTaskItemDelegateName() {
		if (_taskItemDelegateNameSupplier != null) {
			taskItemDelegateName = _taskItemDelegateNameSupplier.get();

			_taskItemDelegateNameSupplier = null;
		}

		return taskItemDelegateName;
	}

	public void setTaskItemDelegateName(String taskItemDelegateName) {
		this.taskItemDelegateName = taskItemDelegateName;

		_taskItemDelegateNameSupplier = null;
	}

	@JsonIgnore
	public void setTaskItemDelegateName(
		UnsafeSupplier<String, Exception> taskItemDelegateNameUnsafeSupplier) {

		_taskItemDelegateNameSupplier = () -> {
			try {
				return taskItemDelegateNameUnsafeSupplier.get();
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
	protected String taskItemDelegateName;

	@JsonIgnore
	private Supplier<String> _taskItemDelegateNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getTemplate() {
		if (_templateSupplier != null) {
			template = _templateSupplier.get();

			_templateSupplier = null;
		}

		return template;
	}

	public void setTemplate(Boolean template) {
		this.template = template;

		_templateSupplier = null;
	}

	@JsonIgnore
	public void setTemplate(
		UnsafeSupplier<Boolean, Exception> templateUnsafeSupplier) {

		_templateSupplier = () -> {
			try {
				return templateUnsafeSupplier.get();
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
	protected Boolean template;

	@JsonIgnore
	private Supplier<Boolean> _templateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getTotal() {
		if (_totalSupplier != null) {
			total = _totalSupplier.get();

			_totalSupplier = null;
		}

		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;

		_totalSupplier = null;
	}

	@JsonIgnore
	public void setTotal(
		UnsafeSupplier<Integer, Exception> totalUnsafeSupplier) {

		_totalSupplier = () -> {
			try {
				return totalUnsafeSupplier.get();
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
	protected Integer total;

	@JsonIgnore
	private Supplier<Integer> _totalSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Plan)) {
			return false;
		}

		Plan plan = (Plan)object;

		return Objects.equals(toString(), plan.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		Boolean export = getExport();

		if (export != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"export\": ");

			sb.append(export);
		}

		String externalType = getExternalType();

		if (externalType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalType\": ");

			sb.append("\"");

			sb.append(_escape(externalType));

			sb.append("\"");
		}

		String externalURL = getExternalURL();

		if (externalURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalURL\": ");

			sb.append("\"");

			sb.append(_escape(externalURL));

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

		String internalClassName = getInternalClassName();

		if (internalClassName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"internalClassName\": ");

			sb.append("\"");

			sb.append(_escape(internalClassName));

			sb.append("\"");
		}

		String internalClassNameKey = getInternalClassNameKey();

		if (internalClassNameKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"internalClassNameKey\": ");

			sb.append("\"");

			sb.append(_escape(internalClassNameKey));

			sb.append("\"");
		}

		Mapping[] mappings = getMappings();

		if (mappings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mappings\": ");

			sb.append("[");

			for (int i = 0; i < mappings.length; i++) {
				sb.append(String.valueOf(mappings[i]));

				if ((i + 1) < mappings.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

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

		Policy[] policies = getPolicies();

		if (policies != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"policies\": ");

			sb.append("[");

			for (int i = 0; i < policies.length; i++) {
				sb.append(String.valueOf(policies[i]));

				if ((i + 1) < policies.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Integer size = getSize();

		if (size != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(size);
		}

		Integer status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(status);
		}

		String taskItemDelegateName = getTaskItemDelegateName();

		if (taskItemDelegateName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taskItemDelegateName\": ");

			sb.append("\"");

			sb.append(_escape(taskItemDelegateName));

			sb.append("\"");
		}

		Boolean template = getTemplate();

		if (template != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"template\": ");

			sb.append(template);
		}

		Integer total = getTotal();

		if (total != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(total);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.batch.planner.rest.dto.v1_0.Plan",
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
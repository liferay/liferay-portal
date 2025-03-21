/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

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
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("ElementInstance")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ElementInstance")
public class ElementInstance implements Serializable {

	public static ElementInstance toDTO(String json) {
		return ObjectMapperUtil.readValue(ElementInstance.class, json);
	}

	public static ElementInstance unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ElementInstance.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Configuration getConfigurationEntry() {
		if (_configurationEntrySupplier != null) {
			configurationEntry = _configurationEntrySupplier.get();

			_configurationEntrySupplier = null;
		}

		return configurationEntry;
	}

	public void setConfigurationEntry(Configuration configurationEntry) {
		this.configurationEntry = configurationEntry;

		_configurationEntrySupplier = null;
	}

	@JsonIgnore
	public void setConfigurationEntry(
		UnsafeSupplier<Configuration, Exception>
			configurationEntryUnsafeSupplier) {

		_configurationEntrySupplier = () -> {
			try {
				return configurationEntryUnsafeSupplier.get();
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
	protected Configuration configurationEntry;

	@JsonIgnore
	private Supplier<Configuration> _configurationEntrySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SXPElement getSxpElement() {
		if (_sxpElementSupplier != null) {
			sxpElement = _sxpElementSupplier.get();

			_sxpElementSupplier = null;
		}

		return sxpElement;
	}

	public void setSxpElement(SXPElement sxpElement) {
		this.sxpElement = sxpElement;

		_sxpElementSupplier = null;
	}

	@JsonIgnore
	public void setSxpElement(
		UnsafeSupplier<SXPElement, Exception> sxpElementUnsafeSupplier) {

		_sxpElementSupplier = () -> {
			try {
				return sxpElementUnsafeSupplier.get();
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
	protected SXPElement sxpElement;

	@JsonIgnore
	private Supplier<SXPElement> _sxpElementSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getSxpElementId() {
		if (_sxpElementIdSupplier != null) {
			sxpElementId = _sxpElementIdSupplier.get();

			_sxpElementIdSupplier = null;
		}

		return sxpElementId;
	}

	public void setSxpElementId(Long sxpElementId) {
		this.sxpElementId = sxpElementId;

		_sxpElementIdSupplier = null;
	}

	@JsonIgnore
	public void setSxpElementId(
		UnsafeSupplier<Long, Exception> sxpElementIdUnsafeSupplier) {

		_sxpElementIdSupplier = () -> {
			try {
				return sxpElementIdUnsafeSupplier.get();
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
	protected Long sxpElementId;

	@JsonIgnore
	private Supplier<Long> _sxpElementIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(Integer type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Integer, Exception> typeUnsafeSupplier) {
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer type;

	@JsonIgnore
	private Supplier<Integer> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Object> getUiConfigurationValues() {
		if (_uiConfigurationValuesSupplier != null) {
			uiConfigurationValues = _uiConfigurationValuesSupplier.get();

			_uiConfigurationValuesSupplier = null;
		}

		return uiConfigurationValues;
	}

	public void setUiConfigurationValues(
		Map<String, Object> uiConfigurationValues) {

		this.uiConfigurationValues = uiConfigurationValues;

		_uiConfigurationValuesSupplier = null;
	}

	@JsonIgnore
	public void setUiConfigurationValues(
		UnsafeSupplier<Map<String, Object>, Exception>
			uiConfigurationValuesUnsafeSupplier) {

		_uiConfigurationValuesSupplier = () -> {
			try {
				return uiConfigurationValuesUnsafeSupplier.get();
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
	protected Map<String, Object> uiConfigurationValues;

	@JsonIgnore
	private Supplier<Map<String, Object>> _uiConfigurationValuesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ElementInstance)) {
			return false;
		}

		ElementInstance elementInstance = (ElementInstance)object;

		return Objects.equals(toString(), elementInstance.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Configuration configurationEntry = getConfigurationEntry();

		if (configurationEntry != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configurationEntry\": ");

			sb.append(String.valueOf(configurationEntry));
		}

		SXPElement sxpElement = getSxpElement();

		if (sxpElement != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sxpElement\": ");

			sb.append(String.valueOf(sxpElement));
		}

		Long sxpElementId = getSxpElementId();

		if (sxpElementId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sxpElementId\": ");

			sb.append(sxpElementId);
		}

		Integer type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(type);
		}

		Map<String, Object> uiConfigurationValues = getUiConfigurationValues();

		if (uiConfigurationValues != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uiConfigurationValues\": ");

			sb.append(_toJSON(uiConfigurationValues));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.ElementInstance",
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
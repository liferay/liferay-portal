/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.dto.v2_0;

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
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
@GraphQLName("DataLayoutRenderingContext")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DataLayoutRenderingContext")
public class DataLayoutRenderingContext implements Serializable {

	public static DataLayoutRenderingContext toDTO(String json) {
		return ObjectMapperUtil.readValue(
			DataLayoutRenderingContext.class, json);
	}

	public static DataLayoutRenderingContext unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			DataLayoutRenderingContext.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getContainerId() {
		if (_containerIdSupplier != null) {
			containerId = _containerIdSupplier.get();

			_containerIdSupplier = null;
		}

		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;

		_containerIdSupplier = null;
	}

	@JsonIgnore
	public void setContainerId(
		UnsafeSupplier<String, Exception> containerIdUnsafeSupplier) {

		_containerIdSupplier = () -> {
			try {
				return containerIdUnsafeSupplier.get();
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
	protected String containerId;

	@JsonIgnore
	private Supplier<String> _containerIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Object> getDataRecordValues() {
		if (_dataRecordValuesSupplier != null) {
			dataRecordValues = _dataRecordValuesSupplier.get();

			_dataRecordValuesSupplier = null;
		}

		return dataRecordValues;
	}

	public void setDataRecordValues(Map<String, Object> dataRecordValues) {
		this.dataRecordValues = dataRecordValues;

		_dataRecordValuesSupplier = null;
	}

	@JsonIgnore
	public void setDataRecordValues(
		UnsafeSupplier<Map<String, Object>, Exception>
			dataRecordValuesUnsafeSupplier) {

		_dataRecordValuesSupplier = () -> {
			try {
				return dataRecordValuesUnsafeSupplier.get();
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
	protected Map<String, Object> dataRecordValues;

	@JsonIgnore
	private Supplier<Map<String, Object>> _dataRecordValuesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getNamespace() {
		if (_namespaceSupplier != null) {
			namespace = _namespaceSupplier.get();

			_namespaceSupplier = null;
		}

		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;

		_namespaceSupplier = null;
	}

	@JsonIgnore
	public void setNamespace(
		UnsafeSupplier<String, Exception> namespaceUnsafeSupplier) {

		_namespaceSupplier = () -> {
			try {
				return namespaceUnsafeSupplier.get();
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
	protected String namespace;

	@JsonIgnore
	private Supplier<String> _namespaceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPathThemeImages() {
		if (_pathThemeImagesSupplier != null) {
			pathThemeImages = _pathThemeImagesSupplier.get();

			_pathThemeImagesSupplier = null;
		}

		return pathThemeImages;
	}

	public void setPathThemeImages(String pathThemeImages) {
		this.pathThemeImages = pathThemeImages;

		_pathThemeImagesSupplier = null;
	}

	@JsonIgnore
	public void setPathThemeImages(
		UnsafeSupplier<String, Exception> pathThemeImagesUnsafeSupplier) {

		_pathThemeImagesSupplier = () -> {
			try {
				return pathThemeImagesUnsafeSupplier.get();
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
	protected String pathThemeImages;

	@JsonIgnore
	private Supplier<String> _pathThemeImagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getReadOnly() {
		if (_readOnlySupplier != null) {
			readOnly = _readOnlySupplier.get();

			_readOnlySupplier = null;
		}

		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;

		_readOnlySupplier = null;
	}

	@JsonIgnore
	public void setReadOnly(
		UnsafeSupplier<Boolean, Exception> readOnlyUnsafeSupplier) {

		_readOnlySupplier = () -> {
			try {
				return readOnlyUnsafeSupplier.get();
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
	protected Boolean readOnly;

	@JsonIgnore
	private Supplier<Boolean> _readOnlySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getScopeGroupId() {
		if (_scopeGroupIdSupplier != null) {
			scopeGroupId = _scopeGroupIdSupplier.get();

			_scopeGroupIdSupplier = null;
		}

		return scopeGroupId;
	}

	public void setScopeGroupId(Long scopeGroupId) {
		this.scopeGroupId = scopeGroupId;

		_scopeGroupIdSupplier = null;
	}

	@JsonIgnore
	public void setScopeGroupId(
		UnsafeSupplier<Long, Exception> scopeGroupIdUnsafeSupplier) {

		_scopeGroupIdSupplier = () -> {
			try {
				return scopeGroupIdUnsafeSupplier.get();
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
	protected Long scopeGroupId;

	@JsonIgnore
	private Supplier<Long> _scopeGroupIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getSiteGroupId() {
		if (_siteGroupIdSupplier != null) {
			siteGroupId = _siteGroupIdSupplier.get();

			_siteGroupIdSupplier = null;
		}

		return siteGroupId;
	}

	public void setSiteGroupId(Long siteGroupId) {
		this.siteGroupId = siteGroupId;

		_siteGroupIdSupplier = null;
	}

	@JsonIgnore
	public void setSiteGroupId(
		UnsafeSupplier<Long, Exception> siteGroupIdUnsafeSupplier) {

		_siteGroupIdSupplier = () -> {
			try {
				return siteGroupIdUnsafeSupplier.get();
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
	protected Long siteGroupId;

	@JsonIgnore
	private Supplier<Long> _siteGroupIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataLayoutRenderingContext)) {
			return false;
		}

		DataLayoutRenderingContext dataLayoutRenderingContext =
			(DataLayoutRenderingContext)object;

		return Objects.equals(
			toString(), dataLayoutRenderingContext.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String containerId = getContainerId();

		if (containerId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"containerId\": ");

			sb.append("\"");

			sb.append(_escape(containerId));

			sb.append("\"");
		}

		Map<String, Object> dataRecordValues = getDataRecordValues();

		if (dataRecordValues != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataRecordValues\": ");

			sb.append(_toJSON(dataRecordValues));
		}

		String namespace = getNamespace();

		if (namespace != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"namespace\": ");

			sb.append("\"");

			sb.append(_escape(namespace));

			sb.append("\"");
		}

		String pathThemeImages = getPathThemeImages();

		if (pathThemeImages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pathThemeImages\": ");

			sb.append("\"");

			sb.append(_escape(pathThemeImages));

			sb.append("\"");
		}

		Boolean readOnly = getReadOnly();

		if (readOnly != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(readOnly);
		}

		Long scopeGroupId = getScopeGroupId();

		if (scopeGroupId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scopeGroupId\": ");

			sb.append(scopeGroupId);
		}

		Long siteGroupId = getSiteGroupId();

		if (siteGroupId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteGroupId\": ");

			sb.append(siteGroupId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.data.engine.rest.dto.v2_0.DataLayoutRenderingContext",
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
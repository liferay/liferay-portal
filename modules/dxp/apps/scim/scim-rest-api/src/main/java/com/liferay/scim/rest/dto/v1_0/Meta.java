/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.dto.v1_0;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A complex attribute containing resource metadata.",
	value = "Meta"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Meta")
public class Meta implements Serializable {

	public static Meta toDTO(String json) {
		return ObjectMapperUtil.readValue(Meta.class, json);
	}

	public static Meta unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Meta.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The \"DateTime\" that the resource was added to the service provider."
	)
	public Date getCreated() {
		if (_createdSupplier != null) {
			created = _createdSupplier.get();

			_createdSupplier = null;
		}

		return created;
	}

	public void setCreated(Date created) {
		this.created = created;

		_createdSupplier = null;
	}

	@JsonIgnore
	public void setCreated(
		UnsafeSupplier<Date, Exception> createdUnsafeSupplier) {

		_createdSupplier = () -> {
			try {
				return createdUnsafeSupplier.get();
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
		description = "The \"DateTime\" that the resource was added to the service provider."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date created;

	@JsonIgnore
	private Supplier<Date> _createdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The most recent DateTime that the details of this resource were updated at the service provider."
	)
	public Date getLastModified() {
		if (_lastModifiedSupplier != null) {
			lastModified = _lastModifiedSupplier.get();

			_lastModifiedSupplier = null;
		}

		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;

		_lastModifiedSupplier = null;
	}

	@JsonIgnore
	public void setLastModified(
		UnsafeSupplier<Date, Exception> lastModifiedUnsafeSupplier) {

		_lastModifiedSupplier = () -> {
			try {
				return lastModifiedUnsafeSupplier.get();
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
		description = "The most recent DateTime that the details of this resource were updated at the service provider."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date lastModified;

	@JsonIgnore
	private Supplier<Date> _lastModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The URI of the resource being returned."
	)
	public String getLocation() {
		if (_locationSupplier != null) {
			location = _locationSupplier.get();

			_locationSupplier = null;
		}

		return location;
	}

	public void setLocation(String location) {
		this.location = location;

		_locationSupplier = null;
	}

	@JsonIgnore
	public void setLocation(
		UnsafeSupplier<String, Exception> locationUnsafeSupplier) {

		_locationSupplier = () -> {
			try {
				return locationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The URI of the resource being returned.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String location;

	@JsonIgnore
	private Supplier<String> _locationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the resource type of the resource."
	)
	public String getResourceType() {
		if (_resourceTypeSupplier != null) {
			resourceType = _resourceTypeSupplier.get();

			_resourceTypeSupplier = null;
		}

		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;

		_resourceTypeSupplier = null;
	}

	@JsonIgnore
	public void setResourceType(
		UnsafeSupplier<String, Exception> resourceTypeUnsafeSupplier) {

		_resourceTypeSupplier = () -> {
			try {
				return resourceTypeUnsafeSupplier.get();
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
		description = "The name of the resource type of the resource."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String resourceType;

	@JsonIgnore
	private Supplier<String> _resourceTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The version of the resource being returned."
	)
	public String getVersion() {
		if (_versionSupplier != null) {
			version = _versionSupplier.get();

			_versionSupplier = null;
		}

		return version;
	}

	public void setVersion(String version) {
		this.version = version;

		_versionSupplier = null;
	}

	@JsonIgnore
	public void setVersion(
		UnsafeSupplier<String, Exception> versionUnsafeSupplier) {

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

	@GraphQLField(description = "The version of the resource being returned.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String version;

	@JsonIgnore
	private Supplier<String> _versionSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Meta)) {
			return false;
		}

		Meta meta = (Meta)object;

		return Objects.equals(toString(), meta.toString());
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

		Date created = getCreated();

		if (created != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"created\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(created));

			sb.append("\"");
		}

		Date lastModified = getLastModified();

		if (lastModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(lastModified));

			sb.append("\"");
		}

		String location = getLocation();

		if (location != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"location\": ");

			sb.append("\"");

			sb.append(_escape(location));

			sb.append("\"");
		}

		String resourceType = getResourceType();

		if (resourceType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resourceType\": ");

			sb.append("\"");

			sb.append(_escape(resourceType));

			sb.append("\"");
		}

		String version = getVersion();

		if (version != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append("\"");

			sb.append(_escape(version));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.scim.rest.dto.v1_0.Meta",
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
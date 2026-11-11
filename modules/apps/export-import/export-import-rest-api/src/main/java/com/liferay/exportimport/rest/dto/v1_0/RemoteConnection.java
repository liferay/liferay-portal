/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.dto.v1_0;

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
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents the connection to the remote live site of a site staged remotely. Each property left out takes the value of the site's remote staging settings.",
	value = "RemoteConnection"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Represents the connection to the remote live site of a site staged remotely. Each property left out takes the value of the site's remote staging settings."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "RemoteConnection")
public class RemoteConnection implements Serializable {

	public static RemoteConnection toDTO(String json) {
		return ObjectMapperUtil.readValue(RemoteConnection.class, json);
	}

	public static RemoteConnection unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(RemoteConnection.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The remote live server's host name or IP address."
	)
	public String getAddress() {
		if (_addressSupplier != null) {
			address = _addressSupplier.get();

			_addressSupplier = null;
		}

		return address;
	}

	public void setAddress(String address) {
		this.address = address;

		_addressSupplier = null;
	}

	@JsonIgnore
	public void setAddress(
		UnsafeSupplier<String, Exception> addressUnsafeSupplier) {

		_addressSupplier = () -> {
			try {
				return addressUnsafeSupplier.get();
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
		description = "The remote live server's host name or IP address."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String address;

	@JsonIgnore
	private Supplier<String> _addressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The remote live portal's path context."
	)
	public String getPathContext() {
		if (_pathContextSupplier != null) {
			pathContext = _pathContextSupplier.get();

			_pathContextSupplier = null;
		}

		return pathContext;
	}

	public void setPathContext(String pathContext) {
		this.pathContext = pathContext;

		_pathContextSupplier = null;
	}

	@JsonIgnore
	public void setPathContext(
		UnsafeSupplier<String, Exception> pathContextUnsafeSupplier) {

		_pathContextSupplier = () -> {
			try {
				return pathContextUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The remote live portal's path context.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String pathContext;

	@JsonIgnore
	private Supplier<String> _pathContextSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The remote live server's port."
	)
	public Integer getPort() {
		if (_portSupplier != null) {
			port = _portSupplier.get();

			_portSupplier = null;
		}

		return port;
	}

	public void setPort(Integer port) {
		this.port = port;

		_portSupplier = null;
	}

	@JsonIgnore
	public void setPort(UnsafeSupplier<Integer, Exception> portUnsafeSupplier) {
		_portSupplier = () -> {
			try {
				return portUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The remote live server's port.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer port;

	@JsonIgnore
	private Supplier<Integer> _portSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the connection to the remote live server is secure."
	)
	public Boolean getSecureConnection() {
		if (_secureConnectionSupplier != null) {
			secureConnection = _secureConnectionSupplier.get();

			_secureConnectionSupplier = null;
		}

		return secureConnection;
	}

	public void setSecureConnection(Boolean secureConnection) {
		this.secureConnection = secureConnection;

		_secureConnectionSupplier = null;
	}

	@JsonIgnore
	public void setSecureConnection(
		UnsafeSupplier<Boolean, Exception> secureConnectionUnsafeSupplier) {

		_secureConnectionSupplier = () -> {
			try {
				return secureConnectionUnsafeSupplier.get();
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
		description = "A flag that indicates whether the connection to the remote live server is secure."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean secureConnection;

	@JsonIgnore
	private Supplier<Boolean> _secureConnectionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The remote live site's ID."
	)
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

	@GraphQLField(description = "The remote live site's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RemoteConnection)) {
			return false;
		}

		RemoteConnection remoteConnection = (RemoteConnection)object;

		return Objects.equals(toString(), remoteConnection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String address = getAddress();

		if (address != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"address\": ");

			sb.append("\"");

			sb.append(_escape(address));

			sb.append("\"");
		}

		String pathContext = getPathContext();

		if (pathContext != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pathContext\": ");

			sb.append("\"");

			sb.append(_escape(pathContext));

			sb.append("\"");
		}

		Integer port = getPort();

		if (port != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"port\": ");

			sb.append(port);
		}

		Boolean secureConnection = getSecureConnection();

		if (secureConnection != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"secureConnection\": ");

			sb.append(secureConnection);
		}

		Long siteId = getSiteId();

		if (siteId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(siteId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.RemoteConnection",
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
// LIFERAY-REST-BUILDER-HASH:-227527328
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.dto.v1_0;

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
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
@GraphQLName("AuthorizationToken")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AuthorizationToken")
public class AuthorizationToken implements Serializable {

	public static AuthorizationToken toDTO(String json) {
		return ObjectMapperUtil.readValue(AuthorizationToken.class, json);
	}

	public static AuthorizationToken unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AuthorizationToken.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAccessToken() {
		if (_accessTokenSupplier != null) {
			accessToken = _accessTokenSupplier.get();

			_accessTokenSupplier = null;
		}

		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;

		_accessTokenSupplier = null;
	}

	@JsonIgnore
	public void setAccessToken(
		UnsafeSupplier<String, Exception> accessTokenUnsafeSupplier) {

		_accessTokenSupplier = () -> {
			try {
				return accessTokenUnsafeSupplier.get();
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
	protected String accessToken;

	@JsonIgnore
	private Supplier<String> _accessTokenSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getScope() {
		if (_scopeSupplier != null) {
			scope = _scopeSupplier.get();

			_scopeSupplier = null;
		}

		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;

		_scopeSupplier = null;
	}

	@JsonIgnore
	public void setScope(
		UnsafeSupplier<String, Exception> scopeUnsafeSupplier) {

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String scope;

	@JsonIgnore
	private Supplier<String> _scopeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getServiceURL() {
		if (_serviceURLSupplier != null) {
			serviceURL = _serviceURLSupplier.get();

			_serviceURLSupplier = null;
		}

		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;

		_serviceURLSupplier = null;
	}

	@JsonIgnore
	public void setServiceURL(
		UnsafeSupplier<String, Exception> serviceURLUnsafeSupplier) {

		_serviceURLSupplier = () -> {
			try {
				return serviceURLUnsafeSupplier.get();
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
	protected String serviceURL;

	@JsonIgnore
	private Supplier<String> _serviceURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getUserToken() {
		if (_userTokenSupplier != null) {
			userToken = _userTokenSupplier.get();

			_userTokenSupplier = null;
		}

		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;

		_userTokenSupplier = null;
	}

	@JsonIgnore
	public void setUserToken(
		UnsafeSupplier<String, Exception> userTokenUnsafeSupplier) {

		_userTokenSupplier = () -> {
			try {
				return userTokenUnsafeSupplier.get();
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
	protected String userToken;

	@JsonIgnore
	private Supplier<String> _userTokenSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AuthorizationToken)) {
			return false;
		}

		AuthorizationToken authorizationToken = (AuthorizationToken)object;

		return Objects.equals(toString(), authorizationToken.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String accessToken = getAccessToken();

		if (accessToken != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accessToken\": ");

			sb.append("\"");

			sb.append(_escape(accessToken));

			sb.append("\"");
		}

		String scope = getScope();

		if (scope != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append("\"");

			sb.append(_escape(scope));

			sb.append("\"");
		}

		String serviceURL = getServiceURL();

		if (serviceURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"serviceURL\": ");

			sb.append("\"");

			sb.append(_escape(serviceURL));

			sb.append("\"");
		}

		String userToken = getUserToken();

		if (userToken != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userToken\": ");

			sb.append("\"");

			sb.append(_escape(userToken));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.ai.hub.cell.rest.dto.v1_0.AuthorizationToken",
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
// LIFERAY-REST-BUILDER-HASH:394196965
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.dto.v1_0;

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
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
@GraphQLName(description = "Administrator information.", value = "Admin")
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Administrator information."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Admin")
public class Admin implements Serializable {

	public static Admin toDTO(String json) {
		return ObjectMapperUtil.readValue(Admin.class, json);
	}

	public static Admin unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Admin.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Administrator's email address."
	)
	public String getEmailAddress() {
		if (_emailAddressSupplier != null) {
			emailAddress = _emailAddressSupplier.get();

			_emailAddressSupplier = null;
		}

		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;

		_emailAddressSupplier = null;
	}

	@JsonIgnore
	public void setEmailAddress(
		UnsafeSupplier<String, Exception> emailAddressUnsafeSupplier) {

		_emailAddressSupplier = () -> {
			try {
				return emailAddressUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Administrator's email address.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String emailAddress;

	@JsonIgnore
	private Supplier<String> _emailAddressSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The admin's surname (last name)."
	)
	public String getFamilyName() {
		if (_familyNameSupplier != null) {
			familyName = _familyNameSupplier.get();

			_familyNameSupplier = null;
		}

		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;

		_familyNameSupplier = null;
	}

	@JsonIgnore
	public void setFamilyName(
		UnsafeSupplier<String, Exception> familyNameUnsafeSupplier) {

		_familyNameSupplier = () -> {
			try {
				return familyNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The admin's surname (last name).")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String familyName;

	@JsonIgnore
	private Supplier<String> _familyNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The admin's first name."
	)
	public String getGivenName() {
		if (_givenNameSupplier != null) {
			givenName = _givenNameSupplier.get();

			_givenNameSupplier = null;
		}

		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;

		_givenNameSupplier = null;
	}

	@JsonIgnore
	public void setGivenName(
		UnsafeSupplier<String, Exception> givenNameUnsafeSupplier) {

		_givenNameSupplier = () -> {
			try {
				return givenNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The admin's first name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String givenName;

	@JsonIgnore
	private Supplier<String> _givenNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The admin's middle name."
	)
	public String getMiddleName() {
		if (_middleNameSupplier != null) {
			middleName = _middleNameSupplier.get();

			_middleNameSupplier = null;
		}

		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;

		_middleNameSupplier = null;
	}

	@JsonIgnore
	public void setMiddleName(
		UnsafeSupplier<String, Exception> middleNameUnsafeSupplier) {

		_middleNameSupplier = () -> {
			try {
				return middleNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The admin's middle name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String middleName;

	@JsonIgnore
	private Supplier<String> _middleNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The admin's password."
	)
	public String getPassword() {
		if (_passwordSupplier != null) {
			password = _passwordSupplier.get();

			_passwordSupplier = null;
		}

		return password;
	}

	public void setPassword(String password) {
		this.password = password;

		_passwordSupplier = null;
	}

	@JsonIgnore
	public void setPassword(
		UnsafeSupplier<String, Exception> passwordUnsafeSupplier) {

		_passwordSupplier = () -> {
			try {
				return passwordUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The admin's password.")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String password;

	@JsonIgnore
	private Supplier<String> _passwordSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The admin's screen name."
	)
	public String getScreenName() {
		if (_screenNameSupplier != null) {
			screenName = _screenNameSupplier.get();

			_screenNameSupplier = null;
		}

		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;

		_screenNameSupplier = null;
	}

	@JsonIgnore
	public void setScreenName(
		UnsafeSupplier<String, Exception> screenNameUnsafeSupplier) {

		_screenNameSupplier = () -> {
			try {
				return screenNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The admin's screen name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String screenName;

	@JsonIgnore
	private Supplier<String> _screenNameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Admin)) {
			return false;
		}

		Admin admin = (Admin)object;

		return Objects.equals(toString(), admin.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String emailAddress = getEmailAddress();

		if (emailAddress != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(emailAddress));

			sb.append("\"");
		}

		String familyName = getFamilyName();

		if (familyName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"familyName\": ");

			sb.append("\"");

			sb.append(_escape(familyName));

			sb.append("\"");
		}

		String givenName = getGivenName();

		if (givenName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"givenName\": ");

			sb.append("\"");

			sb.append(_escape(givenName));

			sb.append("\"");
		}

		String middleName = getMiddleName();

		if (middleName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"middleName\": ");

			sb.append("\"");

			sb.append(_escape(middleName));

			sb.append("\"");
		}

		String password = getPassword();

		if (password != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"password\": ");

			sb.append("\"");

			sb.append(_escape(password));

			sb.append("\"");
		}

		String screenName = getScreenName();

		if (screenName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"screenName\": ");

			sb.append("\"");

			sb.append(_escape(screenName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.portal.instances.dto.v1_0.Admin",
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
// LIFERAY-REST-BUILDER-HASH:-1727326677
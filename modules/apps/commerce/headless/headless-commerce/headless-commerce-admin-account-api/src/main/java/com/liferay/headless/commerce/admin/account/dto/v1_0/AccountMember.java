/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.dto.v1_0;

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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName("AccountMember")
@io.swagger.v3.oas.annotations.media.Schema(requiredProperties = {"email"})
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AccountMember")
public class AccountMember implements Serializable {

	public static AccountMember toDTO(String json) {
		return ObjectMapperUtil.readValue(AccountMember.class, json);
	}

	public static AccountMember unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AccountMember.class, json);
	}

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getAccountId() {
		if (_accountIdSupplier != null) {
			accountId = _accountIdSupplier.get();

			_accountIdSupplier = null;
		}

		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;

		_accountIdSupplier = null;
	}

	@JsonIgnore
	public void setAccountId(
		UnsafeSupplier<Long, Exception> accountIdUnsafeSupplier) {

		_accountIdSupplier = () -> {
			try {
				return accountIdUnsafeSupplier.get();
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
	protected Long accountId;

	@JsonIgnore
	private Supplier<Long> _accountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "[{description={en_US=Account Administrator Description US, hr_HR=Account Administrator Description HR, hu_HU=Account Administrator Description HU}}, {description={en_US=Order Manager Description US, hr_HR=Order Manager Description HR, hu_HU=Order Manager Description HU}}]"
	)
	@Valid
	public AccountRole[] getAccountRoles() {
		if (_accountRolesSupplier != null) {
			accountRoles = _accountRolesSupplier.get();

			_accountRolesSupplier = null;
		}

		return accountRoles;
	}

	public void setAccountRoles(AccountRole[] accountRoles) {
		this.accountRoles = accountRoles;

		_accountRolesSupplier = null;
	}

	@JsonIgnore
	public void setAccountRoles(
		UnsafeSupplier<AccountRole[], Exception> accountRolesUnsafeSupplier) {

		_accountRolesSupplier = () -> {
			try {
				return accountRolesUnsafeSupplier.get();
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
	protected AccountRole[] accountRoles;

	@JsonIgnore
	private Supplier<AccountRole[]> _accountRolesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "joe.1@commerce.com")
	public String getEmail() {
		if (_emailSupplier != null) {
			email = _emailSupplier.get();

			_emailSupplier = null;
		}

		return email;
	}

	public void setEmail(String email) {
		this.email = email;

		_emailSupplier = null;
	}

	@JsonIgnore
	public void setEmail(
		UnsafeSupplier<String, Exception> emailUnsafeSupplier) {

		_emailSupplier = () -> {
			try {
				return emailUnsafeSupplier.get();
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
	@NotEmpty
	protected String email;

	@JsonIgnore
	private Supplier<String> _emailSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "User Name")
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

	@io.swagger.v3.oas.annotations.media.Schema(example = "UAB-34098-789-N")
	public String getUserExternalReferenceCode() {
		if (_userExternalReferenceCodeSupplier != null) {
			userExternalReferenceCode =
				_userExternalReferenceCodeSupplier.get();

			_userExternalReferenceCodeSupplier = null;
		}

		return userExternalReferenceCode;
	}

	public void setUserExternalReferenceCode(String userExternalReferenceCode) {
		this.userExternalReferenceCode = userExternalReferenceCode;

		_userExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setUserExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			userExternalReferenceCodeUnsafeSupplier) {

		_userExternalReferenceCodeSupplier = () -> {
			try {
				return userExternalReferenceCodeUnsafeSupplier.get();
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
	protected String userExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _userExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30002")
	public Long getUserId() {
		if (_userIdSupplier != null) {
			userId = _userIdSupplier.get();

			_userIdSupplier = null;
		}

		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;

		_userIdSupplier = null;
	}

	@JsonIgnore
	public void setUserId(
		UnsafeSupplier<Long, Exception> userIdUnsafeSupplier) {

		_userIdSupplier = () -> {
			try {
				return userIdUnsafeSupplier.get();
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
	protected Long userId;

	@JsonIgnore
	private Supplier<Long> _userIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountMember)) {
			return false;
		}

		AccountMember accountMember = (AccountMember)object;

		return Objects.equals(toString(), accountMember.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long accountId = getAccountId();

		if (accountId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(accountId);
		}

		AccountRole[] accountRoles = getAccountRoles();

		if (accountRoles != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountRoles\": ");

			sb.append("[");

			for (int i = 0; i < accountRoles.length; i++) {
				sb.append(String.valueOf(accountRoles[i]));

				if ((i + 1) < accountRoles.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String email = getEmail();

		if (email != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"email\": ");

			sb.append("\"");

			sb.append(_escape(email));

			sb.append("\"");
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

		String userExternalReferenceCode = getUserExternalReferenceCode();

		if (userExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(userExternalReferenceCode));

			sb.append("\"");
		}

		Long userId = getUserId();

		if (userId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(userId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.account.dto.v1_0.AccountMember",
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
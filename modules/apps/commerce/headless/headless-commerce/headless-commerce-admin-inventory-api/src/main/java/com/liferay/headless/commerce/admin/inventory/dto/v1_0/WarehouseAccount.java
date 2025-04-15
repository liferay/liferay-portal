/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.dto.v1_0;

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
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName("WarehouseAccount")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"accountId", "warehouseId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WarehouseAccount")
public class WarehouseAccount implements Serializable {

	public static WarehouseAccount toDTO(String json) {
		return ObjectMapperUtil.readValue(WarehouseAccount.class, json);
	}

	public static WarehouseAccount unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(WarehouseAccount.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Account getAccount() {
		if (_accountSupplier != null) {
			account = _accountSupplier.get();

			_accountSupplier = null;
		}

		return account;
	}

	public void setAccount(Account account) {
		this.account = account;

		_accountSupplier = null;
	}

	@JsonIgnore
	public void setAccount(
		UnsafeSupplier<Account, Exception> accountUnsafeSupplier) {

		_accountSupplier = () -> {
			try {
				return accountUnsafeSupplier.get();
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
	protected Account account;

	@JsonIgnore
	private Supplier<Account> _accountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DAB-34098-789-N")
	public String getAccountExternalReferenceCode() {
		if (_accountExternalReferenceCodeSupplier != null) {
			accountExternalReferenceCode =
				_accountExternalReferenceCodeSupplier.get();

			_accountExternalReferenceCodeSupplier = null;
		}

		return accountExternalReferenceCode;
	}

	public void setAccountExternalReferenceCode(
		String accountExternalReferenceCode) {

		this.accountExternalReferenceCode = accountExternalReferenceCode;

		_accountExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setAccountExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			accountExternalReferenceCodeUnsafeSupplier) {

		_accountExternalReferenceCodeSupplier = () -> {
			try {
				return accountExternalReferenceCodeUnsafeSupplier.get();
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
	protected String accountExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _accountExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30324")
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
	@NotNull
	protected Long accountId;

	@JsonIgnore
	private Supplier<Long> _accountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30643")
	public Long getWarehouseAccountId() {
		if (_warehouseAccountIdSupplier != null) {
			warehouseAccountId = _warehouseAccountIdSupplier.get();

			_warehouseAccountIdSupplier = null;
		}

		return warehouseAccountId;
	}

	public void setWarehouseAccountId(Long warehouseAccountId) {
		this.warehouseAccountId = warehouseAccountId;

		_warehouseAccountIdSupplier = null;
	}

	@JsonIgnore
	public void setWarehouseAccountId(
		UnsafeSupplier<Long, Exception> warehouseAccountIdUnsafeSupplier) {

		_warehouseAccountIdSupplier = () -> {
			try {
				return warehouseAccountIdUnsafeSupplier.get();
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
	protected Long warehouseAccountId;

	@JsonIgnore
	private Supplier<Long> _warehouseAccountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "PAB-34098-789-N")
	public String getWarehouseExternalReferenceCode() {
		if (_warehouseExternalReferenceCodeSupplier != null) {
			warehouseExternalReferenceCode =
				_warehouseExternalReferenceCodeSupplier.get();

			_warehouseExternalReferenceCodeSupplier = null;
		}

		return warehouseExternalReferenceCode;
	}

	public void setWarehouseExternalReferenceCode(
		String warehouseExternalReferenceCode) {

		this.warehouseExternalReferenceCode = warehouseExternalReferenceCode;

		_warehouseExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setWarehouseExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			warehouseExternalReferenceCodeUnsafeSupplier) {

		_warehouseExternalReferenceCodeSupplier = () -> {
			try {
				return warehouseExternalReferenceCodeUnsafeSupplier.get();
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
	protected String warehouseExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _warehouseExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getWarehouseId() {
		if (_warehouseIdSupplier != null) {
			warehouseId = _warehouseIdSupplier.get();

			_warehouseIdSupplier = null;
		}

		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;

		_warehouseIdSupplier = null;
	}

	@JsonIgnore
	public void setWarehouseId(
		UnsafeSupplier<Long, Exception> warehouseIdUnsafeSupplier) {

		_warehouseIdSupplier = () -> {
			try {
				return warehouseIdUnsafeSupplier.get();
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
	@NotNull
	protected Long warehouseId;

	@JsonIgnore
	private Supplier<Long> _warehouseIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WarehouseAccount)) {
			return false;
		}

		WarehouseAccount warehouseAccount = (WarehouseAccount)object;

		return Objects.equals(toString(), warehouseAccount.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Account account = getAccount();

		if (account != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"account\": ");

			sb.append(String.valueOf(account));
		}

		String accountExternalReferenceCode = getAccountExternalReferenceCode();

		if (accountExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(accountExternalReferenceCode));

			sb.append("\"");
		}

		Long accountId = getAccountId();

		if (accountId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(accountId);
		}

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Long warehouseAccountId = getWarehouseAccountId();

		if (warehouseAccountId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseAccountId\": ");

			sb.append(warehouseAccountId);
		}

		String warehouseExternalReferenceCode =
			getWarehouseExternalReferenceCode();

		if (warehouseExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(warehouseExternalReferenceCode));

			sb.append("\"");
		}

		Long warehouseId = getWarehouseId();

		if (warehouseId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseId\": ");

			sb.append(warehouseId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseAccount",
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
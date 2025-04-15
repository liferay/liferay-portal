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

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@GraphQLName("WarehouseAccountGroup")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"accountGroupId", "warehouseId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WarehouseAccountGroup")
public class WarehouseAccountGroup implements Serializable {

	public static WarehouseAccountGroup toDTO(String json) {
		return ObjectMapperUtil.readValue(WarehouseAccountGroup.class, json);
	}

	public static WarehouseAccountGroup unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			WarehouseAccountGroup.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public AccountGroup getAccountGroup() {
		if (_accountGroupSupplier != null) {
			accountGroup = _accountGroupSupplier.get();

			_accountGroupSupplier = null;
		}

		return accountGroup;
	}

	public void setAccountGroup(AccountGroup accountGroup) {
		this.accountGroup = accountGroup;

		_accountGroupSupplier = null;
	}

	@JsonIgnore
	public void setAccountGroup(
		UnsafeSupplier<AccountGroup, Exception> accountGroupUnsafeSupplier) {

		_accountGroupSupplier = () -> {
			try {
				return accountGroupUnsafeSupplier.get();
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
	protected AccountGroup accountGroup;

	@JsonIgnore
	private Supplier<AccountGroup> _accountGroupSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DAB-34098-789-N")
	public String getAccountGroupExternalReferenceCode() {
		if (_accountGroupExternalReferenceCodeSupplier != null) {
			accountGroupExternalReferenceCode =
				_accountGroupExternalReferenceCodeSupplier.get();

			_accountGroupExternalReferenceCodeSupplier = null;
		}

		return accountGroupExternalReferenceCode;
	}

	public void setAccountGroupExternalReferenceCode(
		String accountGroupExternalReferenceCode) {

		this.accountGroupExternalReferenceCode =
			accountGroupExternalReferenceCode;

		_accountGroupExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setAccountGroupExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			accountGroupExternalReferenceCodeUnsafeSupplier) {

		_accountGroupExternalReferenceCodeSupplier = () -> {
			try {
				return accountGroupExternalReferenceCodeUnsafeSupplier.get();
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
	protected String accountGroupExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _accountGroupExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30324")
	public Long getAccountGroupId() {
		if (_accountGroupIdSupplier != null) {
			accountGroupId = _accountGroupIdSupplier.get();

			_accountGroupIdSupplier = null;
		}

		return accountGroupId;
	}

	public void setAccountGroupId(Long accountGroupId) {
		this.accountGroupId = accountGroupId;

		_accountGroupIdSupplier = null;
	}

	@JsonIgnore
	public void setAccountGroupId(
		UnsafeSupplier<Long, Exception> accountGroupIdUnsafeSupplier) {

		_accountGroupIdSupplier = () -> {
			try {
				return accountGroupIdUnsafeSupplier.get();
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
	protected Long accountGroupId;

	@JsonIgnore
	private Supplier<Long> _accountGroupIdSupplier;

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
	public Long getWarehouseAccountGroupId() {
		if (_warehouseAccountGroupIdSupplier != null) {
			warehouseAccountGroupId = _warehouseAccountGroupIdSupplier.get();

			_warehouseAccountGroupIdSupplier = null;
		}

		return warehouseAccountGroupId;
	}

	public void setWarehouseAccountGroupId(Long warehouseAccountGroupId) {
		this.warehouseAccountGroupId = warehouseAccountGroupId;

		_warehouseAccountGroupIdSupplier = null;
	}

	@JsonIgnore
	public void setWarehouseAccountGroupId(
		UnsafeSupplier<Long, Exception> warehouseAccountGroupIdUnsafeSupplier) {

		_warehouseAccountGroupIdSupplier = () -> {
			try {
				return warehouseAccountGroupIdUnsafeSupplier.get();
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
	protected Long warehouseAccountGroupId;

	@JsonIgnore
	private Supplier<Long> _warehouseAccountGroupIdSupplier;

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

		if (!(object instanceof WarehouseAccountGroup)) {
			return false;
		}

		WarehouseAccountGroup warehouseAccountGroup =
			(WarehouseAccountGroup)object;

		return Objects.equals(toString(), warehouseAccountGroup.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		AccountGroup accountGroup = getAccountGroup();

		if (accountGroup != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroup\": ");

			sb.append(String.valueOf(accountGroup));
		}

		String accountGroupExternalReferenceCode =
			getAccountGroupExternalReferenceCode();

		if (accountGroupExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(accountGroupExternalReferenceCode));

			sb.append("\"");
		}

		Long accountGroupId = getAccountGroupId();

		if (accountGroupId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupId\": ");

			sb.append(accountGroupId);
		}

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Long warehouseAccountGroupId = getWarehouseAccountGroupId();

		if (warehouseAccountGroupId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseAccountGroupId\": ");

			sb.append(warehouseAccountGroupId);
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
		defaultValue = "com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseAccountGroup",
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
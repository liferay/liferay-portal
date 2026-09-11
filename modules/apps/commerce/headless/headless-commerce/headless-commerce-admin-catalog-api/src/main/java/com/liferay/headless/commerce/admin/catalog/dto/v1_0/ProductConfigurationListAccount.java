/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

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

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Association between a configuration list template and an account, qualifying the template to apply only when the cart's account matches.",
	value = "ProductConfigurationListAccount"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Association between a configuration list template and an account, qualifying the template to apply only when the cart's account matches.",
	requiredProperties = {"accountId", "productConfigurationListId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductConfigurationListAccount")
public class ProductConfigurationListAccount implements Serializable {

	public static ProductConfigurationListAccount toDTO(String json) {
		return ObjectMapperUtil.readValue(
			ProductConfigurationListAccount.class, json);
	}

	public static ProductConfigurationListAccount unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProductConfigurationListAccount.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Lightweight projection of the linked account such as identifier, name, and logo identifier for client convenience; read-only."
	)
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

	@GraphQLField(
		description = "Lightweight projection of the linked account such as identifier, name, and logo identifier for client convenience; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Account account;

	@JsonIgnore
	private Supplier<Account> _accountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the account; on create, it is resolved first and falls back to `accountId`.",
		example = "DAB-34098-789-N"
	)
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

	@GraphQLField(
		description = "External reference code of the account; on create, it is resolved first and falls back to `accountId`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String accountExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _accountExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the linked account; required when `accountExternalReferenceCode` does not resolve.",
		example = "30324"
	)
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

	@GraphQLField(
		description = "Identifier of the linked account; required when `accountExternalReferenceCode` does not resolve."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Long accountId;

	@JsonIgnore
	private Supplier<Long> _accountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Map of available operations for the current user keyed by action name (typically `delete` only); each entry carries the URL template and HTTP method; read-only."
	)
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

	@GraphQLField(
		description = "Map of available operations for the current user keyed by action name (typically `delete` only); each entry carries the URL template and HTTP method; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of this account-to-configuration-list association; read-only.",
		example = "30643"
	)
	public Long getProductConfigurationListAccountId() {
		if (_productConfigurationListAccountIdSupplier != null) {
			productConfigurationListAccountId =
				_productConfigurationListAccountIdSupplier.get();

			_productConfigurationListAccountIdSupplier = null;
		}

		return productConfigurationListAccountId;
	}

	public void setProductConfigurationListAccountId(
		Long productConfigurationListAccountId) {

		this.productConfigurationListAccountId =
			productConfigurationListAccountId;

		_productConfigurationListAccountIdSupplier = null;
	}

	@JsonIgnore
	public void setProductConfigurationListAccountId(
		UnsafeSupplier<Long, Exception>
			productConfigurationListAccountIdUnsafeSupplier) {

		_productConfigurationListAccountIdSupplier = () -> {
			try {
				return productConfigurationListAccountIdUnsafeSupplier.get();
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
		description = "Identifier of this account-to-configuration-list association; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long productConfigurationListAccountId;

	@JsonIgnore
	private Supplier<Long> _productConfigurationListAccountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the parent configuration list, surfaced for client navigation; read-only.",
		example = "PAB-34098-789-N"
	)
	public String getProductConfigurationListExternalReferenceCode() {
		if (_productConfigurationListExternalReferenceCodeSupplier != null) {
			productConfigurationListExternalReferenceCode =
				_productConfigurationListExternalReferenceCodeSupplier.get();

			_productConfigurationListExternalReferenceCodeSupplier = null;
		}

		return productConfigurationListExternalReferenceCode;
	}

	public void setProductConfigurationListExternalReferenceCode(
		String productConfigurationListExternalReferenceCode) {

		this.productConfigurationListExternalReferenceCode =
			productConfigurationListExternalReferenceCode;

		_productConfigurationListExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setProductConfigurationListExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productConfigurationListExternalReferenceCodeUnsafeSupplier) {

		_productConfigurationListExternalReferenceCodeSupplier = () -> {
			try {
				return productConfigurationListExternalReferenceCodeUnsafeSupplier.
					get();
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
		description = "External reference code of the parent configuration list, surfaced for client navigation; read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String productConfigurationListExternalReferenceCode;

	@JsonIgnore
	private Supplier<String>
		_productConfigurationListExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Identifier of the parent configuration list; required on create to anchor the relation.",
		example = "30130"
	)
	public Long getProductConfigurationListId() {
		if (_productConfigurationListIdSupplier != null) {
			productConfigurationListId =
				_productConfigurationListIdSupplier.get();

			_productConfigurationListIdSupplier = null;
		}

		return productConfigurationListId;
	}

	public void setProductConfigurationListId(Long productConfigurationListId) {
		this.productConfigurationListId = productConfigurationListId;

		_productConfigurationListIdSupplier = null;
	}

	@JsonIgnore
	public void setProductConfigurationListId(
		UnsafeSupplier<Long, Exception>
			productConfigurationListIdUnsafeSupplier) {

		_productConfigurationListIdSupplier = () -> {
			try {
				return productConfigurationListIdUnsafeSupplier.get();
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
		description = "Identifier of the parent configuration list; required on create to anchor the relation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Long productConfigurationListId;

	@JsonIgnore
	private Supplier<Long> _productConfigurationListIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductConfigurationListAccount)) {
			return false;
		}

		ProductConfigurationListAccount productConfigurationListAccount =
			(ProductConfigurationListAccount)object;

		return Objects.equals(
			toString(), productConfigurationListAccount.toString());
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

		Long productConfigurationListAccountId =
			getProductConfigurationListAccountId();

		if (productConfigurationListAccountId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListAccountId\": ");

			sb.append(productConfigurationListAccountId);
		}

		String productConfigurationListExternalReferenceCode =
			getProductConfigurationListExternalReferenceCode();

		if (productConfigurationListExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(productConfigurationListExternalReferenceCode));

			sb.append("\"");
		}

		Long productConfigurationListId = getProductConfigurationListId();

		if (productConfigurationListId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListId\": ");

			sb.append(productConfigurationListId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListAccount",
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
// LIFERAY-REST-BUILDER-HASH:1944782751
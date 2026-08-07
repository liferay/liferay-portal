/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

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
	description = "Versioned product catalog scoped to a company and tied to a Liferay site group. Create, read, update, and delete operations from the catalog admin perspective; the master catalog and any catalog whose `system` flag is true are managed by the platform and cannot be deleted through the standard delete path.",
	value = "Catalog"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Versioned product catalog scoped to a company and tied to a Liferay site group. Create, read, update, and delete operations from the catalog admin perspective; the master catalog and any catalog whose `system` flag is true are managed by the platform and cannot be deleted through the standard delete path.",
	requiredProperties = {"name"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Catalog")
public class Catalog implements Serializable {

	public static Catalog toDTO(String json) {
		return ObjectMapperUtil.readValue(Catalog.class, json);
	}

	public static Catalog unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Catalog.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the account that owns this catalog; it takes precedence over `accountId`. An unresolved code fails the request, except during an import, where it creates an empty account to be completed later.",
		example = "AB-34098-789-N"
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
		description = "External reference code of the account that owns this catalog; it takes precedence over `accountId`. An unresolved code fails the request, except during an import, where it creates an empty account to be completed later."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String accountExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _accountExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the account that owns this catalog (FK identifier); defaults to no account when omitted on create. The linked account must be a supplier account with approved status; other account types or statuses are rejected.",
		example = "30130"
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
		description = "Reference to the account that owns this catalog (FK identifier); defaults to no account when omitted on create. The linked account must be a supplier account with approved status; other account types or statuses are rejected."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long accountId;

	@JsonIgnore
	private Supplier<Long> _accountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Type of the account named by `accountExternalReferenceCode`; both fields are needed to resolve an account by code, and when this one is omitted the code is ignored and `accountId` is used instead. The type cannot be defaulted because it cannot be changed after an account is created, and a catalog accepts only a supplier account."
	)
	@JsonGetter("accountType")
	@Valid
	public AccountType getAccountType() {
		if (_accountTypeSupplier != null) {
			accountType = _accountTypeSupplier.get();

			_accountTypeSupplier = null;
		}

		return accountType;
	}

	@JsonIgnore
	public String getAccountTypeAsString() {
		AccountType accountType = getAccountType();

		if (accountType == null) {
			return null;
		}

		return accountType.toString();
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;

		_accountTypeSupplier = null;
	}

	@JsonIgnore
	public void setAccountType(
		UnsafeSupplier<AccountType, Exception> accountTypeUnsafeSupplier) {

		_accountTypeSupplier = () -> {
			try {
				return accountTypeUnsafeSupplier.get();
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
		description = "Type of the account named by `accountExternalReferenceCode`; both fields are needed to resolve an account by code, and when this one is omitted the code is ignored and `accountId` is used instead. The type cannot be defaulted because it cannot be changed after an account is created, and a catalog accepts only a supplier account."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected AccountType accountType;

	@JsonIgnore
	private Supplier<AccountType> _accountTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
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
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ISO 4217 code of the catalog's default currency; on write the catalog's default currency can be identified by `currencyCode`, `currencyExternalReferenceCode`, or `currencyId`.",
		example = "USD"
	)
	public String getCurrencyCode() {
		if (_currencyCodeSupplier != null) {
			currencyCode = _currencyCodeSupplier.get();

			_currencyCodeSupplier = null;
		}

		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;

		_currencyCodeSupplier = null;
	}

	@JsonIgnore
	public void setCurrencyCode(
		UnsafeSupplier<String, Exception> currencyCodeUnsafeSupplier) {

		_currencyCodeSupplier = () -> {
			try {
				return currencyCodeUnsafeSupplier.get();
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
		description = "ISO 4217 code of the catalog's default currency; on write the catalog's default currency can be identified by `currencyCode`, `currencyExternalReferenceCode`, or `currencyId`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String currencyCode;

	@JsonIgnore
	private Supplier<String> _currencyCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the catalog's default currency; alternative write-time identifier used together with `currencyCode` and `currencyId`.",
		example = "AB-34098-789-N"
	)
	public String getCurrencyExternalReferenceCode() {
		if (_currencyExternalReferenceCodeSupplier != null) {
			currencyExternalReferenceCode =
				_currencyExternalReferenceCodeSupplier.get();

			_currencyExternalReferenceCodeSupplier = null;
		}

		return currencyExternalReferenceCode;
	}

	public void setCurrencyExternalReferenceCode(
		String currencyExternalReferenceCode) {

		this.currencyExternalReferenceCode = currencyExternalReferenceCode;

		_currencyExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setCurrencyExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			currencyExternalReferenceCodeUnsafeSupplier) {

		_currencyExternalReferenceCodeSupplier = () -> {
			try {
				return currencyExternalReferenceCodeUnsafeSupplier.get();
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
		description = "External reference code of the catalog's default currency; alternative write-time identifier used together with `currencyCode` and `currencyId`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String currencyExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _currencyExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the catalog's default currency (FK identifier); alternative write-time identifier used when `currencyCode` and `currencyExternalReferenceCode` are not supplied.",
		example = "30130"
	)
	public Long getCurrencyId() {
		if (_currencyIdSupplier != null) {
			currencyId = _currencyIdSupplier.get();

			_currencyIdSupplier = null;
		}

		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;

		_currencyIdSupplier = null;
	}

	@JsonIgnore
	public void setCurrencyId(
		UnsafeSupplier<Long, Exception> currencyIdUnsafeSupplier) {

		_currencyIdSupplier = () -> {
			try {
				return currencyIdUnsafeSupplier.get();
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
		description = "Reference to the catalog's default currency (FK identifier); alternative write-time identifier used when `currencyCode` and `currencyExternalReferenceCode` are not supplied."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long currencyId;

	@JsonIgnore
	private Supplier<Long> _currencyIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Liferay locale code used as the catalog's default language for localized product content.",
		example = "en_US"
	)
	public String getDefaultLanguageId() {
		if (_defaultLanguageIdSupplier != null) {
			defaultLanguageId = _defaultLanguageIdSupplier.get();

			_defaultLanguageIdSupplier = null;
		}

		return defaultLanguageId;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		this.defaultLanguageId = defaultLanguageId;

		_defaultLanguageIdSupplier = null;
	}

	@JsonIgnore
	public void setDefaultLanguageId(
		UnsafeSupplier<String, Exception> defaultLanguageIdUnsafeSupplier) {

		_defaultLanguageIdSupplier = () -> {
			try {
				return defaultLanguageIdUnsafeSupplier.get();
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
		description = "Liferay locale code used as the catalog's default language for localized product content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String defaultLanguageId;

	@JsonIgnore
	private Supplier<String> _defaultLanguageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique per catalog within the company; POST upserts when a catalog with this code already exists, and PUT by external reference code creates or replaces.",
		example = "AB-34098-789-N"
	)
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

	@GraphQLField(
		description = "Idempotency key for create and update; must be unique per catalog within the company; POST upserts when a catalog with this code already exists, and PUT by external reference code creates or replaces."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Auto-generated catalog identifier; read-only.",
		example = "30130"
	)
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Auto-generated catalog identifier; read-only.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display name of the catalog; required on create and used to seed the localized name map for the backing Liferay site group; matched by the search query parameter and filterable and sortable through the OData query parameter.",
		example = "Default Catalog"
	)
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

	@GraphQLField(
		description = "Display name of the catalog; required on create and used to seed the localized name map for the backing Liferay site group; matched by the search query parameter and filterable and sortable through the OData query parameter."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Indicates that the catalog is platform-managed (the master catalog and similar); read-only; deletion of system catalogs is blocked.",
		example = "false"
	)
	public Boolean getSystem() {
		if (_systemSupplier != null) {
			system = _systemSupplier.get();

			_systemSupplier = null;
		}

		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;

		_systemSupplier = null;
	}

	@JsonIgnore
	public void setSystem(
		UnsafeSupplier<Boolean, Exception> systemUnsafeSupplier) {

		_systemSupplier = () -> {
			try {
				return systemUnsafeSupplier.get();
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
		description = "Indicates that the catalog is platform-managed (the master catalog and similar); read-only; deletion of system catalogs is blocked."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean system;

	@JsonIgnore
	private Supplier<Boolean> _systemSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Catalog)) {
			return false;
		}

		Catalog catalog = (Catalog)object;

		return Objects.equals(toString(), catalog.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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

		AccountType accountType = getAccountType();

		if (accountType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountType\": ");

			sb.append("\"");
			sb.append(accountType);
			sb.append("\"");
		}

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		String currencyCode = getCurrencyCode();

		if (currencyCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyCode\": ");

			sb.append("\"");

			sb.append(_escape(currencyCode));

			sb.append("\"");
		}

		String currencyExternalReferenceCode =
			getCurrencyExternalReferenceCode();

		if (currencyExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(currencyExternalReferenceCode));

			sb.append("\"");
		}

		Long currencyId = getCurrencyId();

		if (currencyId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyId\": ");

			sb.append(currencyId);
		}

		String defaultLanguageId = getDefaultLanguageId();

		if (defaultLanguageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(defaultLanguageId));

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

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
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

		Boolean system = getSystem();

		if (system != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(system);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.Catalog",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("AccountType")
	public static enum AccountType {

		BUSINESS("business"), GUEST("guest"), PERSON("person"),
		SUPPLIER("supplier");

		@JsonCreator
		public static AccountType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (AccountType accountType : values()) {
				if (Objects.equals(accountType.getValue(), value)) {
					return accountType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private AccountType(String value) {
			_value = value;
		}

		private final String _value;

	}

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
// LIFERAY-REST-BUILDER-HASH:345677316
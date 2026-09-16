/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

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
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "An account moving from one account lifecycle stage to another. An account that moved several times in the requested date range appears once per move.",
	value = "AccountLifecycleStageTransition"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "An account moving from one account lifecycle stage to another. An account that moved several times in the requested date range appears once per move."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AccountLifecycleStageTransition")
public class AccountLifecycleStageTransition implements Serializable {

	public static AccountLifecycleStageTransition toDTO(String json) {
		return ObjectMapperUtil.readValue(
			AccountLifecycleStageTransition.class, json);
	}

	public static AccountLifecycleStageTransition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			AccountLifecycleStageTransition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Account ID. Use this with `getWorkspaceGroupAccount` to fetch the account."
	)
	public String getAccountId() {
		if (_accountIdSupplier != null) {
			accountId = _accountIdSupplier.get();

			_accountIdSupplier = null;
		}

		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;

		_accountIdSupplier = null;
	}

	@JsonIgnore
	public void setAccountId(
		UnsafeSupplier<String, Exception> accountIdUnsafeSupplier) {

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
		description = "Account ID. Use this with `getWorkspaceGroupAccount` to fetch the account."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String accountId;

	@JsonIgnore
	private Supplier<String> _accountIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Name of the account."
	)
	public String getAccountName() {
		if (_accountNameSupplier != null) {
			accountName = _accountNameSupplier.get();

			_accountNameSupplier = null;
		}

		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;

		_accountNameSupplier = null;
	}

	@JsonIgnore
	public void setAccountName(
		UnsafeSupplier<String, Exception> accountNameUnsafeSupplier) {

		_accountNameSupplier = () -> {
			try {
				return accountNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Name of the account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String accountName;

	@JsonIgnore
	private Supplier<String> _accountNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Stage the account left."
	)
	@Valid
	public AccountLifecycleStage getFromAccountLifecycleStage() {
		if (_fromAccountLifecycleStageSupplier != null) {
			fromAccountLifecycleStage =
				_fromAccountLifecycleStageSupplier.get();

			_fromAccountLifecycleStageSupplier = null;
		}

		return fromAccountLifecycleStage;
	}

	public void setFromAccountLifecycleStage(
		AccountLifecycleStage fromAccountLifecycleStage) {

		this.fromAccountLifecycleStage = fromAccountLifecycleStage;

		_fromAccountLifecycleStageSupplier = null;
	}

	@JsonIgnore
	public void setFromAccountLifecycleStage(
		UnsafeSupplier<AccountLifecycleStage, Exception>
			fromAccountLifecycleStageUnsafeSupplier) {

		_fromAccountLifecycleStageSupplier = () -> {
			try {
				return fromAccountLifecycleStageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Stage the account left.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AccountLifecycleStage fromAccountLifecycleStage;

	@JsonIgnore
	private Supplier<AccountLifecycleStage> _fromAccountLifecycleStageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Stage the account entered."
	)
	@Valid
	public AccountLifecycleStage getToAccountLifecycleStage() {
		if (_toAccountLifecycleStageSupplier != null) {
			toAccountLifecycleStage = _toAccountLifecycleStageSupplier.get();

			_toAccountLifecycleStageSupplier = null;
		}

		return toAccountLifecycleStage;
	}

	public void setToAccountLifecycleStage(
		AccountLifecycleStage toAccountLifecycleStage) {

		this.toAccountLifecycleStage = toAccountLifecycleStage;

		_toAccountLifecycleStageSupplier = null;
	}

	@JsonIgnore
	public void setToAccountLifecycleStage(
		UnsafeSupplier<AccountLifecycleStage, Exception>
			toAccountLifecycleStageUnsafeSupplier) {

		_toAccountLifecycleStageSupplier = () -> {
			try {
				return toAccountLifecycleStageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Stage the account entered.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AccountLifecycleStage toAccountLifecycleStage;

	@JsonIgnore
	private Supplier<AccountLifecycleStage> _toAccountLifecycleStageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the account entered the new stage."
	)
	public Date getTransitionDate() {
		if (_transitionDateSupplier != null) {
			transitionDate = _transitionDateSupplier.get();

			_transitionDateSupplier = null;
		}

		return transitionDate;
	}

	public void setTransitionDate(Date transitionDate) {
		this.transitionDate = transitionDate;

		_transitionDateSupplier = null;
	}

	@JsonIgnore
	public void setTransitionDate(
		UnsafeSupplier<Date, Exception> transitionDateUnsafeSupplier) {

		_transitionDateSupplier = () -> {
			try {
				return transitionDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "When the account entered the new stage.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date transitionDate;

	@JsonIgnore
	private Supplier<Date> _transitionDateSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountLifecycleStageTransition)) {
			return false;
		}

		AccountLifecycleStageTransition accountLifecycleStageTransition =
			(AccountLifecycleStageTransition)object;

		return Objects.equals(
			toString(), accountLifecycleStageTransition.toString());
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

		String accountId = getAccountId();

		if (accountId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append("\"");

			sb.append(_escape(accountId));

			sb.append("\"");
		}

		String accountName = getAccountName();

		if (accountName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountName\": ");

			sb.append("\"");

			sb.append(_escape(accountName));

			sb.append("\"");
		}

		AccountLifecycleStage fromAccountLifecycleStage =
			getFromAccountLifecycleStage();

		if (fromAccountLifecycleStage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fromAccountLifecycleStage\": ");

			sb.append(String.valueOf(fromAccountLifecycleStage));
		}

		AccountLifecycleStage toAccountLifecycleStage =
			getToAccountLifecycleStage();

		if (toAccountLifecycleStage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"toAccountLifecycleStage\": ");

			sb.append(String.valueOf(toAccountLifecycleStage));
		}

		Date transitionDate = getTransitionDate();

		if (transitionDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transitionDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(transitionDate));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageTransition",
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
// LIFERAY-REST-BUILDER-HASH:1099544326
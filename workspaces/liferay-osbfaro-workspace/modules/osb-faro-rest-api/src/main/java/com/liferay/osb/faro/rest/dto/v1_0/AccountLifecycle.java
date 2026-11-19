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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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

import java.util.Arrays;
import java.util.Collection;
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
	description = "An account lifecycle of a channel: the ordered stages accounts move through (e.g. 'Aware', 'Engaged', 'Pipeline'). Use `getWorkspaceGroupChannelAccountLifecyclesPage` to list them.",
	value = "AccountLifecycle"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "An account lifecycle of a channel: the ordered stages accounts move through (e.g. 'Aware', 'Engaged', 'Pipeline'). Use `getWorkspaceGroupChannelAccountLifecyclesPage` to list them."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AccountLifecycle")
public class AccountLifecycle implements Serializable {

	public static AccountLifecycle toDTO(String json) {
		return ObjectMapperUtil.readValue(AccountLifecycle.class, json);
	}

	public static AccountLifecycle unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AccountLifecycle.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Stages of the lifecycle, in display order."
	)
	@Valid
	public AccountLifecycleStage[] getAccountLifecycleStages() {
		if (_accountLifecycleStagesSupplier != null) {
			accountLifecycleStages = _accountLifecycleStagesSupplier.get();

			_accountLifecycleStagesSupplier = null;
		}

		return accountLifecycleStages;
	}

	public void setAccountLifecycleStages(
		AccountLifecycleStage[] accountLifecycleStages) {

		this.accountLifecycleStages = accountLifecycleStages;

		_accountLifecycleStagesSupplier = null;
	}

	@JsonIgnore
	public void setAccountLifecycleStages(
		UnsafeSupplier<AccountLifecycleStage[], Exception>
			accountLifecycleStagesUnsafeSupplier) {

		_accountLifecycleStagesSupplier = () -> {
			try {
				return accountLifecycleStagesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Stages of the lifecycle, in display order.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AccountLifecycleStage[] accountLifecycleStages;

	@JsonIgnore
	private Supplier<AccountLifecycleStage[]> _accountLifecycleStagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Last time the lifecycle was modified."
	)
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Last time the lifecycle was modified.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "True if the lifecycle is the default of the channel. A channel can have several lifecycles but only one default, which `getWorkspaceGroupChannelAccountsPage` uses when `accountLifecycleId` is omitted."
	)
	public Boolean getDefaultAccountLifecycle() {
		if (_defaultAccountLifecycleSupplier != null) {
			defaultAccountLifecycle = _defaultAccountLifecycleSupplier.get();

			_defaultAccountLifecycleSupplier = null;
		}

		return defaultAccountLifecycle;
	}

	public void setDefaultAccountLifecycle(Boolean defaultAccountLifecycle) {
		this.defaultAccountLifecycle = defaultAccountLifecycle;

		_defaultAccountLifecycleSupplier = null;
	}

	@JsonIgnore
	public void setDefaultAccountLifecycle(
		UnsafeSupplier<Boolean, Exception>
			defaultAccountLifecycleUnsafeSupplier) {

		_defaultAccountLifecycleSupplier = () -> {
			try {
				return defaultAccountLifecycleUnsafeSupplier.get();
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
		description = "True if the lifecycle is the default of the channel. A channel can have several lifecycles but only one default, which `getWorkspaceGroupChannelAccountsPage` uses when `accountLifecycleId` is omitted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean defaultAccountLifecycle;

	@JsonIgnore
	private Supplier<Boolean> _defaultAccountLifecycleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Description of the lifecycle."
	)
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Description of the lifecycle.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Account lifecycle ID. Use it as `accountLifecycleId` to list the stage metrics and stage transitions of the lifecycle, to fetch the stage history of an account, or to filter accounts by lifecycle stage."
	)
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
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

	@GraphQLField(
		description = "Account lifecycle ID. Use it as `accountLifecycleId` to list the stage metrics and stage transitions of the lifecycle, to fetch the stage history of an account, or to filter accounts by lifecycle stage."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Name of the lifecycle."
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

	@GraphQLField(description = "Name of the lifecycle.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountLifecycle)) {
			return false;
		}

		AccountLifecycle accountLifecycle = (AccountLifecycle)object;

		return Objects.equals(toString(), accountLifecycle.toString());
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

		AccountLifecycleStage[] accountLifecycleStages =
			getAccountLifecycleStages();

		if (accountLifecycleStages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountLifecycleStages\": ");

			sb.append("[");

			for (int i = 0; i < accountLifecycleStages.length; i++) {
				sb.append(String.valueOf(accountLifecycleStages[i]));

				if ((i + 1) < accountLifecycleStages.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

			sb.append("\"");
		}

		Boolean defaultAccountLifecycle = getDefaultAccountLifecycle();

		if (defaultAccountLifecycle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultAccountLifecycle\": ");

			sb.append(defaultAccountLifecycle);
		}

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycle",
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
// LIFERAY-REST-BUILDER-HASH:-725711966
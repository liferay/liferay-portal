/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
@GraphQLName("ContactConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ContactConfiguration")
public class ContactConfiguration implements Serializable {

	public static ContactConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(ContactConfiguration.class, json);
	}

	public static ContactConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ContactConfiguration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSyncAllAccounts() {
		if (_syncAllAccountsSupplier != null) {
			syncAllAccounts = _syncAllAccountsSupplier.get();

			_syncAllAccountsSupplier = null;
		}

		return syncAllAccounts;
	}

	public void setSyncAllAccounts(Boolean syncAllAccounts) {
		this.syncAllAccounts = syncAllAccounts;

		_syncAllAccountsSupplier = null;
	}

	@JsonIgnore
	public void setSyncAllAccounts(
		UnsafeSupplier<Boolean, Exception> syncAllAccountsUnsafeSupplier) {

		_syncAllAccountsSupplier = () -> {
			try {
				return syncAllAccountsUnsafeSupplier.get();
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
	protected Boolean syncAllAccounts;

	@JsonIgnore
	private Supplier<Boolean> _syncAllAccountsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSyncAllContacts() {
		if (_syncAllContactsSupplier != null) {
			syncAllContacts = _syncAllContactsSupplier.get();

			_syncAllContactsSupplier = null;
		}

		return syncAllContacts;
	}

	public void setSyncAllContacts(Boolean syncAllContacts) {
		this.syncAllContacts = syncAllContacts;

		_syncAllContactsSupplier = null;
	}

	@JsonIgnore
	public void setSyncAllContacts(
		UnsafeSupplier<Boolean, Exception> syncAllContactsUnsafeSupplier) {

		_syncAllContactsSupplier = () -> {
			try {
				return syncAllContactsUnsafeSupplier.get();
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
	protected Boolean syncAllContacts;

	@JsonIgnore
	private Supplier<Boolean> _syncAllContactsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getSyncedAccountGroupIds() {
		if (_syncedAccountGroupIdsSupplier != null) {
			syncedAccountGroupIds = _syncedAccountGroupIdsSupplier.get();

			_syncedAccountGroupIdsSupplier = null;
		}

		return syncedAccountGroupIds;
	}

	public void setSyncedAccountGroupIds(String[] syncedAccountGroupIds) {
		this.syncedAccountGroupIds = syncedAccountGroupIds;

		_syncedAccountGroupIdsSupplier = null;
	}

	@JsonIgnore
	public void setSyncedAccountGroupIds(
		UnsafeSupplier<String[], Exception>
			syncedAccountGroupIdsUnsafeSupplier) {

		_syncedAccountGroupIdsSupplier = () -> {
			try {
				return syncedAccountGroupIdsUnsafeSupplier.get();
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
	protected String[] syncedAccountGroupIds;

	@JsonIgnore
	private Supplier<String[]> _syncedAccountGroupIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getSyncedOrganizationIds() {
		if (_syncedOrganizationIdsSupplier != null) {
			syncedOrganizationIds = _syncedOrganizationIdsSupplier.get();

			_syncedOrganizationIdsSupplier = null;
		}

		return syncedOrganizationIds;
	}

	public void setSyncedOrganizationIds(String[] syncedOrganizationIds) {
		this.syncedOrganizationIds = syncedOrganizationIds;

		_syncedOrganizationIdsSupplier = null;
	}

	@JsonIgnore
	public void setSyncedOrganizationIds(
		UnsafeSupplier<String[], Exception>
			syncedOrganizationIdsUnsafeSupplier) {

		_syncedOrganizationIdsSupplier = () -> {
			try {
				return syncedOrganizationIdsUnsafeSupplier.get();
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
	protected String[] syncedOrganizationIds;

	@JsonIgnore
	private Supplier<String[]> _syncedOrganizationIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getSyncedUserGroupIds() {
		if (_syncedUserGroupIdsSupplier != null) {
			syncedUserGroupIds = _syncedUserGroupIdsSupplier.get();

			_syncedUserGroupIdsSupplier = null;
		}

		return syncedUserGroupIds;
	}

	public void setSyncedUserGroupIds(String[] syncedUserGroupIds) {
		this.syncedUserGroupIds = syncedUserGroupIds;

		_syncedUserGroupIdsSupplier = null;
	}

	@JsonIgnore
	public void setSyncedUserGroupIds(
		UnsafeSupplier<String[], Exception> syncedUserGroupIdsUnsafeSupplier) {

		_syncedUserGroupIdsSupplier = () -> {
			try {
				return syncedUserGroupIdsUnsafeSupplier.get();
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
	protected String[] syncedUserGroupIds;

	@JsonIgnore
	private Supplier<String[]> _syncedUserGroupIdsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContactConfiguration)) {
			return false;
		}

		ContactConfiguration contactConfiguration =
			(ContactConfiguration)object;

		return Objects.equals(toString(), contactConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean syncAllAccounts = getSyncAllAccounts();

		if (syncAllAccounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"syncAllAccounts\": ");

			sb.append(syncAllAccounts);
		}

		Boolean syncAllContacts = getSyncAllContacts();

		if (syncAllContacts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"syncAllContacts\": ");

			sb.append(syncAllContacts);
		}

		String[] syncedAccountGroupIds = getSyncedAccountGroupIds();

		if (syncedAccountGroupIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"syncedAccountGroupIds\": ");

			sb.append("[");

			for (int i = 0; i < syncedAccountGroupIds.length; i++) {
				sb.append("\"");

				sb.append(_escape(syncedAccountGroupIds[i]));

				sb.append("\"");

				if ((i + 1) < syncedAccountGroupIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] syncedOrganizationIds = getSyncedOrganizationIds();

		if (syncedOrganizationIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"syncedOrganizationIds\": ");

			sb.append("[");

			for (int i = 0; i < syncedOrganizationIds.length; i++) {
				sb.append("\"");

				sb.append(_escape(syncedOrganizationIds[i]));

				sb.append("\"");

				if ((i + 1) < syncedOrganizationIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] syncedUserGroupIds = getSyncedUserGroupIds();

		if (syncedUserGroupIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"syncedUserGroupIds\": ");

			sb.append("[");

			for (int i = 0; i < syncedUserGroupIds.length; i++) {
				sb.append("\"");

				sb.append(_escape(syncedUserGroupIds[i]));

				sb.append("\"");

				if ((i + 1) < syncedUserGroupIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.analytics.settings.rest.dto.v1_0.ContactConfiguration",
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
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The widget instance's permissions.",
	value = "WidgetPermission"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WidgetPermission")
public class WidgetPermission implements Serializable {

	public static WidgetPermission toDTO(String json) {
		return ObjectMapperUtil.readValue(WidgetPermission.class, json);
	}

	public static WidgetPermission unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(WidgetPermission.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The keys of the actions the role has permission for."
	)
	public String[] getActionKeys() {
		if (_actionKeysSupplier != null) {
			actionKeys = _actionKeysSupplier.get();

			_actionKeysSupplier = null;
		}

		return actionKeys;
	}

	public void setActionKeys(String[] actionKeys) {
		this.actionKeys = actionKeys;

		_actionKeysSupplier = null;
	}

	@JsonIgnore
	public void setActionKeys(
		UnsafeSupplier<String[], Exception> actionKeysUnsafeSupplier) {

		_actionKeysSupplier = () -> {
			try {
				return actionKeysUnsafeSupplier.get();
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
		description = "The keys of the actions the role has permission for."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] actionKeys;

	@JsonIgnore
	private Supplier<String[]> _actionKeysSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(description = "The role's key.")
	public String getRoleKey() {
		if (_roleKeySupplier != null) {
			roleKey = _roleKeySupplier.get();

			_roleKeySupplier = null;
		}

		return roleKey;
	}

	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;

		_roleKeySupplier = null;
	}

	@JsonIgnore
	public void setRoleKey(
		UnsafeSupplier<String, Exception> roleKeyUnsafeSupplier) {

		_roleKeySupplier = () -> {
			try {
				return roleKeyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The role's key.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String roleKey;

	@JsonIgnore
	private Supplier<String> _roleKeySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPermission)) {
			return false;
		}

		WidgetPermission widgetPermission = (WidgetPermission)object;

		return Objects.equals(toString(), widgetPermission.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String[] actionKeys = getActionKeys();

		if (actionKeys != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionKeys\": ");

			sb.append("[");

			for (int i = 0; i < actionKeys.length; i++) {
				sb.append("\"");

				sb.append(_escape(actionKeys[i]));

				sb.append("\"");

				if ((i + 1) < actionKeys.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String roleKey = getRoleKey();

		if (roleKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleKey\": ");

			sb.append("\"");

			sb.append(_escape(roleKey));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.WidgetPermission",
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
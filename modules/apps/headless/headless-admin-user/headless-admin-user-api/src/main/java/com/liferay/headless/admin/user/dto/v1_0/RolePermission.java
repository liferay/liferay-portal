/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("RolePermission")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "RolePermission")
public class RolePermission implements Serializable {

	public static RolePermission toDTO(String json) {
		return ObjectMapperUtil.readValue(RolePermission.class, json);
	}

	public static RolePermission unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(RolePermission.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getActionIds() {
		if (_actionIdsSupplier != null) {
			actionIds = _actionIdsSupplier.get();

			_actionIdsSupplier = null;
		}

		return actionIds;
	}

	public void setActionIds(String[] actionIds) {
		this.actionIds = actionIds;

		_actionIdsSupplier = null;
	}

	@JsonIgnore
	public void setActionIds(
		UnsafeSupplier<String[], Exception> actionIdsUnsafeSupplier) {

		_actionIdsSupplier = () -> {
			try {
				return actionIdsUnsafeSupplier.get();
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
	protected String[] actionIds;

	@JsonIgnore
	private Supplier<String[]> _actionIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	public void setLabel(String label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		_labelSupplier = () -> {
			try {
				return labelUnsafeSupplier.get();
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
	protected String label;

	@JsonIgnore
	private Supplier<String> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPrimaryKey() {
		if (_primaryKeySupplier != null) {
			primaryKey = _primaryKeySupplier.get();

			_primaryKeySupplier = null;
		}

		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;

		_primaryKeySupplier = null;
	}

	@JsonIgnore
	public void setPrimaryKey(
		UnsafeSupplier<String, Exception> primaryKeyUnsafeSupplier) {

		_primaryKeySupplier = () -> {
			try {
				return primaryKeyUnsafeSupplier.get();
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
	protected String primaryKey;

	@JsonIgnore
	private Supplier<String> _primaryKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getResourceName() {
		if (_resourceNameSupplier != null) {
			resourceName = _resourceNameSupplier.get();

			_resourceNameSupplier = null;
		}

		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;

		_resourceNameSupplier = null;
	}

	@JsonIgnore
	public void setResourceName(
		UnsafeSupplier<String, Exception> resourceNameUnsafeSupplier) {

		_resourceNameSupplier = () -> {
			try {
				return resourceNameUnsafeSupplier.get();
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
	protected String resourceName;

	@JsonIgnore
	private Supplier<String> _resourceNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getRoleId() {
		if (_roleIdSupplier != null) {
			roleId = _roleIdSupplier.get();

			_roleIdSupplier = null;
		}

		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;

		_roleIdSupplier = null;
	}

	@JsonIgnore
	public void setRoleId(
		UnsafeSupplier<Long, Exception> roleIdUnsafeSupplier) {

		_roleIdSupplier = () -> {
			try {
				return roleIdUnsafeSupplier.get();
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
	protected Long roleId;

	@JsonIgnore
	private Supplier<Long> _roleIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getScope() {
		if (_scopeSupplier != null) {
			scope = _scopeSupplier.get();

			_scopeSupplier = null;
		}

		return scope;
	}

	public void setScope(Long scope) {
		this.scope = scope;

		_scopeSupplier = null;
	}

	@JsonIgnore
	public void setScope(UnsafeSupplier<Long, Exception> scopeUnsafeSupplier) {
		_scopeSupplier = () -> {
			try {
				return scopeUnsafeSupplier.get();
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
	protected Long scope;

	@JsonIgnore
	private Supplier<Long> _scopeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RolePermission)) {
			return false;
		}

		RolePermission rolePermission = (RolePermission)object;

		return Objects.equals(toString(), rolePermission.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String[] actionIds = getActionIds();

		if (actionIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionIds\": ");

			sb.append("[");

			for (int i = 0; i < actionIds.length; i++) {
				sb.append("\"");

				sb.append(_escape(actionIds[i]));

				sb.append("\"");

				if ((i + 1) < actionIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(label));

			sb.append("\"");
		}

		String primaryKey = getPrimaryKey();

		if (primaryKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primaryKey\": ");

			sb.append("\"");

			sb.append(_escape(primaryKey));

			sb.append("\"");
		}

		String resourceName = getResourceName();

		if (resourceName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resourceName\": ");

			sb.append("\"");

			sb.append(_escape(resourceName));

			sb.append("\"");
		}

		Long roleId = getRoleId();

		if (roleId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleId\": ");

			sb.append(roleId);
		}

		Long scope = getScope();

		if (scope != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(scope);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.RolePermission",
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
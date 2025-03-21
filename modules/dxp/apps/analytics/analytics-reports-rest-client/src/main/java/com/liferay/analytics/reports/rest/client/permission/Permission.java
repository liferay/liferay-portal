/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.permission;

import com.liferay.analytics.reports.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Objects;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class Permission {

	public static Permission toDTO(String json) {
		PermissionJSONParser<Permission> permissionJSONParser =
			new PermissionJSONParser();

		return permissionJSONParser.parseToDTO(json);
	}

	public Object[] getActionIds() {
		return actionIds;
	}

	public String getRoleExternalReferenceCode() {
		return roleExternalReferenceCode;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setActionIds(Object[] actionIds) {
		this.actionIds = actionIds;
	}

	public void setRoleExternalReferenceCode(String roleExternalReferenceCode) {
		this.roleExternalReferenceCode = roleExternalReferenceCode;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (actionIds != null) {
			sb.append("\"actionIds\": [");

			for (int i = 0; i < actionIds.length; i++) {
				sb.append("\"");
				sb.append(actionIds[i]);
				sb.append("\"");

				if ((i + 1) < actionIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (roleExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleExternalReferenceCode\": \"");
			sb.append(roleExternalReferenceCode);
			sb.append("\"");
		}

		if (roleName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleName\": \"");
			sb.append(roleName);
			sb.append("\"");
		}

		if (roleType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleType\": \"");
			sb.append(roleType);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	protected Object[] actionIds;
	protected String roleExternalReferenceCode;
	protected String roleName;
	protected String roleType;

	private static class PermissionJSONParser<T>
		extends BaseJSONParser<Permission> {

		@Override
		protected Permission createDTO() {
			return new Permission();
		}

		@Override
		protected Permission[] createDTOArray(int size) {
			return new Permission[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "roleExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleType")) {
				return false;
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

		@Override
		protected void setField(
			Permission permission, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				if (jsonParserFieldValue != null) {
					permission.setActionIds((Object[])jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "roleExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					permission.setRoleExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleName")) {
				if (jsonParserFieldValue != null) {
					permission.setRoleName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleType")) {
				if (jsonParserFieldValue != null) {
					permission.setRoleType((String)jsonParserFieldValue);
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.permission;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 */
@GraphQLName("Permission")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Permission")
public class Permission {

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	public String[] getActionIds() {
		return actionIds;
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	public String getRoleExternalReferenceCode() {
		return roleExternalReferenceCode;
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	public String getRoleName() {
		return roleName;
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	public String getRoleType() {
		return roleType;
	}

	public void setActionIds(String[] actionIds) {
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

	protected String[] actionIds;
	protected String roleExternalReferenceCode;
	protected String roleName;
	protected String roleType;

}
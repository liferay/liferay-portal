/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition;

import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

/**
 * @author Michael C. Han
 */
public class RoleRecipient extends Recipient {

	public RoleRecipient(long roleId, String roleType) {
		super(RecipientType.ROLE);

		_roleId = roleId;
		_roleType = roleType;

		_roleExternalReferenceCode = null;
		_roleName = null;
	}

	public RoleRecipient(
		String roleExternalReferenceCode, long roleId, String roleName,
		String roleType) {

		super(RecipientType.ROLE);

		_roleExternalReferenceCode = roleExternalReferenceCode;
		_roleId = roleId;
		_roleName = roleName;
		_roleType = roleType;
	}

	public RoleRecipient(String roleName, String roleType) {
		super(RecipientType.ROLE);

		_roleName = roleName;
		_roleType = roleType;

		_roleExternalReferenceCode = null;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RoleRecipient)) {
			return false;
		}

		RoleRecipient roleRecipient = (RoleRecipient)object;

		if (Objects.equals(_roleName, roleRecipient._roleName) &&
			(_roleId == roleRecipient._roleId)) {

			return true;
		}

		return true;
	}

	public String getRoleExternalReferenceCode() {
		return _roleExternalReferenceCode;
	}

	public long getRoleId() {
		return _roleId;
	}

	public String getRoleName() {
		return _roleName;
	}

	public String getRoleType() {
		return _roleType;
	}

	@Override
	public int hashCode() {
		if (Validator.isNotNull(_roleExternalReferenceCode)) {
			return _roleExternalReferenceCode.hashCode();
		}

		if (Validator.isNotNull(_roleName)) {
			return _roleName.hashCode();
		}

		String roleIdString = String.valueOf(_roleId);

		return roleIdString.hashCode();
	}

	public boolean isAutoCreate() {
		return _autoCreate;
	}

	public void setAutoCreate(boolean autoCreate) {
		_autoCreate = autoCreate;
	}

	private boolean _autoCreate;
	private final String _roleExternalReferenceCode;
	private long _roleId;
	private final String _roleName;
	private final String _roleType;

}
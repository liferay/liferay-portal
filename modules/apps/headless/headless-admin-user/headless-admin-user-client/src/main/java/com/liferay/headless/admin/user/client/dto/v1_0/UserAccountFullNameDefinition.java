/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.dto.v1_0;

import com.liferay.headless.admin.user.client.function.UnsafeSupplier;
import com.liferay.headless.admin.user.client.serdes.v1_0.UserAccountFullNameDefinitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class UserAccountFullNameDefinition implements Cloneable, Serializable {

	public static UserAccountFullNameDefinition toDTO(String json) {
		return UserAccountFullNameDefinitionSerDes.toDTO(json);
	}

	public UserAccountFullNameDefinitionField[]
		getUserAccountFullNameDefinitionFields() {

		return userAccountFullNameDefinitionFields;
	}

	public void setUserAccountFullNameDefinitionFields(
		UserAccountFullNameDefinitionField[]
			userAccountFullNameDefinitionFields) {

		this.userAccountFullNameDefinitionFields =
			userAccountFullNameDefinitionFields;
	}

	public void setUserAccountFullNameDefinitionFields(
		UnsafeSupplier<UserAccountFullNameDefinitionField[], Exception>
			userAccountFullNameDefinitionFieldsUnsafeSupplier) {

		try {
			userAccountFullNameDefinitionFields =
				userAccountFullNameDefinitionFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserAccountFullNameDefinitionField[]
		userAccountFullNameDefinitionFields;

	@Override
	public UserAccountFullNameDefinition clone()
		throws CloneNotSupportedException {

		return (UserAccountFullNameDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserAccountFullNameDefinition)) {
			return false;
		}

		UserAccountFullNameDefinition userAccountFullNameDefinition =
			(UserAccountFullNameDefinition)object;

		return Objects.equals(
			toString(), userAccountFullNameDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UserAccountFullNameDefinitionSerDes.toJSON(this);
	}

}
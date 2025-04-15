/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.UserSchemaExtensionSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class UserSchemaExtension implements Cloneable, Serializable {

	public static UserSchemaExtension toDTO(String json) {
		return UserSchemaExtensionSerDes.toDTO(json);
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public void setBirthday(
		UnsafeSupplier<Date, Exception> birthdayUnsafeSupplier) {

		try {
			birthday = birthdayUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date birthday;

	public Boolean getMale() {
		return male;
	}

	public void setMale(Boolean male) {
		this.male = male;
	}

	public void setMale(UnsafeSupplier<Boolean, Exception> maleUnsafeSupplier) {
		try {
			male = maleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean male;

	@Override
	public UserSchemaExtension clone() throws CloneNotSupportedException {
		return (UserSchemaExtension)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserSchemaExtension)) {
			return false;
		}

		UserSchemaExtension userSchemaExtension = (UserSchemaExtension)object;

		return Objects.equals(toString(), userSchemaExtension.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UserSchemaExtensionSerDes.toJSON(this);
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.dto.v1_0;

import com.liferay.headless.cms.client.serdes.v1_0.DeleteBulkActionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public class DeleteBulkAction
	extends BulkAction implements Cloneable, Serializable {

	public static DeleteBulkAction toDTO(String json) {
		return DeleteBulkActionSerDes.toDTO(json);
	}

	@Override
	public DeleteBulkAction clone() throws CloneNotSupportedException {
		return (DeleteBulkAction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DeleteBulkAction)) {
			return false;
		}

		DeleteBulkAction deleteBulkAction = (DeleteBulkAction)object;

		return Objects.equals(toString(), deleteBulkAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DeleteBulkActionSerDes.toJSON(this);
	}

}
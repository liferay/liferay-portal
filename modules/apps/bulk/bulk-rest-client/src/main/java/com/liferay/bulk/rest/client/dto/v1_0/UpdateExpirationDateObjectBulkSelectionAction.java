/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.dto.v1_0;

import com.liferay.bulk.rest.client.function.UnsafeSupplier;
import com.liferay.bulk.rest.client.serdes.v1_0.UpdateExpirationDateObjectBulkSelectionActionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class UpdateExpirationDateObjectBulkSelectionAction
	extends BulkAction implements Cloneable, Serializable {

	public static UpdateExpirationDateObjectBulkSelectionAction toDTO(
		String json) {

		return UpdateExpirationDateObjectBulkSelectionActionSerDes.toDTO(json);
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public void setExpirationDate(
		UnsafeSupplier<Date, Exception> expirationDateUnsafeSupplier) {

		try {
			expirationDate = expirationDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date expirationDate;

	@Override
	public UpdateExpirationDateObjectBulkSelectionAction clone()
		throws CloneNotSupportedException {

		return (UpdateExpirationDateObjectBulkSelectionAction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof
				UpdateExpirationDateObjectBulkSelectionAction)) {

			return false;
		}

		UpdateExpirationDateObjectBulkSelectionAction
			updateExpirationDateObjectBulkSelectionAction =
				(UpdateExpirationDateObjectBulkSelectionAction)object;

		return Objects.equals(
			toString(),
			updateExpirationDateObjectBulkSelectionAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UpdateExpirationDateObjectBulkSelectionActionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1687178603